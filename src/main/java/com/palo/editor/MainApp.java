package com.palo.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.palo.editor.model.Item;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private ObservableList<Item> items;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Properties Editor");
		
		Properties prop = null;
		InputStream input = null;
		Map<String, Item> map = new HashMap<String, Item>();

		String[] arr = { "translations_en.properties", "translations_fi.properties", "translations_et.properties" };
		try {
			for (String filename : arr) {
				prop = new Properties();
				input = getClass().getResourceAsStream(filename);
				prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
				for (Object k : prop.keySet()) {
					String key = (String) k;
					String value = prop.getProperty(key);
					Item item = map.get(key);
					if (item == null) {
						item = new Item(key);
						map.put(key, item);
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
		
		items = FXCollections.observableArrayList(map.values());
		TableView<Item> table = new TableView<Item>(items);
		
		table.setEditable(true);
		
		initRootLayout();
		
		showEditor();
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = loader.load();
		
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showEditor() {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Editor.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public ObservableList<Item> getItems() {
		return items;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
