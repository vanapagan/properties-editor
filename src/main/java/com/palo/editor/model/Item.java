package com.palo.editor.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.palo.util.Constants;
import com.palo.util.PreferencesSingleton;

public class Item implements Comparable<Item> {

	private String key;
	private Map<String, String> valuesMap;
	private LocalDateTime createdTimestamp;
	private LocalDateTime modifiedTimestamp;
	
	public Item(String key) {
		this.key = key;
		this.valuesMap = new HashMap<String, String>();
		initTimestamps();
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

	public String getCreatedTimestamp() {
		return PreferencesSingleton.localDateTimeToString(createdTimestamp);
	}

	private void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getModifiedTimestamp() {
		return PreferencesSingleton.localDateTimeToString(modifiedTimestamp);
	}

	private void setModifiedTimestamp(LocalDateTime modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}
	
	public void updateLastModifiedTimestamp() {
		this.modifiedTimestamp = LocalDateTime.now();
	}
	
	private void initTimestamps() {
		LocalDateTime initialTimestamp = LocalDateTime.now();
		setCreatedTimestamp(initialTimestamp);
		setModifiedTimestamp(initialTimestamp);
	}

	@Override
	public int compareTo(Item anotherItem) {
		return key.toLowerCase().compareToIgnoreCase(anotherItem.getKey().toLowerCase());
	}

	public Map<String, String> getValuesMap() {
		return valuesMap;
	}

	public String getKeyValuePair(String language) {
		return String.join(Constants.OPERATOR_EQUALS, getKey(), fetchValue(language).trim());
	}

}