package il.ac.tau.cs.databases.atlas.ui.screens;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPool;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.ui.listeners.DBFilesUploadListner;
import il.ac.tau.cs.databases.atlas.ui.listeners.MapBrowserListeners;
import il.ac.tau.cs.databases.atlas.ui.map.MapBrowser;
import il.ac.tau.cs.databases.atlas.ui.utils.AudioUtils;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Main application UI screen
 * */
public class MapScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int GAP_BETWEEN_BUTTONS = 15;
	private static final int GAP_BETWEEN_COMPONENTS = 10;
	private static final int TIMELINE_MAX = 2015;
	private static final int TIMELINE_MIN = 1000;
	private static int TIMELINE_EXTENT = 100;
	private static final int TIMELINE_INITIAL_VALUE = 1000;
	public static final String DEFAULT_CATEGORY = "Choose a category...";
	public static final String FAVORITES_CATEGORY = "Favorites";
	private static final String GO_BUTTON = "GO!";
	private static int width;
	private static int height;

	// Define Buttons, Map and Timeline
	private static final MapBrowser map = new MapBrowser();
	private static JComboBox<String> categoriesComboBox;
	private static JButton buttonGo;
	private static JButton buttonStats;
	private static JButton buttonAdd;
	private static JButton buttonSearch;
	private static JButton buttonUpdateFavorites;
	private static JButton buttonUpdateDBFiles;
	private static JButton buttonAudio;

	private static JButton buttonDisplayAsTable;
	private static JScrollBar timeline = new JScrollBar(JScrollBar.HORIZONTAL,
			TIMELINE_INITIAL_VALUE, TIMELINE_EXTENT, TIMELINE_MIN, TIMELINE_MAX);
	private final Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Creates and shows the map screen.
	 * 
	 * @throws Exception
	 */
	public MapScreen() throws Exception {

		String mapImagePath = GraphicUtils.getSkin() + "Background.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(mapImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		width = image.getWidth();
		height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(mapImagePath);
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
				log.info("Closing map window");
				final ConnectionPool connectionPool = ConnectionPoolHolder.INSTANCE
						.get();
				if (connectionPool != null) {
					connectionPool.close();
				}
			}
		});

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
			String mapURL = GraphicUtils.RESOURCES_MAP_FOLDER + "map.html";

			// Create a temporary map HTML file in the file system
			InputStream in = MapScreen.class.getResourceAsStream(mapURL);
			File temp = File.createTempFile("mapAtlas", ".html");
			try (FileOutputStream out = new FileOutputStream(temp)) {
				IOUtils.copy(in, out);
				out.close();
			}
			in.close();
			temp.deleteOnExit();
			URL url = temp.toURI().toURL();
			// Set browser to show the map
			map.setUrl(url.toString());
			// Set map for browser actions
			MapBrowserListeners.setMap(map);

			// Create temporary flag PNG files
			String[] pngFiles = { "favorite.png", "addfavorite.png",
					"removefavorite.png", "flag-birth-favorites.png",
					"flag-birth-monarchist.png", "flag-birth-philosopher.png",
					"flag-birth-scientist.png", "flag-birth.png",
					"flag-death-favorites.png", "flag-death-monarchist.png",
					"flag-death-philosopher.png", "flag-death-scientist.png",
					"flag-death.png" };
			String tempDir = System.getProperty("java.io.tmpdir");
			for (String pngFile : pngFiles) {
				String pngURL = GraphicUtils.RESOURCES_MAP_FOLDER + pngFile;
				in = MapScreen.class.getResourceAsStream(pngURL);
				temp = new File(tempDir + "/" + pngFile);
				try (FileOutputStream out = new FileOutputStream(temp)) {
					IOUtils.copy(in, out);
					out.close();
				}
				in.close();
				temp.deleteOnExit();
			}

		} else {
			throw new MapBrowser.BrowserException();
		}

	}

	private JPanel createButtonsPanel() {
		// Create buttons panel
		FlowLayout buttonsPanelLayout = new FlowLayout(FlowLayout.CENTER,
				GAP_BETWEEN_BUTTONS, GAP_BETWEEN_COMPONENTS);
		JPanel buttonsPanel = new JPanel(buttonsPanelLayout);

		// Make panel transparent
		buttonsPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN,
				GraphicUtils.FONT_SIZE_FIELD);
		Dimension dimensionCategory = new Dimension(width / 7, height / 13);
		Dimension dimensionGo = new Dimension(width / 15, height / 13);
		Dimension dimensionOther = new Dimension(
				(int) dimensionCategory.getWidth() / 3,
				(int) dimensionCategory.getHeight() / 2);

		// Add categories combo box
		try {
			List<String> categories = Main.queries.getAllCategoriesNames();
			categories.add(0, DEFAULT_CATEGORY);
			categories.add(1, FAVORITES_CATEGORY);
			categoriesComboBox = new JComboBox<String>(
					categories.toArray(new String[categories.size()]));
			categoriesComboBox.setFont(fieldFont);
			categoriesComboBox.setPreferredSize(dimensionCategory);
			buttonsPanel.add(categoriesComboBox);
		} catch (AtlasServerException e) {
			log.error("", e);
		}

		// Add a GO button
		buttonGo = new JButton(GO_BUTTON);
		buttonGo.setPreferredSize(dimensionGo);
		buttonGo.setFont(fieldFont);
		buttonsPanel.add(buttonGo);

		// Add an Edit button
		buttonDisplayAsTable = new JButton("");
		buttonDisplayAsTable.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Edit.png")));
		buttonDisplayAsTable.setPreferredSize(dimensionOther);
		buttonDisplayAsTable.setFont(fieldFont);
		buttonDisplayAsTable.setEnabled(false);
		buttonsPanel.add(buttonDisplayAsTable);

		// Add a button for statistics
		buttonStats = new JButton("");
		buttonStats.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Stats.png")));
		buttonStats.setPreferredSize(dimensionOther);
		buttonStats.setFont(fieldFont);
		buttonsPanel.add(buttonStats);

		// Add a button for adding values
		buttonAdd = new JButton("");
		buttonAdd.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Add.png")));
		buttonAdd.setPreferredSize(dimensionOther);
		buttonAdd.setFont(fieldFont);
		buttonsPanel.add(buttonAdd);

		// Add a button for search
		buttonSearch = new JButton("");
		buttonSearch.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Search.png")));
		buttonSearch.setPreferredSize(dimensionOther);
		buttonSearch.setFont(fieldFont);
		buttonsPanel.add(buttonSearch);

		// Add a button for favorites sync
		buttonUpdateFavorites = new JButton("");
		buttonUpdateFavorites.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Update.png")));
		buttonUpdateFavorites.setPreferredSize(dimensionOther);
		buttonUpdateFavorites.setFont(fieldFont);
		buttonsPanel.add(buttonUpdateFavorites);

		// Add a button for db files update
		buttonUpdateDBFiles = new JButton("");
		buttonUpdateDBFiles.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Upload.png")));
		buttonUpdateDBFiles.setPreferredSize(dimensionOther);
		buttonUpdateDBFiles.setFont(fieldFont);
		buttonsPanel.add(buttonUpdateDBFiles);

		// Add a button for audio
		buttonAudio = new JButton("");
		buttonAudio.setIcon(new ImageIcon(getClass().getResource(
				GraphicUtils.getSkin() + "Audio.png")));
		buttonAudio.setPreferredSize(dimensionOther);
		buttonAudio.setFont(fieldFont);
		buttonsPanel.add(buttonAudio);

		// Add listeners
		buttonGo.addActionListener(new MapBrowserListeners.BrowserAddMarkerActionListener(
				timeline, categoriesComboBox));
		buttonStats
				.addActionListener(new MapBrowserListeners.BrowserShowStatsActionListner());
		buttonAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AddPersonScreen();
			}
		});
		buttonSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new SearchScreen();
				} catch (IOException e) {
					MapBrowserListeners
							.executeJS("showError(\"Exception occured while using the search screen.\");");
				}
			}
		});
		buttonUpdateFavorites
				.addActionListener(new MapBrowserListeners.BrowserSyncFavoritesActionListener());
		buttonUpdateDBFiles.addActionListener(new DBFilesUploadListner());
		buttonAudio
				.addActionListener(new AudioUtils.AudioToggleActionListener());

		buttonDisplayAsTable
				.addActionListener(new MapBrowserListeners.UpdateListener());

		// Return the panel
		return buttonsPanel;
	}

	public static void setButtonDisplayAsTableEnabled() {
		MapScreen.buttonDisplayAsTable.setEnabled(true);
	}

	public static void setButtonDisplayAsTableDisabled() {
		MapScreen.buttonDisplayAsTable.setEnabled(false);
	}

	public static void setTimelineExtent(int ext) {
		TIMELINE_EXTENT = ext;
		timeline.getModel().setExtent(ext);
	}
}
