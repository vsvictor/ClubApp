package com.aod.clubapp.communicaton.datamodel;

import com.google.gson.annotations.SerializedName;

public class SearchOptionItem implements CharSequence {
	@SerializedName("id")
	long id;
	
	@SerializedName("name")
	String name;

	public SearchOptionItem(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public char charAt(int index) {		
		return name.charAt(index);
	}

	@Override
	public int length() {
		return name.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return name.subSequence(start, end);
	}

	public String toString() {
		return name;
	}
}
