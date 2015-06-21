package il.ac.tau.cs.databases.atlas.ui.listeners;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class DBFilesUploadListner implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		// Create a file chooser
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Does nothing here
			}
		});

		int status = fileChooser.showOpenDialog(null);

		if (status == JFileChooser.APPROVE_OPTION) {
			// Get files
			File fullPath = fileChooser.getSelectedFile();
			try {
				Main.queries.update(fullPath);
			} catch (AtlasServerException ase) {
				ase.printStackTrace();
				showErrorDialog(ase.getMessage());
			}
		} else if (status == JFileChooser.CANCEL_OPTION) {
			// Do nothing
		}

	}

	private void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(null, msg, GraphicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
	}

}
