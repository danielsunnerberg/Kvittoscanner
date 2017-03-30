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
import java.util.Hashtable;

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

	private JSlider adaptiveThresholdMaxvalSlider;
	private JSlider adaptiveMethodSlider;
	private JSlider adaptiveThresholdTypeSlider;
	private JSlider blockSizeSlider;
	private JSlider cSlider;

	private JSlider blueMinSlider;
	private JSlider blueMaxSlider;
	private JSlider greenMinSlider;
	private JSlider greenMaxSlider;
	private JSlider redMinSlider;
	private JSlider redMaxSlider;

	private JCheckBox noThresholdGrayScaleCheckBox;

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
				thresholdParametersPanel.add(new AdaptiveThresholdParametersPanel());
				frame.validate();
				break;
			case RANGEDTHRESHOLDSTRING:
				currentThreshold = RANGEDTHRESHOLDSTRING;
				thresholdParametersPanel.removeAll();
				thresholdParametersPanel.add(new RangedThresholdParametersPanel());
				frame.validate();
				break;
			case NOTHRESHOLDSTRING:
				currentThreshold = NOTHRESHOLDSTRING;
				thresholdParametersPanel.removeAll();
				thresholdParametersPanel.add(new NoThresholdParametersPanel());
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
										gaussianParametersPanel.getSigmaSliderValue(), adaptiveThresholdMaxvalSlider.getValue(),
										adaptiveMethodSlider.getValue(), adaptiveThresholdTypeSlider.getValue(),
										blockSizeSlider.getValue(), cSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndRangedThreshold(originalMat, false,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(), blueMinSlider.getValue(),
										greenMinSlider.getValue(), redMinSlider.getValue(), blueMaxSlider.getValue(),
										greenMaxSlider.getValue(), redMaxSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										gaussianParametersPanel.getKernelWidthSliderValue(),
										gaussianParametersPanel.getKernelHeightSliderValue(),
										gaussianParametersPanel.getSigmaSliderValue(), 0, 0,
										noThresholdGrayScaleCheckBox.isSelected(), ImageEnhancer.THRESH_NONE,
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
										adaptiveThresholdMaxvalSlider.getValue(), adaptiveMethodSlider.getValue(),
										adaptiveThresholdTypeSlider.getValue(), blockSizeSlider.getValue(),
										cSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndRangedThreshold(originalMat, false,
										medianParametersPanel.getKernelSizeSliderValue(), blueMinSlider.getValue(),
										greenMinSlider.getValue(), redMinSlider.getValue(), blueMaxSlider.getValue(),
										greenMaxSlider.getValue(), redMaxSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndThreshold(originalMat,
										medianParametersPanel.getKernelSizeSliderValue(), 0, 0,
										noThresholdGrayScaleCheckBox.isSelected(), ImageEnhancer.THRESH_NONE,
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
										adaptiveThresholdTypeSlider.getValue(),
										adaptiveThresholdMaxvalSlider.getValue(), adaptiveMethodSlider.getValue(),
										blockSizeSlider.getValue(), cSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyRangedThreshold(originalMat, false,
										blueMinSlider.getValue(), greenMinSlider.getValue(), redMinSlider.getValue(),
										blueMaxSlider.getValue(), greenMaxSlider.getValue(), redMaxSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.onlyThreshold(originalMat,
										noThresholdGrayScaleCheckBox.isSelected(), ImageEnhancer.THRESH_NONE,
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

	private class AdaptiveThresholdParametersPanel extends JPanel {
		private AdaptiveThresholdParametersPanel () {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			JPanel maxvalPanel = new JPanel();
			JLabel maxvalSliderLabel = new JLabel("Maxval");
			maxvalPanel.add(maxvalSliderLabel);
			adaptiveThresholdMaxvalSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
			adaptiveThresholdMaxvalSlider.setMajorTickSpacing(50);
			adaptiveThresholdMaxvalSlider.setMinorTickSpacing(5);
			adaptiveThresholdMaxvalSlider.setPaintTicks(true);
			adaptiveThresholdMaxvalSlider.setPaintLabels(true);
			adaptiveThresholdMaxvalSlider.setSnapToTicks(true);
			maxvalPanel.add(adaptiveThresholdMaxvalSlider);
			this.add(maxvalPanel);

			JPanel adaptiveMethodAndThresholdTypePanel = new JPanel();
			adaptiveMethodSlider = new JSlider(JSlider.VERTICAL, 0, 1, 0);
			adaptiveMethodSlider.setMajorTickSpacing(1);
			adaptiveMethodSlider.setPaintTicks(true);
			Hashtable<Integer, JLabel> adaptiveMethodLabelTable = new Hashtable<>();
			adaptiveMethodLabelTable.put(0, new JLabel("Mean"));
			adaptiveMethodLabelTable.put(1, new JLabel("Gaussian"));
			adaptiveMethodSlider.setLabelTable(adaptiveMethodLabelTable);
			adaptiveMethodSlider.setPaintLabels(true);
			adaptiveMethodSlider.setSnapToTicks(true);
			adaptiveMethodAndThresholdTypePanel.add(adaptiveMethodSlider);

			adaptiveThresholdTypeSlider = new JSlider(JSlider.VERTICAL, 0, 1, 0);
			adaptiveThresholdTypeSlider.setMajorTickSpacing(1);
			adaptiveThresholdTypeSlider.setPaintTicks(true);
			Hashtable<Integer, JLabel> thresholdTypeLabelTable = new Hashtable<>();
			thresholdTypeLabelTable.put(0, new JLabel("Binary"));
			thresholdTypeLabelTable.put(1, new JLabel("Binary inverse"));
			adaptiveThresholdTypeSlider.setLabelTable(thresholdTypeLabelTable);
			adaptiveThresholdTypeSlider.setPaintLabels(true);
			adaptiveThresholdTypeSlider.setSnapToTicks(true);
			adaptiveMethodAndThresholdTypePanel.add(adaptiveThresholdTypeSlider);
			this.add(adaptiveMethodAndThresholdTypePanel);

			JPanel blockSizePanel = new JPanel();
			JLabel blockSizeSliderLabel = new JLabel("Block size");
			blockSizePanel.add(blockSizeSliderLabel);
			blockSizeSlider = new JSlider(JSlider.HORIZONTAL, 3,  33, 3);
			blockSizeSlider.setMajorTickSpacing(10);
			blockSizeSlider.setMinorTickSpacing(2);
			blockSizeSlider.setPaintTicks(true);
			blockSizeSlider.setPaintLabels(true);
			blockSizeSlider.setSnapToTicks(true);
			blockSizePanel.add(blockSizeSlider);
			this.add(blockSizePanel);

			JPanel cPanel = new JPanel();
			JLabel cSliderLabel = new JLabel("C");
			cPanel.add(cSliderLabel);
			cSlider = new JSlider(JSlider.HORIZONTAL, -20, 20, 5);
			cSlider.setMajorTickSpacing(5);
			cSlider.setMinorTickSpacing(1);
			cSlider.setPaintTicks(true);
			cSlider.setPaintLabels(true);
			cSlider.setSnapToTicks(true);
			cPanel.add(cSlider);
			this.add(cPanel);
		}
	}

	private class RangedThresholdParametersPanel extends JPanel {
		private RangedThresholdParametersPanel () {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			JPanel blueMinPanel = new JPanel();
			JLabel blueMinSliderLabel = new JLabel("Blue min");
			blueMinPanel.add(blueMinSliderLabel);
			blueMinSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 200);
			blueMinSlider.setMajorTickSpacing(50);
			blueMinSlider.setMinorTickSpacing(5);
			blueMinSlider.setPaintTicks(true);
			blueMinSlider.setPaintLabels(true);
			blueMinSlider.setSnapToTicks(true);
			blueMinPanel.add(blueMinSlider);
			this.add(blueMinPanel);

			JPanel blueMaxPanel = new JPanel();
			JLabel blueMaxSliderLabel = new JLabel("Blue max");
			blueMaxPanel.add(blueMaxSliderLabel);
			blueMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
			blueMaxSlider.setMajorTickSpacing(50);
			blueMaxSlider.setMinorTickSpacing(5);
			blueMaxSlider.setPaintTicks(true);
			blueMaxSlider.setPaintLabels(true);
			blueMaxSlider.setSnapToTicks(true);
			blueMaxPanel.add(blueMaxSlider);
			this.add(blueMaxPanel);

			JPanel greenMinPanel = new JPanel();
			JLabel greenMinSliderLabel = new JLabel("Green min");
			greenMinPanel.add(greenMinSliderLabel);
			greenMinSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 200);
			greenMinSlider.setMajorTickSpacing(50);
			greenMinSlider.setMinorTickSpacing(5);
			greenMinSlider.setPaintTicks(true);
			greenMinSlider.setPaintLabels(true);
			greenMinSlider.setSnapToTicks(true);
			greenMinPanel.add(greenMinSlider);
			this.add(greenMinPanel);

			JPanel greenMaxPanel = new JPanel();
			JLabel greenMaxSliderLabel = new JLabel("Green max");
			greenMaxPanel.add(greenMaxSliderLabel);
			greenMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
			greenMaxSlider.setMajorTickSpacing(50);
			greenMaxSlider.setMinorTickSpacing(5);
			greenMaxSlider.setPaintTicks(true);
			greenMaxSlider.setPaintLabels(true);
			greenMaxSlider.setSnapToTicks(true);
			greenMaxPanel.add(greenMaxSlider);
			this.add(greenMaxPanel);

			JPanel redMinPanel = new JPanel();
			JLabel redMinSliderLabel = new JLabel("Red min");
			redMinPanel.add(redMinSliderLabel);
			redMinSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 200);
			redMinSlider.setMajorTickSpacing(50);
			redMinSlider.setMinorTickSpacing(5);
			redMinSlider.setPaintTicks(true);
			redMinSlider.setPaintLabels(true);
			redMinSlider.setSnapToTicks(true);
			redMinPanel.add(redMinSlider);
			this.add(redMinPanel);

			JPanel redMaxPanel = new JPanel();
			JLabel redMaxSliderLabel = new JLabel("Red max");
			redMaxPanel.add(redMaxSliderLabel);
			redMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
			redMaxSlider.setMajorTickSpacing(50);
			redMaxSlider.setMinorTickSpacing(5);
			redMaxSlider.setPaintTicks(true);
			redMaxSlider.setPaintLabels(true);
			redMaxSlider.setSnapToTicks(true);
			redMaxPanel.add(redMaxSlider);
			this.add(redMaxPanel);
		}
	}

	private class NoThresholdParametersPanel extends JPanel {
		private NoThresholdParametersPanel () {
			noThresholdGrayScaleCheckBox = new JCheckBox("Gray scale");
			noThresholdGrayScaleCheckBox.setSelected(true);
			this.add(noThresholdGrayScaleCheckBox);
		}
	}
}
