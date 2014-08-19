package com.aod.clubapp;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.aod.clubapp.communicaton.datamodel.SearchOptionItem;
import com.aod.clubapp.communicaton.datamodel.SearchOptions;

public class SelectSeachCategoriesActivity extends AuraBaseActivity {

	Spinner spCategory;
	Spinner spCousine;
	Spinner spAverageBill;
	Spinner spMetro;
	EditText edQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_categories);
		
		getActionBar().hide();
		edQuery = (EditText)findViewById(R.id.query_text);
		spCategory = (Spinner)findViewById(R.id.sp_category);		
		spCousine = (Spinner)findViewById(R.id.sp_cousine);
		spAverageBill = (Spinner)findViewById(R.id.sp_bill);
		spMetro = (Spinner)findViewById(R.id.sp_metro);
		
		findViewById(R.id.icon_filter_search).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				doOnFilterClick();
			}
		});
		
		edQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					doOnFilterClick();
		            return true;
		        }
		        return false;
			}
		});
		fillContent();
	}
	
	@Override
	public void onDataServiceConnected() {
		super.onDataServiceConnected();
		fillContent();
	}
	
	private void fillContent() {
		if(getDataSession() == null)
			return;
		SearchOptions opt = getDataSession().getSearchOptions();
 		int pos;
		
 		fillSpinner(opt.getPlaceCategories(), spCategory, R.string.search_category);
		pos = opt.getPositionPlaceCategoriesPos();
		if(pos >= 0){
			spCategory.setSelection(pos + 1);
		} else {
			spCategory.setSelection(0);
		}
 		
		fillSpinner(opt.getCuisines(), spCousine, R.string.search_cousine);
		pos = opt.getPositionCousines();
		if(pos >= 0){
			spCousine.setSelection(pos + 1);
		} else {
			spCousine.setSelection(0);
		}
		
		fillSpinner(opt.getAverageBills(), spAverageBill, R.string.search_bill);
		pos = opt.getAverageBillPos();
		if(pos >= 0){
			spAverageBill.setSelection(pos + 1);
		} else {
			spAverageBill.setSelection(0);
		}
		
		fillSpinner(opt.getMetros(), spMetro, R.string.search_metro);
		pos = opt.getPositionMetroPos();
		if(pos >= 0){
			spMetro.setSelection(pos + 1);
		} else {
			spMetro.setSelection(0);
		}
	}
	
	private void fillSpinner(SearchOptionItem[] data, Spinner sp, int hindId){
		ArrayList<SearchOptionItem> items = new ArrayList<SearchOptionItem>(Arrays.asList(data));
		SearchOptionItem emptyItem = new SearchOptionItem(-1, getString(hindId));
		items.add(0, emptyItem);
		ArrayAdapter<SearchOptionItem> adapter = new ArrayAdapter<SearchOptionItem>(this, android.R.layout.simple_spinner_dropdown_item,items);
		sp.setAdapter(adapter);
	}

	public void doOnFilterClick(){
		SearchOptions opt = getDataSession().getSearchOptions();
		
		SearchOptionItem item = (SearchOptionItem)spCategory.getSelectedItem();
		opt.setPlaceCategory(item.getId());
		
		item = (SearchOptionItem)spAverageBill.getSelectedItem();
		opt.setAverageBill(item.getId());
		
		item = (SearchOptionItem)spCousine.getSelectedItem();
		opt.setCuisine(item.getId());
		
		item = (SearchOptionItem)spMetro.getSelectedItem();
		opt.setMetro(item.getId());
		
		String qery = edQuery.getText().toString();
		setResult(RESULT_OK);
		setResult(RESULT_OK, new Intent().putExtra(PARAM_SEARCH_QUERY, qery));
		finish();
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	@Override
	public void onDataServiceDisConnected() {
	}
	
}
