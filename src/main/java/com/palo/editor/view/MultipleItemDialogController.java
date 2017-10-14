package com.palo.editor.view;

import java.util.stream.Collectors;

import com.palo.editor.model.Item;
import com.palo.editor.model.Translation;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class MultipleItemDialogController {

	@FXML
	private TableView<Item> keysTable;

	@FXML
	private TableColumn<Item, String> keyCol;
	
	@FXML
	private TextArea keysArea;

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

	@FXML
	private Button addKeyButton;

	@FXML
	private Button removeKeyButton;
	
	@FXML
	private ToggleButton toggleButton;

	private Stage dialogStage;

	private ObservableList<Item> itemsList;

	private boolean okClicked = false;

	private boolean isNew = false;
	
	@FXML
	private void initialize() {
		keyCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getKey()));
		keyCol.setCellFactory(TextFieldTableCell.forTableColumn());
		keyCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow())
				.setKey(t.getNewValue()));
		langCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getLanguage()));

		valueCol.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getValue()));
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
		valueCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow())
				.setValue(t.getNewValue()));

		PreferencesSingleton.getInstace().getTranslationsList()
				.forEach(langName -> translationsTable.getItems().add(new Translation(langName, "")));

		keysTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			removeKeyButton.setDisable(isNew && newSelection != null ? false : true);
		});
		keysTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		removeKeyButton.setDisable(true);
	}

	public void setItemsList(ObservableList<Item> selectedItemsList, boolean isNew) {
		this.itemsList = selectedItemsList;
		this.isNew = isNew;

		if (!isNew) {
			addKeyButton.setVisible(false);
			removeKeyButton.setVisible(false);
			toggleButton.setVisible(false);
		}

		ObservableList<Translation> translations = FXCollections.observableArrayList();
		PreferencesSingleton.getInstace().getTranslationsList().stream()
				.forEach(language -> translations.add(new Translation(language, "")));

		keysTable.setItems(itemsList);
		translationsTable.setItems(translations);
	}

	@FXML
	public void handleAddKey() {
		handleAddKey("");
	}
	
	public void handleAddKey(String key) {
		Item item = new Item(key);
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(s -> item.getValuesMap().put(s, ""));
		keysTable.getItems().add(item);
	}

	@FXML
	public void handleRemoveKey() {
		itemsList.removeAll(keysTable.getSelectionModel().getSelectedItems());
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleConfirmation() {
		if (keysArea.isVisible()) {
			migrateToTableView();
		}
		translationsTable.getItems().stream().forEach(t -> {
			itemsList.stream().forEach(item -> {
				item.getValuesMap().put(t.getLanguage(), t.getValue());
			});
		});
		okClicked = true;
		dialogStage.close();
	}
	
	@FXML
	private void toggle() {
		boolean state = keysTable.isVisible();
		switchBetween(!state, state);
		if (state) {
			keysArea.setText(keysTable.getItems().stream().map(item -> item.getKey()).collect(Collectors.joining("\n")));
			toggleButton.setText("TextArea");
		} else {
			toggleButton.setText("Table");
			migrateToTableView();
		}
	}
	
	private void switchBetween(boolean tableState, boolean areaState) {
		keysTable.setVisible(tableState);
		keysArea.setVisible(areaState);
	}

	
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	private void migrateToTableView() {
		String[] keysArr = keysArea.getText().split(Constants.NEW_LINE);
		for (int i = 0; i < keysArr.length; i++) {
			String key = keysArr[i].trim();
			if (!key.isEmpty() && !itemsList.stream().anyMatch(item -> item.getKey().equals(key))) {
				handleAddKey(key);
			}
		}
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
