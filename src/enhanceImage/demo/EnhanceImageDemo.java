package enhanceImage.demo;

import enhanceImage.ImageEnhancer;
import enhanceImage.demo.components.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import utilities.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gustavbergstrom on 2017-03-21.
 */
public class EnhanceImageDemo implements ActionListener {

	private final String IMAGEPATH = "src/test/resources/randomimages/";

	public static final String GAUSSIANBLURSTRING = "Gaussian blur";
	public static final String MEDIANBLURSTRING = "Median blur";
	public static final String NOBLURSTRING = "No blur";
	public static final String THRESHOLDSTRING = "Threshold";
	public static final String ADAPTIVETHRESHOLDSTRING = "Adaptive threshold";
	public static final String RANGEDTHRESHOLDSTRING = "Ranged threshold";
	public static final String NOTHRESHOLDSTRING = "No threshold";
	public static final String SHOWIMAGESTRING = "Show image";
	public static final String OTSUSTRING = "OTSU";

	private final int IMAGESIZE = 500;

	private Mat originalMat;

	private String currentBlur;
	private String currentThreshold;

	private JFrame frame;
	private JLabel imageLabel;
	private JPanel blurParametersPanel;
	private JPanel thresholdParametersPanel;

	private GaussianParametersPanel gaussianParametersPanel;
	private MedianParametersPanel medianParametersPanel;

	private RegularThresholdParametersPanel regularThresholdParametersPanel;
	private AdaptiveThresholdParametersPanel adaptiveThresholdParametersPanel;
	private RangedThresholdParametersPanel rangedThresholdParametersPanel;
	private NoThresholdParametersPanel noThresholdParametersPanel;

	public EnhanceImageDemo (String imageName) {
		String file = IMAGEPATH + imageName;
		originalMat = Imgcodecs.imread(file);
		Mat resizedMat = ImageUtils.resize(originalMat, IMAGESIZE, IMAGESIZE);
		Image currentImage = ImageUtils.matToImage(resizedMat);

		frame = new JFrame();
		frame.setTitle("Enhance image demo");
		frame.setLayout(new BorderLayout());
		frame.setSize(800, 700);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		leftPanel.setSize(400,700);

		BlurPanel blurPanel = new BlurPanel(this);
		leftPanel.add(blurPanel);

		blurParametersPanel = new JPanel();
		gaussianParametersPanel = new GaussianParametersPanel();
		medianParametersPanel = new MedianParametersPanel();
		blurParametersPanel.add(gaussianParametersPanel);
		leftPanel.add(blurParametersPanel);
		currentBlur = GAUSSIANBLURSTRING;

		ThresholdPanel thresholdPanel = new ThresholdPanel(this);
		leftPanel.add(thresholdPanel);

		thresholdParametersPanel = new JPanel();
		regularThresholdParametersPanel = new RegularThresholdParametersPanel(this);
		adaptiveThresholdParametersPanel = new AdaptiveThresholdParametersPanel();
		rangedThresholdParametersPanel = new RangedThresholdParametersPanel();
		noThresholdParametersPanel = new NoThresholdParametersPanel();
		thresholdParametersPanel.add(regularThresholdParametersPanel);
		leftPanel.add(thresholdParametersPanel);
		currentThreshold = THRESHOLDSTRING;
		frame.add(leftPanel, BorderLayout.LINE_START);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		rightPanel.setSize(400,700);

		ShowImagePanel showImagePanel = new ShowImagePanel(this);
		rightPanel.add(showImagePanel);

		JPanel imagePanel = new JPanel();
		imageLabel = new JLabel();
		ImageIcon icon = new ImageIcon(currentImage);
		imageLabel.setIcon(icon);
		imagePanel.add(imageLabel);
		rightPanel.add(imagePanel);
		frame.add(rightPanel, BorderLayout.LINE_END);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
			case GAUSSIANBLURSTRING:
				currentBlur = GAUSSIANBLURSTRING;
				blurParametersPanel.removeAll();
				blurParametersPanel.add(gaussianParametersPanel);
				frame.validate();
				break;
			case MEDIANBLURSTRING:
				currentBlur = MEDIANBLURSTRING;
				blurParametersPanel.removeAll();
				blurParametersPanel.add(medianParametersPanel);
				frame.validate();
				break;
			case NOBLURSTRING:
				currentBlur = NOBLURSTRING;
				blurParametersPanel.removeAll();
				frame.validate();
				break;
			case THRESHOLDSTRING:
				currentThreshold = THRESHOLDSTRING;
				thresholdParametersPanel.removeAll();
				thresholdParametersPanel.add(regularThresholdParametersPanel);
				frame.validate();
				break;
			case ADAPTIVETHRESHOLDSTRING:
				currentThreshold = ADAPTIVETHRESHOLDSTRING;
				thresholdParametersPanel.removeAll();
				thresholdParametersPanel.add(adaptiveThresholdParametersPanel);
				frame.validate();
				break;
			case RANGEDTHRESHOLDSTRING:
				currentThreshold = RANGEDTHRESHOLDSTRING;
				thresholdParametersPanel.removeAll();
				thresholdParametersPanel.add(rangedThresholdParametersPanel);
				frame.validate();
				break;
			case NOTHRESHOLDSTRING:
				currentThreshold = NOTHRESHOLDSTRING;
				thresholdParametersPanel.removeAll();
				thresholdParametersPanel.add(noThresholdParametersPanel);
				frame.validate();
				break;
			case OTSUSTRING:
				if (regularThresholdParametersPanel.isOtsuCheckBoxSelected()) {
					regularThresholdParametersPanel.setGrayScaleCheckBoxSelected(true);
					regularThresholdParametersPanel.setGrayScaleCheckBoxEnabled(false);
					regularThresholdParametersPanel.setThresholdSliderEnabled(false);
				} else {
					regularThresholdParametersPanel.setGrayScaleCheckBoxEnabled(true);
					regularThresholdParametersPanel.setThresholdSliderEnabled(true);
				}
				break;
			case SHOWIMAGESTRING:
				switch (currentBlur) {
					case GAUSSIANBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								Mat enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(),
										regularThresholdParametersPanel.getThresholdSliderValue(),
										regularThresholdParametersPanel.getMaxvalSliderValue(),
										regularThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										regularThresholdParametersPanel.getThresholdTypeSliderValue(),
										regularThresholdParametersPanel.isOtsuCheckBoxSelected());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndAdaptiveThreshold(originalMat,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(),
										adaptiveThresholdParametersPanel.getMaxvalSliderValue(),
										adaptiveThresholdParametersPanel.getAdaptiveMethodSliderValue(),
										adaptiveThresholdParametersPanel.getThresholdTypeSliderValue(),
										adaptiveThresholdParametersPanel.getBlockSizeSliderValue(),
										adaptiveThresholdParametersPanel.getCSliderValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndRangedThreshold(originalMat, false,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(),
										rangedThresholdParametersPanel.getBlueMinSliderValue(),
										rangedThresholdParametersPanel.getGreenMinSliderValue(),
										rangedThresholdParametersPanel.getRedMinSliderValue(),
										rangedThresholdParametersPanel.getBlueMaxSliderValue(),
										rangedThresholdParametersPanel.getGreenMaxSliderValue(),
										rangedThresholdParametersPanel.getRedMaxSliderValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(), 0, 0,
										noThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										ImageEnhancer.THRESH_NONE,
										false);
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
						}
						break;
					case MEDIANBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								Mat enhancedMat = ImageEnhancer.medianBlurAndThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(),
										regularThresholdParametersPanel.getThresholdSliderValue(),
										regularThresholdParametersPanel.getMaxvalSliderValue(),
										regularThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										regularThresholdParametersPanel.getThresholdTypeSliderValue(),
										regularThresholdParametersPanel.isOtsuCheckBoxSelected());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndAdaptiveThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(),
										adaptiveThresholdParametersPanel.getMaxvalSliderValue(),
										adaptiveThresholdParametersPanel.getAdaptiveMethodSliderValue(),
										adaptiveThresholdParametersPanel.getThresholdTypeSliderValue(),
										adaptiveThresholdParametersPanel.getBlockSizeSliderValue(),
										adaptiveThresholdParametersPanel.getCSliderValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndRangedThreshold(originalMat, false,
										medianParametersPanel.getKernelSizeSliderValue(),
										rangedThresholdParametersPanel.getBlueMinSliderValue(),
										rangedThresholdParametersPanel.getGreenMinSliderValue(),
										rangedThresholdParametersPanel.getRedMinSliderValue(),
										rangedThresholdParametersPanel.getBlueMaxSliderValue(),
										rangedThresholdParametersPanel.getGreenMaxSliderValue(),
										rangedThresholdParametersPanel.getRedMaxSliderValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(), 0, 0,
										noThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										ImageEnhancer.THRESH_NONE,
										false);
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
						}
						break;
					case NOBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								Mat enhancedMat = ImageEnhancer.onlyThreshold(originalMat,
										regularThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										regularThresholdParametersPanel.getThresholdTypeSliderValue(),
										regularThresholdParametersPanel.getThresholdSliderValue(),
										regularThresholdParametersPanel.getMaxvalSliderValue(),
										regularThresholdParametersPanel.isOtsuCheckBoxSelected());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyAdaptiveThreshold(originalMat,
										adaptiveThresholdParametersPanel.getThresholdTypeSliderValue(),
										adaptiveThresholdParametersPanel.getMaxvalSliderValue(),
										adaptiveThresholdParametersPanel.getAdaptiveMethodSliderValue(),
										adaptiveThresholdParametersPanel.getBlockSizeSliderValue(),
										adaptiveThresholdParametersPanel.getCSliderValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyRangedThreshold(originalMat, false,
										rangedThresholdParametersPanel.getBlueMinSliderValue(),
										rangedThresholdParametersPanel.getGreenMinSliderValue(),
										rangedThresholdParametersPanel.getRedMinSliderValue(),
										rangedThresholdParametersPanel.getBlueMaxSliderValue(),
										rangedThresholdParametersPanel.getGreenMaxSliderValue(),
										rangedThresholdParametersPanel.getRedMaxSliderValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyThreshold(originalMat,
										noThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										ImageEnhancer.THRESH_NONE,
										0, 0, false);
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
						}
						break;
				}
				break;
		}
	}

	private void showMat (Mat mat, int width, int height) {
		Mat resizedMat = ImageUtils.resize(mat, width, height);
		Image image = ImageUtils.matToImage(resizedMat);
		ImageIcon icon = new ImageIcon(image);
		imageLabel.setIcon(icon);
		frame.validate();
	}
}
