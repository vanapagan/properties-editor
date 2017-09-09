package com.palo.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.palo.editor.model.FileHolder;
import com.palo.editor.model.Item;
import com.palo.editor.view.EditorController;
import com.palo.editor.view.ItemDialogController;
import com.palo.editor.view.OpenDialogController;
import com.palo.util.PreferencesSingleton;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	public ObservableList<Item> items;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Properties Editor");

		File prefFile = new File("C:\\temp\\preferences.json");

		if (!prefFile.exists()) {
			showOpenDialog();
		} else {
			// TODO populate PreferencesSingleton with 'preferences.properties'
			// content
			JSONTokener tokener;
			try {
				tokener = new JSONTokener(new FileReader("C:\\temp\\preferences.json"));
				JSONArray jsonArr = new JSONArray(tokener);
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject jsonObj = (JSONObject) jsonArr.get(i);
					if (jsonObj != null) {
						String filename = jsonObj.getString("filename");
						String path = jsonObj.getString("path");
						PreferencesSingleton.getInstace().getFileHolders().add(new FileHolder(filename, path));
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map<String, Item> map = new HashMap<String, Item>();

		// TODO load the properies files
		PreferencesSingleton.getInstace().getFileHolders().stream().forEach(fileholder -> {
			Properties prop = new Properties();
			InputStream input = null;
			try {
				Path path = Paths.get(fileholder.getPath());
				input = Files.newInputStream(path);
				prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
			String translation = fileholder.getName();
			PreferencesSingleton.getInstace().getTranslationsList().add(translation);
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

	public boolean showItemDialog(Item item, String activityTitle, String button,
			ObservableList<Item> selectedItemsList) {
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
			controller.setItem(item, button, selectedItemsList);

			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean showOpenDialog() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/OpenDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			OpenDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);

			dialogStage.showAndWait();

			return controller.isOkSelection();
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

	public static void main(String[] args) {
		launch(args);
	}
}
