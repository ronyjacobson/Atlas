package il.ac.tau.cs.databases.atlas.graphics;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JLayeredPane;

public class ImagePanel extends JLayeredPane {
	
	private static final long serialVersionUID = 7137270811725979770L;
	private Image image;
	
	/**
	 * ImagePanel Constructor
	 */
	public ImagePanel(Image image) {
		this.image = image;
	}

	protected void paintComponent(Graphics graphics) {
		graphics.drawImage(image, 0, 0, null);
	}

}
