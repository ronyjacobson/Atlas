package il.ac.tau.cs.databases.atlas;

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
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.swt.widgets.Display;

import com.toedter.calendar.JDateChooser;

/**
 * Create and show a add screen.
 * 
 * @throws IOException
 */
public class Search extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int NUM_OF_COMPONENTS = 10;
	private static final int GAP_BETWEEN_COMPONENTS = 16;

	private JLabel nameLabel;
	private JTextField name;
	private JButton searchNameButton;
	private JLabel datesLabel;
	private JPanel datesPanel;
	private JDateChooser fromDate;
	private JDateChooser untilDate;
	private JButton searchDatesButton;
	private boolean wasNameEntered = false;

	public Search() throws IOException {

		String searchImagePath = GrapicUtils.getSkin() + "SecondaryScreen.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(searchImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(searchImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(GrapicUtils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Add search panel
		setLayout(new BorderLayout());
		setLayout(new FlowLayout());
		GridLayout panelLayout = new GridLayout(NUM_OF_COMPONENTS, 1);
		panelLayout.setVgap(GAP_BETWEEN_COMPONENTS);
		JPanel panel = new JPanel(panelLayout);
		createSearchPanel(panel, width, height);
		add(panel);

		// Show add screen
		setVisible(true);

	}

	/**
	 * Create and fill the search panel
	 * 
	 * @param panel
	 *            The panel to fill
	 * @param width
	 *            The parent window width
	 * @param height
	 *            The parent window height
	 */
	private void createSearchPanel(JPanel panel, int width, int height) {

		// Make panel transparent
		panel.setOpaque(false);

		// Create buttons and text boxes
		ClearTextBox clearTextBoxListner = new ClearTextBox();
		Font labelFont = new Font("Century Gothic", Font.PLAIN, GrapicUtils.FONT_SIZE_LABEL);
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, GrapicUtils.FONT_SIZE_FIELD);

		nameLabel = new JLabel("Search by name:");
		nameLabel.setForeground(Color.DARK_GRAY);
		nameLabel.setFont(labelFont);

		name = new JTextField("Full Name");
		name.addMouseListener(clearTextBoxListner);
		name.setFont(fieldFont);

		searchNameButton = new JButton("Search ATLAS");
		searchNameButton.addActionListener(new SearchNameAction());
		searchNameButton.setFont(fieldFont);
		
		datesLabel = new JLabel("Search by dates:");
		datesLabel.setForeground(Color.DARK_GRAY);
		datesLabel.setFont(labelFont);
		
		createDatesPanel();
		
		searchDatesButton = new JButton("Search ATLAS");
		searchDatesButton.addActionListener(new SearchDatesAction());
		searchDatesButton.setFont(fieldFont);

		// Pad panel with blank labels
		JLabel paddingLabel1 = new JLabel(" ");
		JLabel paddingLabel2 = new JLabel(" ");
		paddingLabel1.setFont(labelFont);
		paddingLabel2.setFont(labelFont);

		// Add buttons and text boxes
		panel.add(paddingLabel1); // Pad
		panel.add(nameLabel);
		panel.add(name);
		panel.add(searchNameButton);
		panel.add(paddingLabel2);
		panel.add(datesLabel);
		panel.add(datesPanel);
		panel.add(searchDatesButton);
	}

	private void createDatesPanel() {

		GridLayout panelLayout = new GridLayout(1, 2);
		panelLayout.setHgap(GAP_BETWEEN_COMPONENTS);
		datesPanel = new JPanel(panelLayout);

		// Make panel transparent
		datesPanel.setOpaque(false);

		// Define buttons attributes
		Font fieldFont = new Font("Century Gothic", Font.PLAIN, GrapicUtils.FONT_SIZE_FIELD);

		// Create dates
		Date today = new Date();
		ClearTextBox clearTextBoxListner = new ClearTextBox();

		fromDate = new JDateChooser();
		fromDate.setDate(today);
		fromDate.setMaxSelectableDate(today);
		fromDate.addMouseListener(clearTextBoxListner);
		fromDate.setFont(fieldFont);

		untilDate = new JDateChooser();
		untilDate.setDate(today);
		untilDate.setMaxSelectableDate(today);
		untilDate.addMouseListener(clearTextBoxListner);
		untilDate.setFont(fieldFont);

		// Add to panel
		datesPanel.add(fromDate);
		datesPanel.add(untilDate);

	}
	/**
	 * Clear text boxes
	 */
	private class ClearTextBox implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (!wasNameEntered) {
				name.setText("");
				wasNameEntered = true;
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
	 * Search an entry in the database by name
	 */
	private class SearchNameAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Validate input
			if (!wasNameEntered || name.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the needed details.", GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
			} else {
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	try {
				    		MapBrowserListeners.showResultsOnMap(Main.queries.getResults(name.getText()));
				    		MapBrowserListeners.setTimespan(Main.queries.getLatestResultsStartTimeLine(), Main.queries.getLatestResultsEndTimeLine());
						} catch (AtlasServerException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e.getMessage(), GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
				    }
				});
				dispose();
			}
		}
	}
	
	/**
	 * Search an entry in the database by dates
	 */
	private class SearchDatesAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Validate input
			if (DateUtils.isAfterDay(fromDate.getCalendar(), untilDate.getCalendar())) {
				JOptionPane.showMessageDialog(null, "Please enter a valid dates range.", GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
			} else {
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	try {
				    		MapBrowserListeners.showResultsOnMap(Main.queries.getResults(fromDate.getCalendar().getTime(), untilDate.getCalendar().getTime()));
				    		MapBrowserListeners.setTimespan(fromDate.getCalendar().get(Calendar.YEAR), untilDate.getCalendar().get(Calendar.YEAR));
				    	} catch (AtlasServerException e) {
				    		// TODO handle Exception
				    	}
				    }
				});
				dispose();
			}
		}
	}
}
