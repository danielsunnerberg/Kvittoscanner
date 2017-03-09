package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.opencv.utils.Converters;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.photo.Photo.inpaint;

public class EdgeDetector {

    private static final Logger logger = LogManager.getLogger(EdgeDetector.class);

    private final int MAT_WIDTH = 500;
    private final int MAT_HEIGHT = 1000;
    private final int NUM_CORNERS = 4;

    private final ContrastDetector contrastDetector;
    private final BoxValidation boxValidation;

    public EdgeDetector() {
        contrastDetector = new ContrastDetector();
        boxValidation = new BoxValidation();
    }

    // @todo Mask if we have all frames?

    private MatOfPoint2f reducePolygon(MatOfPoint2f polygon) {
        double epsilonFactor = 0.50;

        MatOfPoint2f approximation = null;
        for (int reduceCounter = 0; reduceCounter < 30; reduceCounter++) {
            // Simplify the found polygon to contain only the 4 corners.
            // This is a bit tricky, as we cannot directly specify how many points we WANT;
            // so we have to try with a few epsilon-values.
            approximation = new MatOfPoint2f();
            Imgproc.approxPolyDP(polygon, approximation, polygon.total() * epsilonFactor, true);

            long points = approximation.total();
            if (points == NUM_CORNERS) {
                logger.info("Reduced bounding polygon to 4 points in {} attempts", reduceCounter + 1);
                return approximation;
            } else if (points > NUM_CORNERS) {
                epsilonFactor += 0.05;
            } else {
                epsilonFactor -= 0.05;
            }
        }

        return approximation;
    }

    // @todo Extract into class
    private MatOfPoint2f smartPolygonReduction(MatOfPoint2f approximation, int exclusionMode) {
        final int MAX_REDUCTION_ANGLE = 90 + 10; // @todo Doesn't work with weird perspectives
        Point[] points = approximation.toArray();

        // Find the first point with a too wide/narrow angle, a
        Integer a = null;
        for (int i = 0; i < points.length; i++) {
            Point p1 = points[i];
            Point p2 = points[Math.floorMod(i - 1, points.length)];
            Point p3 = points[Math.floorMod(i + 1, points.length)];

            if (Math.abs(boxValidation.angle(p1, p2, p3)) > MAX_REDUCTION_ANGLE) {
                logger.info("Found first extreme point, {}", i);
                a = i;
                break;
            }
        }

        if (a == null) {
            logger.info("Failed to get first extreme point.");
            return null;
        }

        // Walking backwards, find the next point with a too wide/narrow angle, b
        Integer b = null;
        for (int i = points.length - 1; i >= 0; i--) {
            Point p1 = points[i];
            Point p2 = points[Math.floorMod(i - 1, points.length)];
            Point p3 = points[Math.floorMod(i + 1, points.length)];

            if (Math.abs(boxValidation.angle(p1, p2, p3)) > MAX_REDUCTION_ANGLE) {
                logger.info("Found second extreme point, {}", i);
                b = i;
                break;
            }
        }

        if (b == null) {
            logger.info("Failed to get second extreme point.");
            return null;
        }

        if (Objects.equals(a, b)) {
            logger.info("Found extreme points are identical, removing single point");
            List<Point> reduced = new ArrayList<>();
            for (int i = 0; i < points.length; i++) {
                if (i == a) {
                    continue;
                }
                reduced.add(points[i]);
            }

            return new MatOfPoint2f(
                reduced.toArray(new Point[reduced.size()])
            );
        }

        // Create a region of points in [a..b]
        List<Point> _aRegion = new ArrayList<>();
        _aRegion.addAll(Arrays.asList(points).subList(a, b + 1));
        MatOfPoint2f aRegion = new MatOfPoint2f(
            _aRegion.stream().toArray(Point[]::new)
        );

        // Create a region of points in (points \ aRegion)
        List<Point> _bRegion = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            if (i >= a && i <= b) {
                continue;
            }

            _bRegion.add(points[i]);
        }
        MatOfPoint2f bRegion = new MatOfPoint2f(
            _bRegion.stream().toArray(Point[]::new)
        );

        if (_aRegion.isEmpty() || _bRegion.isEmpty()) {
            logger.info("Polygon reduction failed, one area is empty");
            return null;
        }

        // We now have two regions, aRegion and bRegion. One of these (hopefully)
        // contains glare and the other the receipt without the glare.
        // A simple, but not always correct filter is simply choosing the one with the
        // biggest area. In the event that we choose wrong, the angle detector
        // will discard the frame anyways.
        if (Imgproc.contourArea(aRegion) > Imgproc.contourArea(bRegion)) {
            System.out.println("a");
            return aRegion;
        } else {
            switch (exclusionMode) {
                case 1:
                    // (a,b]
                    _bRegion.add(points[b]);
                    break;
                case 2:
                    // [a, b)
                    _bRegion.add(points[a]);
                    break;
                case 3:
                    // [a,b]
                    _bRegion.add(points[a]);
                    _bRegion.add(points[b]);
                    break;
                case 0:
                default:
                    // (a,b)
                    break;
            }
            return new MatOfPoint2f(
                _bRegion.stream().toArray(Point[]::new)
            );
        }
    }

    /**
     * Finds a polygon surrounding the biggest object in the image.
     *
     * @param source Source to analyze
     * @param detectGlare Whether anti-glare methods should be automatically applied
     * @return Points forming a polygon which encloses the biggest object in the image.
     */
    public MatOfPoint2f findBoundingBox(Mat source, boolean detectGlare) {
        MatOfPoint2f boundingPolygon = new MatOfPoint2f(
            findBoundingPolygon(source, detectGlare, false).toArray()
        );

        MatOfPoint2f approximation = reducePolygon(boundingPolygon);
        if (approximation.total() > 4) {
            logger.info("Failed to reduce polygon to 4 points, attempting smart reduction.");
            for (int exclusionMode = 0; exclusionMode < 4; exclusionMode ++) {
                // @todo Document, (a,b), [a,b), ..., angle validation for all examples

                MatOfPoint2f smartReduction = smartPolygonReduction(approximation, exclusionMode);
                if (smartReduction == null || smartReduction.total() != 4) {
                    logger.info("Smart reduction (try {}) failed, discarding", exclusionMode);
                    continue;
                }

                // We have a reduction with 4 points, validate all corners.
                // A perfect corner has an angle of 90*.
                // Allow each corner some slack, discard the frame if it's too bad.
                if (boxValidation.validateCornerAngles(smartReduction)) {
                    logger.info("Found valid smart reduction in {} tries", exclusionMode);
                    return smartReduction;
                } else {
                    logger.info("Smart reduction has invalid corners, discarding");
                }
            }
        } else if (approximation.total() < 4) {
            logger.info("Too few corners in reduction, discarding");
        }

        // @todo Validate angles here, points == 4

        return null;
    }

    MatOfPoint findBoundingPolygon(Mat _source, boolean detectGlare, boolean recursiveCall) {
        // If we're detecting glares, we may paint on the specified source
        // which actually is a clone.
        Mat source = _source; //recursiveCall ? _source : _source.clone();

        if (detectGlare && ! recursiveCall) {
            // @todo Do not use this flag blindly, do some own detection?
            // @todo Improve hit-ratio of the glare-detection. Sketchy at best right now.
            // Detect glare recursively by using stricter thresholds. This call
            // won't result in another recursion level.
            final MatOfPoint glarePolygon = findBoundingPolygon(source, false, true);

            // By drawing the glare's bounding box on the image clone, the following
            // steps require less strict thresholds.
            final Rect glareBoundingBox = boundingRect(glarePolygon);
            final Scalar boundingRectColor = new Scalar(255, 255, 255);
            Imgproc.rectangle(source, glareBoundingBox.br(), glareBoundingBox.tl(), boundingRectColor);

            // @todo Do this based on where the glare is located?
            // Left heavy => re-align
            int xMin = (int) (glareBoundingBox.tl().x - 100);
            if (xMin < 0) {
                xMin = 0;
            }

            int xMax = (int) (glareBoundingBox.br().x + 100);
            if (xMax > source.width()) {
                xMax = source.width();
            }

            int yMin = (int) (glareBoundingBox.tl().y - 120);
            if (yMin < 0) {
                yMin = 0;
            }

            int yMax = (int) (glareBoundingBox.br().y + 100);
            if (yMax > source.height()) {
                yMax = source.height();
            }

            // Create a mask which masks out only the parts in the glare bounding box.
            Mat mask = new Mat(source.rows(), source.cols(), CV_8U);
            for (int x = xMin; x < xMax; x++) {
                for (int y = yMin; y < yMax; y++) {
                    mask.put(y, x, 255, 255, 255);
                }
            }

            // "Stretch" the content in the bounding box in a content-aware way.
            // Think of it as a content aware/smart removal.
            inpaint(source, mask, source, 5, Photo.INPAINT_TELEA);

            // @todo Remove
//            List<MatOfPoint> contours = new ArrayList<>();
//            contours.add(glarePolygon);
//            Imgproc.drawContours(source, contours, 0, new Scalar(255, 0, 0), 2, 8, new Mat(), 0, new Point());
        }

        // Convert to black and white
        Mat blackWhite = new Mat();
        cvtColor(source, blackWhite, COLOR_BGR2GRAY);

        // Apply threshold
        Mat threshOut = new Mat();
        int thresh = findThresholdValue(source);
        if (! detectGlare && recursiveCall) {
            // We're detecting glares; which requires a far stricter threshold.
            thresh *= 2;
        }
        logger.info("Finding contours with threshold value {}", thresh);


        threshold(blackWhite, threshOut, thresh, 255, THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(threshOut, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find contour with biggest area
        MatOfPoint maxContour = null;
        double maxArea = 0;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);

            if (area > maxArea) {
                maxArea = area;
                maxContour = contour;
            }
        }

        if (maxContour == null) {
            // No contour found (one colored image?). Hence, the original image is already bounded.
            Size size = source.size();
            return new MatOfPoint(
                    new Point(0, 0),
                    new Point(size.width, 0),
                    new Point(size.width, size.height),
                    new Point(0, size.height)
            );
        }

        if (detectGlare) {
            // Simplify the found contour, which eventually removes
            // extreme points caused by glare.
            MatOfPoint2f contour2f = new MatOfPoint2f(maxContour.toArray());
            approxPolyDP(contour2f, contour2f, 150, true);
            return new MatOfPoint(contour2f.toArray());
        }

        return maxContour;
    }

    /**
     * Finds a polygon surrounding the biggest object in the image.
     *
     * @param source Source to analyze
     * @param detectGlare Whether anti-glare methods should be automatically applied
     * @return Points forming a polygon which encloses the biggest object in the image.
     */
    private Mat getCornerMat(Mat source, boolean detectGlare) {
        MatOfPoint2f approxCurve = findBoundingBox(source, detectGlare);
        if (approxCurve == null) {
            return null;
        }

        List<Point> points = new ArrayList<>();
        for(int i = 0; i < NUM_CORNERS; i++) {
            double[] coordinates = approxCurve.get(i,0);
            Point p = new Point(coordinates[0], coordinates[1]);
            points.add(p);
        }

        return createOrderedCornerMat(points);
    }

    private Mat createOrderedCornerMat(List<Point> points) {
        Point topLeft, topRight, bottomLeft, bottomRight;

        // Sort by y-axis ascending, the two points with lowest y are our
        // top left/right points.
        points.sort(Comparator.comparingDouble(p -> p.y));
        Point p1 = points.get(0);
        Point p2 = points.get(1);
        if (p1.x > p2.x) {
            topLeft = p2;
            topRight = p1;
        } else {
            topLeft = p1;
            topRight = p2;
        }

        // Sort by y-axis descending
        points = points.stream()
                .sorted(Comparator.comparingDouble(p -> ((Point) p).y).reversed())
                .collect(Collectors.toList());
        Point p3 = points.get(0);
        Point p4 = points.get(1);
        if (p3.x > p4.x) {
            bottomLeft = p4;
            bottomRight = p3;
        } else {
            bottomLeft = p3;
            bottomRight = p4;
        }

        // Create a mat with perspective to the 4 corners we get from previous approximation
        // Corners are expected to go counter-clockwise from the top right corner.
        List<Point> corners = new ArrayList<>();
        corners.add(topRight);
        corners.add(topLeft);
        corners.add(bottomLeft);
        corners.add(bottomRight);

        return Converters.vector_Point2f_to_Mat(corners);
    }

    /**
     * Extracts the biggest object found in the image, automatically skewing the result.
     *
     * @param source Source to analyze
     * @param detectGlare Whether anti-glare methods should be automatically applied. May be time consuming.
     * @return A new Mat, consisting only of the found object.
     */
    public Mat extractBiggestObject(Mat source, boolean detectGlare) {
        Mat mat = getCornerMat(source, detectGlare);
        if (mat == null) {
            return null;
        }
        return skew(source, mat);
    }

    private int findThresholdValue(Mat source) {
        final double contrast = contrastDetector.calculateContrast(source);
        // We want to convert the contrast value to a threshold value, [0, 255].
        // Some testing of images with various contrast and which threshold they needed
        // gave the following points: fit {1.1, 240}, {2.5, 100}, {5, 0}, where (x = contrast, y = threshold).
        // Creating a fit gives the following function, which gives us our threshold:
        return (int) (-157.763 * Math.log(0.204531 * contrast));
    }

    /**
     * Skews the source image according to where we have our corners.
     *
     * @param imgSrc The image source to be skewed.
     * @param start The mat that contains the corners from the source.
     * @return A skewed mat according to where the corner points were.
     */
    private Mat skew(Mat imgSrc, Mat start) {
        //Set the mat to a standard size to make sure all images get the same size.
        Mat out = new Mat(MAT_WIDTH, MAT_HEIGHT, CvType.CV_8UC1);

        Point p1 = new Point(MAT_WIDTH, 0);
        Point p2 = new Point(0, 0);
        Point p3 = new Point(0, MAT_HEIGHT);
        Point p4 = new Point(MAT_WIDTH, MAT_HEIGHT);

        List<Point> points = Arrays.asList(p1, p2, p3, p4);
        Mat end = Converters.vector_Point2f_to_Mat(points);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(start, end);
        Imgproc.warpPerspective(imgSrc, out, perspectiveTransform, new Size(MAT_WIDTH, MAT_HEIGHT), Imgproc.INTER_CUBIC);

        return out;
    }
}
