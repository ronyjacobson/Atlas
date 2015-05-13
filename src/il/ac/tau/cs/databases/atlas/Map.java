package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int NUM_OF_BUTTONS = 6;
	private static final int GAP_BETWEEN_COMPONENTS = 25;
	private static int width;
	private static int height;

	/**
	 * Creates and shows the map screen.
	 * 
	 * @throws IOException
	 */
	public Map() throws IOException {

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

		// Show map screen
		setVisible(true);
	}

	@SuppressWarnings("unchecked")
	private void addComponentsToPanel(Container pane) {

		// Create Components
		final MapBrowser map = new MapBrowser();
		
		// Define Layout
		pane.setLayout(new GridBagLayout());
		GridBagConstraints gBC = new GridBagConstraints();

		// Add Buttons
		gBC.fill = GridBagConstraints.HORIZONTAL;
		gBC.gridx = 0;
		gBC.gridy = 0;
		pane.add(createButtonsPanel());
			
		// Map
		gBC.ipady = 350; // This component has more breadth compared to other buttons
		gBC.weightx = 0.0;
		gBC.gridwidth = 3;
		gBC.gridx = 0;
		gBC.gridy = 1;
		pane.add(map, gBC);

		@SuppressWarnings("rawtypes")
		JComboBox jcmbSample = new JComboBox(new String[] { "ComboBox 1", "hi", "hello" });
		gBC.ipady = 0;
		gBC.weighty = 1.0;
		gBC.anchor = GridBagConstraints.PAGE_END;
		gBC.insets = new Insets(10, 0, 0, 0); // Padding
		gBC.gridx = 1;
		gBC.gridwidth = 2;
		gBC.gridy = 2;
		pane.add(jcmbSample, gBC);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// Dispose of the native component cleanly
				map.dispose();
			}
		});

		setVisible(true);

		// Initialize the native browser component, and if successful...
		if (map.initialise()) {
			// ...navigate to the desired URL
			map.setUrl("http://www.google.com/maps/");
		} else {
			System.out.println("Failed to initialise browser");
		}

	}

	private JPanel createButtonsPanel() {
		
		// Create buttons panel
		GridLayout buttonsPanelLayout = new GridLayout(1, NUM_OF_BUTTONS);
		buttonsPanelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
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

		// Create buttons and text boxes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, GrapicUtils.FONT_SIZE_FIELD);
		
		// Add a button for Category1 
		buttonCategory1 = new JButton("Category 1");
		buttonCategory1.setFont(fieldFont);
		buttonsPanel.add(buttonCategory1);
		// Add a button for Category2
		buttonCategory2 = new JButton("Category 2");
		buttonCategory2.setFont(fieldFont);
		buttonsPanel.add(buttonCategory2);
		// Add a button for Category3
		buttonCategory3 = new JButton("Category 3");
		buttonCategory3.setFont(fieldFont);
		buttonsPanel.add(buttonCategory3);
		// Add a button for Category4
		buttonCategory4 = new JButton("Category 4");
		buttonCategory4.setFont(fieldFont);
		buttonsPanel.add(buttonCategory4);
		// Add a button for adding values 
		buttonAdd = new JButton("Add");
		buttonAdd.setFont(fieldFont);
		buttonsPanel.add(buttonAdd);
		// Add a button for search 
		buttonSearch = new JButton("Search");
		buttonSearch.setFont(fieldFont);
		buttonsPanel.add(buttonSearch);
		
		// Return the panel
		return buttonsPanel;
	}
}
