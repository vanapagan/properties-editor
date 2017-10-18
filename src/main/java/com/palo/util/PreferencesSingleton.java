package com.palo.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.palo.editor.model.FileHolder;

public class PreferencesSingleton {

	private static PreferencesSingleton INSTANCE = new PreferencesSingleton();
	private List<String> translationsList = new ArrayList<>();
	private Map<String, FileHolder> fileHolders = new HashMap<>();

	private PreferencesSingleton() {
	}

	public static PreferencesSingleton getInstace() {
		return INSTANCE;
	}

	public FileHolder addFileHolder(FileHolder fileHolder) {
		String name = fileHolder.getName();
		translationsList.add(name);
		return fileHolders.put(name, fileHolder);
	}

	public List<String> getTranslationsList() {
		return Collections.unmodifiableList(translationsList);
	}

	public void setTranslationsList(List<String> translationsList) {
		this.translationsList = translationsList;
	}

	public FileHolder getFileHolder(String key) {
		return fileHolders.get(key);
	}

	public void removeFile(String key) {
		removeFileHolder(key);
		setTranslationsList(translationsList.stream().filter(t -> !t.equals(key)).collect(Collectors.toList()));
	}

	private FileHolder removeFileHolder(String key) {
		return fileHolders.remove(key);
	}

	public void truncateAll() {
		fileHolders.clear();
		translationsList.clear();
	}
	
	public void saveUserPreferences() throws IOException {
		FileWriter fw = new FileWriter(Constants.PREFERENCES_FILE_LOCATION);
		fw.write(getPreferencesJson());
		fw.close();
	}
	
	public static String getPreferencesJson() {
		JSONArray jsonArr = new JSONArray();
		PreferencesSingleton.getInstace().getTranslationsList().stream().forEach(lang -> {
			FileHolder fh = PreferencesSingleton.getInstace().getFileHolder(lang);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(Constants.PREFERENCES_FILENAME, fh.getName());
			jsonObj.put(Constants.PREFERENCES_PATH, fh.getPath());
			jsonArr.put(jsonObj);
		});
		return jsonArr.toString();
	}

}
