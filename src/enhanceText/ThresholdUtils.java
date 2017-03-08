package enhanceText;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Utility class containing methods for applying thresholds to matrices.
 *
 * Created by gustavbergstrom on 2017-03-08.
 */
public class ThresholdUtils {

	/**
	 * Applies a binary threshold to a matrix.
	 * Pixels with a higher value than the threshold are set to maxval. All other pixels are set to 0.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyBinaryThreshold (Mat mat, int threshold, int maxval) {
		return applyThreshold(mat, threshold, maxval, Imgproc.THRESH_BINARY);
	}

	/**
	 * Applies an inverse binary threshold to a matrix.
	 * Pixels with a higher value than the threshold are set to 0. All other pixels are set to maxval.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyInverseBinaryThreshold (Mat mat, int threshold, int maxval) {
		return applyThreshold(mat, threshold, maxval, Imgproc.THRESH_BINARY_INV);
	}

	/**
	 * Applies a truncating threshold to a matrix.
	 * Pixels with a higher value than the threshold are set to the threshold value. All other pixels are kept as they are.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyTruncateThreshold (Mat mat, int threshold) {
		return applyThreshold(mat, threshold, 0, Imgproc.THRESH_TRUNC);
	}

	/**
	 * Applies a to zero threshold to a matrix.
	 * Pixels with a higher value than the threshold are kept as they are. All other pixels are set to 0.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyToZeroThreshold (Mat mat, int threshold) {
		return applyThreshold(mat, threshold, 0, Imgproc.THRESH_TOZERO);
	}

	/**
	 * Applies an inverse to zero threshold to a matrix.
	 * Pixels with a higher value than the threshold are set to 0. All other pixels are kept as they are.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyInverseToZeroThreshold (Mat mat, int threshold) {
		return applyThreshold(mat, threshold, 0, Imgproc.THRESH_TOZERO_INV);
	}

	/**
	 * Applies a binary threshold calculated by Otsu's method to a matrix.
	 * Pixels with a higher value than the threshold are set to maxval. All other pixels are set to 0.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyBinaryThresholdWithOtsu (Mat mat, int maxval) {
		return applyThreshold(mat, 0, maxval, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
	}

	/**
	 * Applies an inverse binary threshold calculated by Otsu's method to a matrix.
	 * Pixels with a higher value than the threshold are set to 0. All other pixels are set to maxval.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyInverseBinaryThresholdWithOtsu (Mat mat, int maxval) {
		return applyThreshold(mat, 0, maxval, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY_INV);
	}

	/**
	 * Applies a truncated threshold calculated by Otsu's method to a matrix.
	 * Pixels with a higher value than the threshold are set to the threshold value. All other pixels are kept as they are.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyTruncateThresholdWithOtsu (Mat mat) {
		return applyThreshold(mat, 0, 0, Imgproc.THRESH_OTSU + Imgproc.THRESH_TRUNC);
	}

	/**
	 * Applies a to zero threshold calculated by Otsu's method to a matrix.
	 * Pixels with a higher value than the threshold are kept as they are. All other pixels are set to 0.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyToZeroThresholdWithOtsu (Mat mat) {
		return applyThreshold(mat, 0, 0, Imgproc.THRESH_OTSU + Imgproc.THRESH_TOZERO);
	}

	/**
	 * Applies an inverse to zero threshold calculated by Otsu's method to a matrix.
	 * Pixels with a higher value than the threshold are set to 0. All other pixels are kept as they are.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @return A matrix with the the threshold applied.
	 */
	public static Mat applyInverseToZeroThresholdWithOtsu (Mat mat) {
		return applyThreshold(mat, 0, 0, Imgproc.THRESH_OTSU + Imgproc.THRESH_TOZERO_INV);
	}

	/**
	 * Helper method to apply a threshold to a matrix.
	 *
	 * @param mat The matrix to apply the threshold to.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @param type The threshold type to apply.
	 * @return A matrix with the the threshold applied.
	 */
	private static Mat applyThreshold (Mat mat, int threshold, int maxval, int type) {
		Mat modifiedMat = new Mat();
		Imgproc.threshold(mat, modifiedMat, threshold, maxval, type);
		return modifiedMat;
	}
}
