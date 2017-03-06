import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import utilities.BlurDetector;
import utilities.EdgeDetector;
import utilities.VideoSplitter;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ReceiptExtractor {

    // @todo Set these constants based on the video itself?
    private final static int NUM_SPLITTED_FRAMES = 10;
    private final static int NUM_IMAGE_PIECES = 2;

    private static final Logger logger = LogManager.getLogger(ReceiptExtractor.class);

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
        logger.info("Extracting receipt from video capture [detectGlare={}]", detectGlare);
        // Split the video to frames and select the best frames.
        List<Mat> frames = VideoSplitter.getFrames(source, NUM_SPLITTED_FRAMES);
        if (frames.isEmpty()) {
            throw new IllegalArgumentException(
                "Splitted video to 0 frames. If the source isn't empty, ensure that OpenCV is installed correctly."
            );
        }
        logger.info("Splitted capture to {} frames", frames.size());

        frames = BlurDetector.getBestFrames(frames, frames.size() / 2);
        logger.info("Selected the {} best frames", frames.size());

        // Extract the receipt from the frames
        List<Mat> receipts = new ArrayList<>();
        for (Mat frame : frames) {
            Mat receipt = edgeDetector.extractBiggestObject(frame, detectGlare);
            if (receipt == null) {
                // Failed to extract receipt
                logger.warn("Failed to extract receipt from frame");
                continue;
            }

            receipts.add(receipt);
        }

        logger.info("Extracted {} receipts from {} frames", receipts.size(), frames.size());

        // Merge receipts into super-image
        return BlurDetector.createImageRows(receipts, NUM_IMAGE_PIECES);
    }
}
