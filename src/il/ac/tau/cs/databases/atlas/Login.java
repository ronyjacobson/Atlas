package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.graphics.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

/**
 * Create and show a login screen.
 * 
 * @throws IOException
 */
public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int NUM_OF_COMPONENTS = 4;
	private static final int GAP_BETWEEN_COMPONENTS = 10;

	private JLabel label;
	private JTextField username;
	private JPasswordField password;
	private JDateChooser dateOfBirth; 
	private JButton loginButton;
	private boolean wereCredentialsEntered = false;

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
		setTitle(Utils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Add login panel
		// TODO change layout
		// http://stackoverflow.com/questions/11165807/put-jbutton-in-bottom-right
		// https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
		setLayout(new BorderLayout());
		setLayout(new FlowLayout());
		GridLayout panelLayout = new GridLayout(NUM_OF_COMPONENTS, 1);
		panelLayout.setVgap(GAP_BETWEEN_COMPONENTS);
		JPanel panel = new JPanel(panelLayout);
		createLoginPanel(panel, width, height);
		add(panel);

		// Show login screen
		setVisible(true);

	}

	/**
	 * Create and fill the login panel
	 * @param panel The panel to fill
	 * @param width The parent window width
	 * @param height The parent window height
	 */
	private void createLoginPanel(JPanel panel, int width, int height) {

		// Make panel transparent
		panel.setOpaque(false);

		// Create buttons and text boxes
		
		label = new JLabel("Log in or sign up:");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Century Gothic", Font.PLAIN, 36));
		
		username = new JTextField("Username");
		username.addMouseListener(new ClearTextBox());
		username.setFont(new Font("Century Gothic", Font.PLAIN, 15));
		
		password = new JPasswordField("Password", 20);
		password.addMouseListener(new ClearTextBox());
		password.setFont(new Font("Century Gothic", Font.PLAIN, 15));
		
		loginButton = new JButton("Glimpse into the past!");
		loginButton.addActionListener(new LoginAction());
		loginButton.setFont(new Font("Century Gothic", Font.PLAIN, 20));

		// Add buttons and text boxes
		panel.add(label);
		panel.add(username);
		panel.add(password);
		panel.add(loginButton);
	}

	/**
	 * Clear text boxes
	 */
	private class ClearTextBox implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (!wereCredentialsEntered) {
				username.setText("");
				password.setText("");
				wereCredentialsEntered = true;
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	/**
	 * Log in or sign up to the system using the data base
	 */
	private class LoginAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// Validate input
			if (!wereCredentialsEntered) {
				JOptionPane.showMessageDialog(null,
						"Please enter login credentials.", Utils.PROJECT_NAME,
						1);
			} else if (username.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						"Username can not be blank.", Utils.PROJECT_NAME, 1);
			} else if (password.getPassword().length == 0) {
				JOptionPane.showMessageDialog(null,
						"Password can not be blank.", Utils.PROJECT_NAME, 1);
			} else {
				// Login or signup
				verifyUsernamePassword();
			}
		}

		private void verifyUsernamePassword() {
			// TODO
			
			// Close login screen
			setVisible(false);
			dispose();
			
			// Show map
			try {
				new Map();
			} catch (IOException e) {
				// TODO Handle Exception
				e.printStackTrace();
			}
		}
	}
}
