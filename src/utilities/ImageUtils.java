package utilities;

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
 * Utility class containing methods to resize, display and save images. Can also convert a Mat to an image.
 *
 * Created by gustavbergstrom on 2017-02-21.
 */

public class ImageUtils {

	/**
	 * Resize a Mat to fit the specified size.
	 *
	 * @param image The Mat to resize.
	 * @param width The maximum width of the resized Mat.
	 * @param height The maximum height of the resized Mat.
	 * @return The resized Mat.
	 */
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

	/**
	 * Display an image in a JFrame.
	 *
	 * @param image The image to display.
	 * @param name The name of the image. The name is shown on the JFrame.
	 */
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

	/**
	 * Display a Mat in a JFrame.
	 *
	 * @param mat The mat to display.
	 * @param name The name of the Mat. The name is shown on the JFrame.
	 */
	public static void displayMat (Mat mat, String name) {
		Image image = matToImage(mat);
		displayImage(image, name);
	}

	/**
	 * Convert a Mat to a BufferedImage.
	 * @param mat The Mat to convert.
	 * @return A BufferedImage.
	 */
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

	/**
	 * Save an image to disk.
	 *
	 * @param image The image to save.
	 * @param path The path where the image will be saved.
	 * @param name The name of the image.
	 */
	public static void saveImage (BufferedImage image, String path, String name) {
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

	/**
	 * Save a Mat to disk by converting it to an image.
	 *
	 * @param mat The Mat to save.
	 * @param path The path where the image will be saved.
	 * @param name The name of the Mat.
	 */
	public static void saveMat (Mat mat, String path, String name) {
		BufferedImage image = matToImage(mat);
		saveImage(image, path, name);
	}
}
