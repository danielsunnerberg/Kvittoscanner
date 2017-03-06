package utilities;

import org.opencv.core.*;
import org.opencv.core.Point;
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

    private final int MAT_WIDTH = 500;
    private final int MAT_HEIGHT = 1000;
    private final int NUM_CORNERS = 4;

    /**
     * Finds a polygon surrounding the biggest object in the image.
     *
     * @param source Source to analyze
     * @return Points forming a polygon which encloses the biggest object in the image.
     */
    public MatOfPoint2f findBoundingPolygon(Mat source) {
        // Convert to black and white
        Mat blackWhite = new Mat();
        cvtColor(source, blackWhite, COLOR_BGR2GRAY);

        // Apply threshold
        Mat threshOut = new Mat();
        final int thresh = findThresholdValue(source);

        threshold(blackWhite, threshOut, thresh, 255, THRESH_BINARY);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(threshOut, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f approxCurve = new MatOfPoint2f();

        // Find contour with biggest area
        MatOfPoint maxContour = null;
        double maxArea = 0;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);

            if (area > maxArea) {
                MatOfPoint2f tmpMat = new MatOfPoint2f(contour.toArray() );
                MatOfPoint2f aCurve = new MatOfPoint2f();
                Imgproc.approxPolyDP(tmpMat, aCurve, contour.total() * 0.05, true);

                //See if we got 4 corners in the approximation
                if (aCurve.total() == NUM_CORNERS) {
                    maxArea = area;
                    maxContour = contour;
                    approxCurve = aCurve;
                }
            }
        }

        if (maxContour == null) {
            // No contour found (one colored image?). Hence, the original image is already bounded.
            Size size = source.size();
            return new MatOfPoint2f(
                    new Point(0, 0),
                    new Point(size.width, 0),
                    new Point(size.width, size.height),
                    new Point(0, size.height)
            );
        }

        return approxCurve;
    }

    // @todo Mask if we have all frames?

    /**
     * Finds a polygon surrounding the biggest object in the image.
     *
     * @param _source Source to analyze
     * @param detectGlare Whether anti-glare actions should be applied. May be time consuming.
     * @return Points forming a polygon which encloses the biggest object in the image.
     */
    public MatOfPoint findBoundingPolygon3(Mat _source, boolean detectGlare) {
        return findBoundingPolygon3(_source, detectGlare, false);
    }

    private MatOfPoint findBoundingPolygon3(Mat _source, boolean detectGlare, boolean recursiveCall) {
        // If we're detecting glares, we may paint on the specified source
        // which actually is a clone.
        Mat source = detectGlare ? _source.clone() : _source;

        if (detectGlare && ! recursiveCall) {
            // @todo Do not use this flag blindly, do some own detection?
            // Detect glare recursively by using stricter thresholds. This call
            // won't result in another recursion level.
            final MatOfPoint glarePolygon = findBoundingPolygon3(source, false, true);

            // By drawing the glare's bounding box on the image clone, the following
            // steps require less strict thresholds.
            final Rect glareBoundingBox = boundingRect(glarePolygon);
            final Scalar boundingRectColor = new Scalar(255, 255, 255);
            Imgproc.rectangle(source, glareBoundingBox.br(), glareBoundingBox.tl(), boundingRectColor);

            // @todo Do this based on where the glare is located?
            // Left heavy => re-align
            int xMin = (int) (glareBoundingBox.tl().x + 0);
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
            // Simplify the found contour
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
     * @return Points forming a polygon which encloses the biggest object in the image.
     */
    private Mat getCornerMat(Mat source) {
        MatOfPoint2f approxCurve = findBoundingPolygon(source);

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
     * @return A new Mat, consisting only of the found object.
     */
    public Mat extractBiggestObject(Mat source) {
        Mat mat = getCornerMat(source);
        return skew(source, mat);
    }

    private int findThresholdValue(Mat source) {
        final double contrast = new ContrastDetector().calculateContrast(source);
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
