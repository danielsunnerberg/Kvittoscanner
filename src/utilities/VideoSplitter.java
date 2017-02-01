package utilities;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.List;

public class VideoSplitter {

    /**
     * Loads the video at the provided location and extracts a specified number of evenly distributed
     * frames from it.
     *
     * @param sourcePath string to path of video
     * @param requiredFrames If 0, all frames. Otherwise, the frames will be evenly distributed across the video
     * @return extracted frames
     */
    public static List<Mat> getFrames(String sourcePath, int requiredFrames) {
        final VideoCapture capture = new VideoCapture(sourcePath);
        List<Mat> frames = getFrames(capture, requiredFrames);
        capture.release();
        return frames;
    }

    /**
     * Extracts a specified number of evenly distributed frames from the provided video capture.
     * This method will not release the given capture.
     *
     * @param capture capture containing video
     * @param requiredFrames If 0, all frames. Otherwise, the frames will be evenly distributed across the video
     * @return extracted frames
     */
    public static List<Mat> getFrames(VideoCapture capture, int requiredFrames) {
        final Mat mat = new Mat();

        final double frameCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
        int frameDelay = 0;
        if (requiredFrames > 0 && requiredFrames <= frameCount) {
            frameDelay = (int) Math.round((frameCount - requiredFrames) / requiredFrames);
        }

        List<Mat> frames = new ArrayList<>();
        while (capture.read(mat)) {
            if (requiredFrames > 0 && frames.size() >= requiredFrames) {
                break;
            }

            frames.add(mat);

            for (int delay = 0; delay < frameDelay; delay++) {
                // Read and discard frames
                capture.read(mat);
            }
        }

        mat.release();
        return frames;
    }

}
