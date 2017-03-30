package enhanceImage.demo;

import enhanceImage.ImageEnhancer;
import enhanceImage.demo.components.BlurPanel;
import enhanceImage.demo.components.ShowImagePanel;
import enhanceImage.demo.components.ThresholdPanel;
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
	private final String OTSUSTRING = "OTSU";

	private final int IMAGESIZE = 500;

	private Mat originalMat;

	private String currentBlur;
	private String currentThreshold;

	private JFrame frame;
	private JLabel imageLabel;
	private JPanel blurParametersPanel;
	private JPanel thresholdParametersPanel;

	private JSlider kernelWidthSlider;
	private JSlider kernelHeightSlider;
	private JSlider sigmaSlider;

	private JSlider kernelSizeSlider;

	private JSlider thresholdSlider;
	private JSlider thresholdMaxvalSlider;
	private JSlider thresholdTypeSlider;
	private JCheckBox otsuCheckBox;
	private JCheckBox thresholdGrayScaleCheckBox;

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
		blurParametersPanel.add(new GaussianParametersPanel());
		leftPanel.add(blurParametersPanel);
		currentBlur = GAUSSIANBLURSTRING;

		ThresholdPanel thresholdPanel = new ThresholdPanel(this);
		leftPanel.add(thresholdPanel);

		thresholdParametersPanel = new JPanel();
		thresholdParametersPanel.add(new ThresholdParametersPanel(this));
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
				blurParametersPanel.add(new GaussianParametersPanel());
				frame.validate();
				break;
			case MEDIANBLURSTRING:
				currentBlur = MEDIANBLURSTRING;
				blurParametersPanel.removeAll();
				blurParametersPanel.add(new MedianParametersPanel());
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
				thresholdParametersPanel.add(new ThresholdParametersPanel(this));
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
				if (otsuCheckBox.isSelected()) {
					thresholdGrayScaleCheckBox.setSelected(true);
					thresholdGrayScaleCheckBox.setEnabled(false);
					thresholdSlider.setEnabled(false);
				} else {
					thresholdGrayScaleCheckBox.setEnabled(true);
					thresholdSlider.setEnabled(true);
				}
				break;
			case SHOWIMAGESTRING:
				switch (currentBlur) {
					case GAUSSIANBLURSTRING:
						switch (currentThreshold) {
							case THRESHOLDSTRING:
								Mat enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										kernelWidthSlider.getValue(), kernelHeightSlider.getValue(),
										sigmaSlider.getValue(), thresholdSlider.getValue(),
										thresholdMaxvalSlider.getValue(), thresholdGrayScaleCheckBox.isSelected(),
										thresholdTypeSlider.getValue(), otsuCheckBox.isSelected());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndAdaptiveThreshold(originalMat,
										kernelWidthSlider.getValue(), kernelHeightSlider.getValue(),
										sigmaSlider.getValue(), adaptiveThresholdMaxvalSlider.getValue(),
										adaptiveMethodSlider.getValue(), adaptiveThresholdTypeSlider.getValue(),
										blockSizeSlider.getValue(), cSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndRangedThreshold(originalMat, false,
										kernelWidthSlider.getValue(), kernelHeightSlider.getValue(),
										sigmaSlider.getValue(), blueMinSlider.getValue(), greenMinSlider.getValue(),
										redMinSlider.getValue(), blueMaxSlider.getValue(), greenMaxSlider.getValue(),
										redMaxSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.gaussianBlurAndThreshold(originalMat,
										kernelWidthSlider.getValue(), kernelHeightSlider.getValue(),
										sigmaSlider.getValue(), 0, 0,
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
										kernelSizeSlider.getValue(), thresholdSlider.getValue(),
										thresholdMaxvalSlider.getValue(), thresholdGrayScaleCheckBox.isSelected(),
										thresholdTypeSlider.getValue(), otsuCheckBox.isSelected());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case ADAPTIVETHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndAdaptiveThreshold(originalMat,
										kernelSizeSlider.getValue(), adaptiveThresholdMaxvalSlider.getValue(),
										adaptiveMethodSlider.getValue(), adaptiveThresholdTypeSlider.getValue(),
										blockSizeSlider.getValue(), cSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case RANGEDTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndRangedThreshold(originalMat, false,
										kernelSizeSlider.getValue(), blueMinSlider.getValue(),
										greenMinSlider.getValue(), redMinSlider.getValue(), blueMaxSlider.getValue(),
										greenMaxSlider.getValue(), redMaxSlider.getValue());
								showMat(enhancedMat, IMAGESIZE, IMAGESIZE);
								break;
							case NOTHRESHOLDSTRING:
								enhancedMat = ImageEnhancer.medianBlurAndThreshold(originalMat,
										kernelSizeSlider.getValue(), 0, 0,
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
										thresholdGrayScaleCheckBox.isSelected(), thresholdTypeSlider.getValue(),
										thresholdSlider.getValue(), thresholdMaxvalSlider.getValue(),
										otsuCheckBox.isSelected());
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

	private class GaussianParametersPanel extends JPanel {
		private GaussianParametersPanel () {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			JPanel kernelWidthPanel = new JPanel();
			JLabel kernelWidthSliderLabel = new JLabel("Kernel width");
			kernelWidthPanel.add(kernelWidthSliderLabel);
			kernelWidthSlider = new JSlider(JSlider.HORIZONTAL,1,31,3);
			kernelWidthSlider.setMajorTickSpacing(10);
			kernelWidthSlider.setMinorTickSpacing(2);
			kernelWidthSlider.setPaintTicks(true);
			kernelWidthSlider.setPaintLabels(true);
			kernelWidthSlider.setSnapToTicks(true);
			kernelWidthPanel.add(kernelWidthSlider);
			this.add(kernelWidthPanel);

			JPanel kernelHeightPanel = new JPanel();
			JLabel kernelHeightSliderLabel = new JLabel("Kernel height");
			kernelHeightPanel.add(kernelHeightSliderLabel);
			kernelHeightSlider = new JSlider(JSlider.HORIZONTAL,1,31,3);
			kernelHeightSlider.setMajorTickSpacing(10);
			kernelHeightSlider.setMinorTickSpacing(2);
			kernelHeightSlider.setPaintTicks(true);
			kernelHeightSlider.setPaintLabels(true);
			kernelHeightSlider.setSnapToTicks(true);
			kernelHeightPanel.add(kernelHeightSlider);
			this.add(kernelHeightPanel);

			JPanel sigmaPanel = new JPanel();
			JLabel sigmaSliderLabel = new JLabel("Sigma");
			sigmaPanel.add(sigmaSliderLabel);
			sigmaSlider = new JSlider(JSlider.HORIZONTAL, 0, 30, 0);
			sigmaSlider.setMajorTickSpacing(10);
			sigmaSlider.setMinorTickSpacing(1);
			sigmaSlider.setPaintTicks(true);
			sigmaSlider.setPaintLabels(true);
			sigmaSlider.setSnapToTicks(true);
			sigmaPanel.add(sigmaSlider);
			this.add(sigmaPanel);
		}
	}

	private class MedianParametersPanel extends JPanel {
		private MedianParametersPanel () {
			JLabel kernelSizeSliderLabel = new JLabel("Kernel size");
			this.add(kernelSizeSliderLabel);
			kernelSizeSlider = new JSlider(JSlider.HORIZONTAL,1,31,3);
			kernelSizeSlider.setMajorTickSpacing(10);
			kernelSizeSlider.setMinorTickSpacing(2);
			kernelSizeSlider.setPaintTicks(true);
			kernelSizeSlider.setPaintLabels(true);
			kernelSizeSlider.setSnapToTicks(true);
			this.add(kernelSizeSlider);
		}
	}

	private class ThresholdParametersPanel extends JPanel {
		private ThresholdParametersPanel (ActionListener actionListener) {
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			JPanel thresholdPanel = new JPanel();
			JLabel thresholdSliderLabel = new JLabel("Threshold");
			thresholdPanel.add(thresholdSliderLabel);
			thresholdSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 100);
			thresholdSlider.setMajorTickSpacing(50);
			thresholdSlider.setMinorTickSpacing(5);
			thresholdSlider.setPaintTicks(true);
			thresholdSlider.setPaintLabels(true);
			thresholdSlider.setSnapToTicks(true);
			thresholdSlider.setEnabled(false);
			thresholdPanel.add(thresholdSlider);
			this.add(thresholdPanel);

			JPanel maxvalPanel = new JPanel();
			JLabel maxvalSliderLabel = new JLabel("Maxval");
			maxvalPanel.add(maxvalSliderLabel);
			thresholdMaxvalSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
			thresholdMaxvalSlider.setMajorTickSpacing(50);
			thresholdMaxvalSlider.setMinorTickSpacing(5);
			thresholdMaxvalSlider.setPaintTicks(true);
			thresholdMaxvalSlider.setPaintLabels(true);
			thresholdMaxvalSlider.setSnapToTicks(true);
			maxvalPanel.add(thresholdMaxvalSlider);
			this.add(maxvalPanel);

			JPanel thresholdTypeAndCheckBoxPanel = new JPanel();
			thresholdTypeSlider = new JSlider(JSlider.VERTICAL, 0, 4, 0);
			thresholdTypeSlider.setMajorTickSpacing(1);
			thresholdTypeSlider.setPaintTicks(true);
			thresholdTypeSlider.setSnapToTicks(true);
			Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
			labelTable.put(0, new JLabel("Binary"));
			labelTable.put(1, new JLabel("Binary inverse"));
			labelTable.put(2, new JLabel("Truncate"));
			labelTable.put(3, new JLabel("To zero"));
			labelTable.put(4, new JLabel("To zero inverse"));
			thresholdTypeSlider.setLabelTable(labelTable);
			thresholdTypeSlider.setPaintLabels(true);
			thresholdTypeAndCheckBoxPanel.add(thresholdTypeSlider);

			otsuCheckBox = new JCheckBox("OTSU");
			otsuCheckBox.setSelected(true);
			otsuCheckBox.setActionCommand(OTSUSTRING);
			otsuCheckBox.addActionListener(actionListener);
			thresholdTypeAndCheckBoxPanel.add(otsuCheckBox);
			thresholdGrayScaleCheckBox = new JCheckBox("Gray scale");
			thresholdGrayScaleCheckBox.setSelected(true);
			thresholdGrayScaleCheckBox.setEnabled(false);
			thresholdTypeAndCheckBoxPanel.add(thresholdGrayScaleCheckBox);
			this.add(thresholdTypeAndCheckBoxPanel);
		}
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
