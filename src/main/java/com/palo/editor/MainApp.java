package com.palo.editor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.palo.editor.model.Item;
import com.palo.editor.view.EditorController;
import com.palo.editor.view.ItemDialogController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Path;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static final String location = "C:/Temp/translations";
	public static String[] arr = { "translations_en.properties", "translations_fi.properties",
			"translations_et.properties" };
	public static List<String> translationsList = new ArrayList<>();

	private Stage primaryStage;
	private BorderPane rootLayout;

	public ObservableList<Item> items;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Properties Editor");

		Map<String, Item> map = new HashMap<String, Item>();

		// TODO load properies files
		java.nio.file.Path path = Paths.get(location);
		try {
			Files.list(path).filter(f -> f.toString().endsWith(".properties")).forEach(filepath -> {
				Properties prop = new Properties();
				InputStream input = null;
				try {
					input = Files.newInputStream(filepath);
					prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
				}
				String translation = filepath.getFileName().toString().replace(".properties", "");
				translationsList.add(translation);
				for (Object k : prop.keySet()) {
					String key = (String) k;
					String value = prop.getProperty(key);
					Item item = map.get(key);
					if (item == null) {
						item = new Item(key);
						map.put(key, item);
					}
					item.addNewValue(translation, value);
				}
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		items = FXCollections.observableArrayList(map.values());

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

			EditorController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean showItemDialog(Item item, String activityTitle, String button) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ItemDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle(activityTitle + " " + "Key");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			ItemDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setItem(item, button);

			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public ObservableList<Item> getItems() {
		return items;
	}
	
	public List<String> getTranslationsList() {
		return translationsList;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
