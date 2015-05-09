package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.graphics.Utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;

	public Login() throws IOException {
		String loginImagePath = Utils.getSkin() + "Login.png";

		// Get graphics attributes
		InputStream imageStream = getClass()
				.getResourceAsStream(loginImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(loginImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle("ATLAS");
		setLocationRelativeTo(null);
		setResizable(false);
		
		// Set Actions
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Add buttons and text bars
		setLayout(new BorderLayout());
		setLayout(new FlowLayout());
		JButton loginButton = new JButton("I am a button");
		add(loginButton);
		
		// Show login screen
		setVisible(true);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// If someone stops the splash screen, do nothing
		}

		// Close login screen
		this.setVisible(false);
		this.dispose();

		// Start next screen
		// new Login();
	}

}
