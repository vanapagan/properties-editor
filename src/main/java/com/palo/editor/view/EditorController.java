package com.palo.editor.view;

import java.io.IOException;
import com.palo.editor.MainApp;
import com.palo.editor.model.Item;
import com.palo.editor.model.TranslationFile;
import com.palo.util.Action;
import com.palo.util.Action.Type;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	
	@FXML
	private Label activityLabel;

	private MainApp mainApp;

	public EditorController() {
	}

	@FXML
	private void initialize() {
		ObservableList<TableColumn<Item, ?>> columns = itemTable.getColumns();
		
		TableColumn<Item, String> keyColumn = new TableColumn<Item, String>("Key");
		keyColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getKey()));
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		keyColumn.setSortType(TableColumn.SortType.ASCENDING);
		keyColumn.setOnEditCommit(e -> {
			String oldValue = e.getOldValue();
			String newValue = e.getNewValue();
			if (newValue != null && !newValue.isEmpty() && mainApp.getItems().stream().noneMatch(existingItem -> existingItem.getKey().equals(newValue))) {
				Item item = itemTable.getItems().get(e.getTablePosition().getRow());
				item.setKey(newValue);
				item.updateLastModifiedTimestamp();
				mainApp.addNewAction(new Action(Type.EDIT_KEY, oldValue + "' to '" + newValue));
			}
			itemTable.refresh();
		});
		columns.add(keyColumn);

		PreferencesSingleton.getInstace().getTranslationFiles().stream().map(TranslationFile::getName).forEach(fn -> {
			TableColumn<Item, String> languageColumn = new TableColumn<Item, String>(fn);
			languageColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().fetchValue(fn)));
			languageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			languageColumn.setOnEditCommit(e -> {
				Item item = e.getTableView().getItems().get(e.getTablePosition().getRow());
				item.getValuesMap().put(e.getTableColumn().getText(), e.getNewValue());
				item.updateLastModifiedTimestamp();
				mainApp.addNewAction(new Action(Type.EDIT_VALUE, item.getKey()));
				itemTable.refresh();
			});
			languageColumn.setId(fn);
			itemTable.getColumns().add(languageColumn);
		});
		
		if (!columns.isEmpty()) {
			TableColumn<Item, String> createdColumn = new TableColumn<Item, String>("Created");
			createdColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getCreatedTimestamp()));
			createdColumn.setVisible(false);
			
			TableColumn<Item, String> modifiedColumn = new TableColumn<Item, String>("Last Modified");
			modifiedColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getModifiedTimestamp()));
			modifiedColumn.setVisible(false);
			
			columns.add(createdColumn);
			columns.add(modifiedColumn);
		}
		
		itemTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			boolean status = true;
			if (newSelection != null) {
				status = false;
			}
			toggleModifying(status);
		});
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
		this.mainApp.setItemTable(itemTable);
		SortedList<Item> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(itemTable.comparatorProperty());
		itemTable.setItems(sortedData);
		
		activityLabel.textProperty().bind(mainApp.getActivityProperty());
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
				mainApp.addNewAction(new Action(Type.NEW_ITEM, item));
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
				}
			});
			mainApp.addNewAction(new Action(Type.NEW_ITEM, newItemsList));
		}
	}

	@FXML
	private void handleEdit() throws IOException {
		ObservableList<Item> selectedItemsList = itemTable.getSelectionModel().getSelectedItems();
		if (selectedItemsList != null && !selectedItemsList.isEmpty()) {
			if (selectedItemsList.size() > 1) {
				mainApp.showMultipleItemDialog(selectedItemsList, Constants.EDITOR_EDIT_TITLE_MULTIPLE, false);
				mainApp.addNewAction(new Action(Type.EDIT_ITEM, selectedItemsList));
			} else {
				mainApp.showSingleItemDialog(selectedItemsList.get(0), Constants.EDITOR_EDIT_TITLE);
				mainApp.addNewAction(new Action(Type.EDIT_ITEM, selectedItemsList.get(0)));
			}
		}
		itemTable.refresh();
	}

	@FXML
	private void handleDelete() {
		mainApp.addNewAction(new Action(Type.REMOVE_ITEM, itemTable.getSelectionModel().getSelectedItems()));
		mainApp.getItems().removeAll(itemTable.getSelectionModel().getSelectedItems());
	}
	
	private void toggleModifying(boolean status) {
		editButton.setDisable(status);
		deleteButton.setDisable(status);
	}

}
