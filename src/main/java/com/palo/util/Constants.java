package com.palo.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {
	
	public static final String TITLE_APP = "Properties Editor";
	public static final String TITLE_OPEN = "Open files";
	public static final String TITLE_ACTIVITY = "Latest activity";
	public static final String TITLE_PREFERENCES = "Preferences";
	
	public static final String PREFERENCES_FILENAME = "filename";
	public static final String PREFERENCES_ENCODING = "encoding";
	public static final String PREFERENCES_PATH = "path";
	
	public static final String VIEW = "view";
	
	public static final String VIEW_ROOT_LAYOUT = VIEW + "/" + "RootLayout.fxml";
	public static final String VIEW_EDITOR = VIEW + "/" + "Editor.fxml";
	public static final String VIEW_SINGLE_ITEM_DIALOG = VIEW + "/" + "SingleItemDialog.fxml";
	public static final String VIEW_MULTIPLE_ITEM_DIALOG = VIEW + "/" + "MultipleItemDialog.fxml";
	public static final String VIEW_OPEN_DIALOG = VIEW + "/" + "OpenDialog.fxml";
	public static final String VIEW_ACTIVITY_DIALOG = VIEW + "/" + "ActivityDialog.fxml";
	public static final String VIEW_REMOVE_LANGUAGE_DIALOG = VIEW + "/" + "RemoveLanguageDialog.fxml";
	public static final String VIEW_PREFERENCES_DIALOG = VIEW + "/" + "PreferencesDialog.fxml";
	
	public static final String PREFERENCES_FILE_LOCATION = "C:\\temp\\preferences.json";
	
	public static final String EDITOR_ADD_NEW_TITLE = "New";
	public static final String EDITOR_ADD_NEW_BUTTON = "Add";
	public static final String EDITOR_EDIT_TITLE = "Edit";
	public static final String EDITOR_EDIT_TITLE_MULTIPLE = "Edit Multiple";
	public static final String EDITOR_EDIT_BUTTON = "Edit";
	
	public static final String ITEM_DIALOG_TITLE_KEY = "Key";
	public static final String ITEM_DIALOG_TITLE_KEYS = "Keys";
	
	public static final String OPEN_DIALOG_NO_DIR_SELECTED = "No directory selected";
	public static final String OPEN_DIALOG_NO_FILES_SELECTED = "No files selected";
	public static final String OPEN_DIALOG_FILES_SELECTED = "files selected";
	
	public static final String LABEL_KEY = "Key";
	public static final String LABEL_CREATED = "Created";
	public static final String LABEL_LAST_MODIFIED = "Last Modified";
	public static final String LABEL_FILENAME = "Filename";
	public static final String LABEL_ENCODING = "Encoding";
	public static final String LABEL_LOCATION = "Location";
	
	public static final String EXTENSION_PROPERTIES = ".properties";
	
	public static final String OPERATOR_EQUALS = "=";
	public static final String NEW_LINE = "\n";
	public static final String INTENT = "\t";
	public static final String ASTERISK = "*";
	public static final String LINE_SEPARATOR = "line.separator";
	
	public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
	
	private Constants() {
	}

}
