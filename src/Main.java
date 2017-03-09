import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import utilities.EdgeDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static EdgeDetector edgeDetector = new EdgeDetector();
    private static ReceiptExtractor receiptExtractor = new ReceiptExtractor();

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String videoPath = String.format(
            "%s\\src\\test\\resources\\videos\\reflection-test.mp4",
            System.getProperty("user.dir")
        );

        VideoCapture videoCapture = new VideoCapture(videoPath);
        boolean detectGlare = true;
        Mat extractReceipt = receiptExtractor.extractReceipt(videoCapture, detectGlare);

        String outPath = String.format("%s\\Desktop\\frames\\merged.png", System.getProperty("user.home"));
        Imgcodecs.imwrite(outPath, extractReceipt);
    }

}
