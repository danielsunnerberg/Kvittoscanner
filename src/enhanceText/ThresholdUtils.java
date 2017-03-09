package enhanceText;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Utility class containing methods for applying thresholds to matrices.
 *
 * Created by gustavbergstrom on 2017-03-08.
 */
public class ThresholdUtils {

	/**
	 * Applies a threshold to a matrix that is in gray scale.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 *                  This value is only used when otsu is set to false. Otherwise it can be set to anything.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 *               This value is only used when thresholdType is set to THRESH_BINARY and THRESH_BINARY_INV.
	 *               Otherwise it can be set to anything.
	 * @param thresholdType The type of threshold to be applied. Can be one of the following values:
	 *                      THRESH_BINARY Pixels with a higher value than the threshold are set to maxval.
	 *                      All other pixels are set to 0.
	 *                      THRESH_BINARY_INV Pixels with a higher value than the threshold are set to 0.
	 *                      All other pixels are set to maxval.
	 *                      THRESH_TRUNC Pixels with a higher value than the threshold are set to the threshold value.
	 *                      All other pixels are kept as they are.
	 *                      THRESH_TOZERO Pixels with a higher value than the threshold are kept as they are.
	 *                      All other pixels are set to 0.
	 *                      THRESH_TOZERO_INV Pixels with a higher value than the threshold are set to 0.
	 *                      All other pixels are kept as they are.
	 * @param otsu If false, the given threshold value is used. If true, the threshold is calculated using Otsu's method.
	 * @return A matrix with the threshold applied.
	 */
	public static Mat applyThreshold (Mat mat, int threshold, int maxval, int thresholdType, boolean otsu) {

		if (otsu) {
			thresholdType += Imgproc.THRESH_OTSU;
		}
		Mat modifiedMat = new Mat();
		Imgproc.threshold(mat, modifiedMat, threshold, maxval, thresholdType);
		return modifiedMat;
	}

	/**
	 * Applies an adaptive threshold to a matrix that is in gray scale.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @param adaptiveMethod The adaptive thresholding algorithm to use. Can be one of the following values:
	 *                       ADAPTIVE_THRESH_MEAN_C The threshold value T(x,y) is a mean of the blockSize * blockSize
	 *                       neighborhood of (x, y) minus C.
	 *                       ADAPTIVE_THRESH_GAUSSIAN_C The threshold value T(x,y) is a weighted sum of the
	 *                       blockSize * blockSize neighborhood of (x, y) minus C.
	 * @param thresholdType The type of threshold to be applied. Can be one of the following values:
	 *                      THRESH_BINARY Pixels with a higher value than the threshold are set to maxval.
	 *                      All other pixels are set to 0.
	 *                      THRESH_BINARY_INV Pixels with a higher value than the threshold are set to 0.
	 *                      All other pixels are set to maxval.
	 * @param blockSize Size of a pixel neighborhood that is used to calculate a threshold value for the pixel.
	 *                  The value should be odd: 3, 5, 7, and so on.
	 * @param c Constant subtracted from the mean or weighted mean.
	 *          Normally, it is positive but may be zero or negative as well. Default is 5.
	 * @return A matrix with the threshold applied.
	 */
	public static Mat applyAdaptiveThreshold (
			Mat mat, double maxval, int adaptiveMethod, int thresholdType, int blockSize, double c) {

		Mat modifiedMat = new Mat();
		Imgproc.adaptiveThreshold(mat, modifiedMat, maxval, adaptiveMethod, thresholdType, blockSize, c);
		return modifiedMat;
	}

	/**
	 * Applies a ranged threshold to a matrix that is in RGB. The pixels that are in the ranges are set to white.
	 * All other pixels are set to black.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param low_b The lower threshold for the blue channel.
	 * @param low_g The lower threshold for the green channel.
	 * @param low_r The lower threshold for the red channel.
	 * @param high_b The higher threshold for the blue channel.
	 * @param high_g The higher threshold for the green channel.
	 * @param high_r The higher threshold for the red channel.
	 * @return A matrix with the threshold applied.
	 */
	public static Mat applyRangedThreshold (
			Mat mat, int low_b, int low_g, int low_r, int high_b, int high_g, int high_r) {

		Mat modifiedMat = new Mat();
		Core.inRange(mat, new Scalar(low_b, low_g, low_r), new Scalar (high_b, high_g, high_r), modifiedMat);
		return modifiedMat;
	}
}
