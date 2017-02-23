package enhanceText;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Created by gustavbergstrom on 2017-02-01.
 */
public class TwoTone {

	public static void main(String args[]) {

		System.load(System.getProperty("java.library.path"));
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		//Create and display original image
		String imagePath = "src/test/resources/randomimages/";
		String imageName = "ReceiptSwiss.jpeg";
		String file = imagePath + imageName;
		Mat image = Imgcodecs.imread(file);
		Mat resizedImage = Utils.resize(image, 600, 600);
		Utils.displayMat(resizedImage, "Original");

		// Make two tone image and save to disk
		int v0 = 200;
		int v1 = 200;
		int v2 = 200;
		Core.inRange(image, new Scalar(v0,v1,v2), new Scalar (255,255,255), image);
		Utils.saveMat(image, "TwoTone", imageName.substring(0, imageName.indexOf('.')),
				v0 + "_" + v1 + "_" + v2);

		//Resize and show processed image
		Mat resizedProcessedMat = Utils.resize(image, 600, 600);
		Utils.displayMat(resizedProcessedMat, "In range original");
	}
}
