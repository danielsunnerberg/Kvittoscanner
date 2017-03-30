package enhanceImage.demo.components;

import javax.swing.*;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class NoThresholdParametersPanel extends JPanel {

	private JCheckBox grayScaleCheckBox;

	public NoThresholdParametersPanel () {
		grayScaleCheckBox = new JCheckBox("Gray scale");
		grayScaleCheckBox.setSelected(true);
		this.add(grayScaleCheckBox);
	}

	public boolean isGrayScaleCheckBoxSelected () {
		return grayScaleCheckBox.isSelected();
	}
}
