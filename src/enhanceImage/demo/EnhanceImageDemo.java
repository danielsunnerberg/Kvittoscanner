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

	private void updateBlur (String blur, JPanel parametersPanel) {
		currentBlur = blur;
		blurParametersPanel.removeAll();
		if (parametersPanel != null) {
			blurParametersPanel.add(parametersPanel);
		}
		frame.validate();
	}

	private void updateThreshold (String threshold, JPanel parametersPanel) {
		currentThreshold = threshold;
		thresholdParametersPanel.removeAll();
		thresholdParametersPanel.add(parametersPanel);
		frame.validate();
	}

	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
			case GAUSSIANBLURSTRING:
				updateBlur(GAUSSIANBLURSTRING, gaussianParametersPanel);
				break;
			case MEDIANBLURSTRING:
				updateBlur(MEDIANBLURSTRING, medianParametersPanel);
				break;
			case NOBLURSTRING:
				updateBlur(NOBLURSTRING, null);
				break;
			case THRESHOLDSTRING:
				updateThreshold(THRESHOLDSTRING, regularThresholdParametersPanel);
				break;
			case ADAPTIVETHRESHOLDSTRING:
				updateThreshold(ADAPTIVETHRESHOLDSTRING, adaptiveThresholdParametersPanel);
				break;
			case RANGEDTHRESHOLDSTRING:
				updateThreshold(RANGEDTHRESHOLDSTRING, rangedThresholdParametersPanel);
				break;
			case NOTHRESHOLDSTRING:
				updateThreshold(NOBLURSTRING, noThresholdParametersPanel);
				break;
			case OTSUSTRING:
				boolean isOtsuCheckBoxSelected = regularThresholdParametersPanel.isOtsuCheckBoxSelected();
				regularThresholdParametersPanel.setGrayScaleCheckBoxSelected(true);
				regularThresholdParametersPanel.setGrayScaleCheckBoxEnabled(!isOtsuCheckBoxSelected);
				regularThresholdParametersPanel.setThresholdSliderEnabled(!isOtsuCheckBoxSelected);
				break;
			case SHOWIMAGESTRING:
				Mat enhancedMat = new Mat();
				switch (currentBlur) {
					case GAUSSIANBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(),
										regularThresholdParametersPanel.getThresholdSliderValue(),
										regularThresholdParametersPanel.getMaxvalSliderValue(),
										regularThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										regularThresholdParametersPanel.getThresholdTypeSliderValue(),
										regularThresholdParametersPanel.isOtsuCheckBoxSelected());
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
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(), 0, 0,
										noThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										ImageEnhancer.THRESH_NONE,
										false);
								break;
						}
						break;
					case MEDIANBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(),
										regularThresholdParametersPanel.getThresholdSliderValue(),
										regularThresholdParametersPanel.getMaxvalSliderValue(),
										regularThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										regularThresholdParametersPanel.getThresholdTypeSliderValue(),
										regularThresholdParametersPanel.isOtsuCheckBoxSelected());
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndAdaptiveThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(),
										adaptiveThresholdParametersPanel.getMaxvalSliderValue(),
										adaptiveThresholdParametersPanel.getAdaptiveMethodSliderValue(),
										adaptiveThresholdParametersPanel.getThresholdTypeSliderValue(),
										adaptiveThresholdParametersPanel.getBlockSizeSliderValue(),
										adaptiveThresholdParametersPanel.getCSliderValue());
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
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(), 0, 0,
										noThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										ImageEnhancer.THRESH_NONE,
										false);
								break;
						}
						break;
					case NOBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyThreshold(originalMat,
										regularThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										regularThresholdParametersPanel.getThresholdTypeSliderValue(),
										regularThresholdParametersPanel.getThresholdSliderValue(),
										regularThresholdParametersPanel.getMaxvalSliderValue(),
										regularThresholdParametersPanel.isOtsuCheckBoxSelected());
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyAdaptiveThreshold(originalMat,
										adaptiveThresholdParametersPanel.getThresholdTypeSliderValue(),
										adaptiveThresholdParametersPanel.getMaxvalSliderValue(),
										adaptiveThresholdParametersPanel.getAdaptiveMethodSliderValue(),
										adaptiveThresholdParametersPanel.getBlockSizeSliderValue(),
										adaptiveThresholdParametersPanel.getCSliderValue());
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyRangedThreshold(originalMat, false,
										rangedThresholdParametersPanel.getBlueMinSliderValue(),
										rangedThresholdParametersPanel.getGreenMinSliderValue(),
										rangedThresholdParametersPanel.getRedMinSliderValue(),
										rangedThresholdParametersPanel.getBlueMaxSliderValue(),
										rangedThresholdParametersPanel.getGreenMaxSliderValue(),
										rangedThresholdParametersPanel.getRedMaxSliderValue());
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyThreshold(originalMat,
										noThresholdParametersPanel.isGrayScaleCheckBoxSelected(),
										ImageEnhancer.THRESH_NONE,
										0, 0, false);
								break;
						}
						break;
				}
				showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
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
