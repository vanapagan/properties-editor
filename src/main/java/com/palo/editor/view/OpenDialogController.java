package com.palo.editor.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.palo.editor.model.FileHolder;
import com.palo.util.PreferencesSingleton;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
		List<File> selectedFilesList = fileChooser.showOpenMultipleDialog(dialogStage);
		for (File f : selectedFilesList) {
			String name = f.getName().toString().replace(".properties", "");
			String pathLiteral = f.toString();
			PreferencesSingleton.getInstace().getFileHolders().add(new FileHolder(name, pathLiteral));
		}
		pathLabel.setText(selectedFilesList.size() + " files selected");
		okSelection = true;

		// TODO create preferences.properties file and save it to disk
		JSONArray jsonArr = new JSONArray();
		PreferencesSingleton.getInstace().getFileHolders().stream().forEach(fh -> {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("filename", fh.getName());
			jsonObj.put("path", fh.getPath());
			jsonArr.put(jsonObj);
		});
		FileWriter fw = new FileWriter("C:\\temp\\preferences.json");
		fw.write(jsonArr.toString());
		
		dialogStage.close();
	}

	@FXML
	private void handleDirectory() throws IOException {
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDirectory = dirChooser.showDialog(dialogStage);
		if (selectedDirectory == null) {
			pathLabel.setText("No directory selected");
		}
		Path path = Paths.get(selectedDirectory.getAbsolutePath());
		Files.list(path).filter(f -> f.toString().endsWith(".properties")).forEach(filepath -> {
			String name = filepath.getFileName().toString().replace(".properties", "");
			String pathLiteral = filepath.toString();
			PreferencesSingleton.getInstace().getFileHolders().add(new FileHolder(name, pathLiteral));
		});
		pathLabel.setText(selectedDirectory.getAbsolutePath());
		okSelection = true;

		// TODO create preferences.properties file and save it to disk
		JSONArray jsonArr = new JSONArray();
		PreferencesSingleton.getInstace().getFileHolders().stream().forEach(fh -> {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("filename", fh.getName());
			jsonObj.put("path", fh.getPath());
			jsonArr.put(jsonObj);
		});
		FileWriter fw = new FileWriter("C:\\temp\\preferences.json");
		fw.write(jsonArr.toString());
		fw.close();
		
		dialogStage.close();
	}

	public boolean isOkSelection() {
		return okSelection;
	}

}