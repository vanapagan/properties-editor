package com.palo.editor.view;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.palo.editor.model.TranslationFile;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class PreferencesDialogController {

	@FXML
	private ComboBox<Charset> encodingComboBox;

	@FXML
	private CheckBox trailingLineCheckBox;

	@FXML
	private TableView<TranslationFile> fileTable;

	private ObservableList<Charset> charsetsList;
	private Stage dialogStage;
	private boolean okSelection;
	private boolean reloadNeeded;

	public PreferencesDialogController() {
		charsetsList = FXCollections.observableArrayList();
	}

	public void initialize() {
		charsetsList.add(StandardCharsets.UTF_8);
		charsetsList.add(StandardCharsets.UTF_16);
		charsetsList.add(StandardCharsets.ISO_8859_1);
		charsetsList.add(StandardCharsets.US_ASCII);
		encodingComboBox.getItems().addAll(charsetsList);
		encodingComboBox.setValue(PreferencesSingleton.getInstace().getEncoding());
		encodingComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			reloadNeeded = oldValue != newValue;
		});

		trailingLineCheckBox.selectedProperty().set(PreferencesSingleton.getInstace().isAddTrailingNewLine());

		ObservableList<TableColumn<TranslationFile, ?>> columns = fileTable.getColumns();

		TableColumn<TranslationFile, String> filenameColumn = new TableColumn<TranslationFile, String>(
				Constants.LABEL_FILENAME);
		filenameColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getName()));
		filenameColumn.setSortType(TableColumn.SortType.ASCENDING);
		columns.add(filenameColumn);

		TableColumn<TranslationFile, String> locationColumn = new TableColumn<TranslationFile, String>(
				Constants.LABEL_LOCATION);
		locationColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getPath().toString()));
		columns.add(locationColumn);

		fileTable.getItems().addAll(FXCollections.observableArrayList(PreferencesSingleton.getInstace()
				.getTranslationFiles().stream().map(tf -> tf.replicate()).collect(Collectors.toList())));
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public boolean isOkSelection() {
		return okSelection;
	}

	@FXML
	private void handleConfirmation() {
		okSelection = true;
		PreferencesSingleton pref = PreferencesSingleton.getInstace();
		pref.getTranslationFiles().stream().forEach(tf -> tf.setEncoding(encodingComboBox.getValue()));
		pref.setAddTrailingNewLine(trailingLineCheckBox.isSelected());
		dialogStage.close();
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	private boolean isReloadNeeded() {
		return reloadNeeded;
	}
	
	public boolean reloadNeeded() {
		return isOkSelection() && isReloadNeeded();
	}

}
