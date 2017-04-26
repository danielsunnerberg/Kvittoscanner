import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import utilities.HDRCreator;

import java.io.IOException;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String HDR_MODE = "hdr";
    private static final String VIDEO_MODE = "video";

    public static void main(String[] args) throws IOException {
        String opencvNative = "C:\\Users\\contact_jacob_lundbe\\Documents\\ReciptBackend\\opencv\\";
        System.load(opencvNative + Core.NATIVE_LIBRARY_NAME + ".dll");
        System.load(opencvNative + "opencv_ffmpeg320_64.dll");

        String mode = args[0];

        if(mode.equals(VIDEO_MODE)) {
            String videoPath = args[1];

            VideoCapture videoCapture = new VideoCapture(videoPath);
            boolean detectGlare = false;
            Mat extractReceipt = new ReceiptExtractor().extractSuperReceipt(videoCapture, detectGlare);

            String outPath = args[2];
            Imgcodecs.imwrite(outPath, extractReceipt);
        }

        else if(mode.equals(HDR_MODE)) {
            String path = args[1];
            String method = args[2];
            String resultPath = args[3];

            Mat out = HDRCreator.createHDR(path, method);
            Imgcodecs.imwrite(resultPath, out);
        }
    }
}
