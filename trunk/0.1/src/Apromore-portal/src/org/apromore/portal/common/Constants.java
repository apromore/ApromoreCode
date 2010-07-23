package org.apromore.portal.common;

public final class Constants {
	// repository manager details
	public static final String PROPERTY_FILE = "apromore.properties";
	
	// max number of searches kept for users
	public static final Integer maxSearches = 10;

	// colors and style used in the table view
	// #E5E5E5 light gray
	// #598DCA blue

	public static final String TOOLBARBUTTON_STYLE = "font-size:12px";
	public static final String UNSELECTED_VERSION = "background-color:#E5E5E5" + ";" + TOOLBARBUTTON_STYLE;
	public static final String SELECTED_VERSION = "background-color:#598DCA" + ";" + TOOLBARBUTTON_STYLE;
	public static final String SELECTED_PROCESS = "background-color:#598DCA" + ";" + TOOLBARBUTTON_STYLE ;
	public static final String UNSELECTED_EVEN = "background-color:#FFFFFF" + ";" + TOOLBARBUTTON_STYLE;
	public static final String UNSELECTED_ODD = "background-color:#F0FAFF" + ";" + TOOLBARBUTTON_STYLE;
	
	public static final String NO_ANNOTATIONS = "-- no annotations --";
	public static final String INITIAL_ANNOTATION = "Initial";
	public static final String ANNOTATIONS = "Annotations";
	public static final String CANONICAL = "Canonical";
	
	public static final String RELEASE_NOTES = "http://code.google.com/p/apromore/wiki/ReleaseNotes";
}
