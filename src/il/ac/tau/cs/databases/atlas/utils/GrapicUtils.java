package il.ac.tau.cs.databases.atlas.utils;

import il.ac.tau.cs.databases.atlas.State;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

public class GrapicUtils {

	public static Dimension screenSize;
	public static final int FONT_SIZE_LABEL = 36;
	public static final int FONT_SIZE_FIELD = 15;
	public static final int FONT_SIZE_DATE = 11;
	public final static String PROJECT_NAME = "ATLAS";
	public final static String DEFAULT_FILE_NAME = PROJECT_NAME + ".sqlproj";
	public final static String DEFAULT_SKIN = "graphics/skins/default/";
	public final static String PACKAGE_PATH = "/il/ac/tau/cs/databases/atlas/";
	public final static String RESOURCES_FOLDER = "/resources/";
	public final static String RESOURCES_MAP_FOLDER = RESOURCES_FOLDER + "map/";
	
	/**
	 * Returns the current skin.
	 */
	public static String getSkin(){
		return State.getData().getSkin();
	}
	
	/**
	 * Exit action listener to shutdown to program.
	 */
	public static class ExitActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				State.autoSave();
			} catch (Exception exception) {
				//new XDialog("Error", exception.getMessage());
			}
			System.exit(0);
		}
	}
	
	public static File GetFileFromDialog()
	{
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		  return fileChooser.getSelectedFile();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Exit mouse listener to shutdown to program.
	 */
	public static class ExitMouseListener implements MouseListener{

		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseUp(MouseEvent e) {
			try {
				State.autoSave();
			} catch (Exception exception) {
				//new XDialog("Error", exception.getMessage());
			}
			System.exit(0);
		}
	}

}


