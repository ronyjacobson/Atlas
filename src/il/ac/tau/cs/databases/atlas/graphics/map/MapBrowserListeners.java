package il.ac.tau.cs.databases.atlas.graphics.map;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.Map;
import il.ac.tau.cs.databases.atlas.PersonTable;
import il.ac.tau.cs.databases.atlas.ResultsHolder;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

public class MapBrowserListeners {
	
	protected final static Logger logger = Logger.getLogger(MapBrowserListeners.class.getName());
	public volatile static MapBrowser map = null;

	public static void setMap(MapBrowser map) {
		MapBrowserListeners.map = map;
	}

	public static void updateFavorites() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (map != null) {
					String msg;
					try {
						List<String> favs = Main.queries.getFavoritesIDs();
						String favList = favs.toString();
						map.getBrowser().execute(
								"updateFavorites(" + favList + ");");
					} catch (AtlasServerException e) {
						msg = "Couldnt get favorites from database.";
						map.getBrowser().execute("showError(\"" + msg + "\");");
					}
				} else {
					// TODO Show message?
				}
			}
		});
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
						map.getBrowser().execute("showError(\"" + msg + "\");");
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}

	public static class BrowserAddMarkerActionListener implements
			ActionListener {
		static boolean firstQuery = true;
		JScrollBar timeline;
		JComboBox<String> categoriesComboBox;

		public BrowserAddMarkerActionListener(JScrollBar timeline,
				JComboBox<String> categoriesComboBox) {
			this.timeline = timeline;
			this.categoriesComboBox = categoriesComboBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (firstQuery) {
				firstQuery = false;
				// Set up favorites list
				MapBrowserListeners.updateFavorites();
			}
			if (map != null) {

				int startYear = timeline.getModel().getValue();
				int endYear = timeline.getModel().getValue()
						+ timeline.getModel().getExtent();
				String category = categoriesComboBox.getSelectedItem()
						.toString();
				// Check category
				List<Result> results = null;
				executeJS("showSpinner();");
				try {
					if (category.equals(Map.DEFAULT_CATEGORY)) {
						executeJS("showError(\""
								+ "Please select a category.\");");
					} else if (category.equals(Map.FAVORITES_CATEGORY)) {
						showSpinner();
						results = Main.queries.getFavorites();
					} else {
						showSpinner();
						results = Main.queries.getResults(startYear, endYear, category);
					}
				} catch (AtlasServerException ase) {
					hideSpinner();
					executeJS("showError(\"" + ase.getMessage() + "\");");
					ase.printStackTrace();
				}
				if (results != null) {
					showResultsOnMap(results, category);
				}
			} else {
				// TODO Show message?
			}
		}
	}

	public static void executeJS(final String code) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (map!=null) {
					map.getBrowser().execute(code);
				}
			}
		});
	}

	public static void showResultsOnMap(final List<Result> results,
			final String category) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				map.getBrowser().execute("deleteMarkers();");
				if (results.isEmpty()) {
					map.getBrowser().execute("noResults();");
					MapBrowserListeners.setCategory("");
					hideSpinner();
				} else {
					MapBrowserListeners.setCategory(category);

					for (Result result : results) {
						if (!result.isValidResult()) {
							continue;
						}
						double lat = result.getLocation().getLat();
						double lng = result.getLocation().getLng();
						String imageIcon = "flag-";
						if (result.isBirth()) {
							imageIcon += "birth";
							// TODO can offset the coordinates a little so
							// results in the same place
							// won't be in the exact mark on the map
						} else {
							imageIcon += "death";
							// TODO can offset the coordinates a little so
							// results in the same place
							// won't be in the exact mark on the map
						}
						if (!result.getCategory().equalsIgnoreCase("")) {
							// Check for existing category flag
							String categoryPostfix = result.getCategory()
									.toLowerCase().replace(" ", "-");
							String flagFilename = imageIcon + "-"
									+ categoryPostfix + ".png";
							String tempDir = System
									.getProperty("java.io.tmpdir");
							String flagFilePath = tempDir + flagFilename;
							File file = new File(flagFilePath);
							if (file.exists() && !file.isDirectory()) {
								imageIcon += "-" + categoryPostfix;
							}
						}
						imageIcon += ".png";
						map.getBrowser().execute(
								"addMarker(" + result.getID() + "," + lat + ","
										+ lng + ",\"" + result.getName()
										+ "\",\"" + imageIcon + "\",\""
										+ result.getSummary() + "\",\""
										+ result.getWikiLink() + "\");");

					}
					hideSpinner();

				}
			}
		});
	}

	public static class BrowserDeleteMarkersActionListener implements
			ActionListener {
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

	public static class UpdateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final java.util.Map<String, Result> resultMap = ResultsHolder.INSTANCE.getResultMap();
			if (resultMap == null || resultMap.isEmpty()) {
				JOptionPane.showMessageDialog(null, "There are no results on map!", GrapicUtils.PROJECT_NAME, JOptionPane.WARNING_MESSAGE);
			} else {
				new PersonTable(resultMap);
			}
		}
	}

	public static class BrowserShowStatsActionListner implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						showSpinner();
						String msg = " Showing "
								+ Main.queries.getAmountOfLatestResults()
								+ " results:<br>"
								+ Main.queries.getBirthsOfLatestResults()
								+ " Births<br>"
								+ (Main.queries.getAmountOfLatestResults() - Main.queries
										.getBirthsOfLatestResults())
								+ " Deaths<br>"
								+ Main.queries.getStatsOfLatestResults()
								+ " Females<br>"
								+ (Main.queries.getAmountOfLatestResults() - Main.queries
										.getStatsOfLatestResults()) + " Males";
						hideSpinner();
						map.getBrowser().execute("showStats(\"" + msg + "\");");
					} else {
						// TODO Show message?
					}
				}
			});

		}

	}

	public static class BrowserSyncFavoritesActionListener implements
			ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						showSpinner();
						String favorites = ((String) map.getBrowser().evaluate(
								"return getFavorites()"));
						String removeFromFavorites = ((String) map.getBrowser()
								.evaluate("return getRemoveFromFavorites()"));
						List<String> favoritesList = Arrays.asList(favorites
								.split(","));
						List<String> removeList = Arrays
								.asList(removeFromFavorites.split(","));
						String msg;
						try {
							Main.queries.storeFavoriteIDs(favoritesList,
									removeList);

						} catch (AtlasServerException e) {
							msg = "Favorites failed to sync to database.";
							map.getBrowser().execute(
									"showError(\"" + msg + "\");");
						} finally {
							hideSpinner();
							String favList = "[" + favorites + "]";
							map.getBrowser().execute(
									"updateFavorites(" + favList + ");");
							map.getBrowser().execute("syncComplete();");
						}

					} else {
						// TODO Show message?
					}
				}
			});
		}
	}

	public static class BrowserTimespanAdjustmentListener implements
			AdjustmentListener {

		@Override
		public void adjustmentValueChanged(final AdjustmentEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						JScrollBar timeline = (JScrollBar) e.getAdjustable();
						int start = timeline.getValue();
						int end = timeline.getValue()
								+ timeline.getModel().getExtent();
						setTimespan(start, end);
					} else {
						// TODO Show message?
					}
				}
			});

		}
	}

	public static void setTimespan(int startYear, int endYear) {
		logger.info("Adjusting timeSpan...");
		executeJS("setTimespan(" + startYear + "," + endYear + ");");
	}

	public static void showSpinner() {
		logger.info("Showing Spinner...");
		executeJS("showSpinner();");
	}

	public static void hideSpinner() {
		logger.info("Hiding Spinner...");
		executeJS("hideSpinner();");
	}

	public static void setCategory(String cat) {
		String exec = "setCategory(\"" + cat + "\");";
		logger.info("Adjusting category label by executing JS:" + exec);
		map.getBrowser().execute(exec);
	}
}
