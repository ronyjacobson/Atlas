package il.ac.tau.cs.databases.atlas.graphics.map;

import il.ac.tau.cs.databases.atlas.db.MockQueries;
import il.ac.tau.cs.databases.atlas.db.Queries;
import il.ac.tau.cs.databases.atlas.db.Result;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.swt.widgets.Display;

public class MapBrowserListeners {

	private static MapBrowser map = null;

	private static final Queries queries = new MockQueries();

	public static void setMap(MapBrowser map) {
		MapBrowserListeners.map = map;
	}

	public static class BrowserErrorActionListener implements ActionListener {

		String msg;

		public BrowserErrorActionListener() {
			this.msg = "A browser error occurred.";
		}

		public BrowserErrorActionListener(String msg) {
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
		
		int timeSlot;
		
		public BrowserAddMarkerActionListener(int timeSlot) {
			this.timeSlot = timeSlot;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						map.getBrowser().execute("deleteMarkers();");
						for (Result result : queries.getResults(timeSlot)) {
							String imageIcon;
							if (result.isBirth()){
								imageIcon = "./flag-birth.png";
							} else {
								imageIcon = "./flag-death.png";
							}
							map.getBrowser().execute("addMarker(" + result.getLocation().getLat() + "," + result.getLocation().getLng() + ",\"" + result.getLocation().getName() + "\");");
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
