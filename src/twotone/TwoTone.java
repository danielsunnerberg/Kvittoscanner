package twotone;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

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
		Mat resizedImage = resize(image, 600, 600);
		displayImage(matToImage(resizedImage), "Original");

//		Mat hsvImage = new Mat();
//		Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
//		displayImage(matToImage(hsvImage), "HSV");
//		Core.inRange(hsvImage, new Scalar(0,0,100), new Scalar (255,255,255), hsvImage);
//		displayImage(matToImage(hsvImage), "In range HSV");

		// Make two tone image and save to disk
		int v0 = 200;
		int v1 = 200;
		int v2 = 200;
		Core.inRange(image, new Scalar(v0,v1,v2), new Scalar (255,255,255), image);
		BufferedImage processedImage = matToImage(image);
		saveImage(processedImage, imageName.substring(0, imageName.indexOf('.')),
				v0 + "_" + v1 + "_" + v2);

		//Resize and show processed image
		Mat resizedProcessedMat = resize(image, 600, 600);
		BufferedImage resizedProcessedImage = matToImage(resizedProcessedMat);
		displayImage(resizedProcessedImage, "In range original");
	}

	// Save an image to disk
	public static void saveImage (BufferedImage image, String dirName, String name) {
		String path = "/Users/gustavbergstrom/Documents/Kandidat/Output/" + dirName;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}

		try {
			File outputFile = new File(
					path + "/" + name + ".png");
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Resize a Mat to fit the specified size
	public static Mat resize (Mat image, int width, int height) {

		Mat newImage = new Mat();
		int newWidth;
		int newHeight;

		if (image.height() > height && image.height() > image.width()) {
			newHeight = height;
			double ratio = (double) newHeight / image.height();
			newWidth = (int) (image.width() * ratio);
		} else if (image.width() > width) {
			newWidth = width;
			double ratio = (double) newWidth / image.width();
			newHeight = (int) (image.width() * ratio);
		} else {
			return image;
		}

		Imgproc.resize(image, newImage, new Size(newWidth, newHeight));
		return newImage;
	}

	// Convert a Mat to a BufferedImage
	public static BufferedImage matToImage(Mat mat){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (mat.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = mat.channels() * mat.cols() * mat.rows();
		byte[] b = new byte[bufferSize];
		mat.get(0,0, b); // get all the pixels
		BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	// Display an image on the screen
	public static void displayImage (Image image, String name) {
		ImageIcon icon = new ImageIcon(image);
		JFrame frame = new JFrame();
		frame.setTitle(name);
		frame.setLayout(new FlowLayout());
		frame.setSize(image.getWidth(null) + 50, image.getHeight(null) + 50);
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
