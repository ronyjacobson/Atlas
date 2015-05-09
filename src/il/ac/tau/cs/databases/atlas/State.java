package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class State {

	// Define the program parameters
	private static Data data = new Data();

	/**
	 * Get program's data.
	 * @return Program's data parameters
	 */
	public static Data getData() {
		return data;
	}

	// Save/Load functions
	public static void save() throws Exception {

	}

	public static void load() throws Exception {

	}

	public static void autoSave() throws Exception {
		try {
			// Serialize data object to a file
			FileOutputStream fileOut = new FileOutputStream(GrapicUtils.DEFAULT_FILE_NAME);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(data);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			throw new Exception("Failed to save state!");
		}
	}

	public static void autoLoad() throws Exception {
		try {
			// Deserialize data object from a file
			FileInputStream fileIn = new FileInputStream(GrapicUtils.DEFAULT_FILE_NAME);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			data = (Data) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException e) {
			//TODO throw new Exception("Failed to load state!");
		}
	}
}
