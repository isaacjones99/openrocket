package net.sf.openrocket.util;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.openrocket.document.Simulation;


public class Icons {

	/**
	 * Icons used for showing the status of a simulation (up to date, out of date, etc).
	 */
	public static final Map<Simulation.Status, Icon> SIMULATION_STATUS_ICON_MAP;
	static {
		HashMap<Simulation.Status, Icon> map = new HashMap<Simulation.Status, Icon>();
		map.put(Simulation.Status.NOT_SIMULATED, loadImageIcon("pix/spheres/gray-16x16.png", "Not simulated"));
		map.put(Simulation.Status.UPTODATE, loadImageIcon("pix/spheres/green-16x16.png", "Up to date"));
		map.put(Simulation.Status.LOADED, loadImageIcon("pix/spheres/yellow-16x16.png", "Loaded from file"));
		map.put(Simulation.Status.OUTDATED, loadImageIcon("pix/spheres/red-16x16.png", "Out-of-date"));
		map.put(Simulation.Status.EXTERNAL, loadImageIcon("pix/spheres/blue-16x16.png", "Imported data"));
		SIMULATION_STATUS_ICON_MAP = Collections.unmodifiableMap(map);
	}
	
	public static final Icon SIMULATION_LISTENER_OK;
	public static final Icon SIMULATION_LISTENER_ERROR;
	static {
		SIMULATION_LISTENER_OK = SIMULATION_STATUS_ICON_MAP.get(Simulation.Status.UPTODATE);
		SIMULATION_LISTENER_ERROR = SIMULATION_STATUS_ICON_MAP.get(Simulation.Status.OUTDATED);
	}


	public static final Icon FILE_NEW = loadImageIcon("pix/icons/document-new.png", "New document");
	public static final Icon FILE_OPEN = loadImageIcon("pix/icons/document-open.png", "Open document");
	public static final Icon FILE_OPEN_EXAMPLE = loadImageIcon("pix/icons/document-open-example.png", "Open example document");
	public static final Icon FILE_SAVE = loadImageIcon("pix/icons/document-save.png", "Save document");
	public static final Icon FILE_SAVE_AS = loadImageIcon("pix/icons/document-save-as.png", "Save document as");
	public static final Icon FILE_CLOSE = loadImageIcon("pix/icons/document-close.png", "Close document");
	public static final Icon FILE_QUIT = loadImageIcon("pix/icons/application-exit.png", "Quit OpenRocket");
	
	public static final Icon EDIT_UNDO = loadImageIcon("pix/icons/edit-undo.png", "Undo");
	public static final Icon EDIT_REDO = loadImageIcon("pix/icons/edit-redo.png", "Redo");
	public static final Icon EDIT_CUT = loadImageIcon("pix/icons/edit-cut.png", "Cut");
	public static final Icon EDIT_COPY = loadImageIcon("pix/icons/edit-copy.png", "Copy");
	public static final Icon EDIT_PASTE = loadImageIcon("pix/icons/edit-paste.png", "Paste");
	public static final Icon EDIT_DELETE = loadImageIcon("pix/icons/edit-delete.png", "Delete");

	public static final Icon ZOOM_IN = loadImageIcon("pix/icons/zoom-in.png", "Zoom in");
	public static final Icon ZOOM_OUT = loadImageIcon("pix/icons/zoom-out.png", "Zoom out");

	public static final Icon PREFERENCES = loadImageIcon("pix/icons/preferences.png", "Preferences");

	public static final Icon DELETE = loadImageIcon("pix/icons/delete.png", "Delete");
	
	
	
	public static ImageIcon loadImageIcon(String file, String name) {
		URL url = ClassLoader.getSystemResource(file);
		if (url == null) {
			System.err.println("Resource "+file+" not found!  Ignoring...");
			return null;
		}
		return new ImageIcon(url, name);
	}
}
