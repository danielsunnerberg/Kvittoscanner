package enhanceImage;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Class containing methods for enhancing an image. The methods combine applying Gaussian blurDetectors, median blurDetectors and no blurDetectors
 * with applying regular threshold, adaptive threshold and ranged threshold.
 *
 * Created by gustavbergstrom on 2017-02-21.
 */
public class ImageEnhancer {

	public static int THRESH_NONE = Integer.MAX_VALUE;

	/**
	 * Enhance image using Gaussian blurDetectors and a threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelWidth The kernel width to apply gaussian blurDetectors with. Default is 3.
	 * @param kernelHeight The kernel height to apply gaussian blurDetectors with. Default is 3.
	 * @param sigma The standard deviation to be used when applying gaussian blue. Default is 0.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 *                  This value is only used when otsu is set to false. Otherwise it can be set to anything.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 *               This value is only used when thresholdType is set to THRESH_BINARY and THRESH_BINARY_INV.
	 *               Otherwise it can be set to anything.
	 * @param grayScale If true, the image is converted to gray scale before applying blurDetectors. Must be true if otsu is true.
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
			System.out.println("[ImageEnhancer] (gaussianBlurAndThreshold): grayScale must be true if otsu is true");
			return mat;
		} else {
			modifiedMat = mat;
		}

		// Apply Gaussian blurDetectors to image
		Imgproc.GaussianBlur(modifiedMat, modifiedMat, new Size(kernelWidth, kernelHeight), sigma);

		if (thresholdType != THRESH_NONE) {
			// Apply threshold to image
			modifiedMat = ThresholdApplier.applyThreshold(modifiedMat, threshold, maxval, thresholdType, otsu);
		}

		return modifiedMat;
	}

	/**
	 * Enhance image using Gaussian blurDetectors and an adaptive threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelWidth The kernel width to apply gaussian blurDetectors with. Default is 3.
	 * @param kernelHeight The kernel height to apply gaussian blurDetectors with. Default is 3.
	 * @param sigma The standard deviation to be used when applying gaussian blue. Default is 0.
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
	 * @return A matrix with enhanced text.
	 */
	public static Mat gaussianBlurAndAdaptiveThreshold (
			Mat mat, int kernelWidth, int kernelHeight, int sigma, double maxval, int adaptiveMethod,
			int thresholdType, int blockSize, double c) {

		Mat modifiedMat = new Mat();

		// Convert image to gray scale
		Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);

		// Apply Gaussian blurDetectors to image
		Imgproc.GaussianBlur(modifiedMat, modifiedMat, new Size(kernelWidth, kernelHeight), sigma);

		// Apply threshold to image
		modifiedMat = ThresholdApplier.applyAdaptiveThreshold (
				modifiedMat, maxval, adaptiveMethod, thresholdType, blockSize, c);

		return modifiedMat;
	}

	/**
	 * Enhance image using Gaussian blurDetectors and a ranged threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelWidth The kernel width to apply gaussian blurDetectors with. Default is 3.
	 * @param kernelHeight The kernel height to apply gaussian blurDetectors with. Default is 3.
	 * @param sigma The standard deviation to be used when applying gaussian blue. Default is 0.
	 * @param grayScale If true, the image is converted to gray scale before applying blurDetectors.
	 * @param low_b The lower threshold for the blue channel.
	 * @param low_g The lower threshold for the green channel.
	 * @param low_r The lower threshold for the red channel.
	 * @param high_b The higher threshold for the blue channel.
	 * @param high_g The higher threshold for the green channel.
	 * @param high_r The higher threshold for the red channel.
	 * @return A matrix with enhanced text.
	 */
	public static Mat gaussianBlurAndRangedThreshold (
			Mat mat, boolean grayScale, int kernelWidth, int kernelHeight, int sigma,
			int low_b, int low_g, int low_r, int high_b, int high_g, int high_r) {

		Mat modifiedMat = new Mat();

		if (grayScale) {
			// Convert image to gray scale
			Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
		} else {
			modifiedMat = mat;
		}

		// Apply Gaussian blurDetectors to image
		Imgproc.GaussianBlur(modifiedMat, modifiedMat, new Size(kernelWidth, kernelHeight), sigma);

		// Apply ranged threshold to image
		modifiedMat = ThresholdApplier.applyRangedThreshold(mat, low_b, low_g, low_r, high_b, high_g, high_r);

		return modifiedMat;
	}

	/**
	 * Enhance image using median blurDetectors and a threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelSize The kernel size to apply median blurDetectors with. Default is 3.
	 * @param threshold The threshold value. Should be between 0 and 255.
	 *                  This value is only used when otsu is set to false. Otherwise it can be set to anything.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 *               This value is only used when thresholdType is set to THRESH_BINARY and THRESH_BINARY_INV.
	 *               Otherwise it can be set to anything.
	 * @param grayScale If true, the image is converted to gray scale before applying blurDetectors. Must be true if otsu is true.
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
			System.out.println("[ImageEnhancer] (medianBlurAndThreshold): grayScale must be true if otsu is true");
			return mat;
		} else {
			modifiedMat = mat;
		}

		// Apply Median blurDetectors to image
		Imgproc.medianBlur(modifiedMat, modifiedMat, kernelSize);

		if (thresholdType != THRESH_NONE) {
			// Apply threshold to image
			modifiedMat = ThresholdApplier.applyThreshold(modifiedMat, threshold, maxval, thresholdType, otsu);
		}

		return modifiedMat;
	}

	/**
	 * Enhance image using median blurDetectors and an adaptive threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelSize The kernel size to apply median blurDetectors with. Default is 3.
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
	 * @return A matrix with enhanced text.
	 */
	public static Mat medianBlurAndAdaptiveThreshold (
			Mat mat, int kernelSize, double maxval, int adaptiveMethod, int thresholdType, int blockSize, double c) {

		Mat modifiedMat = new Mat();

		// Convert image to gray scale
		Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);

		// Apply median blurDetectors to image
		Imgproc.medianBlur(modifiedMat, modifiedMat, kernelSize);

		// Apply threshold to image
		modifiedMat = ThresholdApplier.applyAdaptiveThreshold (
				modifiedMat, maxval, adaptiveMethod, thresholdType, blockSize, c);

		return modifiedMat;
	}

	/**
	 * Enhance image using median blurDetectors and a ranged threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param kernelSize The kernel size to apply gaussian blurDetectors with. Default is 3.
	 * @param grayScale If true, the image is converted to gray scale before applying blurDetectors.
	 * @param low_b The lower threshold for the blue channel.
	 * @param low_g The lower threshold for the green channel.
	 * @param low_r The lower threshold for the red channel.
	 * @param high_b The higher threshold for the blue channel.
	 * @param high_g The higher threshold for the green channel.
	 * @param high_r The higher threshold for the red channel.
	 * @return A matrix with enhanced text.
	 */
	public static Mat medianBlurAndRangedThreshold (Mat mat, boolean grayScale, int kernelSize,
			int low_b, int low_g, int low_r, int high_b, int high_g, int high_r) {

		Mat modifiedMat = new Mat();

		if (grayScale) {
			// Convert image to gray scale
			Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
		} else {
			modifiedMat = mat;
		}

		// Apply median blurDetectors to image
		Imgproc.medianBlur(modifiedMat, modifiedMat, kernelSize);

		// Apply ranged threshold to image
		modifiedMat = ThresholdApplier.applyRangedThreshold(mat, low_b, low_g, low_r, high_b, high_g, high_r);

		return modifiedMat;
	}

	/**
	 * Enhance image using a threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param grayScale If true, the image is converted to gray scale before applying threshold. Must be true if otsu is true.
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
	 * @param threshold The threshold value. Should be between 0 and 255.
	 *                  This value is only used when otsu is set to false. Otherwise it can be set to anything.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 *               This value is only used when thresholdType is set to THRESH_BINARY and THRESH_BINARY_INV.
	 *               Otherwise it can be set to anything.
	 * @param otsu If false, the given threshold value is used. If true, the threshold is calculated using Otsu's method.
	 *             This value is only used when thresholdType is not set to THRESH_NONE. Otherwise it can be set to anything.
	 * @return A matrix with enhanced text.
	 */
	public static Mat onlyThreshold (
			Mat mat, boolean grayScale, int thresholdType, int threshold, int maxval, boolean otsu) {

		Mat modifiedMat = new Mat();

		if (grayScale) {
			// Convert image to gray scale
			Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
		} else if (otsu) {
			System.out.println("[ImageEnhancer] (onlyThreshold): grayScale must be true if otsu is true");
			return mat;
		} else {
			modifiedMat = mat;
		}

		if (thresholdType != THRESH_NONE) {
			// Apply threshold to image
			modifiedMat = ThresholdApplier.applyThreshold(modifiedMat, threshold, maxval, thresholdType, otsu);
		}

		return modifiedMat;
	}

	/**
	 * Enhance image using an adaptive threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param thresholdType The type of threshold to be applied. Can be one of the following values:
	 *                      THRESH_BINARY Pixels with a higher value than the threshold are set to maxval.
	 *                      All other pixels are set to 0.
	 *                      THRESH_BINARY_INV Pixels with a higher value than the threshold are set to 0.
	 *                      All other pixels are set to maxval.
	 * @param maxval The maximum value to set pixels to. Should be between 0 and 255.
	 * @param adaptiveMethod The adaptive thresholding algorithm to use. Can be one of the following values:
	 *                       ADAPTIVE_THRESH_MEAN_C The threshold value T(x,y) is a mean of the blockSize * blockSize
	 *                       neighborhood of (x, y) minus C.
	 *                       ADAPTIVE_THRESH_GAUSSIAN_C The threshold value T(x,y) is a weighted sum of the
	 *                       blockSize * blockSize neighborhood of (x, y) minus C.
	 * @param blockSize Size of a pixel neighborhood that is used to calculate a threshold value for the pixel.
	 *                  The value should be odd: 3, 5, 7, and so on.
	 * @param c Constant subtracted from the mean or weighted mean.
	 *          Normally, it is positive but may be zero or negative as well. Default is 5.
	 * @return A matrix with enhanced text.
	 */
	public static Mat onlyAdaptiveThreshold (
			Mat mat, int thresholdType, double maxval, int adaptiveMethod, int blockSize, int c) {

		Mat modifiedMat = new Mat();

		// Convert image to gray scale
		Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);

		// Apply threshold to image
		modifiedMat = ThresholdApplier.applyAdaptiveThreshold(
				modifiedMat, maxval, adaptiveMethod, thresholdType, blockSize, c);

		return modifiedMat;
	}

	/**
	 * Enhance image using a ranged threshold.
	 *
	 * @param mat The matrix to enhance text in.
	 * @param grayScale If true, the image is converted to gray scale before applying threshold.
	 * @param low_b The lower threshold for the blue channel.
	 * @param low_g The lower threshold for the green channel.
	 * @param low_r The lower threshold for the red channel.
	 * @param high_b The higher threshold for the blue channel.
	 * @param high_g The higher threshold for the green channel.
	 * @param high_r The higher threshold for the red channel.
	 * @return A matrix with enhanced text.
	 */
	public static Mat onlyRangedThreshold (
			Mat mat, boolean grayScale, int low_b, int low_g, int low_r, int high_b, int high_g, int high_r) {

		Mat modifiedMat = new Mat();

		if (grayScale) {
			// Convert image to gray scale
			Imgproc.cvtColor(mat, modifiedMat, Imgproc.COLOR_BGR2GRAY);
		} else {
			modifiedMat = mat;
		}

		// Apply ranged threshold to image
		modifiedMat = ThresholdApplier.applyRangedThreshold(mat, low_b, low_g, low_r, high_b, high_g, high_r);

		return modifiedMat;
	}
}
