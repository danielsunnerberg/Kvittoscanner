package enhanceImage.demo.components;

import javax.swing.*;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class MedianParametersPanel extends JPanel {

	private JSlider kernelSizeSlider;

	public MedianParametersPanel () {
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

	public int getKernelSizeSliderValue () {
		return kernelSizeSlider.getValue();
	}
}
