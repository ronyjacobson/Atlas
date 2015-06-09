package il.ac.tau.cs.databases.atlas.graphics.map;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Arrays;
import java.util.List;

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
		String category;
		
		public BrowserAddMarkerActionListener(JScrollBar timeline, String category) {
			this.timeline = timeline;
			this.category = category;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						int startYear = timeline.getModel().getValue();
						int endYear =  timeline.getModel().getValue() + timeline.getModel().getExtent();
						try {
							showResultsOnMap(Main.queries.getResults(startYear, endYear, category));
						} catch (AtlasServerException e) {
							// TODO Auto-generated catch block HANDLE EXCEPTION
							e.printStackTrace();
						}
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}
	
	public static void showResultsOnMap(List<Result> results){
		map.getBrowser().execute("deleteMarkers();");
		for (Result result : results) {
			String imageIcon;
			if (result.isBirth()){
				imageIcon = "./flag-birth.png";
			} else {
				imageIcon = "./flag-death.png";
			}
			map.getBrowser().execute("addMarker(" + result.getID() + "," + result.getLocation().getLat() + "," + result.getLocation().getLng() + ",\"" + result.getName() + "\",\"" + imageIcon + "\",\"" + result.getSummary() + "\",\"" + result.getWikiLink() + "\");");
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
						} catch (AtlasServerException e){
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
						int end =  timeline.getValue() + timeline.getModel().getExtent();
						setTimespan(start, end);
					} else {
						// TODO Show message?
					}
				}
			});
			
		}
	}
	
	public static void setTimespan(int startYear, int endYear){
		map.getBrowser().execute("setTimespan(" + startYear + "," + endYear + ");");
	}
}
