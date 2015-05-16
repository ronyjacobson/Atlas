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
		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						for (Result result : queries.getResults(0)) {
							map.getBrowser().execute("addMarker(" + result.getPlaceOnBirth().getLat() + "," + result.getPlaceOnBirth().getLng() + ",\"" + result.getPlaceOnBirth().getName() + "\");");
						}
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}
}
