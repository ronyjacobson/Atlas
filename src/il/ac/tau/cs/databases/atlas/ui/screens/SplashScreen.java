package il.ac.tau.cs.databases.atlas.ui.screens;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

/**
 * Create and show a splash screen.
 * 
 * @throws IOException
 */
public class SplashScreen {
	
	public SplashScreen() throws IOException {
		
		String splashImagePath = GraphicUtils.DEFAULT_SKIN + "Splash.png";
		
		// Create Window
		JWindow splashWindow = new JWindow();
		
		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(splashImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();
		
		// Set graphics
		URL imageURL = getClass().getResource(splashImagePath);
		splashWindow.getContentPane().add(new JLabel("", new ImageIcon(imageURL), SwingConstants.CENTER));
		splashWindow.setBounds((GraphicUtils.screenSize.width - width) / 2,
				(GraphicUtils.screenSize.height - height) / 2, width, height);
		
		// Show splash screen
		splashWindow.setVisible(true);
		try {
		    Thread.sleep(6000);
		} catch (InterruptedException e) {
		    // If someone stops the splash screen, do nothing
		}
		
		// Close splash screen
		splashWindow.setVisible(false);
		splashWindow.dispose();
					
		if (Main.queries.isConnectedToDB()){
			// Start next screen
			new LoginScreen();
		} else {
			// Error and exit
			JOptionPane.showMessageDialog(null, "Failed to connect to the database. Please check your configuration file. Program will terminate.", GraphicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
		}
		
	}
}