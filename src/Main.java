import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String videoPath = String.format(
            "%s\\src\\test\\resources\\videos\\reflection-test.mp4",
            System.getProperty("user.dir")
        );

        VideoCapture videoCapture = new VideoCapture(videoPath);
        boolean detectGlare = true;
        List<Mat> extractReceipt = new ReceiptExtractor().extractReceipts(videoCapture, detectGlare);
        System.out.println(extractReceipt.size());
    }

}
