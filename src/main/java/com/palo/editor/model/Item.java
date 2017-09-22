package com.palo.editor.model;

import java.util.HashMap;
import java.util.Map;

public class Item implements Comparable<Item> {

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int compareTo(Item anotherItem) {
		return anotherItem.getKey().toLowerCase().compareTo(this.key.toLowerCase());
	}

	public Map<String, String> getValuesMap() {
		return valuesMap;
	}

}