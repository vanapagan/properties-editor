package com.palo.editor.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.palo.editor.MainApp;
import com.palo.editor.model.FileHolder;
import com.palo.editor.model.Item;
import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class RootLayoutController {

	@FXML
	private MenuItem openMenuItem;

	@FXML
	private MenuItem aboutMenuItem;

	private MainApp mainApp;

	public RootLayoutController() {
	}

	@FXML
	private void handleOpenDialog() throws IOException {
		mainApp.showOpenDialog();
		mainApp.setItems(FXCollections.observableArrayList(mainApp.mapProperties().values()));
		mainApp.showEditor();
	}

	@FXML
	private void handleSave() throws FileNotFoundException, IOException {
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(s -> {

			StringBuilder sb = new StringBuilder();
			mainApp.getItems().stream().sorted(Item::compareTo).forEach(item -> {
				String line = String.join(Constants.OPERATOR_EQUALS, item.getKey(), item.fetchValue(s));
				sb.append(line.trim());
				sb.append(Constants.NEW_LINE);
			});
			sb.setLength(sb.length() - 2);

			FileHolder fileholder = PreferencesSingleton.getInstace().getFileHolders().stream()
					.filter(fh -> s.equals(fh.getName())).collect(Collectors.toList()).get(0);
			File file = new File(fileholder.getPath());

			try (OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(file),
					StandardCharsets.UTF_8)) {
				output.write(sb.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
