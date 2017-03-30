package enhanceImage.demo.components;

import enhanceImage.demo.EnhanceImageDemo;

import javax.swing.*;
import java.awt.event.ActionListener;

import static enhanceImage.demo.EnhanceImageDemo.GAUSSIANBLURSTRING;
import static enhanceImage.demo.EnhanceImageDemo.MEDIANBLURSTRING;
import static enhanceImage.demo.EnhanceImageDemo.NOBLURSTRING;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class BlurPanel extends JPanel {

	public BlurPanel (ActionListener actionListener) {
		JRadioButton gaussianButton = new JRadioButton(GAUSSIANBLURSTRING);
		gaussianButton.setActionCommand(GAUSSIANBLURSTRING);
		gaussianButton.addActionListener(actionListener);
		JRadioButton medianButton = new JRadioButton(MEDIANBLURSTRING);
		medianButton.setActionCommand(MEDIANBLURSTRING);
		medianButton.addActionListener(actionListener);
		JRadioButton noBlurButton = new JRadioButton(NOBLURSTRING);
		noBlurButton.setActionCommand(NOBLURSTRING);
		noBlurButton.addActionListener(actionListener);
		ButtonGroup blurButtons = new ButtonGroup();
		blurButtons.add(gaussianButton);
		blurButtons.add(medianButton);
		blurButtons.add(noBlurButton);
		this.add(gaussianButton);
		this.add(medianButton);
		this.add(noBlurButton);
		gaussianButton.setSelected(true);
	}
}
