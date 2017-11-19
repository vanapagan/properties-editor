package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.editor.model.Item;
import com.palo.editor.model.TranslationFile;
import com.palo.util.Action;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;
import com.palo.util.Action.Type;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController {

	@FXML
	private MenuItem openMenuItem;

	@FXML
	private MenuItem addLanguageMenuItem;

	@FXML
	private MenuItem removeLanguageMenuItem;

	@FXML
	private MenuItem preferencesMenuItem;

	@FXML
	private MenuItem aboutMenuItem;

	private MainApp mainApp;

	public RootLayoutController() {
	}

	@FXML
	private void handleOpenDialog() throws IOException {
		boolean okClicked = mainApp.showOpenDialog(Constants.TITLE_OPEN, true);
		if (okClicked) {
			reload();
		}
	}

	@FXML
	private void handleSave() throws FileNotFoundException, IOException {
		if (mainApp.anyUnsavedChanges()) {
			PreferencesSingleton pref = PreferencesSingleton.getInstace();
			List<TranslationFile> filesList = pref.getTranslationFiles();
			filesList.stream().forEach(tf -> {
				String fileContent = mainApp.getItems().parallelStream().sorted(Item::compareTo)
						.filter(item -> item != null && item.fetchValue(tf.getName()) != null)
						.map(item -> item.getKeyValuePair(tf.getName()))
						.collect(Collectors.joining(System.getProperty(Constants.LINE_SEPARATOR)));
				fileContent = pref.applyUserPreferences(fileContent);
				try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(new File(tf.getPath())),
						StandardCharsets.UTF_8)) {
					output.write(fileContent);
				} catch (FileNotFoundException e) {
					mainApp.handleException(e);
				} catch (IOException e) {
					mainApp.handleException(e);
				}
			});
			pref.saveUserPreferences();
			mainApp.addNewAction(new Action(Type.SAVE,
					filesList.stream().map(TranslationFile::getName).collect(Collectors.toList())));
			mainApp.truncateUnsavedChanges();
		}
	}

	@FXML
	private void handleAddLanguages() throws IOException {
		boolean okClicked = mainApp.showOpenDialog(Constants.TITLE_ADD, false);
		if (okClicked) {
			reload();
		}
	}

	@FXML
	private void handleRemoveLanguage() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_REMOVE_LANGUAGE_DIALOG));
		AnchorPane page = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		RemoveLanguageDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.setMainApp(mainApp);

		dialogStage.showAndWait();
	}

	@FXML
	private void handleShowPreferencesDialog() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(Constants.VIEW_PREFERENCES_DIALOG));
		AnchorPane page = loader.load();

		Stage dialogStage = new Stage();
		dialogStage.setTitle(Constants.TITLE_PREFERENCES);
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		PreferencesDialogController controller = loader.getController();
		controller.setDialogStage(dialogStage);

		dialogStage.showAndWait();
		
		if (controller.reloadNeeded()) {
			reload();
		}
		
	}

	@FXML
	private void handleAboutDialog() {

	}
	
	private void reload() throws IOException {
		mainApp.setItems(FXCollections.observableArrayList(mainApp.mapProperties().values()));
		mainApp.showEditor();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
