package com.palo.editor.model;

import java.nio.charset.Charset;

public class TranslationFile {
	
	private String name;
	private Charset encoding;
	private String path;
	
	public TranslationFile(String name, Charset encoding, String path) {
		this.name = name;
		this.encoding = encoding;
		this.path = path;
	}
	
	public TranslationFile replicate() {
		return new TranslationFile(name, encoding, path);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Charset getEncoding() {
		return encoding;
	}
	
	public String getEncodingName() {
		return encoding.displayName();
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}
	
}
