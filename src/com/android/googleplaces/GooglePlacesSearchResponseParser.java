package com.android.googleplaces;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.googleplaces.GooglePlacesGenericResult.GPEvent;
import com.android.googleplaces.GooglePlacesGenericResult.GPGeometry;

public class GooglePlacesSearchResponseParser extends GooglePlacesRequestsParser {	
	
	@Override
	public GooglePlacesSearchResponse parseResults(JSONObject jsonToBeParsed) throws JSONException {		
		GooglePlacesSearchResponse gpResponse = new GooglePlacesSearchResponse();
		final String[] htmlAttributions = this.getParsedStringArray(jsonToBeParsed, "html_attributions");
		final String statusCheck = this.getJSONStringWithException(jsonToBeParsed, "status");
		
		if (statusCheck == null || !statusCheck.equals("OK")) {
			Log.i("STATUS_NOT_OK", "Looks like the search was unsuccessful");
		} else {
			
			final JSONArray jsonResultsArray = this.getJSONArrayWithException(jsonToBeParsed, "results");
			int resultsCount = jsonResultsArray.length();
			
			for (int resultIter = 0; resultIter < resultsCount; resultIter++) {
				GooglePlacesSearchResult currentResult = new GooglePlacesSearchResult();
				final JSONObject currentJSONObject = this.getJSONObjectWithException(jsonResultsArray, resultIter);
				
				final GPEvent[] events = this.getParsedEvents(currentJSONObject);
				final String iconURLObject = this.getJSONStringWithException(currentJSONObject, "icon");
				final String idObject = this.getJSONStringWithException(currentJSONObject, "id");
				final GPGeometry geometry = this.getParsedGeometry(currentJSONObject);
				final String nameObject = this.getJSONStringWithException(currentJSONObject, "name");
				final String ratingObject = this.getJSONStringWithException(currentJSONObject, "rating");
				final String referenceObject = this.getJSONStringWithException(currentJSONObject, "reference");
				final String[] types = this.getParsedStringArray(currentJSONObject, "types");
				final String vicinityObject = this.getJSONStringWithException(currentJSONObject, "vicinity");
				
				currentResult.setEvents(events);
				currentResult.setIconURL(iconURLObject);
				currentResult.setId(idObject);
				currentResult.setGeometry(geometry);
				currentResult.setName(nameObject);
				currentResult.setRating(ratingObject);
				currentResult.setReference(referenceObject);
				currentResult.setTypes(types);
				currentResult.setVicinity(vicinityObject);
				
				gpResponse.appendGPResult(currentResult);
			}
		}
		gpResponse.setHtmlAttribution(htmlAttributions);
		gpResponse.setStatus(statusCheck);
		
		return gpResponse;
	}
}
