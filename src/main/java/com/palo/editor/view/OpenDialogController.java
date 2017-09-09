package com.palo.editor.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import com.palo.editor.model.FileHolder;
import com.palo.editor.model.Item;
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
	private void handleFiles() {
		FileChooser fileChooser = new FileChooser();
		List<File> selectedFilesList = fileChooser.showOpenMultipleDialog(dialogStage);
		for (File f : selectedFilesList) {
			String name = f.getName().toString().replace(".properties", "");
			String pathLiteral = f.toString();
			PreferencesSingleton.getInstace().getFileHolders().add(new FileHolder(name, pathLiteral));
		}
		pathLabel.setText(selectedFilesList.size() + " files selected");
		okSelection = true;
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
		dialogStage.close();
	}
	
	public boolean isOkSelection() {
		return okSelection;
	}
	
}
