package com.palo.editor.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.palo.editor.model.TranslationFile;
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

	private boolean okSelection;
	
	private boolean truncate;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setTruncate(boolean truncate) {
		this.truncate = truncate;
	}

	@FXML
	private void handleFiles() throws IOException {
		FileChooser fileChooser = new FileChooser();
		ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Properties files", "properties");
		fileChooser.setSelectedExtensionFilter(extFilter);
		List<File> selectedFilesList = fileChooser.showOpenMultipleDialog(dialogStage);
		if (selectedFilesList == null) {
			pathLabel.setText(Constants.OPEN_DIALOG_NO_FILES_SELECTED);
		} else {
			mapSelectedFiles(selectedFilesList);
			pathLabel.setText(selectedFilesList.size() + " " + Constants.OPEN_DIALOG_FILES_SELECTED);
			closeDialog();
		}
	}

	@FXML
	private void handleDirectory() throws IOException {
		DirectoryChooser dirChooser = new DirectoryChooser();
		File selectedDirectory = dirChooser.showDialog(dialogStage);
		if (selectedDirectory == null) {
			pathLabel.setText(Constants.OPEN_DIALOG_NO_DIR_SELECTED);
		} else {
			mapSelectedDirectory(selectedDirectory);
			pathLabel.setText(selectedDirectory.getAbsolutePath());
			closeDialog();
		}
	}

	private List<TranslationFile> mapSelectedFiles(List<File> selectedFilesList) {
		return addTranslationFiles(selectedFilesList.stream().map(f -> {
			Path filepath = Paths.get(f.getAbsolutePath());
			String name = filepath.getFileName().toString().replace(Constants.EXTENSION_PROPERTIES, "");
			String pathLiteral = filepath.toString();
			return new TranslationFile(name, PreferencesSingleton.getInstace().getEncoding(), pathLiteral);
		}).collect(Collectors.toList()));
	}

	private List<TranslationFile> mapSelectedDirectory(File selectedDirectory) throws IOException {
		Path path = Paths.get(selectedDirectory.getAbsolutePath());
		return addTranslationFiles(
				Files.list(path).filter(p -> p.toString().endsWith(Constants.EXTENSION_PROPERTIES)).map(filepath -> {
					String name = filepath.getFileName().toString().replace(Constants.EXTENSION_PROPERTIES, "");
					String pathLiteral = filepath.toString();
					return new TranslationFile(name, PreferencesSingleton.getInstace().getEncoding(), pathLiteral);
				}).collect(Collectors.toList()));
	}

	private List<TranslationFile> addTranslationFiles(List<TranslationFile> list) {
		PreferencesSingleton pref = PreferencesSingleton.getInstace();
		if (truncate) {
			pref.truncateAddTranslationFiles(list);
		} else {
			pref.addTranslationFiles(list);
		}
		return list;
	}

	private void closeDialog() throws IOException {
		okSelection = true;
		PreferencesSingleton.getInstace().saveUserPreferences();
		dialogStage.close();
	}

	public boolean isOkSelection() {
		return okSelection;
	}

}
