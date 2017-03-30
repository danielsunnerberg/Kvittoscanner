package enhanceImage.demo.components;

import javax.swing.*;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class RangedThresholdParametersPanel extends JPanel {

	private JSlider blueMinSlider;
	private JSlider blueMaxSlider;
	private JSlider greenMinSlider;
	private JSlider greenMaxSlider;
	private JSlider redMinSlider;
	private JSlider redMaxSlider;

	public RangedThresholdParametersPanel () {

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

	public int getBlueMinSliderValue () {
		return blueMinSlider.getValue();
	}

	public int getBlueMaxSliderValue () {
		return blueMaxSlider.getValue();
	}

	public int getGreenMinSliderValue () {
		return greenMinSlider.getValue();
	}

	public int getGreenMaxSliderValue () {
		return greenMaxSlider.getValue();
	}

	public int getRedMinSliderValue () {
		return redMinSlider.getValue();
	}

	public int getRedMaxSliderValue () {
		return redMaxSlider.getValue();
	}
}
