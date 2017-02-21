package enhanceText;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Created by gustavbergstrom on 2017-02-21.
 */
public class Utils {

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

	// Display a Mat on the screen
	public static void displayMat (Mat mat, String name) {
		Image image = matToImage(mat);
		displayImage(image, name);
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

	// Save an image to disk
	public static void saveImage (BufferedImage image, String methodName, String dirName, String name) {
		String path = "/Users/gustavbergstrom/Documents/Kandidat/Output/" + methodName + "/" + dirName;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			File outputFile = new File(
					path + "/" + name + ".png");
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Save a mat to disk by converting it to an image
	public static void saveMat (Mat mat, String methodName, String dirName, String name) {
		BufferedImage image = matToImage(mat);
		saveImage(image, methodName, dirName, name);
	}
}
