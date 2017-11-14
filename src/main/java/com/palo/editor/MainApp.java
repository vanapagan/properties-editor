package com.palo.editor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.palo.editor.model.TranslationFile;
import com.palo.editor.model.Item;
import com.palo.editor.view.ActivityDialogController;
import com.palo.editor.view.EditorController;
import com.palo.editor.view.MultipleItemDialogController;
import com.palo.editor.view.SingleItemDialogController;
import com.palo.editor.view.OpenDialogController;
import com.palo.editor.view.RootLayoutController;
import com.palo.util.Action;
import com.palo.util.Action.Type;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	private Stack<Action> unsavedChangesStack;
	private StringProperty activityProperty;
	private ObservableList<Action> actionsList;

	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			setupEnvironment();
		} catch (IOException e) {
			handleException(e);
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void setupEnvironment() throws IOException {
		initUnsavedChanges();
		initActivityMonitoring();
		setTitle();
		initPreferences();
		initItems();
		showRootLayout();
		showEditor();
	}
	
	private void initActivityMonitoring() {
		actionsList = FXCollections.observableList(new ArrayList<>());
		activityProperty = new SimpleStringProperty();
	}

	private void initItems() {
		items = FXCollections.observableArrayList(mapProperties().values());
		addNewIdempotentAction(new Action(Type.LOAD_FILE, PreferencesSingleton.getInstace().getTranslationFiles()
				.stream().map(f -> f.getName()).collect(Collectors.toList())));
	}

	private void initPreferences() throws IOException {
		File prefFile = new File(Constants.PREFERENCES_FILE_LOCATION);
		if (!prefFile.exists()) {
			showOpenDialog();
		} else {
			JSONTokener tokener = new JSONTokener(new FileReader(Constants.PREFERENCES_FILE_LOCATION));
			JSONArray jsonArr = new JSONArray(tokener);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				if (jsonObj != null) {
					String filename = jsonObj.getString(Constants.PREFERENCES_FILENAME);
					String path = jsonObj.getString(Constants.PREFERENCES_PATH);
					PreferencesSingleton.getInstace().addFileHolder(new TranslationFile(filename, path));
				}
			}
		}
	}

	public Map<String, Item> mapProperties() {
		Map<String, Item> map = new HashMap<>();
		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			Path path = Paths.get(tf.getPath());
			try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
				String translation = tf.getName();
				stream.forEach(line -> {
					String[] lineContent = line.split(Constants.OPERATOR_EQUALS, 2);
					int length = lineContent.length;
					String key = length > 0 ? lineContent[0].trim() : "";
					String value = length > 1 ? lineContent[1].trim() : "";
					Item item = map.get(key);
					if (item == null) {
						item = new Item(key);
						map.put(key, item);
					}
					item.addNewValue(translation, value);
				});
			} catch (IOException e) {
				handleException(e);
			}
		});
		return map;
	}

	public void showRootLayout() throws IOException {
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

	public boolean showSingleItemDialog(Item item, String title) throws IOException {
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
	
	public boolean showActivityDialog() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_ACTIVITY_DIALOG));
		AnchorPane page = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		ActivityDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setMainApp(this);

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

	public void initUnsavedChanges() {
		unsavedChangesStack = new Stack<>();
	}

	public void truncateUnsavedChanges() {
		unsavedChangesStack.clear();
		setTitle();
	}

	public void addNewAction(Action a) {
		unsavedChangesStack.push(a);
		addNewIdempotentAction(a);
	}
	
	public void addNewIdempotentAction(Action a) {
		actionsList.add(a);
		setActivityPropertyText(a);
		setTitle();
	}

	private void setTitle() {
		String title = unsavedChangesStack.size() > 0 ? Constants.APP_TITLE + Constants.ASTERISK : Constants.APP_TITLE;
		primaryStage.setTitle(title);
	}

	public StringProperty getActivityProperty() {
		return activityProperty;
	}

	public ObservableList<Action> getActionsList() {
		return actionsList;
	}

	public void setActivityPropertyText(Action a) {
		activityProperty.set(a.getActivity().getGenericInfo());
	}

	private void handleException(Exception e) {
		e.printStackTrace();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
