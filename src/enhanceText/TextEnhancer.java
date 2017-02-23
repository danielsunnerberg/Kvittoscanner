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
	 * @param gray
	 * @return
	 */
	public static Mat enhanceWithGaussianBlur (
			Mat imageMat, int thresh, int maxval, int kernelWidth, int kernelHeight, int sigma,
			ThresholdType thresholdType, boolean gray) {

		Mat newMat = new Mat();

		if (gray || thresholdType == ThresholdType.OTSU) {
			Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_BGR2GRAY);
		} else {
			newMat = imageMat;
		}

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
	 * @param gray
	 * @return
	 */
	public static Mat enhanceWithMedianBlur (
			Mat imageMat, int thresh, int maxval, int ksize, ThresholdType thresholdType, boolean gray) {

		Mat newMat = new Mat();

		if (gray || thresholdType == ThresholdType.OTSU) {
			Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_BGR2GRAY);
		} else {
			newMat = imageMat;
		}

		Imgproc.medianBlur(newMat, newMat, ksize);

		if (thresholdType == ThresholdType.OTSU) {
			Imgproc.threshold(newMat, newMat, thresh, maxval, Imgproc.THRESH_OTSU);
		}

		return newMat;
	}
}
