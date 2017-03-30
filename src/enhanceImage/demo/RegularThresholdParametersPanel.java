package enhanceImage.demo;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import static enhanceImage.demo.EnhanceImageDemo.OTSUSTRING;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class RegularThresholdParametersPanel extends JPanel {

	private JSlider thresholdSlider;
	private JSlider maxvalSlider;
	private JSlider thresholdTypeSlider;
	private JCheckBox otsuCheckBox;
	private JCheckBox grayScaleCheckBox;

	public RegularThresholdParametersPanel(ActionListener actionListener) {
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
		maxvalSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		maxvalSlider.setMajorTickSpacing(50);
		maxvalSlider.setMinorTickSpacing(5);
		maxvalSlider.setPaintTicks(true);
		maxvalSlider.setPaintLabels(true);
		maxvalSlider.setSnapToTicks(true);
		maxvalPanel.add(maxvalSlider);
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
		grayScaleCheckBox = new JCheckBox("Gray scale");
		grayScaleCheckBox.setSelected(true);
		grayScaleCheckBox.setEnabled(false);
		thresholdTypeAndCheckBoxPanel.add(grayScaleCheckBox);
		this.add(thresholdTypeAndCheckBoxPanel);
	}

	public int getThresholdSliderValue () {
		return thresholdSlider.getValue();
	}

	public int getMaxvalSliderValue () {
		return maxvalSlider.getValue();
	}

	public int getThresholdTypeSliderValue () {
		return thresholdTypeSlider.getValue();
	}

	public boolean isOtsuCheckBoxSelected () {
		return otsuCheckBox.isSelected();
	}

	public boolean isGrayScaleCheckBoxSelected () {
		return grayScaleCheckBox.isSelected();
	}

	public void setGrayScaleCheckBoxSelected (boolean selected) {
		grayScaleCheckBox.setSelected(selected);
	}

	public void setGrayScaleCheckBoxEnabled (boolean enabled) {
		grayScaleCheckBox.setEnabled(enabled);
	}

	public void setThresholdSliderEnabled (boolean enabled) {
		thresholdSlider.setEnabled(enabled);
	}
}
