package enhanceImage.demo.components;

import javax.swing.*;
import java.util.Hashtable;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class AdaptiveThresholdParametersPanel extends JPanel {

	private JSlider maxvalSlider;
	private JSlider adaptiveMethodSlider;
	private JSlider thresholdTypeSlider;
	private JSlider blockSizeSlider;
	private JSlider cSlider;

	public AdaptiveThresholdParametersPanel () {

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		JPanel maxvalPanel = new JPanel();
		JLabel maxvalSliderLabel = new JLabel("Maxval");
		maxvalPanel.add(maxvalSliderLabel);
		maxvalSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		maxvalSlider.setMajorTickSpacing(50);
		maxvalSlider.setMinorTickSpacing(5);
		maxvalSlider.setPaintTicks(true);
		maxvalSlider.setPaintLabels(true);
		maxvalSlider.setSnapToTicks(true);
		maxvalPanel.add(maxvalSlider);
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

		thresholdTypeSlider = new JSlider(JSlider.VERTICAL, 0, 1, 0);
		thresholdTypeSlider.setMajorTickSpacing(1);
		thresholdTypeSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> thresholdTypeLabelTable = new Hashtable<>();
		thresholdTypeLabelTable.put(0, new JLabel("Binary"));
		thresholdTypeLabelTable.put(1, new JLabel("Binary inverse"));
		thresholdTypeSlider.setLabelTable(thresholdTypeLabelTable);
		thresholdTypeSlider.setPaintLabels(true);
		thresholdTypeSlider.setSnapToTicks(true);
		adaptiveMethodAndThresholdTypePanel.add(thresholdTypeSlider);
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

	public int getMaxvalSliderValue () {
		return maxvalSlider.getValue();
	}

	public int getAdaptiveMethodSliderValue () {
		return adaptiveMethodSlider.getValue();
	}

	public int getThresholdTypeSliderValue () {
		return thresholdTypeSlider.getValue();
	}

	public int getBlockSizeSliderValue () {
		return blockSizeSlider.getValue();
	}

	public int getCSliderValue () {
		return cSlider.getValue();
	}
}
