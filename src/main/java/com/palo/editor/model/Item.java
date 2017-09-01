package com.palo.editor.model;

import java.util.HashMap;
import java.util.Map;

public class Item {

	private String key;
	private Map<String, String> valuesMap;
	
	public Item(String key) {
		this.key = key;
		this.valuesMap = new HashMap<String, String>();
	}
	
	public String addNewValue(String key, String value) {
		return valuesMap.put(key, value);
	}
	
	public String fetchValue(String key) {
		return valuesMap.get(key);
	}
	
	public int compareTo(Item anotherItem) {
		return this.key.compareTo(anotherItem.getKey());
	}

	public String getKey() {
		return key;
	}

}