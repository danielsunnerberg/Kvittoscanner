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

	/**
	 * Trying to enhance the text using Gaussian blur
	 *
	 * @param imageMat The image to enhance text in
	 * @param thresh Default is 0
	 * @param maxval Default is 255
	 * @param kernelWidth Default is 3
	 * @param kernelHeight Default is 3
	 * @param sigma Default is 0
	 * @param thresholdType The type of threshold to be used for converting image to binary
	 * @return An image where the text is enhanced
	 */
	public static Mat enhanceWithGaussianBlur (
			Mat imageMat, int thresh, int maxval, int kernelWidth, int kernelHeight, int sigma,
			ThresholdType thresholdType) {

		Mat newMat = new Mat();

		// Convert image to gray scale
		Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_BGR2GRAY);

		// Apply Gaussian blur to image
		Imgproc.GaussianBlur(newMat, newMat, new Size(kernelWidth, kernelHeight), sigma);

		// Convert image to binary using the specified threshold
		if (thresholdType == ThresholdType.OTSU) {
			newMat = ThresholdUtils.applyBinaryThresholdWithOtsu(newMat, maxval);
		}

		return newMat;
	}

	/**
	 * Trying to enhance the text using Median blur
	 *
	 * @param imageMat The image to enhance text in
	 * @param thresh Default is 0
	 * @param maxval Default is 255
	 * @param ksize Default is 3
	 * @param thresholdType The type of threshold to be used for converting image to binary
	 * @return An image where the text is enhanced
	 */
	public static Mat enhanceWithMedianBlur (
			Mat imageMat, int thresh, int maxval, int ksize, ThresholdType thresholdType) {

		Mat newMat = new Mat();

		// Convert image to gray scale
		Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_BGR2GRAY);

		// Apply Median blur to image
		Imgproc.medianBlur(newMat, newMat, ksize);

		// Convert image to binary using the specified threshold
		if (thresholdType == ThresholdType.OTSU) {
			newMat = ThresholdUtils.applyBinaryThresholdWithOtsu(newMat, maxval);
		}

		return newMat;
	}
}
