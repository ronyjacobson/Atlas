package il.ac.tau.cs.databases.atlas.ui.screens;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.modal.User;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPool;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.db.queries.Queries;
import il.ac.tau.cs.databases.atlas.ui.listeners.DBFilesUploadListner;
import il.ac.tau.cs.databases.atlas.ui.utils.AudioUtils;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;
import il.ac.tau.cs.databases.atlas.utils.DateUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import com.toedter.calendar.JDateChooser;

/**
 * Create and show a login screen.
 * 
 * @throws IOException
 */
public class LoginScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int NUM_OF_COMPONENTS = 12;
	private static final int GAP_BETWEEN_COMPONENTS = 10;
	private static final String DEFAULT_LOCATION = "Choose birth place...";
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private User fetchedUser = null;
	private JLabel label;
	private JTextField username;
	private JPasswordField password;
	private JRadioButton isMale;
	private JRadioButton isFemale;
	private JPanel sexPanel;
	private JDateChooser wasBornOn;
	private JComboBox<String> wasBornIn;
	private JPanel birthPanel;
	private JButton loginButton;
	private boolean signupEnabled = false;
	private boolean wereCredentialsEntered = false;
	private JLabel installLabel;
	List<String> options;
	private JButton installButton;

	public LoginScreen() throws IOException {

		String loginImagePath = GraphicUtils.getSkin() + "Login.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(loginImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(loginImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(GraphicUtils.PROJECT_NAME);
		setLocationRelativeTo(null);


		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				log.info("Closing login window");
				final ConnectionPool connectionPool = ConnectionPoolHolder.INSTANCE.get();
				if (connectionPool != null) {
					connectionPool.close();
				}
			}
		});

		// Add login panel
//		setLayout(new BorderLayout());
		setLayout(new FlowLayout());
		GridLayout panelLayout = new GridLayout(NUM_OF_COMPONENTS, 1);
		panelLayout.setVgap(GAP_BETWEEN_COMPONENTS - 5);
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
		Font labelFont = new Font("Century Gothic", Font.PLAIN, GraphicUtils.FONT_SIZE_LABEL);
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, GraphicUtils.FONT_SIZE_FIELD);
		Font dateFont = new Font("Century Gothic", Font.PLAIN, GraphicUtils.FONT_SIZE_DATE);

		label = new JLabel("Log in or sign up:", SwingConstants.CENTER);
		label.setForeground(Color.black);
		label.setFont(labelFont);

		username = new JTextField("Username");
		username.addMouseListener(clearTextBoxListner);
		username.setFont(fieldFont);

		password = new JPasswordField("Password", 20);
		password.addMouseListener(clearTextBoxListner);
		password.setFont(fieldFont);

		isMale = new JRadioButton("Male");
		isMale.setFont(fieldFont);
		isMale.setOpaque(false);
		isMale.setForeground(Color.WHITE);
		isMale.setEnabled(false);
		isMale.addActionListener(new SexListener(false));
		isFemale = new JRadioButton("Female");
		isFemale.setFont(fieldFont);
		isFemale.setOpaque(false);
		isFemale.setForeground(Color.WHITE);
		isFemale.setEnabled(false);
		isFemale.addActionListener(new SexListener(true));

		sexPanel = new JPanel();
		sexPanel.add(isMale);
		sexPanel.add(isFemale);
		sexPanel.setOpaque(false);

		Date today = new Date();
		wasBornOn = new JDateChooser();
		wasBornOn.setDate(today);
		wasBornOn.setMaxSelectableDate(today);
		wasBornOn.addMouseListener(clearTextBoxListner);
		wasBornOn.setFont(dateFont);
		wasBornOn.setEnabled(false);

		try {
			options = new ArrayList<String>(Main.queries.getAllGeoLocationsNames());
			options.add(0, DEFAULT_LOCATION);
			wasBornIn = new JComboBox<>(options.toArray(new String[options.size()]));
			wasBornIn.setEditable(true);
			AutoCompleteDecorator.decorate(wasBornIn);
			wasBornIn.setPreferredSize(new Dimension(200,10));
			wasBornIn.setFont(fieldFont);
			wasBornIn.setEnabled(false);
		} catch (AtlasServerException e) {
			log.error("", e);
		}

		JLabel dateLabel = new JLabel("Birthday:", SwingConstants.LEFT);
		dateLabel.setForeground(Color.DARK_GRAY);
		dateLabel.setFont(fieldFont);
		
		JLabel placeLabel = new JLabel("Birth Place (optional):", SwingConstants.LEFT);
		placeLabel.setForeground(Color.DARK_GRAY);
		placeLabel.setFont(fieldFont);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = gbc.weighty = 1.0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		
		birthPanel = new JPanel(new GridBagLayout());
		birthPanel.setBackground(new Color(1f, 1f, 1f, 0.5f));
		birthPanel.add(dateLabel);
		birthPanel.add(placeLabel);
		birthPanel.add(wasBornOn, gbc);
		gbc.insets = new Insets(0, GAP_BETWEEN_COMPONENTS, 0, 0);
		gbc.gridx = 1;
		birthPanel.add(wasBornIn, gbc);

		loginButton = new JButton("Glimpse into the past!");
		loginButton.addActionListener(new LoginAction());
		loginButton.setFont(fieldFont);

		installLabel = new JLabel("First time in Atlas? Press install:", SwingConstants.CENTER);
		installLabel.setForeground(Color.white);
		installLabel.setFont(new Font("Century Gothic", Font.PLAIN, GraphicUtils.FONT_SIZE_FIELD));

		installButton = new JButton("Install DB");
		installButton.setFont(fieldFont);
		installButton.addActionListener(new DBFilesUploadListner());

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
		panel.add(birthPanel);
		panel.add(sexPanel);
		panel.add(loginButton);
		panel.add(installLabel);
		panel.add(installButton);
	}

	/**
	 * Clear text boxes listener
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
	 * Sex radio buttons listener
	 */
	private class SexListener implements ActionListener {

		boolean female;

		public SexListener(boolean female) {
			this.female = female;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			isMale.setSelected(!this.female);
			isFemale.setSelected(this.female);
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
				JOptionPane.showMessageDialog(null, "Please enter login credentials.", GraphicUtils.PROJECT_NAME, 1);
			} else if (username.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Username can not be blank.", GraphicUtils.PROJECT_NAME, 1);
			} else if (username.getText().length() > DBConstants.USERNAME_SIZE) {
				JOptionPane.showMessageDialog(null, "<HTML>Username can not exceed "+DBConstants.USERNAME_SIZE+" characters.</HTML>", GraphicUtils.PROJECT_NAME, 1);
			} else if (password.getPassword().length == 0) {
				JOptionPane.showMessageDialog(null, "Password can not be blank.", GraphicUtils.PROJECT_NAME, 1);
			} else if (password.getPassword().length  > DBConstants.PASSWORD_SIZE) {
				JOptionPane.showMessageDialog(null, "<HTML>Password can not exceed "+DBConstants.PASSWORD_SIZE+" characters.</HTML>", GraphicUtils.PROJECT_NAME, 1);
			} else if (DateUtils.isToday(wasBornOn.getCalendar()) && signupEnabled) {
				JOptionPane.showMessageDialog(null, "No way you were born today, enter a valid birthday.", GraphicUtils.PROJECT_NAME, 1);
			} else if (!options.contains(wasBornIn.getSelectedItem().toString())) { 
				JOptionPane.showMessageDialog(null,
						"Please choose a birth place that exists in the list.",
						GraphicUtils.PROJECT_NAME, 1);
			} else if (!isFemale.isSelected() && !isMale.isSelected() && signupEnabled) {
				JOptionPane.showMessageDialog(null, "Please choose male or female.", GraphicUtils.PROJECT_NAME, 1);
			} else {
				if (!signupEnabled) {
					// Create user
					User user = new User(username.getText(), String.copyValueOf(password.getPassword()));

					// Log in or sign up
					// Check if user already registered
					try {
						fetchedUser = Main.queries.fetchUser(user);
					} catch (AtlasServerException e) {
						// Server error
						JOptionPane.showMessageDialog(null, e.getMessage(), GraphicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
					}
					if (fetchedUser != null) {
						// Check password validity
						if (fetchedUser.getPassword().equals(user.getPassword())) {
							// Login
							loginSuccesful();
						} else {
							// Login failed
							JOptionPane.showMessageDialog(null,
									"Username/password combination not found.", GraphicUtils.PROJECT_NAME,
									JOptionPane.ERROR_MESSAGE);
						}
					} else {
						// Suggest to sign up
						int reply = JOptionPane.showConfirmDialog(null, "Unregistered user. Would you like to register?",
								GraphicUtils.PROJECT_NAME, JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							// Enable Sign up
							signupEnabled = true;
							wasBornIn.setEnabled(true);
							wasBornOn.setEnabled(true);
							isMale.setEnabled(true);
							isFemale.setEnabled(true);
						}
					}
				} else {
					// Sign Up
					
					fetchedUser = new User(username.getText(), String.copyValueOf(password.getPassword()), wasBornOn.getDate(),
							Queries.locationsMap.get(wasBornIn.getSelectedItem().toString()), isFemale.isSelected());
					boolean status;
					try {
						status = Main.queries.registerUser(fetchedUser);
						if (status) {
							JOptionPane.showMessageDialog(null, "You are now registered!");
							// Login
							loginSuccesful();
						} else {
							JOptionPane.showMessageDialog(null, "Failed to register. Try again later.", GraphicUtils.PROJECT_NAME,
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (AtlasServerException e) {
						JOptionPane.showMessageDialog(null, "Failed to register.\n" + e.getMessage(), GraphicUtils.PROJECT_NAME,
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	private void loginSuccesful() {

		// Store User
		Main.user = fetchedUser;

		// Close login screen
		setVisible(false);

		// Play audio
		Runnable r = new Runnable() {
			public void run() {
				new AudioUtils().playSound();
			}
		};
		new Thread(r).start();

		// Show map
		try {
			new MapScreen();
			
		} catch (Exception e) {
			log.error("", e);
			JOptionPane.showMessageDialog(null, e.getMessage() + ". Program exiting.", GraphicUtils.PROJECT_NAME,
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
