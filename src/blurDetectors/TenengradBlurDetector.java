package blurDetectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_64F;

/**
 * BlurDetector using the tenengrad algorithm.
 */
public class TenengradBlurDetector implements BlurDetector {

    /**
     * {@inheritDoc}
     */
    public double getVariance(Mat source) {
        Mat matGray = new Mat();
        Imgproc.cvtColor(source, matGray, Imgproc.COLOR_BGR2GRAY);
        return tenengrad(matGray);
    }

    /**
     * Calculates the contrast using the Tenengrad-algorithm.
     *
     * @param source source which the algorithm should be run upon
     * @return contrast value
     */
    private double tenengrad(Mat source) {
        Mat gx = new Mat();
        Mat gy = new Mat();

        final int KERNEL_SIZE = 3;
        Imgproc.Sobel(source, gx, CV_64F, 1, 0, KERNEL_SIZE, 1, 5);
        Imgproc.Sobel(source, gy, CV_64F, 0, 1, KERNEL_SIZE, 1, 5);

        Mat FM = new Mat();
        Core.add(gx.mul(gx), gy.mul(gy), FM);

        return Core.mean(FM).val[0];
    }
}
