package enhanceImage.demo.components;

import javax.swing.*;
import java.awt.event.ActionListener;

import static enhanceImage.demo.EnhanceImageDemo.SHOWIMAGESTRING;

/**
 * Created by gustavbergstrom on 2017-03-30.
 */
public class ShowImagePanel extends JPanel {
	public ShowImagePanel (ActionListener actionListener) {
		JButton showImageButton = new JButton(SHOWIMAGESTRING);
		showImageButton.setActionCommand(SHOWIMAGESTRING);
		showImageButton.addActionListener(actionListener);
		this.add(showImageButton);
	}
}
