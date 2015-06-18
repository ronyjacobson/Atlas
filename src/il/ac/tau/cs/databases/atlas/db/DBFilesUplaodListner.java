package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.ParserConstants;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

public class DBFilesUplaodListner implements ActionListener {

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
			String fullPathString = fullPath.toString();
			try {
				Map<String,File> filesMap = checkAndGetFiles(fullPath);
				Main.queries.update(filesMap);
			} catch (AtlasServerException ase) {
				ase.printStackTrace();
				showErrorDialog(ase.getMessage());
			}
		} else if (status == JFileChooser.CANCEL_OPTION) {
			// Do nothing
		}

	}

	private Map<String,File> checkAndGetFiles(File fullPath) throws AtlasServerException {
		final File[] files = fullPath.listFiles();
		if (files == null) {
			throw new AtlasServerException(fullPath + ": The path doesn't exist");
		}
		Map<String, File> fileMap = new HashMap<>();
		Set<String> required = new HashSet<>(Arrays.asList(ParserConstants.REQUIRED_FILES));
		for (File file : files) {
			final String relevantFileName = file.getName();
			if (required.remove(relevantFileName)) {
				fileMap.put(relevantFileName, file);
			}
		}
		if (!required.isEmpty()) {
			String msg = "The following files are missing:";
			for (String missing : required) {
				msg += "\n" + missing;
			}
			throw new AtlasServerException(msg);
		}

		for (Map.Entry<String, File> stringFileEntry : fileMap.entrySet()) {
			final File file = stringFileEntry.getValue();
			if (!(file.exists() && !file.isDirectory() && file.canRead())) {
				throw new AtlasServerException("The file: " + stringFileEntry.getKey() + ", is not valid");
			}
		}
		return fileMap;
	}

	private void showErrorDialog(String msg) {
		JOptionPane.showMessageDialog(null, msg, GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
	}

}
