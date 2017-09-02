package com.palo.editor.view;

import com.palo.editor.MainApp;
import com.palo.editor.model.Item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class EditorController {

	@FXML
	private TextField filterField;

	@FXML
	private TableView<Item> itemTable;

	private MainApp mainApp;

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
		itemTable.getColumns().add(keyColumn);

		for (final String filename : MainApp.arr) {
			TableColumn<Item, String> languageColumn = new TableColumn<Item, String>(
					filename.replace(".properties", ""));
			languageColumn.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> p) {
							return new SimpleStringProperty(p.getValue().fetchValue(filename));
						}
					});
			itemTable.getColumns().add(languageColumn);
		}

		keyColumn.setSortType(TableColumn.SortType.ASCENDING);
		itemTable.getSortOrder().add(keyColumn);
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

		SortedList<Item> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(itemTable.comparatorProperty());
		itemTable.setItems(sortedData);

	}

}
