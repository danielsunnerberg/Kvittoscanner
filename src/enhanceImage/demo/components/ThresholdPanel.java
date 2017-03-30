package enhanceImage.demo.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static enhanceImage.demo.EnhanceImageDemo.*;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class ThresholdPanel extends JPanel {

	public ThresholdPanel (ActionListener actionListener) {

		this.setLayout(new GridLayout(2,2));

		JRadioButton thresholdButton = new JRadioButton(THRESHOLDSTRING);
		thresholdButton.setActionCommand(THRESHOLDSTRING);
		thresholdButton.addActionListener(actionListener);
		JRadioButton adaptiveThreshButton = new JRadioButton(ADAPTIVETHRESHOLDSTRING);
		adaptiveThreshButton.setActionCommand(ADAPTIVETHRESHOLDSTRING);
		adaptiveThreshButton.addActionListener(actionListener);
		JRadioButton rangedThresholdButton = new JRadioButton(RANGEDTHRESHOLDSTRING);
		rangedThresholdButton.setActionCommand(RANGEDTHRESHOLDSTRING);
		rangedThresholdButton.addActionListener(actionListener);
		JRadioButton noThresholdButton = new JRadioButton(NOTHRESHOLDSTRING);
		noThresholdButton.setActionCommand(NOTHRESHOLDSTRING);
		noThresholdButton.addActionListener(actionListener);
		ButtonGroup thresholdButtons = new ButtonGroup();
		thresholdButtons.add(thresholdButton);
		thresholdButtons.add(adaptiveThreshButton);
		thresholdButtons.add(rangedThresholdButton);
		thresholdButtons.add(noThresholdButton);
		this.add(thresholdButton);
		this.add(adaptiveThreshButton);
		this.add(rangedThresholdButton);
		this.add(noThresholdButton);
		thresholdButton.setSelected(true);
	}
}
