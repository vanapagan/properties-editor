package com.palo.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.palo.editor.model.TranslationFile;

public class PreferencesSingleton {

	private static PreferencesSingleton INSTANCE = new PreferencesSingleton();
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
	private List<TranslationFile> translationFiles = new ArrayList<>();

	private PreferencesSingleton() {
	}

	public static PreferencesSingleton getInstace() {
		return INSTANCE;
	}

	public boolean addFileHolder(TranslationFile translationFile) {
		return translationFiles.add(translationFile);
	}

	public TranslationFile getTranslationFile(String name) {
		return translationFiles.stream().filter(tf -> name.equals(tf)).findAny().get();
	}

	public List<TranslationFile> getTranslationFiles() {
		return translationFiles;
	}

	public void setTranslationFiles(List<TranslationFile> translationFiles) {
		this.translationFiles = translationFiles;
	}

	public void removeFile(String key) {
		setTranslationFiles(translationFiles.stream().filter(t -> !t.getName().equals(key)).collect(Collectors.toList()));
	}

	public void truncateAll() {
		translationFiles.clear();
	}

	public void saveUserPreferences() throws IOException {
		FileWriter fw = new FileWriter(Constants.PREFERENCES_FILE_LOCATION);
		fw.write(getPreferencesJson());
		fw.close();
	}

	public static String getPreferencesJson() {
		JSONArray jsonArr = new JSONArray();
		PreferencesSingleton.getInstace().getTranslationFiles().stream().forEach(tf -> {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(Constants.PREFERENCES_FILENAME, tf.getName());
			jsonObj.put(Constants.PREFERENCES_PATH, tf.getPath());
			jsonArr.put(jsonObj);
		});
		return jsonArr.toString();
	}
	
	public static String localDateTimeToString(LocalDateTime ldt) {
		return ldt.format(formatter);
	}

}
