package il.ac.tau.cs.databases.atlas.ui.screens;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.exception.PersonExistsError;
import il.ac.tau.cs.databases.atlas.db.queries.Queries;
import il.ac.tau.cs.databases.atlas.ui.listeners.MapBrowserListeners;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import com.toedter.calendar.JDateChooser;

/**
 * Create and show a add or edit screen.
 * 
 * @throws IOException
 */
public abstract class BaseModifyPersonScreen extends JFrame {
	
	protected final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final long serialVersionUID = 1L;
	protected int NUM_OF_COMPONENTS = 10;
	protected int GAP_BETWEEN_COMPONENTS = 16;
	protected static final String DEFAULT_BIRTH_LOCATION = "Choose place of birth...";
	protected static final String DEFAULT_DEATH_LOCATION = "Choose place of death...";
	private static final String DEFAULT_BIRTH_DATE = "Birth date:";
	private static final String DEFAULT_DEATH_DATE = "Death date (Optional):";
	protected static final String NOT_DEAD_LOCATION = "NOT DEAD";

	private JLabel label;
	protected JTextField name;
	protected JComboBox<String> category;
	private JPanel datesPanel;
	protected JDateChooser wasBornOn;
	protected JDateChooser hasDiedOn;
	private JPanel locationsPanel;
	protected JComboBox<String> wasBornIn;
	protected JComboBox<String> hasDiedIn;
	protected JTextField wikiLink;
	protected JRadioButton isMale;
	protected JRadioButton isFemale;
	private JPanel sexPanel;
	private JButton addButton;
	protected boolean wereDetailsEntered = false;
	public String stringName;
	List<String> locations;

	public BaseModifyPersonScreen() {
		logger.info("Creating Add/Edit Dialog window...");
		String addImagePath = GraphicUtils.getSkin() + "SecondaryScreen.png";
		int width;
		int height;
		// Get graphics attributes
		try {
			InputStream imageStream = getClass().getResourceAsStream(addImagePath);
			BufferedImage image = ImageIO.read(imageStream);
			width = image.getWidth();
			height = image.getHeight();
		} catch (IOException e) {
			logger.warn("Failed to load image ('" + addImagePath + "'), using default background instead");
			width = 487;
			height = 589;
		}

		// Set graphics
		URL imageURL = getClass().getResource(addImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(GraphicUtils.PROJECT_NAME);
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
		logger.info("Creating Add/Edit view...");
		createDialogPanel(panel, width, height);
		add(panel);

		// Show add screen
		logger.info("Making dialog visible...");
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
	private void createDialogPanel(JPanel panel, int width, int height) {

		// Make panel transparent
		panel.setOpaque(false);

		// Create buttons and text boxes
		Font labelFont = new Font("Century Gothic", Font.PLAIN,
				GraphicUtils.FONT_SIZE_LABEL);
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GraphicUtils.FONT_SIZE_FIELD);

		label = new JLabel(getTitleText(), SwingConstants.CENTER);
		label.setForeground(Color.DARK_GRAY);
		label.setFont(labelFont);

		name = new JTextField("Full Name");
		name.setFont(fieldFont);
		setCategoriesComboBox(fieldFont);

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
		wikiLink.setFont(fieldFont);

		addButton = new JButton(getButtonText());
		addButton.addActionListener(new AddAction());
		addButton.setFont(fieldFont);

		// Pad panel with blank labels
		JLabel paddingLabel1 = new JLabel(" ");
		paddingLabel1.setFont(labelFont);

		addClearTextBoxListenersIfNeeded();

		// Add buttons and text boxes
		panel.add(paddingLabel1); // Pad
		panel.add(label);
		panel.add(sexPanel);
		panel.add(name);
		addCategoryBarToPanel(panel);
		panel.add(datesPanel);
		panel.add(locationsPanel);
		panel.add(wikiLink);
		panel.add(addButton);
	}

	protected abstract String getTitleText();

	protected abstract void addClearTextBoxListenersIfNeeded();

	protected abstract void setCategoriesComboBox(Font fieldFont);

	protected abstract void addCategoryBarToPanel(JPanel panel);

	protected abstract String getButtonText();

	private void createDatesPanel() {
		logger.info("Making dates panel...");
		GridLayout panelLayout = new GridLayout(1, 1);
		panelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		datesPanel = new JPanel(panelLayout);

		GridLayout birthPanelLayout = new GridLayout(2, 1);
		birthPanelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		JPanel birthPanel = new JPanel(birthPanelLayout);

		GridLayout deathPanelLayout = new GridLayout(2, 1);
		deathPanelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		JPanel deathPanel = new JPanel(deathPanelLayout);

		// Make panel transparent
		datesPanel.setOpaque(false);
		birthPanel.setBackground(new Color(1f, 1f, 1f, 0.5f));
		deathPanel.setBackground(new Color(1f, 1f, 1f, 0.5f));

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GraphicUtils.FONT_SIZE_FIELD);

		// Create labels
		JLabel deathLabel = new JLabel(DEFAULT_DEATH_DATE, SwingConstants.LEFT);
		deathLabel.setForeground(Color.DARK_GRAY);
		deathLabel.setFont(fieldFont);
		JLabel birthLabel = new JLabel(DEFAULT_BIRTH_DATE, SwingConstants.LEFT);
		birthLabel.setForeground(Color.DARK_GRAY);
		birthLabel.setFont(fieldFont);

		// Create dates
		Date today = new Date();

		wasBornOn = new JDateChooser();
		wasBornOn.setDate(null);
		wasBornOn.setMaxSelectableDate(today);
		wasBornOn.setFont(fieldFont);

		hasDiedOn = new JDateChooser();
		hasDiedOn.setDate(null);
		hasDiedOn.setToolTipText(DEFAULT_DEATH_DATE);
		hasDiedOn.setMaxSelectableDate(today);
		hasDiedOn.setFont(fieldFont);

		// Add to panels

		birthPanel.add(birthLabel);
		deathPanel.add(deathLabel);
		birthPanel.add(wasBornOn);
		deathPanel.add(hasDiedOn);

		datesPanel.add(birthPanel);
		datesPanel.add(deathPanel);
		logger.info("Making dates panel done");
	}

	private void createLocationsPanel() {
		logger.info("Making locations panel...");
		GridLayout panelLayout = new GridLayout(1, 2);
		panelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		locationsPanel = new JPanel(panelLayout);

		// Make panel transparent
		locationsPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GraphicUtils.FONT_SIZE_FIELD);

		// Create location pickers
		try {
			locations = new ArrayList<String>(
					Main.queries.getAllGeoLocationsNames());
			
			locations.add(0, DEFAULT_BIRTH_LOCATION);
			wasBornIn = new JComboBox<String>(
					locations.toArray(new String[locations.size()]));
			wasBornIn.setEditable(true);
			AutoCompleteDecorator.decorate(wasBornIn);
			Dimension d= new Dimension(100,10);		
			wasBornIn.setPreferredSize(d);
			locations.set(0, DEFAULT_DEATH_LOCATION);
			locations.add(1, NOT_DEAD_LOCATION);
			hasDiedIn = new JComboBox<String>(
					locations.toArray(new String[locations.size()]));
			hasDiedIn.setEditable(true);
			AutoCompleteDecorator.decorate(hasDiedIn);
			hasDiedIn.setPreferredSize(d);

		} catch (AtlasServerException e) {
			logger.error("", e);
		}

		wasBornIn.setFont(fieldFont);
		hasDiedIn.setFont(fieldFont);
		// Add to panel
		locationsPanel.add(wasBornIn);
		locationsPanel.add(hasDiedIn);
		logger.info("Making locations panel done");
	}

	/**
	 * Clear text boxes
	 */
	protected class ClearTextBox implements MouseListener {

		private JTextField textField;
		public ClearTextBox(JTextField textField) {
			this.textField = textField;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
				textField.setText("");
				wereDetailsEntered = true;
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
			if (isInputValidated()) {
				dispose();
				MapBrowserListeners.showSpinner();
				try {
					// Get locations IDs
					Long birthLocationId = Queries.locationsMap.get(wasBornIn.getSelectedItem().toString());
					Long deathLocationId = (hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION)) ? null
							: Queries.locationsMap.get(hasDiedIn.getSelectedItem().toString());
					// Get dates
					Date birthDate = wasBornOn.getDate();
					Date deathDate = (deathLocationId == null) ? null
							: hasDiedOn.getDate();
					
					//get wikiLink
					String link = wikiLink.getText();
					if (!link.toLowerCase().contains("http://")) {
						link = "http://"+link;
					}
					

					execQuery(birthLocationId, deathLocationId, birthDate, deathDate, link);

					// Succeeded- Show message.
					showMessage();

				} catch (PersonExistsError e) {
					// User Already Exists - Show message.
					triggerJsCode("personExists('"
							+ name.getText() + "');");

				} catch (AtlasServerException e) {
					// Failed- Show message.
					triggerJsCode("showError(\""
							+ e.getMessage() + "\");");

				}
			}

		}
	}

	protected abstract void showMessage();

	protected void triggerJsCode(String code) {
		MapBrowserListeners.hideSpinner();
		MapBrowserListeners.executeJS(code);
	}

	protected abstract void execQuery(Long birthLocationId, Long deathLocationId, Date birthDate, Date deathDate, String link) throws AtlasServerException;

	protected abstract boolean isInputValidated();
}
