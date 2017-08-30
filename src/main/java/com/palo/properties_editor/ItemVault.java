package com.palo.properties_editor;

import java.util.Map;
import java.util.TreeMap;

public class ItemVault {
	
	private Map<String, Item> itemsMap;
	
	public ItemVault() {
		itemsMap = new TreeMap<String, Item>();
	}
	
	public Item addItem(String key, Item item) {
		return itemsMap.put(key, item);
	}

	public Map<String, Item> getItemsMap() {
		return itemsMap;
	}

}
