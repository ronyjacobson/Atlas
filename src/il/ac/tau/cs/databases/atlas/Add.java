package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.Queries;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.graphics.map.MapBrowserListeners;
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
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.toedter.calendar.JDateChooser;

/**
 * Create and show a add screen.
 * 
 * @throws IOException
 */
public class Add extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int NUM_OF_COMPONENTS = 10;
	private static final int GAP_BETWEEN_COMPONENTS = 16;
	private static final String DEFAULT_CATEGORY = "Choose a category...";
	private static final String DEFAULT_BIRTH_LOCATION = "Choose birth place...";
	private static final String DEFAULT_DEATH_LOCATION = "Choose death place...";
	private static final String DEFAULT_BIRTH_DATE = "Enter birthday";
	private static final String DEFAULT_DEATH_DATE = "Enter death date";
	private static final String NOT_DEAD_LOCATION = "NOT DEAD";

	private JLabel label;
	private JTextField name;
	private JComboBox<String> category;
	private JPanel datesPanel;
	private JDateChooser wasBornOn;
	private JDateChooser hasDiedOn;
	private JPanel locationsPanel;
	private JComboBox<String> wasBornIn;
	private JComboBox<String> hasDiedIn;
	private JTextField wikiLink;
	private JRadioButton isMale;
	private JRadioButton isFemale;
	private JPanel sexPanel;
	private JButton addButton;
	private boolean wereDetailsEntered = false;
	private String status = "";
	public String stringName;

	public Add() throws IOException {

		String addImagePath = GrapicUtils.getSkin() + "SecondaryScreen.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(addImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(addImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(GrapicUtils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Add add panel
		setLayout(new BorderLayout());
		setLayout(new FlowLayout());
		GridLayout panelLayout = new GridLayout(NUM_OF_COMPONENTS, 1);
		panelLayout.setVgap(GAP_BETWEEN_COMPONENTS);
		JPanel panel = new JPanel(panelLayout);
		createAddPanel(panel, width, height);
		add(panel);

		// Show add screen
		setVisible(true);

	}

	/**
	 * Create and fill the add panel
	 * 
	 * @param panel
	 *            The panel to fill
	 * @param width
	 *            The parent window width
	 * @param height
	 *            The parent window height
	 */
	private void createAddPanel(JPanel panel, int width, int height) {

		// Make panel transparent
		panel.setOpaque(false);

		// Create buttons and text boxes
		ClearTextBox clearTextBoxListner = new ClearTextBox();
		Font labelFont = new Font("Century Gothic", Font.PLAIN,
				GrapicUtils.FONT_SIZE_LABEL);
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GrapicUtils.FONT_SIZE_FIELD);

		label = new JLabel("Add a new person:", SwingConstants.CENTER);
		label.setForeground(Color.DARK_GRAY);
		label.setFont(labelFont);

		name = new JTextField("Full Name");
		name.addMouseListener(clearTextBoxListner);
		name.setFont(fieldFont);
		try {
			List<String> categories = Main.queries.getAllCategoriesNames();
			categories.add(0, DEFAULT_CATEGORY);
			category = new JComboBox<String>(
					categories.toArray(new String[categories.size()]));
			category.setFont(fieldFont);
		} catch (AtlasServerException e) {
			// TODO handle exception
		}

		isMale = new JRadioButton("Male");
		isMale.setFont(fieldFont);
		isMale.setOpaque(false);
		isMale.setForeground(Color.DARK_GRAY);
		isMale.addActionListener(new SexListener(false));
		isFemale = new JRadioButton("Female");
		isFemale.setFont(fieldFont);
		isFemale.setOpaque(false);
		isFemale.setForeground(Color.DARK_GRAY);
		isFemale.addActionListener(new SexListener(true));

		sexPanel = new JPanel();
		sexPanel.add(isMale);
		sexPanel.add(isFemale);
		sexPanel.setOpaque(false);

		createDatesPanel();

		createLocationsPanel();

		wikiLink = new JTextField("Link to wikipedia...");
		wikiLink.addMouseListener(clearTextBoxListner);
		wikiLink.setFont(fieldFont);

		addButton = new JButton("Add to ATLAS!");
		addButton.addActionListener(new AddAction());
		addButton.setFont(fieldFont);

		// Pad panel with blank labels
		JLabel paddingLabel1 = new JLabel(" ");
		paddingLabel1.setFont(labelFont);

		// Add buttons and text boxes
		panel.add(paddingLabel1); // Pad
		panel.add(label);
		panel.add(sexPanel);
		panel.add(name);
		panel.add(category);
		panel.add(datesPanel);
		panel.add(locationsPanel);
		panel.add(wikiLink);
		panel.add(addButton);
	}

	private void createDatesPanel() {

		GridLayout panelLayout = new GridLayout(1, 2);
		panelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		datesPanel = new JPanel(panelLayout);

		// Make panel transparent
		datesPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GrapicUtils.FONT_SIZE_FIELD);

		// Create dates
		Date today = new Date();
		ClearTextBox clearTextBoxListner = new ClearTextBox();

		wasBornOn = new JDateChooser();
		wasBornOn.setToolTipText(DEFAULT_BIRTH_DATE);
		wasBornOn.setDate(null);
		wasBornOn.setMaxSelectableDate(today);
		wasBornOn.addMouseListener(clearTextBoxListner);
		wasBornOn.setFont(fieldFont);

		hasDiedOn = new JDateChooser();
		hasDiedOn.setDate(null);
		hasDiedOn.setToolTipText(DEFAULT_DEATH_DATE);
		hasDiedOn.setMaxSelectableDate(today);
		hasDiedOn.addMouseListener(clearTextBoxListner);
		hasDiedOn.setFont(fieldFont);

		// Add to panel
		datesPanel.add(wasBornOn);
		datesPanel.add(hasDiedOn);

	}

	private void createLocationsPanel() {

		GridLayout panelLayout = new GridLayout(1, 2);
		panelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		locationsPanel = new JPanel(panelLayout);

		// Make panel transparent
		locationsPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GrapicUtils.FONT_SIZE_FIELD);

		// Create location pickers
		try {
			List<String> locations = new ArrayList<String>(
					Main.queries.getAllGeoLocationsNames());

			locations.add(0, DEFAULT_BIRTH_LOCATION);
			wasBornIn = new JComboBox<String>(
					locations.toArray(new String[locations.size()]));

			locations.set(0, DEFAULT_DEATH_LOCATION);
			locations.add(1, NOT_DEAD_LOCATION);
			hasDiedIn = new JComboBox<String>(
					locations.toArray(new String[locations.size()]));

		} catch (AtlasServerException e) {
			// TODO handle Exception
		}

		wasBornIn.setFont(fieldFont);
		hasDiedIn.setFont(fieldFont);
		// Add to panel
		locationsPanel.add(wasBornIn);
		locationsPanel.add(hasDiedIn);

	}

	/**
	 * Clear text boxes
	 */
	private class ClearTextBox implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (!wereDetailsEntered) {
				name.setText("");
				wikiLink.setText("");
				wereDetailsEntered = true;
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
			if (!wereDetailsEntered) {
				name.setText("");
				wikiLink.setText("");
				wereDetailsEntered = true;
			}
		}
	}

	/**
	 * Add an entry to the database
	 */
	private class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Validate input
			if (!wereDetailsEntered) {
				JOptionPane.showMessageDialog(null,
						"Please enter the needed details.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (!isFemale.isSelected() && !isMale.isSelected()) {
				JOptionPane.showMessageDialog(null,
						"Please choose male or female.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (name.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Name can not be blank.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (category.getSelectedItem().toString()
					.equals(DEFAULT_CATEGORY)) {
				JOptionPane.showMessageDialog(null,
						"Please choose a category place from the list.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (wasBornOn.getCalendar() == null) {
				JOptionPane.showMessageDialog(null,
						"Please choose a birth date", GrapicUtils.PROJECT_NAME,
						1);
			} else if (hasDiedOn.getCalendar() != null
					&& DateUtils.isSameDay(wasBornOn.getCalendar(),
							hasDiedOn.getCalendar())) {
				JOptionPane.showMessageDialog(null,
						"No way that the birth and death dates are the same.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (hasDiedOn.getCalendar() != null
					&& DateUtils.isAfterDay(wasBornOn.getCalendar(),
							hasDiedOn.getCalendar())) {
				JOptionPane.showMessageDialog(null,
						"No way that the birth date is after the death date.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (wasBornIn.getSelectedItem().toString()
					.equals(DEFAULT_BIRTH_LOCATION)) {
				JOptionPane.showMessageDialog(null,
						"Please choose a birth place from the list.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (hasDiedIn.getSelectedItem().toString()
					.equals(DEFAULT_DEATH_LOCATION)) {
				JOptionPane.showMessageDialog(null,
						"Please choose a death place from the list.",
						GrapicUtils.PROJECT_NAME, 1);
			} else if (wikiLink.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						"Wikipedia link can not be blank.",
						GrapicUtils.PROJECT_NAME, 1);
			} else {
				try {
					// Get locations IDs
					Integer birthLocaionID = Queries.locationsMap.get(wasBornIn
							.getSelectedItem().toString());
					Integer deathLocaionID = (hasDiedIn.getSelectedItem()
							.toString().equals(NOT_DEAD_LOCATION)) ? null
							: Queries.locationsMap.get(hasDiedIn
									.getSelectedItem().toString());
					// Get dates
					Date birthDate = wasBornOn.getDate();
					Date deathDate = (hasDiedOn.getCalendar() == null) ? null
							: hasDiedOn.getDate();
					// Add entry
					stringName = name.getText();
					Main.queries.addNew(stringName, category.getSelectedItem()
							.toString(), birthDate, birthLocaionID, deathDate,
							deathLocaionID, wikiLink.getText(), isFemale
									.isSelected());
					status = "";
					JOptionPane.showMessageDialog(null, "New entry added to the data base.",
							GrapicUtils.PROJECT_NAME,
							JOptionPane.INFORMATION_MESSAGE);
				} catch (AtlasServerException e) {
					JOptionPane
							.showMessageDialog(
									null,
									"Failed to add new entry: " + e.getMessage() + ".",
									GrapicUtils.PROJECT_NAME,
									JOptionPane.ERROR_MESSAGE);
					status = e.getMessage();
					// Close the windows
					dispose();
				}
			}
		}
	}

	public String getStatus() {
		return this.status;
	}

	public String getName() {
		return stringName;
	}
}
