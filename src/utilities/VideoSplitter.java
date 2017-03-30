package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.List;

public class VideoSplitter {

    private static double sizeLimit = 1000;

    private static final Logger logger = LogManager.getLogger(VideoSplitter.class);

    /**
     * Loads the video at the provided location and extracts a specified number of evenly distributed
     * frames from it.
     *
     * @param sourcePath     string to path of video
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
     * @param capture        capture containing video
     * @param requiredFrames If 0, all frames. Otherwise, the frames will be evenly distributed across the video
     * @return extracted frames
     */
    public static List<Mat> getFrames(VideoCapture capture, int requiredFrames) {
        final double frameCount = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
        logger.info("Provided capture has {} frames", frameCount);

        int frameDelay = 0;
        if (requiredFrames > 0 && requiredFrames <= frameCount) {
            frameDelay = (int) Math.round((frameCount - requiredFrames) / requiredFrames);
            logger.info("Frame delay is {}", frameDelay);
        }

        List<Mat> frames = new ArrayList<>();
        // To get an uniform distribution of frames, skip a set number of frames
        // between each retrieve. We don't read the frames in between for
        // performance reasons.
        for (int index = 0; index < frameCount; index += frameDelay + 1) {
            logger.info("Extracting frame {}", index);
            capture.set(Videoio.CAP_PROP_POS_FRAMES, index);
            Mat frame = new Mat();
            capture.read(frame);

            resizeImage(frame);
            frames.add(frame);
        }

        capture.release();
        return frames;
    }

    private static void resizeImage(Mat mat){
        double factor = 1;
        if (mat.height() > mat.width() && mat.height() > sizeLimit){
            factor = mat.height() / sizeLimit;
        } else if(mat.width() > mat.height() && mat.width() > sizeLimit){
            factor = mat.width() / sizeLimit;
        }
        Size size = new Size(mat.width() / factor, mat.height() / factor);

        Imgproc.resize(mat, mat, size, 0, 0, Imgproc.INTER_AREA);
    }
}
