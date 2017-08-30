package com.palo.properties_editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		Properties prop = null;
		InputStream input = null;
		Map<String, Item> itemsMap = new HashMap<String, Item>();

		String[] arr = { "translations_en.properties", "translations_fi.properties", "translations_et.properties" };
		try {
			for (String filename : arr) {
				prop = new Properties();
				input = getClass().getResourceAsStream(filename);
				prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
				for (Object k : prop.keySet()) {
					String key = (String) k;
					String value = prop.getProperty(key);
					Item item = itemsMap.get(key);
					if (item == null) {
						item = new Item();
						itemsMap.put(key, item);
					}
					item.addNewValue(filename, value);
				}
				if (input != null) {
					input.close();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		ObservableList<Map.Entry<String, Item>> items = FXCollections.observableArrayList(itemsMap.entrySet());
		TableView<Map.Entry<String, Item>> table = new TableView<Entry<String, Item>>(items);

		table.setEditable(true);

		// TODO table creation
		TableColumn<Map.Entry<String, Item>, String> keyColumn = new TableColumn<Entry<String, Item>, String>("Key");
		keyColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Item>, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(
							TableColumn.CellDataFeatures<Map.Entry<String, Item>, String> e) {
						return new SimpleStringProperty(e.getValue().getKey());
					}
				});
		keyColumn.setSortType(TableColumn.SortType.ASCENDING);
		table.getColumns().add(keyColumn);

		for (final String filename : arr) {
			TableColumn<Map.Entry<String, Item>, String> languageColumn = new TableColumn<Entry<String, Item>, String>(
					filename.replace(".properties", ""));
			languageColumn.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Item>, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(
								TableColumn.CellDataFeatures<Map.Entry<String, Item>, String> p) {
							return new SimpleStringProperty(p.getValue().getValue().fetchValue(filename));
						}
					});
			table.getColumns().add(languageColumn);
		}
		table.getSortOrder().add(keyColumn);

		try {
			Scene scene = new Scene(table, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}
