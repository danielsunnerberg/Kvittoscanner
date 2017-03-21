import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String videoPath = String.format(
            "%s\\src\\test\\resources\\videos\\reflection-test.mp4",
            System.getProperty("user.dir")
        );

        VideoCapture videoCapture = new VideoCapture(videoPath);
        boolean detectGlare = true;
        Mat extractReceipt = new ReceiptExtractor().extractReceipt(videoCapture, detectGlare);

        String outPath = String.format("%s\\Desktop\\frames\\merged.png", System.getProperty("user.home"));
        Imgcodecs.imwrite(outPath, extractReceipt);
    }

}
