package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.State;
import il.ac.tau.cs.databases.atlas.graphics.Utils;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class Splash {

	/**
	 * Initialize the program parameters and load its state.
	 * 
	 * @throws Exception
	 */
	private static void initialize() throws Exception {
		// Get the user's screen size
		Utils.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Load earlier state
		try {
			State.autoLoad();
		} catch (Exception e) {
			// TODO Print error to screen
		}
	}
	
	/**
	 * Create and show a splash screen.
	 * 
	 * @throws IOException
	 */
	public Splash() throws IOException {
		
		String splashImagePath = Utils.getSkin() + "Splash.png";
		
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
		splashWindow.setBounds((Utils.screenSize.width - width) / 2,
				(Utils.screenSize.height - height) / 2, width, height);
		
		// Show splash screen
		splashWindow.setVisible(true);
		try {
		    Thread.sleep(3000);
		} catch (InterruptedException e) {
		    // If someone stops the splash screen, do nothing
		}
		
		// Close splash screen
		splashWindow.setVisible(false);
		splashWindow.dispose();
		
		// Start next screen
		JFrame frame = new JFrame();
		frame.add(new JLabel("Welcome"));
		frame.setVisible(true);
		frame.setSize(300,100);
		
	}

	/**
	 * Main running method
	 */
	public static void main(String[] args) {
		try {
			// Initialize the program
			initialize();
			// Show splash screen
			new Splash();
			
		} catch (Exception e) {
			// TODO Handle Exception
			e.printStackTrace();
		}
	}
}
