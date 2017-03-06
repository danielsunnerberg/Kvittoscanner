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
    private final static int NUM_IMAGE_PIECES = 2;

    private EdgeDetector edgeDetector = new EdgeDetector();

    /**
     * Extracts a receipt from a video stream by finding the best pieces of
     * the best frames.
     *
     * @param source Video stream to extract receipt from
     * @param detectGlare Whether anti-glare methods should be automatically applied. Should be `true` if flash was used.
     * @return The extracted receipt
     */
    public Mat extractReceipt(VideoCapture source, boolean detectGlare) {

        // Split the video to frames and select the best frames.
        List<Mat> frames = VideoSplitter.getFrames(source, NUM_SPLITTED_FRAMES);
        frames = BlurDetector.getBestFrames(frames, frames.size() / 4);

        // Extract the receipt from the frames
        List<Mat> receipts = new ArrayList<>();
        for (Mat frame : frames) {
            Mat receipt = edgeDetector.extractBiggestObject(frame, detectGlare);
            if (receipt == null) {
                // Failed to extract receipt
                continue;
            }

            // Skew each receipt to make them align
            // @todo Skew + make sure all images have exactly same size.
            // @todo How to re-size without loosing perspective?
            // @todo Maybe pad smaller sizes?
            receipts.add(receipt);
        }

        // Merge receipts into super-image
        return BlurDetector.createImageRows(receipts, NUM_IMAGE_PIECES);
    }
}
