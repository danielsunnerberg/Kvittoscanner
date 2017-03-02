package enhanceText;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by gustavbergstrom on 2017-02-21.
 */
public class TextEnhancer {

	/**
	 *
	 * @param imageMat
	 * @param thresh
	 * @param maxval
	 * @param kernelWidth
	 * @param kernelHeight
	 * @param sigma
	 * @param thresholdType
	 * @return
	 */
	public static Mat enhanceWithGaussianBlur (
			Mat imageMat, int thresh, int maxval, int kernelWidth, int kernelHeight, int sigma,
			ThresholdType thresholdType) {

		Mat newMat = new Mat();

		Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_BGR2GRAY);

		Imgproc.GaussianBlur(newMat, newMat, new Size(kernelWidth, kernelHeight), sigma);

		if (thresholdType == ThresholdType.OTSU) {
			Imgproc.threshold(newMat, newMat, thresh, maxval, Imgproc.THRESH_OTSU);
		}

		return newMat;
	}

	/**
	 *
	 * @param imageMat
	 * @param thresh
	 * @param maxval
	 * @param ksize
	 * @param thresholdType
	 * @return
	 */
	public static Mat enhanceWithMedianBlur (
			Mat imageMat, int thresh, int maxval, int ksize, ThresholdType thresholdType) {

		Mat newMat = new Mat();

		Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_BGR2GRAY);

		Imgproc.medianBlur(newMat, newMat, ksize);

		if (thresholdType == ThresholdType.OTSU) {
			Imgproc.threshold(newMat, newMat, thresh, maxval, Imgproc.THRESH_OTSU);
		}

		return newMat;
	}
}
