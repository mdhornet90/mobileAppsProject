package com.android.googleplaces;

import java.util.ArrayList;

public class GooglePlacesSearchResponse extends GooglePlacesGenericResponse {
	private ArrayList<GooglePlacesSearchResult> gpSearchResults;
	public GooglePlacesSearchResponse() {
		gpSearchResults = new ArrayList<GooglePlacesSearchResult>();
	}
	public ArrayList<GooglePlacesSearchResult> getGpSearchResults() {
		return gpSearchResults;
	}
	public void setGpSearchResults(ArrayList<GooglePlacesSearchResult> gpSearchResults) {
		this.gpSearchResults = gpSearchResults;
	}
	
	public void appendGPResult(GooglePlacesSearchResult gpResultObj) {
		this.gpSearchResults.add(gpResultObj);
	}
}
