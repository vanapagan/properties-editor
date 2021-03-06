package com.palo.editor.view;

import com.palo.editor.model.Item;
import com.palo.editor.model.Translation;
import com.palo.util.PreferencesSingleton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class SingleItemDialogController {

	@FXML
	private TextField keyField;

	@FXML
	private TableView<Translation> translationsTable;

	@FXML
	private TableColumn<Translation, String> langCol;

	@FXML
	private TableColumn<Translation, String> valueCol;

	@FXML
	private Button confirmButton;

	@FXML
	private Button cancelButton;

	private Item item;

	private boolean okClicked = false;

	private Stage dialogStage;

	@FXML
	private void initialize() {
		langCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getLanguage()));
		valueCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getValue()));
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
		valueCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow())
				.setValue(t.getNewValue()));
	}

	public void setItem(Item item) {
		this.item = item;
		keyField.setText(item.getKey());
		ObservableList<Translation> translations = FXCollections.observableArrayList();
		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			String value = this.item.getValuesMap().get(tf.getName());
			translations.add(new Translation(tf.getName(), value));
		});
		translationsTable.setItems(translations);
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleConfirmation() {
		item.setKey(keyField.getText());
		translationsTable.getItems().stream().forEach(t -> {
			item.getValuesMap().put(t.getLanguage(), t.getValue());
			item.updateLastModifiedTimestamp();
		});
		okClicked = true;
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
