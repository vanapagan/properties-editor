package com.palo.editor.view;

import com.palo.editor.model.Item;
import com.palo.editor.model.Translation;
import com.palo.util.PreferencesSingleton;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ItemDialogController {

	@FXML
	private TextField keyField;

	@FXML
	private TableColumn<Item, String> keyCol;

	@FXML
	private TableView<Item> keysTable;

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

	private Stage dialogStage;

	private ObservableList<Item> itemsList;

	private boolean okClicked = false;

	@FXML
	private void initialize() {
		keyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> e) {
				return new SimpleStringProperty(e.getValue().getKey());
			}
		});
		keyCol.setCellFactory(TextFieldTableCell.forTableColumn());
		keyCol.setOnEditCommit(new EventHandler<CellEditEvent<Item, String>>() {
			@Override
			public void handle(CellEditEvent<Item, String> t) {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setKey(t.getNewValue());
			}
		});
		langCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Translation, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Translation, String> e) {
						return new SimpleStringProperty(e.getValue().getLanguage());
					}
				});
		valueCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Translation, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Translation, String> e) {
						return new SimpleStringProperty(e.getValue().getValue());
					}
				});
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
		valueCol.setOnEditCommit(new EventHandler<CellEditEvent<Translation, String>>() {
			@Override
			public void handle(CellEditEvent<Translation, String> t) {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue());
			}
		});
		for (String langName : PreferencesSingleton.getInstace().getTranslationsList()) {
			translationsTable.getItems().add(new Translation(langName, ""));
		}
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setItem(String buttonText, ObservableList<Item> selectedItemsList) {

		this.itemsList = selectedItemsList;

		if (itemsList.size() > 1) {
			keyCol.setText("Keys");
		}
		confirmButton.setText(buttonText);

		ObservableList<Translation> translations = FXCollections.observableArrayList();
		for (String language : PreferencesSingleton.getInstace().getTranslationsList()) {
			String value = itemsList.get(0).getValuesMap().get(language);
			if (itemsList.size() > 1) {
				value = "";
			}
			translations.add(new Translation(language, value));
		}

		keysTable.setItems(itemsList);
		translationsTable.setItems(translations);
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleConfirmation() {
		for (Translation t : translationsTable.getItems()) {
			itemsList.stream().forEach(item -> {
				item.getValuesMap().put(t.getLanguage(), t.getValue());
			});
		}
		okClicked = true;
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

}
