package utilities;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.boundingRect;

public class EdgeDetector {

    public MatOfPoint findBoundingPolygon(Mat sourceImage) {
        // @todo Canny before?
        // Convert to black and white
        Mat blackWhite = new Mat();
        cvtColor(sourceImage, blackWhite, COLOR_BGR2GRAY);

        // Apply threshold
        // @todo Inject / detect which threshold to use?
        Mat threshOut = new Mat();
        threshold(blackWhite, threshOut, 100, 255, THRESH_BINARY);

        List<MatOfPoint> contours = new ArrayList<>();
        // @todo Maybe Imgproc.RETR_LIST -> Imgproc.RETR_EXTERNAL?
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

        return maxContour;
    }

    public Rect findBoundingRect(Mat sourceImage) {
        MatOfPoint contour = findBoundingPolygon(sourceImage);
        return boundingRect(contour);
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