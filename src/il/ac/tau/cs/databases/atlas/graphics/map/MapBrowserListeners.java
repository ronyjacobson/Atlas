package il.ac.tau.cs.databases.atlas.graphics.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.swt.widgets.Display;

public class MapBrowserListeners {

	private static MapBrowser map = null;

	public static void setMap(MapBrowser map) {
		MapBrowserListeners.map = map;
	}

	public static class BrowserErrorActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (map != null) {
						map.getBrowser().execute("error();");
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
						map.getBrowser().execute("addMarker(61.850033,-87.6500523);");
					} else {
						// TODO Show message?
					}
				}
			});
		}
	}
}
