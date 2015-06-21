package il.ac.tau.cs.databases.atlas.ui.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {

	private static final long serialVersionUID = 5814113658041942393L;
	
	// Parameters
	private int category;
	private String skin;
	private List<String> skins;

	/**
	 * Construct a set of parameters
	 */
	public Data() {
		setCategory(0);
		setSkin(GraphicUtils.DEFAULT_SKIN); // Initialize default skin
		skins = new ArrayList<>();
		skins.add(GraphicUtils.DEFAULT_SKIN);
	}
		
	public int setCategory() {
		return this.category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getSkin() {
		return skin;
	}
	
	public void setSkin(String skin) {
		this.skin = skin;
	}
	
	public List<String> getSkins() {
		return skins;
	}

	public void addSkin(String skin) {
		this.skins.add(skin);
	}
}
