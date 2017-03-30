package enhanceImage.demo.components;

import javax.swing.*;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class GaussianParametersPanel extends JPanel {

	private JSlider kernelWidthSlider;
	private JSlider kernelHeightSlider;
	private JSlider sigmaSlider;

	public GaussianParametersPanel () {

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

	public int getKernelWidthSliderValue () {
		return kernelWidthSlider.getValue();
	}

	public int getKernelHeightSliderValue () {
		return kernelHeightSlider.getValue();
	}

	public int getSigmaSliderValue () {
		return sigmaSlider.getValue();
	}
}
