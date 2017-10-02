package com.palo.editor.view;

import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.editor.model.Item;
import com.palo.util.PreferencesSingleton;

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

		Integer index = null;
		for (int i = 1; i < mainApp.getItemTable().getColumns().size(); i++) {
			TableColumn<Item, ?> column = mainApp.getItemTable().getColumns().get(i);
			if (selectedLanguage.equals(column.getId())) {
				index = i;
			}
		}

		if (index != null) {
			mainApp.getItemTable().getColumns().remove(index.intValue());
		}

		PreferencesSingleton.getInstace().setFileHolders(PreferencesSingleton.getInstace().getFileHolders().stream()
				.filter(fh -> !fh.getName().equals(selectedLanguage)).collect(Collectors.toList()));
		PreferencesSingleton.getInstace().setTranslationsList(PreferencesSingleton.getInstace().getTranslationsList()
				.stream().filter(t -> !t.equals(selectedLanguage)).collect(Collectors.toList()));

		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(lang -> {
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
