package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.MockQueries;
import il.ac.tau.cs.databases.atlas.db.Queries;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.utils.AudioUtils;
import il.ac.tau.cs.databases.atlas.utils.DateUtils;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

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
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
	private static final int NUM_OF_COMPONENTS = 9;
	private static final int GAP_BETWEEN_COMPONENTS = 16;
	private static final int FONT_SIZE_LABEL = 36;
	private static final int FONT_SIZE_FIELD = 15;
	private static final String DEFAULT_LOCATION = "Choose birth place...";

	private static final Queries queries = new MockQueries();

	private JLabel label;
	private JTextField username;
	private JPasswordField password;
	private JDateChooser wasBornOn;
	private JComboBox<String> wasBornIn;
	private JButton loginButton;
	private boolean wereCredentialsEntered = false;

	public Login() throws IOException {

		String loginImagePath = GrapicUtils.getSkin() + "Login.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(loginImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(loginImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(GrapicUtils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Add login panel
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
	 * 
	 * @param panel
	 *            The panel to fill
	 * @param width
	 *            The parent window width
	 * @param height
	 *            The parent window height
	 */
	private void createLoginPanel(JPanel panel, int width, int height) {

		// Make panel transparent
		panel.setOpaque(false);

		// Create buttons and text boxes
		ClearTextBox clearTextBoxListner = new ClearTextBox();
		Font labelFont = new Font("Century Gothic", Font.PLAIN, FONT_SIZE_LABEL);
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, FONT_SIZE_FIELD);

		label = new JLabel("Log in or sign up:");
		label.setForeground(Color.WHITE);
		label.setFont(labelFont);

		username = new JTextField("Username");
		username.addMouseListener(clearTextBoxListner);
		username.setFont(fieldFont);

		password = new JPasswordField("Password", 20);
		password.addMouseListener(clearTextBoxListner);
		password.setFont(fieldFont);

		Date today = new Date();
		wasBornOn = new JDateChooser();
		wasBornOn.setDate(today);
		wasBornOn.setMaxSelectableDate(today);
		wasBornOn.addMouseListener(clearTextBoxListner);
		wasBornOn.setFont(fieldFont);

		List<String> options = queries.getAllGeoLocations();
		options.add(0, DEFAULT_LOCATION);
		wasBornIn = new JComboBox<String>(options.toArray(new String[options.size()]));
		wasBornIn.setFont(fieldFont);

		loginButton = new JButton("Glimpse into the past!");
		loginButton.addActionListener(new LoginAction());
		loginButton.setFont(fieldFont);

		// Pad panel with blank label
		JLabel paddingLabel1 = new JLabel(" ");
		JLabel paddingLabel2 = new JLabel(" ");
		JLabel paddingLabel3 = new JLabel(" ");
		paddingLabel1.setFont(labelFont);
		paddingLabel2.setFont(labelFont);
		paddingLabel3.setFont(labelFont);
		panel.add(paddingLabel1);
		panel.add(paddingLabel2);
		panel.add(paddingLabel3);

		// Add buttons and text boxes
		panel.add(label);
		panel.add(username);
		panel.add(password);
		panel.add(wasBornOn);
		panel.add(wasBornIn);
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
				JOptionPane.showMessageDialog(null, "Please enter login credentials.", GrapicUtils.PROJECT_NAME, 1);
			} else if (username.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Username can not be blank.", GrapicUtils.PROJECT_NAME, 1);
			} else if (password.getPassword().length == 0) {
				JOptionPane.showMessageDialog(null, "Password can not be blank.", GrapicUtils.PROJECT_NAME, 1);
			} else if (DateUtils.isToday(wasBornOn.getCalendar())) {
				JOptionPane.showMessageDialog(null, "No way you were born today, enter a valid birthday.", GrapicUtils.PROJECT_NAME, 1);
			} else if (wasBornIn.getSelectedItem().toString().equals(DEFAULT_LOCATION)) {
				JOptionPane.showMessageDialog(null, "Please choose a birth place from the list.", GrapicUtils.PROJECT_NAME, 1);
			} else {
				// Create user
				User user = new User(username.getText(), String.copyValueOf(password.getPassword()), wasBornOn.getDate(), wasBornIn
						.getSelectedItem().toString());

				// Log in or sign up
				// Check if user already registered
				if (queries.isRegisteredUser(user)) {
					// Check password validity
					if (queries.areUsernamePasswordCorrect(user)) {
						// Login
						LoginSuccesful();
					} else {
						// Error
						JOptionPane.showMessageDialog(null, "Wrong username and password combination.", GrapicUtils.PROJECT_NAME, 1);
					}
				} else {
					// Suggest to sing up
					int reply = JOptionPane.showConfirmDialog(null, "Unregistered user. Would you like to register?", GrapicUtils.PROJECT_NAME,
							JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						if (queries.registerUser(user)) {
							JOptionPane.showMessageDialog(null, "You are now registered!");
							// Login
							LoginSuccesful();
						} else {
							// TODO Throw exception?
							JOptionPane.showMessageDialog(null, "Failed to register.");
						}
					}
				}
			}
		}
	}

	private void LoginSuccesful() {
		// Close login screen
		setVisible(false);
		dispose();

		// Play audio
		Runnable r = new Runnable() {
			public void run() {
				String loginAudioPath = getClass().getResource(GrapicUtils.getSkin() + "glimpse_melody.wav").getPath();
				new AudioUtils().playSound(loginAudioPath);
			}
		};
		new Thread(r).start();

		// Show map
		try {
			new Map();
		} catch (IOException e) {
			// TODO Handle Exception
			e.printStackTrace();
		}
	}
}
