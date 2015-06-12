package il.ac.tau.cs.databases.atlas.graphics.map;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.Map;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JScrollBar;

import org.eclipse.swt.widgets.Display;

public class MapBrowserListeners {

	public volatile static MapBrowser map = null;

	public static void setMap(MapBrowser map) {
		MapBrowserListeners.map = map;
	}

	public static class BrowserMessageActionListener implements ActionListener {

		String msg;

		public BrowserMessageActionListener() {
			this.msg = "A browser error occurred.";
		}

		public BrowserMessageActionListener(String msg) {
			this.msg = msg;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						map.getBrowser().execute("error(\"" + msg + "\");");
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}

	public static class BrowserAddMarkerActionListener implements ActionListener {

		JScrollBar timeline;
		JComboBox<String> categoriesComboBox;

		public BrowserAddMarkerActionListener(JScrollBar timeline, JComboBox<String> categoriesComboBox) {
			this.timeline = timeline;
			this.categoriesComboBox = categoriesComboBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						int startYear = timeline.getModel().getValue();
						int endYear = timeline.getModel().getValue() + timeline.getModel().getExtent();
						String category = categoriesComboBox.getSelectedItem().toString();
						// Check category
						if (category.equals(Map.DEFAULT_CATEGORY)) {
							map.getBrowser().execute("error(\"" + "Please select a category." + "\");");
						} else if (category.equals(Map.FAVORITES_CATEGORY)){
							try {
								showResultsOnMap(Main.queries.getFavorites());
							} catch (AtlasServerException e) {
								e.printStackTrace();
							}
						} else {
							try {
								showResultsOnMap(Main.queries.getResults(startYear, endYear, category));
							} catch (AtlasServerException e) {
								e.printStackTrace();
							}
						}
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}
	
	public static void showResultsOnMap(List<Result> results) {
		map.getBrowser().execute("deleteMarkers();");
		for (Result result : results) {
			double lat = result.getLocation().getLat();
			double lng = result.getLocation().getLng();
			String imageIcon = "flag-";
			if (result.isBirth()) {
				imageIcon += "birth";
				// TODO can offset the coordinates a little so results in the same place
				// won't be in the exact mark on the map
			} else {
				imageIcon += "death";
				// TODO can offset the coordinates a little so results in the same place
				// won't be in the exact mark on the map
			}
			if (!result.getCategory().equalsIgnoreCase("")){
				// Check for existing category flag
				String categoryPostfix = result.getCategory().toLowerCase().replace(" ", "-");
				String flagFilename = imageIcon + "-" + categoryPostfix + ".png";
				String tempDir = System.getProperty("java.io.tmpdir");
				String flagFilePath = tempDir + flagFilename;
				File file = new File(flagFilePath);
				if(file.exists() && !file.isDirectory()) {
					imageIcon += "-" + categoryPostfix;
				}
			}
			imageIcon += ".png";
			map.getBrowser().execute(
					"addMarker(" + result.getID() + "," + lat + "," + lng + ",\""
							+ result.getName() + "\",\"" + imageIcon + "\",\"" + result.getSummary() + "\",\"" + result.getWikiLink()
							+ "\");");
		}
	}

	public static class BrowserDeleteMarkersActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						map.getBrowser().execute("deleteMarkers();");
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}
	
	public static class BrowserShowStatsActionListner implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						String msg = " Showing "
								+ Main.queries.getAmountOfLatestResults() + " results:\\n\\n " + Main.queries.getStatsOfLatestResults() + " Males\\n "
								+ (Main.queries.getAmountOfLatestResults() - Main.queries.getStatsOfLatestResults()) + " Females";
						map.getBrowser().execute("error(\"" + msg + "\");");
					} else {
						// TODO Show message?
					}
				}
			});

		}

	}

	public static class BrowserSyncFavoritesActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						String favorites = ((String) map.getBrowser().evaluate("return getFavorites()"));
						List<String> favoritesList = Arrays.asList(favorites.split(","));
						String msg;
						try {
							Main.queries.storeFavoriteIDs(favoritesList);
							msg = "Favorites synced to database successfully.";
						} catch (AtlasServerException e) {
							msg = "Favorites failed to synced to database.";
						}
						map.getBrowser().execute("error(\"" + msg + "\");");
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}

	public static class BrowserTimespanAdjustmentListener implements AdjustmentListener {

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						JScrollBar timeline = (JScrollBar) e.getAdjustable();
						int start = timeline.getValue();
						int end = timeline.getValue() + timeline.getModel().getExtent();
						setTimespan(start, end);
					} else {
						// TODO Show message?
					}
				}
			});

		}
	}

	public static void setTimespan(int startYear, int endYear) {
		map.getBrowser().execute("setTimespan(" + startYear + "," + endYear + ");");
	}
}
