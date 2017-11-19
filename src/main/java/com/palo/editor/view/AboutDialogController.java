package com.palo.editor.view;

import java.io.IOException;
import java.util.Properties;

import com.palo.util.PreferencesSingleton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutDialogController implements IDialogController {

	@FXML
	private Label nameLabel;

	@FXML
	private Label versionLabel;

	@FXML
	private Label authorLabel;

	private Stage dialogStage;

	@FXML
	private void initialize() throws IOException {
		Properties prop = PreferencesSingleton.getInstace().getProperties();
		nameLabel.setText(prop.getProperty("name"));
		versionLabel.setText(prop.getProperty("version"));
		authorLabel.setText(prop.getProperty("author"));
	}
	
	@FXML
	private void handleConfirmation() {
		dialogStage.close();
	}

	@Override
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
