import com.google.gson.JsonObject;
import com.google.gson.*;
import com.sun.javafx.geom.Edge;
import org.junit.runner.Computer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import utilities.ComputerVision;
import utilities.EdgeDetector;
import utilities.VideoSplitter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static EdgeDetector edgeDetector = new EdgeDetector();
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//        String path = "C:\\Users\\daniel-windevbox\\projects\\Kvittoscanner\\src\\test\\resources\\images\\contrastTest\\mid-contrast2.jpg";
//        String path = "C:\\Users\\daniel-windevbox\\Desktop\\contrast.jpg";
        String path = "D:\\Kvittoscanner\\src\\test\\resources\\images\\reflection.jpg";

        Mat frame = Imgcodecs.imread(path);

        boolean detectGlare = false;
        MatOfPoint fc2 = new MatOfPoint(edgeDetector.findBoundingBox(frame, detectGlare).toArray());
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(fc2);

        Scalar color = new Scalar(255, 0, 0);
        Imgproc.drawContours(frame, contours, 0, color, 2, 8, new Mat(), 0, new Point());
        Imgcodecs.imwrite("C:\\Users\\Joakim\\bounding-box.png", frame);






    }
}
