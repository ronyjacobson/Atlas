package il.ac.tau.cs.databases.atlas;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import il.ac.tau.cs.databases.atlas.graphics.Utils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates and shows the map screen.
	 * 
	 * @throws IOException
	 */
	public Map() throws IOException {

		String mapImagePath = Utils.getSkin() + "Background.png";

		// Get graphics attributes
		InputStream imageStream = getClass().getResourceAsStream(mapImagePath);
		BufferedImage image = ImageIO.read(imageStream);
		int width = image.getWidth();
		int height = image.getHeight();

		// Set graphics
		URL imageURL = getClass().getResource(mapImagePath);
		setContentPane(new JLabel(new ImageIcon(imageURL)));
		setSize(width, height);
		setTitle(Utils.PROJECT_NAME);
		setLocationRelativeTo(null);

		// Set Actions
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//Set up the content pane.
        addComponentsToPanel(getContentPane());
        pack();
        
		// Show map screen
		setVisible(true);
	}

	private void addComponentsToPanel(Container pane) {

        JButton jbnButton;
        pane.setLayout(new GridBagLayout());
        GridBagConstraints gBC = new GridBagConstraints();
        gBC.fill = GridBagConstraints.HORIZONTAL;

        jbnButton = new JButton("Button 1");
        gBC.weightx = 0.5;
        gBC.gridx = 0;
        gBC.gridy = 0;
        pane.add(jbnButton, gBC);

        JTextField jtf = new JTextField("TextField 1");
        gBC.gridx = 2;
        gBC.gridy = 0;
        jtf.setEditable(false);
        pane.add(jtf, gBC);

        jbnButton = new JButton("Button 3");
        gBC.gridx = 2;
        gBC.gridy = 0;
        pane.add(jbnButton, gBC);

        jbnButton = new JButton("Button 4");
        gBC.ipady = 40;     //This component has more breadth compared to other buttons
        gBC.weightx = 0.0;
        gBC.gridwidth = 3;
        gBC.gridx = 0;
        gBC.gridy = 1;
        pane.add(jbnButton, gBC);

        JComboBox jcmbSample = new JComboBox(new String[]{"ComboBox 1", "hi", "hello"});
        gBC.ipady = 0;
        gBC.weighty = 1.0;
        gBC.anchor = GridBagConstraints.PAGE_END;
        gBC.insets = new Insets(10,0,0,0);  //Padding
        gBC.gridx = 1;
        gBC.gridwidth = 2;
        gBC.gridy = 2;
        pane.add(jcmbSample, gBC);
 
	}
}
