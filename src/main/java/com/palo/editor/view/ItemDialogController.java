package com.palo.editor.view;

import com.palo.editor.MainApp;
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
	
	private Item item;
	
	private boolean okClicked = false;
	
	@FXML
    private void initialize() {
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
	
	public void setItem(Item item, String buttonText) {
		
		this.item = item;
		
		if ("MULTIPLE".equals(item.getKey())) {
			keyField.setDisable(true);
		}
		keyField.setText(item.getKey());
		confirmButton.setText(buttonText);
		
		ObservableList<Translation> translations = FXCollections.observableArrayList();
		for (String s : PreferencesSingleton.getInstace().getTranslationsList()) {
			translations.add(new Translation(s, item.getValuesMap().get(s)));
		}
		
		translationsTable.setItems(translations);
	}
	
	public boolean isOkClicked() {
		return okClicked;
	}
	
	@FXML
	private void handleConfirmation() {
		if (isInputValid()) {
			
			item.setKey(keyField.getText());
			
			for (String s : PreferencesSingleton.getInstace().getTranslationsList()) {
				String value = null;
				for (Translation t : translationsTable.getItems()) {
					if (t.getLanguage().equals(s)) {
						value = t.getValue();
					}
				}
				item.getValuesMap().put(s, value);
			}
			
			okClicked = true;
			dialogStage.close();
		} else {
			System.out.println("Not valid");
		}
	}
	
	@FXML
    private void handleCancel() {
        dialogStage.close();
    }
	
	private boolean isInputValid() {
		if (keyField.isDisabled() || !keyField.getText().isEmpty()) {
			return true;
		}
		return false;
	}
	
}
