package il.ac.tau.cs.databases.atlas;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import il.ac.tau.cs.databases.atlas.graphics.Utils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates and shows the map screen.
	 * 
	 * @throws IOException
	 */
	public Map() throws IOException {

		String mapImagePath = Utils.getSkin() + "Background.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(mapImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(mapImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(Utils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Set Panel
		
		// Show map screen
		setVisible(true);
	}
}
