import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import utilities.BlurDetector;
import utilities.EdgeDetector;
import utilities.VideoSplitter;

import java.util.ArrayList;
import java.util.List;

public class ReceiptExtractor {

    // @todo Set these constants based on the video itself?
    private final static int NUM_SPLITTED_FRAMES = 50;
    private final static int NUM_IMAGE_PIECES = 4;

    private EdgeDetector edgeDetector = new EdgeDetector();

    public Mat extractReceipt(VideoCapture source) {

        // Split the video to frames and select the best frames.
        List<Mat> frames = VideoSplitter.getFrames(source, NUM_SPLITTED_FRAMES);
        frames = BlurDetector.getBestFrames(frames, frames.size() / 2);

        System.out.println(frames.size());

        // Extract the receipt from the frames
        List<Mat> receipts = new ArrayList<>();
        int x = 0;
        for (Mat frame : frames) {
            Mat receipt = edgeDetector.extractBiggestObject(frame);
            System.out.println(receipt.size());

        //    Imgcodecs.imwrite("C:/Users/jacobth/Pictures/Camera Roll/rec" + (x++) + ".png", receipt);
            // Skew each receipt to make them align
            // @todo Skew + make sure all images have exactly same size.
            // @todo How to re-size without loosing perspective?
            // @todo Maybe pad smaller sizes?
            receipts.add(receipt);
        }

        // Merge receipts into super-image
        return BlurDetector.createImage(receipts, NUM_IMAGE_PIECES);
    }
}
