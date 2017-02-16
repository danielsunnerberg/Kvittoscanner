package utilities;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32FC2;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.boundingRect;

public class EdgeDetector {

    /**
     * Finds a polygon surrounding the biggest object in the image.
     *
     * @param source Source to analyze
     * @return Points forming a polygon which encloses the biggest object in the image.
     */
    public MatOfPoint findBoundingPolygon(Mat source) {
        // Convert to black and white
        Mat blackWhite = new Mat();
        cvtColor(source, blackWhite, COLOR_BGR2GRAY);

        // Apply threshold
        Mat threshOut = new Mat();
        final int thresh = findThresholdValue(source);
        System.out.println("Got thresh: " + thresh);
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

        return maxContour;
    }

    /**
     * Finds a bounding rectangle which surrounds the biggest object in the image.
     *
     * @param source Source to analyze
     * @return Rectangle which encloses the biggest object in the image
     */
    public Rect findBoundingRect(Mat source) {
        MatOfPoint contour = findBoundingPolygon(source);
        return boundingRect(contour);
    }

    /**
     * Extracts the biggest object found in the image.
     *
     * @param source Source to analyze
     * @return A new Mat, consisting only of the found object.
     */
    public Mat extractBiggestObject(Mat source) {
        Rect bounds = findBoundingRect(source);
        return new Mat(source, bounds);
    }

    public Mat skewMat(Mat imageMat, Point p1, Point p2, Point p3, Point p4)
    {
        Mat src = new Mat(4,1,CV_32FC2);
        src.put(0,0, (int)p2.x,(int)p2.y, (int)p3.x,(int)p3.y, (int)p4.x,(int)p4.y);
        Mat dst = new Mat(4,1,CV_32FC2);
        dst.put(0,0, imageMat.width(),(int)p1.y, 0,imageMat.height(), imageMat.width(),imageMat.height());

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src, dst);
        Mat rotated_image = imageMat.clone();
        Imgproc.warpPerspective(imageMat, rotated_image, perspectiveTransform, new Size(imageMat.width(),imageMat.height()));
        Mat cropped_image = rotated_image.submat((int)p1.y, imageMat.height(), (int)p1.x, imageMat.width());
        return cropped_image;
    }

    private int findThresholdValue(Mat source) {
        final double contrast = new ContrastDetector().calculateContrast(source);
        // We want to convert the contrast value to a threshold value, [0, 255].
        // Some testing of images with various contrast and which threshold they needed
        // gave the following points: fit {1.1, 240}, {2.5, 100}, {5, 0}, where (x = contrast, y = threshold).
        // Creating a fit gives the following function, which gives us our threshold:
        return (int) (-157.763 * Math.log(0.204531 * contrast));
    }

    /*
        @todo Example usage, remove when documented.
        Scalar color = new Scalar(202, 0, 42);

        // Debug: draw polygon
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(edgeDetector.findBoundingPolygon(img));
        Imgproc.drawContours(img, contours, 0, color, 2, 8, new Mat(), 0, new Point());
        Imgcodecs.imwrite("C:\\Users\\daniel-windevbox\\Desktop\\fozo-polygon.png", img);

        // Debug: draw rect
        img = new Mat(image.getHeight(),image.getWidth(), CvType.CV_8UC3); // CV_32SC1
        img.put(0, 0, data);
        Rect r = edgeDetector.findBoundingRect(img);
        rectangle(img, r.br(), r.tl(), color, 2);
        Imgcodecs.imwrite("C:\\Users\\daniel-windevbox\\Desktop\\fozo-rect.png", img);
     */

}
