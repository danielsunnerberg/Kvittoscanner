import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ReceiptExtractorTest {

    private ReceiptExtractor receiptExtractor;

    @Before
    public void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        receiptExtractor = new ReceiptExtractor();
    }

    private void testVideo(String filename, int expectedFrames, boolean detectGlare) {
        String videoPath = ReceiptExtractorTest.class.getResource("/videos/" + filename).getFile().substring(1);
        VideoCapture videoCapture = new VideoCapture(videoPath);
        assertNotNull(videoCapture);

        List<Mat> receipts = receiptExtractor.extractReceipts(videoCapture, detectGlare);
        assertTrue(receipts.size() >= expectedFrames);
    }

    @Test
    public void testExtractVideoReflection() {
        testVideo("reflection-test.mp4", 13, true);
    }

    @Test
    public void testExtractVideoReceipt() {
        testVideo("receipt-video.mp4", 20, false);
    }

    @Test
    public void testExtractVideoNonGlare() {
        testVideo("non-glare-receipt.mp4", 16, false);
    }

    @Test
    public void testExtractVideoReflectionNoEdges() {
        testVideo("reflection-test-no-edges.mp4", 14, true);
    }
}
