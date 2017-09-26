package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.editor.model.FileHolder;
import com.palo.editor.model.Item;
import com.palo.util.PreferencesSingleton;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class RootLayoutController {
	
	@FXML
	private MenuItem openMenuItem;
	
	@FXML
	private MenuItem aboutMenuItem;
	
	private MainApp mainApp;
	
	public RootLayoutController() {
	}
	
	@FXML
	private void handleOpenDialog() throws IOException {
		mainApp.showOpenDialog();
		mainApp.setItems(FXCollections.observableArrayList(mainApp.mapProperties().values()));
		mainApp.showEditor();
	}
	
	@FXML
	private void handleSave() throws FileNotFoundException, IOException {
		for (String s : PreferencesSingleton.getInstace().getTranslationsList()) {
			Map<String, String> map = mainApp.getItems().stream().collect(Collectors.toMap(Item::getKey, item -> {
				String value = item.fetchValue(s);
				if (value == null) {
					value = "";
				}
				return value;
			}));

			SortedProperties properties = new SortedProperties();
			properties.putAll(map);

			FileHolder fileholder = PreferencesSingleton.getInstace().getFileHolders().stream()
					.filter(fh -> s.equals(fh.getName())).collect(Collectors.toList()).get(0);
			File file = new File(fileholder.getPath());

			try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
				properties.store(output, null);
			}
			
		}
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
}

class SortedProperties extends Properties {

	private static final long serialVersionUID = 3838181836191268646L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector<String> keyList = new Vector<String>();
		while (keysEnum.hasMoreElements()) {
			keyList.add((String) keysEnum.nextElement());
		}
		Collections.sort(keyList, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
			}
		});
		
		return keyList.elements();
	}

}
