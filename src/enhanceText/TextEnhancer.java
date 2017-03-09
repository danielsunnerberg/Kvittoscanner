package enhanceText;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Class for trying to enhance text in an image
 *
 * Created by gustavbergstrom on 2017-02-21.
 */
public class TextEnhancer {

	public static int THRESH_NONE = Integer.MAX_VALUE;

	/**
	 * Enhance text using Gaussian blur and a threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelWidth The kernel width to apply gaussian blur with. Default is 3.
	 * @param kernelHeight The kernel height to apply gaussian blur with. Default is 3.
	 * @param sigma The standard deviation to be used when applying gaussian blue. Default is 0.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 *                  This value is only used when otsu is set to false. Otherwise it can be set to anything.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 *               This value is only used when thresholdType is set to THRESH_BINARY and THRESH_BINARY_INV.
	 *               Otherwise it can be set to anything.
	 * @param grayScale If true, the image is converted to gray scale before applying blur. Must be true if otsu is true.
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
	 *                      THRESH_NONE No threshold is applied.
	 * @param otsu If false, the given threshold value is used. If true, the threshold is calculated using Otsu's method.
	 *             This value is only used when thresholdType is not set to THRESH_NONE. Otherwise it can be set to anything.
	 * @return A matrix with enhanced text.
	 */
	public static Mat gaussianBlurAndThreshold (
			Mat mat, int kernelWidth, int kernelHeight, int sigma, int threshold, int maxval,
			boolean grayScale, int thresholdType, boolean otsu) {

		Mat modifiedMat = new Mat();

		if (grayScale) {
			// Convert image to gray scale
			Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
		} else if (otsu) {
			System.out.println("[TextEnhancer] (gaussianBlurAndThreshold): grayScale must be true if otsu is true");
			return mat;
		} else {
			modifiedMat = mat;
		}

		// Apply Gaussian blur to image
		Imgproc.GaussianBlur(modifiedMat, modifiedMat, new Size(kernelWidth, kernelHeight), sigma);

		if (thresholdType != THRESH_NONE) {
			// Apply threshold to image
			modifiedMat = ThresholdUtils.applyThreshold(modifiedMat, threshold, maxval, thresholdType, otsu);
		}

		return modifiedMat;
	}

//	public static Mat gaussianBlurAndAdaptiveThreshold (
//			Mat mat, int kernelWidth, int kernelHeight, int sigma, int maxval, int adaptiveMethod, int thresholdType, int blockSize) {
//
//		Mat modifiedMat = new Mat();
//
//		// Convert image to gray scale
//		Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
//
//		// Apply Gaussian blur to image
//		Imgproc.GaussianBlur(modifiedMat, modifiedMat, new Size(kernelWidth, kernelHeight), sigma);
//
//		if (thresholdType != THRESH_NONE) {
//			// Apply threshold to image
//			modifiedMat = ThresholdUtils.applyThreshold(modifiedMat, threshold, maxval, thresholdType);
//			modifiedMat = ThresholdUtils.applyAdaptiveThreshold(modifiedMat, maxval, adaptiveMethod, thresholdType, blockSize);
//		}
//
//		return modifiedMat;
//	}

	/**
	 * Enhance text using median blur and a threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelSize The kernel size to apply median blur with. Default is 3.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 *                  This value is only used when otsu is set to false. Otherwise it can be set to anything.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 *               This value is only used when thresholdType is set to THRESH_BINARY and THRESH_BINARY_INV.
	 *               Otherwise it can be set to anything.
	 * @param grayScale If true, the image is converted to gray scale before applying blur. Must be true if otsu is true.
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
	 *                      THRESH_NONE No threshold is applied.
	 * @param otsu If false, the given threshold value is used. If true, the threshold is calculated using Otsu's method.
	 *             This value is only used when thresholdType is not set to THRESH_NONE. Otherwise it can be set to anything.
	 * @return A matrix with enhanced text.
	 */
	public static Mat medianBlurAndThreshold (
			Mat mat, int kernelSize, int threshold, int maxval, boolean grayScale, int thresholdType, boolean otsu) {

		Mat modifiedMat = new Mat();

		if (grayScale) {
			// Convert image to gray scale
			Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
		} else if (otsu) {
			System.out.println("[TextEnhancer] (medianBlurAndThreshold): grayScale must be true if otsu is true");
			return mat;
		} else {
			modifiedMat = mat;
		}

		// Apply Median blur to image
		Imgproc.medianBlur(modifiedMat, modifiedMat, kernelSize);

		if (thresholdType != THRESH_NONE) {
			// Apply threshold to image
			modifiedMat = ThresholdUtils.applyThreshold(modifiedMat, threshold, maxval, thresholdType, otsu);
		}

		return modifiedMat;
	}
}
