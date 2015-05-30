package il.ac.tau.cs.databases.atlas.graphics.map;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.db.Result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.swt.widgets.Display;

public class MapBrowserListeners {

	private static MapBrowser map = null;

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
		
		int startYear;
		int endYear;
		String category;
		
		public BrowserAddMarkerActionListener(int startYear, int endYear, String category) {
			this.startYear = startYear;
			this.endYear = endYear;
			this.category = category;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						map.getBrowser().execute("deleteMarkers();");
						for (Result result : Main.queries.getResults(startYear, endYear, category)) {
							String imageIcon;
							if (result.isBirth()){
								imageIcon = "./flag-birth.png";
							} else {
								imageIcon = "./flag-death.png";
							}
							map.getBrowser().execute("addMarker(" + result.getLocation().getLat() + "," + result.getLocation().getLng() + ",\"" + result.getName() + "\",\"" + imageIcon + "\",\"" + result.getSummary() + "\",\"" + result.getWikiLink() + "\");");
						}
					} else {
						// TODO Show message?
					}
				}
			});
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
}
