package com.palo.editor.view;

import com.palo.editor.MainApp;
import com.palo.editor.model.Item;
import com.palo.util.PreferencesSingleton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class RemoveLanguageDialogController {
	
	@FXML
	private ComboBox<String> languageCombo;
	
	@FXML
	private Button confirmationButton;
	
	@FXML
	private Button cancelButton;

	private boolean okClicked = false;
	
	private Stage dialogStage;
	
	private MainApp mainApp;


	public RemoveLanguageDialogController() {
	}
	
	@FXML
	private void initialize() {
	}

	@FXML
	private void handleConfirmation() {
		okClicked = true;
		String selectedLanguage = languageCombo.getValue();
		mainApp.getItems().parallelStream().forEach(item -> {
			item.getValuesMap().remove(selectedLanguage);
		});
		TableColumn<Item, String> tableCol;
		mainApp.getItemTable().getColumns().stream().forEach(column -> {
			String columnId = column.getId();
			if (selectedLanguage.equals(columnId)) {
			}
		});
		
		mainApp.getItemTable().getColumns().remove(selectedLanguage);
		mainApp.getItemTable().refresh();
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(lang-> {
			languageCombo.getItems().add(lang);
		});
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
