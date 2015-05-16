package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.graphics.map.MapBrowser;
import il.ac.tau.cs.databases.atlas.graphics.map.MapBrowserListeners;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int GAP_BETWEEN_BUTTONS = 45;
	private static final int GAP_BETWEEN_COMPONENTS = 10;
	private static int width;
	private static int height;
	private static final MapBrowser map = new MapBrowser();

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
		pane.add(createButtonsPanel());

		// Add Map
		gBC.fill = GridBagConstraints.HORIZONTAL;
		gBC.ipady = 300; // This component has more breadth compared to other
							// buttons
		gBC.weightx = 0.0;
		gBC.gridwidth = 3;
		gBC.gridx = 0;
		gBC.gridy = 2;
		pane.add(map, gBC);

		JScrollBar timeline = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 100);
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

		// Define Buttons
		JButton buttonCategory1;
		JButton buttonCategory2;
		JButton buttonCategory3;
		JButton buttonCategory4;
		JButton buttonAdd;
		JButton buttonSearch;

		// Make panel transparent
		buttonsPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, GrapicUtils.FONT_SIZE_FIELD);
		Dimension dimensionCategory = new Dimension(width / 7, height / 13);
		Dimension dimensionOther = new Dimension((int) dimensionCategory.getWidth() / 3, (int) dimensionCategory.getHeight());

		// Create buttons
		// Add a button for Category1
		buttonCategory1 = new JButton("Category 1");
		buttonCategory1.setPreferredSize(dimensionCategory);
		buttonCategory1.setFont(fieldFont);
		buttonsPanel.add(buttonCategory1);
		// Add a button for Category2
		buttonCategory2 = new JButton("Category 2");
		buttonCategory2.setPreferredSize(dimensionCategory);
		buttonCategory2.setFont(fieldFont);
		buttonsPanel.add(buttonCategory2);
		// Add a button for Category3
		buttonCategory3 = new JButton("Category 3");
		buttonCategory3.setPreferredSize(dimensionCategory);
		buttonCategory3.setFont(fieldFont);
		buttonsPanel.add(buttonCategory3);
		// Add a button for Category4
		buttonCategory4 = new JButton("Category 4");
		buttonCategory4.setPreferredSize(dimensionCategory);
		buttonCategory4.setFont(fieldFont);
		buttonsPanel.add(buttonCategory4);
		// Add a button for adding values
		buttonAdd = new JButton("+");
		buttonAdd.setPreferredSize(dimensionOther);
		buttonAdd.setFont(fieldFont);
		buttonsPanel.add(buttonAdd);
		// Add a button for search
		buttonSearch = new JButton("@");
		buttonSearch.setPreferredSize(dimensionOther);
		buttonSearch.setFont(fieldFont);
		buttonsPanel.add(buttonSearch);

		// Add listeners
		buttonCategory1.addActionListener(new MapBrowserListeners.BrowserErrorActionListener());
		buttonCategory2.addActionListener(new MapBrowserListeners.BrowserErrorActionListener("My Error"));
		buttonCategory3.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(3));
		buttonCategory4.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(4));
		buttonAdd.addActionListener(new MapBrowserListeners.BrowserDeleteMarkersActionListener());

		// Return the panel
		return buttonsPanel;
	}
}
