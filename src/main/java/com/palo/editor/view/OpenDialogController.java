package com.palo.editor.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.palo.editor.model.FileHolder;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class OpenDialogController {

	@FXML
	private Label pathLabel;

	@FXML
	private Button filesButton;

	@FXML
	private Button directoryButton;

	private Stage dialogStage;

	private boolean okSelection = false;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	@FXML
	private void handleFiles() throws IOException {
		FileChooser fileChooser = new FileChooser();
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Properties files", "properties");
		fileChooser.setSelectedExtensionFilter(extFilter);
		List<File> selectedFilesList = fileChooser.showOpenMultipleDialog(dialogStage);
		selectedFilesList.stream().forEach(f -> addNewFileHolder(f));
		pathLabel.setText(selectedFilesList.size() + " " + Constants.OPEN_DIALOG_FILES_SELECTED);
		okSelection = true;

		saveUserPreferences();

		dialogStage.close();
	}

	@FXML
	private void handleDirectory() throws IOException {
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDirectory = dirChooser.showDialog(dialogStage);
		if (selectedDirectory == null) {
			pathLabel.setText(Constants.OPEN_DIALOG_NO_DIR_SELECTED);
		} else {
			Path path = Paths.get(selectedDirectory.getAbsolutePath());
			Files.list(path).filter(f -> f.toString().endsWith(Constants.SUFFIX_PROPERTIES)).forEach(filepath -> {
				addNewFile(filepath);
			});
			pathLabel.setText(selectedDirectory.getAbsolutePath());
			okSelection = true;

			saveUserPreferences();

			dialogStage.close();
		}
	}

	private void addNewFileHolder(File file) {
		addNewFile(Paths.get(file.getAbsolutePath()));
	}

	private void addNewFile(Path filepath) {
		String name = filepath.getFileName().toString().replace(Constants.SUFFIX_PROPERTIES, "");
		String pathLiteral = filepath.toString();
		PreferencesSingleton.getInstace().getFileHolders().add(new FileHolder(name, pathLiteral));
	}

	private void saveUserPreferences() throws IOException {
		JSONArray jsonArr = new JSONArray();
		PreferencesSingleton.getInstace().getFileHolders().stream().forEach(fh -> {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(Constants.PREFERENCES_FILENAME, fh.getName());
			jsonObj.put(Constants.PREFERENCES_PATH, fh.getPath());
			jsonArr.put(jsonObj);
		});
		FileWriter fw = new FileWriter(Constants.PREFERENCES_FILE_LOCATION);
		fw.write(jsonArr.toString());
		fw.close();
	}

	public boolean isOkSelection() {
		return okSelection;
	}

}
