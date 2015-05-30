package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.DBFilesUplaodListner;
import il.ac.tau.cs.databases.atlas.graphics.map.MapBrowser;
import il.ac.tau.cs.databases.atlas.graphics.map.MapBrowserListeners;
import il.ac.tau.cs.databases.atlas.utils.AudioUtils;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int GAP_BETWEEN_BUTTONS = 24;
	private static final int GAP_BETWEEN_COMPONENTS = 10;
	private static final int TIMELINE_MAX = 2015;
	private static final int TIMELINE_MIN = 1000;
	private static final int TIMELINE_EXTENT = 100;
	private static final int TIMELINE_INITIAL_VALUE = 1000;
	private static int width;
	private static int height;
	
	// Define Buttons, Map and Timeline
	private static final MapBrowser map = new MapBrowser();
	private static JButton buttonCategory1;
	private static JButton buttonCategory2;
	private static JButton buttonCategory3;
	private static JButton buttonCategory4;
	private static JButton buttonStats;
	private static JButton buttonAdd;
	private static JButton buttonSearch;
	private static JButton buttonUpdate;
	private static JButton buttonAudio;
	private static JScrollBar timeline = new JScrollBar(JScrollBar.HORIZONTAL, TIMELINE_INITIAL_VALUE, TIMELINE_EXTENT, TIMELINE_MIN, TIMELINE_MAX);;

	/**
	 * Creates and shows the map screen.
	 * 
	 * @throws Exception
	 */
	public Map() throws Exception {

		String mapImagePath = GrapicUtils.getSkin() + "Background.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(mapImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		width = image.getWidth();
		height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(mapImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(GrapicUtils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Set up the content pane.
		addComponentsToPanel(getContentPane());
		pack();

	}

	private void addComponentsToPanel(Container pane) throws Exception {

		// Define Layout
		pane.setLayout(new GridBagLayout());
		GridBagConstraints gBC = new GridBagConstraints();
		gBC.insets = new Insets(GAP_BETWEEN_COMPONENTS, 0, 0, 0); // Padding

		// Add Buttons
		gBC.gridx = 0;
		gBC.gridy = 1;
		pane.add(createButtonsPanel(), gBC);

		// Add Map
		gBC.fill = GridBagConstraints.HORIZONTAL;
		gBC.ipady = 300; // This component has more breadth compared to other
							// buttons
		gBC.weightx = 0.0;
		gBC.gridwidth = 3;
		gBC.gridx = 0;
		gBC.gridy = 2;
		pane.add(map, gBC);

		// Add Timeline
		timeline.addAdjustmentListener(new MapBrowserListeners.BrowserTimespanAdjustmentListener());
		gBC.ipady = 0;
		gBC.gridx = 0;
		gBC.gridwidth = 2;
		gBC.gridy = 3;
		pane.add(timeline, gBC);

		// Add close listener so map will be disposed on close
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Dispose of the native component cleanly
				map.dispose();
			}
		});

		// Set frame to be visible
		setVisible(true);

		// Initialize the browser
		if (map.initialize()) {
			// Navigate to the following URL
			map.setUrl(GrapicUtils.MAP_HTML_PATH);
			// Set map for browser actions
			MapBrowserListeners.setMap(map);
		} else {
			throw new MapBrowser.BrowserException();
		}

	}

	private JPanel createButtonsPanel() {
		// Create buttons panel
		FlowLayout buttonsPanelLayout = new FlowLayout(FlowLayout.CENTER, GAP_BETWEEN_BUTTONS, GAP_BETWEEN_COMPONENTS);
		JPanel buttonsPanel = new JPanel(buttonsPanelLayout);

		// Make panel transparent
		buttonsPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, GrapicUtils.FONT_SIZE_FIELD);
		Dimension dimensionCategory = new Dimension(width / 7, height / 13);
		Dimension dimensionStats = new Dimension((int) dimensionCategory.getWidth() / 3, (int) dimensionCategory.getHeight());
		Dimension dimensionOther = new Dimension((int) dimensionCategory.getWidth() / 3, (int) dimensionCategory.getHeight() / 2);

		// Create buttons
		List<String> categories = Main.queries.getAllCategoriesNames();
		// Add a button for Category1
		buttonCategory1 = new JButton(categories.get(0));
		buttonCategory1.setPreferredSize(dimensionCategory);
		buttonCategory1.setFont(fieldFont);
		buttonsPanel.add(buttonCategory1);
		// Add a button for Category2
		buttonCategory2 = new JButton(categories.get(1));
		buttonCategory2.setPreferredSize(dimensionCategory);
		buttonCategory2.setFont(fieldFont);
		buttonsPanel.add(buttonCategory2);
		// Add a button for Category3
		buttonCategory3 = new JButton(categories.get(2));
		buttonCategory3.setPreferredSize(dimensionCategory);
		buttonCategory3.setFont(fieldFont);
		buttonsPanel.add(buttonCategory3);
		// Add a button for Category4
		buttonCategory4 = new JButton(categories.get(3));
		buttonCategory4.setPreferredSize(dimensionCategory);
		buttonCategory4.setFont(fieldFont);
		buttonsPanel.add(buttonCategory4);
		// Add a button for statistics
		buttonStats = new JButton("");
		buttonStats.setIcon(new ImageIcon(getClass().getResource(GrapicUtils.getSkin() + "Stats.png")));
		buttonStats.setPreferredSize(dimensionStats);
		buttonStats.setFont(fieldFont);
		buttonsPanel.add(buttonStats);
		// Add a button for adding values
		buttonAdd = new JButton("");
		buttonAdd.setIcon(new ImageIcon(getClass().getResource(GrapicUtils.getSkin() + "Add.png")));
		buttonAdd.setPreferredSize(dimensionOther);
		buttonAdd.setFont(fieldFont);
		buttonsPanel.add(buttonAdd);
		// Add a button for search
		buttonSearch = new JButton("");
		buttonSearch.setIcon(new ImageIcon(getClass().getResource(GrapicUtils.getSkin() + "Search.png")));
		buttonSearch.setPreferredSize(dimensionOther);
		buttonSearch.setFont(fieldFont);
		buttonsPanel.add(buttonSearch);
		// Add a button for update
		buttonUpdate = new JButton("");
		buttonUpdate.setIcon(new ImageIcon(getClass().getResource(GrapicUtils.getSkin() + "Update.png")));
		buttonUpdate.setPreferredSize(dimensionOther);
		buttonUpdate.setFont(fieldFont);
		buttonsPanel.add(buttonUpdate);
		// Add a button for audio
		buttonAudio = new JButton("");
		buttonAudio.setIcon(new ImageIcon(getClass().getResource(GrapicUtils.getSkin() + "Audio.png")));
		buttonAudio.setPreferredSize(dimensionOther);
		buttonAudio.setFont(fieldFont);
		buttonsPanel.add(buttonAudio);
		
		// Add listeners
		buttonCategory1.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(timeline, buttonCategory1.getText()));
		buttonCategory2.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(timeline, buttonCategory2.getText()));
		buttonCategory3.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(timeline, buttonCategory3.getText()));
		buttonCategory4.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(timeline, buttonCategory4.getText()));
		buttonStats.addActionListener(new MapBrowserListeners.BrowserMessageActionListener(" Showing " + Main.queries.getAmountOfLatestResults() +  " results:\\n\\n " + Main.queries.getStatsOfLatestResults() + " Males\\n " + (Main.queries.getAmountOfLatestResults() - Main.queries.getStatsOfLatestResults()) + " Females"));
		buttonAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new Add();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Exception occured while using the add screen.", GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new Add();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Exception occured while using the search screen.", GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonUpdate.addActionListener(new DBFilesUplaodListner());
		buttonAudio.addActionListener(new AudioUtils.AudioToggleActionListener());

		// Return the panel
		return buttonsPanel;
	}
}
