package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.graphics.ImagePanel;
import il.ac.tau.cs.databases.atlas.graphics.Utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JFrame;

public class Map {

	// Define main parameters
	private JFrame mainFrame;
	
	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public Map() throws Exception {
		// Initialize the application.
		initialize();
	}

	/**
	 * Show the application's main frame.
	 */
	public void show(){
		// Show main frame
		mainFrame.setVisible(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {

		// Initialize frame
		mainFrame = new JFrame();

		// Set frame attributes
		// Frame borders
		mainFrame.setBounds(0, 0, Utils.screenSize.width,
				Utils.screenSize.height);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setUndecorated(true);
		BufferedImage image = null;
		try {
			// Frame background panel
			InputStream imageStream = getClass().getResourceAsStream(
					Utils.getSkin() + "Background.png");
			image = ImageIO.read(imageStream);
			ImagePanel imagePanel = new ImagePanel(image);
			// Layout
			GroupLayout layout = new GroupLayout(imagePanel);
			imagePanel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			// Add Button
			// XButton exitButton = new XButton("Exit", new Utils.ExitActionListener());
			//imagePanel.add(exitButton);
			// JComponent b1 = new XButton("Exit", new Utils.ExitActionListener());
			// JComponent b2 = new XButton("Exit", new Utils.ExitActionListener());
			// JComponent b3 = new XButton("Exit", new Utils.ExitActionListener());
/*
			layout.setHorizontalGroup(
					 layout.createSequentialGroup()
			                .addComponent(b1, 0, b1.getWidth(), b1.getWidth())
			                .addGroup(layout.createParallelGroup()
			                    .addComponent(b2, 0, b2.getWidth(), b2.getWidth())
			                    .addComponent(b3, 0, b3.getWidth(), b3.getWidth())));

			 layout.setVerticalGroup(
			    		layout.createParallelGroup()
			                .addComponent(b1, 0, b1.getHeight(), b1.getHeight())
			                .addGroup(layout.createSequentialGroup()
			                    .addComponent(b2, 0, b2.getHeight(), b2.getHeight())
			                    .addComponent(b3, 0, b3.getHeight(), b3.getHeight())));
			 */
			// Set Panel
			mainFrame.setContentPane(imagePanel);
		} catch (IOException e) {
			// Background image failed to load
			throw new Exception("Background image failed to load!");
		}

	}
}
