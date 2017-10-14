package com.palo.editor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


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
	
	private int unsavedChanges;

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(Constants.APP_TITLE);
		
		initUnsavedChanges();
	
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
			Path path = Paths.get(fileholder.getPath());
			try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
				String translation = fileholder.getName();
				PreferencesSingleton.getInstace().getTranslationsList().add(translation);
				stream.forEach(line -> {
					String[] lineContent = line.split(Constants.OPERATOR_EQUALS, 2);
					String key = "";
					String value = "";
					if (lineContent.length > 0) {
						key = lineContent[0].trim();
					}
					if (lineContent.length > 1) {
						value = lineContent[1].trim();
					}
					Item item = map.get(key);
					if (item == null) {
						item = new Item(key);
						map.put(key, item);
					}
					item.addNewValue(translation, value);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	
	public void addNewChange() {
		++unsavedChanges;
		updateTitle();
	}

	public void resetUnsavedChanges() {
		initUnsavedChanges();
		updateTitle();
	}
	
	public void initUnsavedChanges() {
		this.unsavedChanges = 0;
	}
	
	private void updateTitle() {
		String title = unsavedChanges > 0 ? Constants.APP_TITLE + Constants.ASTERISK : Constants.APP_TITLE;
		primaryStage.setTitle(title);
	}

	public int getUnsavedChanges() {
		return unsavedChanges;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
