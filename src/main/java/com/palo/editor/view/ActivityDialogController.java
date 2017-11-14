package com.palo.editor.view;

import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.util.Action;
import com.palo.util.Constants;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ActivityDialogController {
	
	@FXML
	private TextArea textArea;
	
	@FXML
	private Button closeButton;
	
	private Stage dialogStage;
	
	private boolean okSelection = false;

	public ActivityDialogController() {
	}

	@FXML
	private void initialize() {
	}
	
	@FXML
	private void handleConfirmation() {
		okSelection = true;
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		String activityLog = mainApp.getActionsList().stream().map(Action::toString).collect(Collectors.joining(Constants.NEW_LINE));
		textArea.appendText(activityLog);
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public boolean isOkSelection() {
		return okSelection;
	}
	
}
