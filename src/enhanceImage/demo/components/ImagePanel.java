package enhanceImage.demo.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class ImagePanel extends JPanel {

	private JLabel imageLabel;

	public ImagePanel (Image image) {
		imageLabel = new JLabel();
		add(imageLabel);
		updateImage(image);
	}

	public void updateImage (Image image) {
		ImageIcon imageIcon = new ImageIcon(image);
		imageLabel.setIcon(imageIcon);
	}
}
