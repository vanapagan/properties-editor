package com.palo.util;

import java.util.ArrayList;
import java.util.List;

public class PreferencesSingleton {

	private static PreferencesSingleton INSTANCE = new PreferencesSingleton();

	private String location;
	private List<String> translationsList = new ArrayList<>();
	
	private PreferencesSingleton() {
	}

	public static PreferencesSingleton getInstace() {
		return INSTANCE;
	}
	
	public List<String> getTranslationsList() {
		return translationsList;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
