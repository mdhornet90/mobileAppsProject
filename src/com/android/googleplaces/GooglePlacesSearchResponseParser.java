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

	private GPGeometry getParsedGeometry(final JSONObject jsonObjToBeParsed) {
		final JSONObject geometryObject = this.getJSONObjectWithException(jsonObjToBeParsed, "geometry");
		final JSONObject locationObject = this.getJSONObjectWithException(geometryObject, "location");
		
		final String longitude = this.getJSONStringWithException(locationObject, "lng");
		final String latitude = this.getJSONStringWithException(locationObject, "lat");
		final String viewport = this.getJSONStringWithException(geometryObject, "viewport");
		
		return new GPGeometry(longitude, latitude, viewport);
	}
	
	private GPEvent[] getParsedEvents(final JSONObject jsonObjToBeParsed) {
		final JSONArray eventsArray = this.getJSONArrayWithException(jsonObjToBeParsed, "events");
		if (eventsArray == null) {
			return null; //Short-circuit that avoids the hazard of a NullPointerException
		}
		
		final int eventCount = eventsArray.length();
		final GPEvent[] events = new GPEvent[eventCount];
		
		for (int eventIter = 0; eventIter < eventCount; eventIter++) {
			JSONObject eventObject = this.getJSONObjectWithException(eventsArray, eventIter);
			events[eventIter].setEventID(this.getJSONStringWithException(eventObject, "event_id"));
			events[eventIter].setSummary(this.getJSONStringWithException(eventObject, "summary"));
			events[eventIter].setEventURL(this.getJSONStringWithException(eventObject, "url"));
		}
		
		return events;
	}
}
