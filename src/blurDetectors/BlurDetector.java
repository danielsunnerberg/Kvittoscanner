package blurDetectors;

import org.opencv.core.*;

public interface BlurDetector {

    /**
     * Calculate the variance for a specific matrix to determine how blurry it is.
     *
     * @param source the image in for of a mat
     * @return the variance of the image
     */
    double getVariance(Mat source);

}
