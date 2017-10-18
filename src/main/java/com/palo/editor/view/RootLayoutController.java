package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.editor.model.FileHolder;
import com.palo.editor.model.Item;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

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
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(s -> {
			String allLines = mainApp.getItems().parallelStream().sorted(Item::compareTo)
					.map(item -> String.join(Constants.OPERATOR_EQUALS, item.getKey(), item.fetchValue(s).trim()))
					.collect(Collectors.joining(System.getProperty("line.separator")))
					+ System.getProperty("line.separator");

			String fhName = PreferencesSingleton.getInstace().getFileHoldersInsertOrder().stream()
					.filter(fh -> s.equals(fh)).findAny().get();
			FileHolder fileholder = PreferencesSingleton.getInstace().getFileHolder(fhName);
			File file = new File(fileholder.getPath());

			try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file),
					StandardCharsets.UTF_8)) {
				output.write(allLines);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		PreferencesSingleton.getInstace().saveUserPreferences();
		mainApp.resetUnsavedChanges();
	}

	@FXML
	private void handleAddLanguage() {

	}

	@FXML
	private void handleRemoveLanguage() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/RemoveLanguageDialog.fxml"));
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
