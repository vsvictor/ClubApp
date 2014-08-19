package com.aod.clubapp.communicaton.datamodel;

import java.net.URLEncoder;

import android.text.TextUtils;

import com.aod.clubapp.communicaton.auraapi.BaseGetApiCommand;
import com.google.gson.annotations.SerializedName;

public class SearchOptions {
	@SerializedName("metros")
	SearchOptionItem[] metros;
	
	@SerializedName("cuisines")
	SearchOptionItem[] cuisines;
	
	@SerializedName("average_bills")
	SearchOptionItem[] averageBills;
	
	@SerializedName("place_categories")
	SearchOptionItem[] placeCategories;
	
	transient long metro = -1;
	transient long cuisine = -1;
	transient long averageBill = -1;
	transient long placeCategory = -1;
	
	public SearchOptionItem[] getMetros() {
		return metros;
	}
	public void setMetros(SearchOptionItem[] metros) {
		this.metros = metros;
	}
	public SearchOptionItem[] getCuisines() {
		return cuisines;
	}
	public void setCuisines(SearchOptionItem[] cuisines) {
		this.cuisines = cuisines;
	}
	public SearchOptionItem[] getAverageBills() {
		return averageBills;
	}
	public void setAverageBills(SearchOptionItem[] averageBills) {
		this.averageBills = averageBills;
	}
	public SearchOptionItem[] getPlaceCategories() {
		return placeCategories;
	}
	public void setPlaceCategories(SearchOptionItem[] placeCategories) {
		this.placeCategories = placeCategories;
	}
	public long getMetro() {
		return metro;
	}
	public void setMetro(long metro) {
		this.metro = metro;
	}
	public long getCuisine() {
		return cuisine;
	}
	public void setCuisine(long cuisine) {
		this.cuisine = cuisine;
	}
	public long getAverageBill() {
		return averageBill;
	}
	public void setAverageBill(long averageBill) {
		this.averageBill = averageBill;
	}
	public long getPlaceCategory() {
		return placeCategory;
	}
	public void setPlaceCategory(long placeCategory) {
		this.placeCategory = placeCategory;
	}
	
	public int getPositionMetroPos(){
		return idToPosition(metros, metro);
	}
	
	public int getPositionCousines(){
		return idToPosition(cuisines, cuisine);
	}
	
	public int getAverageBillPos(){
		return idToPosition(averageBills, averageBill);
	}

	public int getPositionPlaceCategoriesPos(){
		return idToPosition(placeCategories, placeCategory);	
	}
	
	private int idToPosition(SearchOptionItem[] data, long id){
		for(int i = 0; i < data.length; i++){
			if(data[i].getId() == id)
				return i;
		}
		return -1;
	}
	private static final String URLBASE =  AuraDataSession.BASE_URL_OLD + "places?";
	
	public String createSearchUrl(String searchQery, String authTooken) {
		//http://fixapp-clubs.herokuapp.com/api/v1/places
		//?auth_token=dde9bc2fc9039f641dc4e73329741637
		//&average_bill_id=2&cuisine_id=0&metro_id=0&place_category_id=2&q=vin

		StringBuilder builder = new StringBuilder();
		builder.append(URLBASE)
		.append("auth_token=")
		.append(authTooken);
		
		if(placeCategory > 0){
			builder.append("&place_category_id=").append(placeCategory);
		}
		if(metro > 0){
			builder.append("&metro_id=").append(metro);
		} 
		if(cuisine > 0){
			builder.append("&cuisine_id=").append(cuisine);
		}
		if(averageBill > 0){
			builder.append("&average_bill_id=").append(averageBill);
		}
		if(!TextUtils.isEmpty(searchQery)){
			builder.append("&q=").append( URLEncoder.encode(searchQery));
		}
		
		return builder.toString();
	}
}
