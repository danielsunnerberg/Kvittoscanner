package twotone;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gustavbergstrom on 2017-02-01.
 */
public class TwoTone {
	public static void main(String args[]) {

		System.load(System.getProperty("java.library.path"));
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String file = "src/test/resources/randomimages/ReceiptSwiss.jpeg";

//		BufferedImage img = null;
//		try {
//			img=ImageIO.read(new File(file));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		displayImage(img);

		Mat image = Imgcodecs.imread(file);
		image = resize(image, 600, 600);
		displayImage(matToImage(image), "Original");

		Mat hsvImage = new Mat();
		Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
		displayImage(matToImage(hsvImage), "HSV");
		Core.inRange(hsvImage, new Scalar(0,0,100), new Scalar (255,255,255), hsvImage);
		displayImage(matToImage(hsvImage), "In range HSV");

		Core.inRange(image, new Scalar(220,220,220), new Scalar (255,255,255), image);
		displayImage(matToImage(image), "In range original");
	}

	public static Mat resize (Mat image, int height, int width) {

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

	public static BufferedImage matToImage(Mat m){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public static void displayImage(Image image, String name) {
		ImageIcon icon=new ImageIcon(image);
		JFrame frame=new JFrame();
		frame.setTitle(name);
		frame.setLayout(new FlowLayout());
		frame.setSize(image.getWidth(null)+50, image.getHeight(null)+50);
		JLabel lbl=new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


}
