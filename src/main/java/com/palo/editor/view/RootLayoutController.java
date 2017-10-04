package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

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
		mainApp.showOpenDialog();
		mainApp.setItems(FXCollections.observableArrayList(mainApp.mapProperties().values()));
		mainApp.showEditor();
	}

	@FXML
	private void handleSave() throws FileNotFoundException, IOException {
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(s -> {
			StringBuilder sb = new StringBuilder();
			mainApp.getItems().stream().sorted(Item::compareTo).forEach(item -> {
				String line = String.join(Constants.OPERATOR_EQUALS, item.getKey(), item.fetchValue(s));
				sb.append(line.trim());
				sb.append(System.getProperty("line.separator"));
			});
			sb.setLength(sb.length() - 2);

			FileHolder fileholder = PreferencesSingleton.getInstace().getFileHolders().stream()
					.filter(fh -> s.equals(fh.getName())).collect(Collectors.toList()).get(0);
			File file = new File(fileholder.getPath());

			try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file),
					StandardCharsets.UTF_8)) {
				output.write(sb.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			JSONArray jsonArr = new JSONArray();
			PreferencesSingleton.getInstace().getFileHolders().stream().forEach(fh -> {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put(Constants.PREFERENCES_FILENAME, fh.getName());
				jsonObj.put(Constants.PREFERENCES_PATH, fh.getPath());
				jsonArr.put(jsonObj);
			});
			FileWriter fw;
			try {
				fw = new FileWriter(Constants.PREFERENCES_FILE_LOCATION);
				fw.write(jsonArr.toString());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
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
