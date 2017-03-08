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



    }
}
