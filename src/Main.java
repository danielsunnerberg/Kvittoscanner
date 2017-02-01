import org.opencv.core.*;
import utilities.ComputerVision;
import utilities.VideoSplitter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());
    }
}
