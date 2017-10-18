package com.palo.editor.view;

import java.io.IOException;
import com.palo.editor.MainApp;
import com.palo.editor.model.Item;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class EditorController {

	@FXML
	private TextField filterField;

	@FXML
	private MenuItem newSingleMenuItem;

	@FXML
	private MenuItem newMultipleMenuItem;

	@FXML
	private Button editButton;

	@FXML
	private Button deleteButton;

	@FXML
	private TableView<Item> itemTable;

	private MainApp mainApp;

	SortedList<Item> sortedData;

	public EditorController() {
	}

	@FXML
	private void initialize() {
		TableColumn<Item, String> keyColumn = new TableColumn<Item, String>("Key");
		keyColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getKey()));
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		keyColumn.setOnEditCommit(e -> {
			String value = e.getOldValue();
			if (!mainApp.getItems().stream().anyMatch(existingItem -> existingItem.getKey().equals(e.getNewValue()))) {
				value = e.getNewValue();
			}
			itemTable.getItems().get(e.getTablePosition().getRow()).setKey(value);
			itemTable.refresh();
			mainApp.addNewChange();
		});
		itemTable.getColumns().add(keyColumn);

		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			String filename = tf.getName();
			TableColumn<Item, String> languageColumn = new TableColumn<Item, String>(filename);
			languageColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().fetchValue(filename)));
			languageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			languageColumn.setOnEditCommit(e -> {
				e.getTableView().getItems().get(e.getTablePosition().getRow()).getValuesMap().put(e.getTableColumn().getText(), e.getNewValue());
				mainApp.addNewChange();
			});
			languageColumn.setId(filename);
			itemTable.getColumns().add(languageColumn);
		});

		itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			boolean status = true;
			if (newSelection != null) {
				status = false;
			}
			toggleModifying(status);
		});

		itemTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		keyColumn.setSortType(TableColumn.SortType.ASCENDING);
		itemTable.getSortOrder().add(keyColumn);

		itemTable.setTableMenuButtonVisible(true);
		
		toggleModifying(true);

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		FilteredList<Item> filteredData = new FilteredList<>(this.mainApp.getItems(), item -> true);
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(item -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String lowerCaseFilter = newValue.toLowerCase();
				if (item.getKey().toLowerCase().contains(lowerCaseFilter) || item.getValuesMap().values().stream()
						.anyMatch(val -> val.toLowerCase().contains(lowerCaseFilter))) {
					return true;
				}
				return false;
			});
		});
		mainApp.setItemTable(itemTable);
		sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(itemTable.comparatorProperty());
		itemTable.setItems(sortedData);
	}

	@FXML
	private void handleAddNew() throws IOException {
		Item item = new Item("");
		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			item.getValuesMap().put(tf.getName(), "");
		});
		boolean okClicked = mainApp.showSingleItemDialog(item, Constants.EDITOR_ADD_NEW_TITLE);
		if (okClicked) {
			if (!mainApp.getItems().stream().anyMatch(existingItem -> existingItem.getKey().equals(item.getKey()))) {
				mainApp.getItems().add(item);
				mainApp.addNewChange();
			}
		}
	}

	@FXML
	private void handleAddMultipleNew() throws IOException {
		Item item = new Item("");
		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			item.getValuesMap().put(tf.getName(), "");
		});

		ObservableList<Item> newItemsList = FXCollections.observableArrayList();
		newItemsList.add(item);
		boolean okClicked = mainApp.showMultipleItemDialog(newItemsList, Constants.EDITOR_ADD_NEW_TITLE, true);
		if (okClicked) {
			newItemsList.stream().forEach(newItem -> {
				if (!mainApp.getItems().stream()
						.anyMatch(existingItem -> existingItem.getKey().equals(newItem.getKey()))) {
					mainApp.getItems().add(newItem);
					mainApp.addNewChange();
				}
			});
		}
	}

	@FXML
	private void handleEdit() throws IOException {
		ObservableList<Item> selectedItemsList = itemTable.getSelectionModel().getSelectedItems();
		if (selectedItemsList != null && !selectedItemsList.isEmpty()) {
			if (selectedItemsList.size() > 1) {
				mainApp.showMultipleItemDialog(selectedItemsList, Constants.EDITOR_EDIT_TITLE_MULTIPLE, false);
			} else {
				mainApp.showSingleItemDialog(selectedItemsList.get(0), Constants.EDITOR_EDIT_TITLE);
			}
		}
		itemTable.refresh();
		mainApp.addNewChange();
	}

	@FXML
	private void handleDelete() {
		mainApp.getItems().removeAll(itemTable.getSelectionModel().getSelectedItems());
		mainApp.addNewChange();
	}
	
	private void toggleModifying(boolean status) {
		editButton.setDisable(status);
		deleteButton.setDisable(status);
	}
	
	public void removeLanguageColumn(String lang) {
		itemTable.getColumns().remove(lang);
		mainApp.addNewChange();
	}

}
