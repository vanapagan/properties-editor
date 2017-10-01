package com.palo.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.palo.editor.model.FileHolder;
import com.palo.editor.model.Item;
import com.palo.editor.view.EditorController;
import com.palo.editor.view.MultipleItemDialogController;
import com.palo.editor.view.SingleItemDialogController;
import com.palo.editor.view.OpenDialogController;
import com.palo.editor.view.RootLayoutController;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Item> items;
	
	private TableView<Item> itemTable;

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(Constants.APP_TITLE);

		checkForExistingPreferences();
		items = FXCollections.observableArrayList(mapProperties().values());
		initRootLayout();
		showEditor();
	}

	private void checkForExistingPreferences() throws IOException {
		File prefFile = new File(Constants.PREFERENCES_FILE_LOCATION);
		if (!prefFile.exists()) {
			showOpenDialog();
		} else {
			JSONTokener tokener;
			tokener = new JSONTokener(new FileReader(Constants.PREFERENCES_FILE_LOCATION));
			JSONArray jsonArr = new JSONArray(tokener);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				if (jsonObj != null) {
					String filename = jsonObj.getString(Constants.PREFERENCES_FILENAME);
					String path = jsonObj.getString(Constants.PREFERENCES_PATH);
					PreferencesSingleton.getInstace().getFileHolders().add(new FileHolder(filename, path));
				}
			}
		}
	}

	public Map<String, Item> mapProperties() {
		Map<String, Item> map = new HashMap<>();
		PreferencesSingleton.getInstace().getFileHolders().stream().forEach(fileholder -> {
			Properties properties = new Properties();
			Path path = Paths.get(fileholder.getPath());
			try (InputStream inputStream = Files.newInputStream(path)) {
				String fileContent = convert(inputStream, StandardCharsets.UTF_8);
				properties.load(new StringReader(fileContent.replace(Constants.BACKSLASH_COLON, Constants.ESCAPED_BACKSLASH_COLON)));
			} catch (IOException e) {
				e.printStackTrace();
			}		
			String translation = fileholder.getName();
			PreferencesSingleton.getInstace().getTranslationsList().add(translation);
			properties.keySet().stream().forEach(k -> {
				String key = (String) k;
				String value = properties.getProperty(key);
				Item item = map.get(key);
				if (item == null) {
					item = new Item(key);
					map.put(key, item);
				}
				item.addNewValue(translation, value);
			});
		});
		return map;
	}

	public void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_ROOT_LAYOUT));
		rootLayout = loader.load();
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		RootLayoutController controller = loader.getController();
		controller.setMainApp(this);
	}

	public void showEditor() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_EDITOR));
		AnchorPane personOverview = loader.load();
		rootLayout.setCenter(personOverview);
		EditorController controller = loader.getController();
		controller.setMainApp(this);
	}

	public boolean showSingleItemDialog(Item item, String title)
			throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_SINGLE_ITEM_DIALOG));
		AnchorPane page = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.setTitle(title + " " + Constants.ITEM_DIALOG_TITLE_KEY);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		SingleItemDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setItem(item);

		dialogStage.showAndWait();

		return controller.isOkClicked();
	}
	
	public boolean showMultipleItemDialog(ObservableList<Item> selectedItemsList, String title, boolean isNew)
			throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_MULTIPLE_ITEM_DIALOG));
		AnchorPane page = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.setTitle(title + " " + Constants.ITEM_DIALOG_TITLE_KEYS);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		MultipleItemDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setItemsList(selectedItemsList, isNew);

		dialogStage.showAndWait();

		return controller.isOkClicked();
	}

	public boolean showOpenDialog() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_OPEN_DIALOG));
		AnchorPane page = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		OpenDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);

		dialogStage.showAndWait();

		return controller.isOkSelection();
	}
	
	private String convert(InputStream inputStream, Charset charset) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public ObservableList<Item> getItems() {
		return items;
	}

	public void setItems(ObservableList<Item> items) {
		this.items = items;
	}
	
	public void setItemTable(TableView<Item> itemTable) {
		this.itemTable = itemTable;
	}
	
	public TableView<Item> getItemTable() {
		return itemTable;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
