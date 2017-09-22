package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.editor.model.FileHolder;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class EditorController {

	@FXML
	private TextField filterField;

	@FXML
	private Button saveButton;

	@FXML
	private Button newSingleButton;

	@FXML
	private Button newMultipleButton;

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
		keyColumn.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow())
				.setKey(e.getNewValue()));
		itemTable.getColumns().add(keyColumn);

		PreferencesSingleton
				.getInstace()
				.getTranslationsList()
				.stream()
				.forEach(
						filename -> {
							TableColumn<Item, String> languageColumn = new TableColumn<Item, String>(filename);
							languageColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().fetchValue(
									filename)));
							languageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
							languageColumn.setOnEditCommit(e -> e.getTableView().getItems()
									.get(e.getTablePosition().getRow()).getValuesMap()
									.put(e.getTableColumn().getText(), e.getNewValue()));
							itemTable.getColumns().add(languageColumn);
						});

		itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				editButton.setDisable(false);
				deleteButton.setDisable(false);
			} else {
				editButton.setDisable(true);
				deleteButton.setDisable(true);
			}
		});

		itemTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		keyColumn.setSortType(TableColumn.SortType.ASCENDING);
		itemTable.getSortOrder().add(keyColumn);

		itemTable.setEditable(true);

		editButton.setDisable(true);
		deleteButton.setDisable(true);

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
				if (item.getKey().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false;
			});
		});
		sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(itemTable.comparatorProperty());
		itemTable.setItems(sortedData);
	}

	@FXML
	private void handleAddNew() throws IOException {
		Item item = new Item("");
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(lang -> {
			item.getValuesMap().put(lang, "");
		});
		boolean okClicked = mainApp.showSingleItemDialog(item, Constants.EDITOR_ADD_NEW_TITLE);
		if (okClicked) {
			mainApp.getItems().add(item);
		}
	}

	@FXML
	private void handleAddMultipleNew() throws IOException {
		Item item = new Item("");
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(lang -> {
			item.getValuesMap().put(lang, "");
		});

		ObservableList<Item> newItemsList = FXCollections.observableArrayList();
		newItemsList.add(item);
		boolean okClicked = mainApp.showMultipleItemDialog(newItemsList, Constants.EDITOR_ADD_NEW_TITLE, true);
		if (okClicked) {
			newItemsList.stream().forEach(newItem -> {
				mainApp.getItems().add(newItem);
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
	}

	@FXML
	private void handleDelete() {
		itemTable.getSelectionModel().getSelectedItems().stream()
				.forEach(selectedItem -> mainApp.getItems().remove(selectedItem));
	}

	@FXML
	private void handleSaveButton() throws FileNotFoundException, IOException {
		for (String s : PreferencesSingleton.getInstace().getTranslationsList()) {
			Map<String, String> map = mainApp.getItems().stream().collect(Collectors.toMap(Item::getKey, item -> {
				String value = item.fetchValue(s);
				if (value == null) {
					value = "";
				}
				return value;
			}));

			SortedProperties properties = new SortedProperties();
			properties.putAll(map);

			FileHolder fileholder = PreferencesSingleton.getInstace().getFileHolders().stream()
					.filter(fh -> s.equals(fh.getName())).collect(Collectors.toList()).get(0);
			File file = new File(fileholder.getPath());

			try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
				properties.store(output, null);
			}
			
		}
	}
}

class SortedProperties extends Properties {

	private static final long serialVersionUID = 3838181836191268646L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector<String> keyList = new Vector<String>();
		while (keysEnum.hasMoreElements()) {
			keyList.add((String) keysEnum.nextElement());
		}
		Collections.sort(keyList, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
			}
		});
		
		return keyList.elements();
	}

}
