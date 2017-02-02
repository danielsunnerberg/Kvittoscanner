package utilities;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VideoSplitterTest {

    @Before
    public void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void testGetFramesFromString() {
        String filePath = VideoSplitterTest.class.getResource("/videos/example-video.mp4").getFile().substring(1);
        final VideoCapture capture = new VideoCapture(filePath);
        final int frameCount = (int) capture.get(Videoio.CAP_PROP_FRAME_COUNT);

        assertTrue("Capture has to be opened. Ensure OpenCV is correctly installed.", capture.isOpened());
        assertTrue("Loaded file should have frames", frameCount > 0);

        // When requiredFrames=0 all frames should be extracted
        List<Mat> frames = VideoSplitter.getFrames(filePath, 0);
        assertEquals(frameCount, frames.size());

        // Should only extract as many frames as required
        frames = VideoSplitter.getFrames(filePath, frameCount - 1);
        assertEquals(frameCount - 1, frames.size());

        // Should extract all frames if too many are requested
        frames = VideoSplitter.getFrames(filePath, frameCount + 1);
        assertEquals(frameCount, frames.size());

        capture.release();
    }
}
