package com.palo.editor.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import com.palo.editor.MainApp;
import com.palo.editor.model.Item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class EditorController {

	@FXML
	private TextField filterField;

	@FXML
	private Button saveButton;
	
	@FXML
	private Button newButton;
	
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
		keyColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> e) {
						return new SimpleStringProperty(e.getValue().getKey());
					}
				});
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		keyColumn.setOnEditCommit(new EventHandler<CellEditEvent<Item, String>>() {
			@Override
			public void handle(CellEditEvent<Item, String> t) {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).setKey(t.getNewValue());
			}
		});
		itemTable.getColumns().add(keyColumn);

		for (String filename : MainApp.translationsList) {
			TableColumn<Item, String> languageColumn = new TableColumn<Item, String>(filename);
			languageColumn.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> p) {
							return new SimpleStringProperty(
									p.getValue().fetchValue(filename));
						}
					});
			languageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
			languageColumn.setOnEditCommit(new EventHandler<CellEditEvent<Item, String>>() {
				@Override
				public void handle(CellEditEvent<Item, String> t) {
					t.getTableView().getItems().get(t.getTablePosition().getRow()).getValuesMap()
							.put(t.getTableColumn().getText(), t.getNewValue());
				}

			});
			itemTable.getColumns().add(languageColumn);
		}
		
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
	private void handleAddNewItem() {
		Item item = new Item("");
		for (String s : mainApp.getTranslationsList()) {
			item.getValuesMap().put(s, "");
		}
		boolean okClicked = mainApp.showItemDialog(item, "New", "Add");
		if (okClicked) {
			mainApp.getItems().add(item);
		}
	}

	@FXML
	private void handleEditItem() {
		/*
		Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			mainApp.showItemDialog(selectedItem, "Edit");
		}*/
		ObservableList<Item> selectedItemsList = itemTable.getSelectionModel().getSelectedItems();
		if (selectedItemsList != null && !selectedItemsList.isEmpty()) {
			Item item = selectedItemsList.get(0);
			String title = "Edit";
			String button = "Edit";
			if (selectedItemsList.size() > 1) {
				item = new Item("MULTIPLE");
				title = "Edit Multiple";
				
				for (String s : mainApp.getTranslationsList()) {
					item.getValuesMap().put(s, "");
				}
			}
			for (String s : mainApp.getTranslationsList()) {
				item.getValuesMap().put(s, "");
			}
			boolean okClicked = mainApp.showItemDialog(item, title, button);
			if (okClicked) {
				for (Item selectedItem : selectedItemsList) {
					for (String s : mainApp.getTranslationsList()) {
						selectedItem.getValuesMap().put(s, item.getValuesMap().get(s.replace(".properties", "")));
					}
				}
			}
		}
		itemTable.refresh();
	}
	
	@FXML
	private void handleDeleteItem() {
		ObservableList<Item> selectedItemsList = itemTable.getSelectionModel().getSelectedItems();
		for (Item selectedItem : selectedItemsList) {
			mainApp.getItems().remove(selectedItem);
		}
	}

	@FXML
	private void handleSaveButton() {
		ObservableList<Item> items = itemTable.getItems();
		for (String s : mainApp.getTranslationsList()) {
			Map<String, String> map = new TreeMap<>();
			for (Item item : items) {
				String key = item.getKey();
				String value = item.fetchValue(s);
				if (value == null) {
					value = "";
				}
				map.put(key, value);
			}
			SortedProperties properties = new SortedProperties();
			properties.putAll(map);

			URL url = getClass().getResource("../" + s);
			File file = new File(url.getFile());

			OutputStreamWriter output = null;

			try {
				output = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
				properties.store(output, null);
			} catch (IOException io) {
				io.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
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
		Collections.sort(keyList);
		return keyList.elements();
	}

}
