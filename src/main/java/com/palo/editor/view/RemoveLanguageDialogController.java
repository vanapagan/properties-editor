package com.palo.editor.view;

import com.palo.editor.MainApp;
import com.palo.editor.model.Item;
import com.palo.util.Action;
import com.palo.util.PreferencesSingleton;
import com.palo.util.Action.Type;

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

	private boolean okClicked;

	private Stage dialogStage;

	private MainApp mainApp;

	public RemoveLanguageDialogController() {
	}

	@FXML
	private void initialize() {
		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			languageCombo.getItems().add(tf.getName());
		});
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

		PreferencesSingleton.getInstace().removeFile(selectedLanguage);
		mainApp.addNewAction(new Action(Type.REMOVE_LANGUAGE, selectedLanguage));
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
