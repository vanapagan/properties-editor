package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
		boolean okClicked = mainApp.showOpenDialog();
		if (okClicked) {
			mainApp.setItems(FXCollections.observableArrayList(mainApp.mapProperties().values()));
			mainApp.showEditor();
		}
	}

	@FXML
	private void handleSave() throws FileNotFoundException, IOException {
		if (mainApp.anyUnsavedChanges()) {
			PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
				String allLines = mainApp.getItems().parallelStream().sorted(Item::compareTo)
						.filter(item -> item != null && item.fetchValue(tf.getName()) != null).map(item -> {
							return String.join(Constants.OPERATOR_EQUALS, item.getKey(),
									item.fetchValue(tf.getName()).trim());
						}).collect(Collectors.joining(System.getProperty(Constants.LINE_SEPARATOR)))
						+ System.getProperty(Constants.LINE_SEPARATOR);
				try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(new File(tf.getPath())),
						StandardCharsets.UTF_8)) {
					output.write(allLines);
				} catch (FileNotFoundException e) {
					mainApp.handleException(e);
				} catch (IOException e) {
					mainApp.handleException(e);
				}
			});
			PreferencesSingleton.getInstace().saveUserPreferences();
			mainApp.addNewAction(new Action(Type.SAVE, PreferencesSingleton.getInstace().getTranslationFiles().stream()
					.map(TranslationFile::getName).collect(Collectors.toList())));
			mainApp.truncateUnsavedChanges();
		}
	}

	@FXML
	private void handleAddLanguage() {

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
	private void handlePreferencesDialog() {

	}

	@FXML
	private void handleAboutDialog() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
