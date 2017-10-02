package com.palo.util;

import java.util.ArrayList;
import java.util.List;

import com.palo.editor.model.FileHolder;

public class PreferencesSingleton {

	private static PreferencesSingleton INSTANCE = new PreferencesSingleton();

	private List<FileHolder> fileHolders = new ArrayList<>();
	private List<String> translationsList = new ArrayList<>();
	
	private PreferencesSingleton() {
	}

	public static PreferencesSingleton getInstace() {
		return INSTANCE;
	}
	
	public List<String> getTranslationsList() {
		return translationsList;
	}
	
	public void setTranslationsList(List<String> translationsList) {
		this.translationsList = translationsList;
	}

	public List<FileHolder> getFileHolders() {
		return fileHolders;
	}
	
	public void setFileHolders(List<FileHolder> fileHolders) {
		this.fileHolders = fileHolders;
	}
	
	public void truncateAll() {
		fileHolders.clear();
		translationsList.clear();
	}

}
