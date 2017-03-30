import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import utilities.EdgeDetector;
import utilities.ReceiptAligner;
import utilities.ReceiptMerger;
import utilities.VideoSplitter;

import java.util.ArrayList;
import java.util.List;

public class ReceiptExtractor {

    // @todo Set these constants based on the video itself?
    private final static int NUM_SPLITTED_FRAMES = 30;

    private static final Logger logger = LogManager.getLogger(ReceiptExtractor.class);

    private final EdgeDetector edgeDetector = new EdgeDetector();
    private final ReceiptAligner receiptAligner = new ReceiptAligner();
    private final ReceiptMerger receiptMerger = new ReceiptMerger(false);

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

        frames = receiptMerger.getBestFrames(frames, frames.size() / 2);
        logger.info("Selected the {} best frames", frames.size());

        rotateFrames(frames);

        // Extract the receipt from the frames
        Mat reference = null;
        List<Mat> receipts = new ArrayList<>();
        for (Mat frame : frames) {
            Mat receipt = edgeDetector.extractBiggestObject(frame, detectGlare);
            if (receipt == null) {
                logger.warn("Failed to extract receipt from frame");
                continue;
            }

            if (reference == null) {
                reference = receipt; // @todo We may want to select the reference in another way
            }

            // Align images to fit the reference
            Mat aligned = receiptAligner.align(reference, receipt);

            logger.info("Extracted receipt from frame successfully");
            receipts.add(aligned);
        }

        logger.info("Extracted {} receipts from {} frames", receipts.size(), frames.size());

        // Merge receipts into super-image
        return receiptMerger.createImageRows(receipts);
    }

    private void rotateFrames(List<Mat> frames) {
        for (Mat frame : frames) {
            if (frame.width() > frame.height()) {
                Core.flip(frame.t(), frame, Core.ROTATE_90_COUNTERCLOCKWISE);
            }
        }
    }
}
