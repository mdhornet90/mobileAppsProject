package com.android.googleplaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.googleplaces.GooglePlacesGenericResult.GPEvent;
import com.android.googleplaces.GooglePlacesGenericResult.GPGeometry;

import android.util.Log;
//import com.android.googleplaces.ProjectUtils;

public abstract class GooglePlacesRequestsParser {

	public abstract GooglePlacesGenericResponse parseResults(JSONObject jsonToBeParsed) throws JSONException;
	
	protected String[] getParsedStringArray(JSONObject jsonObjToBeParsed, String arrayName) {
		final JSONArray array = this.getJSONArrayWithException(jsonObjToBeParsed, arrayName);
		if (array == null) {
			return null; //Short-circuit that avoids the hazard of a NullPointerException
		}
		
		final int arrayCount = array.length();
		final ArrayList<String> strings = new ArrayList<String>();
		for (int arrayIter = 0; arrayIter < arrayCount; arrayIter++) {
			strings.add(ProjectUtils.makeEmptyStringFromNull(this.getJSONStringWithException(array, arrayIter)));
		}
		
		return strings.toArray(new String[arrayCount]);
	}

	protected JSONArray getJSONArrayWithException(JSONObject jsonObjToBeHandled, String nameOfObject) {
		JSONArray handledResult = null;
		try {
			handledResult = jsonObjToBeHandled.getJSONArray(nameOfObject);
		} catch (JSONException JSONe) {
			Log.i("NO_" + nameOfObject.toUpperCase(), "The object " + nameOfObject + " does not exist");
		}
		
		return handledResult;
	}

	protected JSONObject getJSONObjectWithException(JSONObject jsonObjToBeHandled, String nameOfObject) {
		JSONObject handledResult = null;
		try {
			handledResult = jsonObjToBeHandled.getJSONObject(nameOfObject);
		} catch (JSONException JSONe) {
			Log.i("NO_" + nameOfObject.toUpperCase(), "The object " + nameOfObject + " does not exist");
		}
			
		return handledResult;
	}

	protected JSONObject getJSONObjectWithException(JSONArray jsonArrayToBeHandled, int indexOfJSONObj) {
		JSONObject handledResult = null;
		try {
			handledResult = jsonArrayToBeHandled.getJSONObject(indexOfJSONObj);
		} catch (JSONException JSONe) {
			Log.i("NO_OBJECT", "The object does not exist");
		}
			
		return handledResult;
	}

	protected String getJSONStringWithException(JSONObject jsonObjToBeHandled, String nameOfObject) {
		String handledResult = null;
		try {
			handledResult = jsonObjToBeHandled.getString(nameOfObject);
		} catch (JSONException JSONe) {
			Log.i("NO_" + nameOfObject.toUpperCase(), "The object " + nameOfObject + " does not exist");
		}
		
		return ProjectUtils.makeEmptyStringFromNull(handledResult);
	}

	private String getJSONStringWithException(JSONArray jsonArrayToBeHandled, int indexOfString) {
		String handledResult = null;
		try {
			handledResult = jsonArrayToBeHandled.getString(indexOfString);
		} catch (JSONException JSONe) {
			Log.i("NO_OBJECT", "The object does not exist");
		}
		
		return ProjectUtils.makeEmptyStringFromNull(handledResult);
	}
	protected GPEvent[] getParsedEvents(final JSONObject jsonObjToBeParsed) {
		final JSONArray eventsArray = this.getJSONArrayWithException(jsonObjToBeParsed, "events");
		if (eventsArray == null) {
			return null; //Short-circuit that avoids the hazard of a NullPointerException
		}
		
		final int eventCount = eventsArray.length();
		final GPEvent[] events = new GPEvent[eventCount];
		
		for (int eventIter = 0; eventIter < eventCount; eventIter++) {
			JSONObject eventObject = this.getJSONObjectWithException(eventsArray, eventIter);
			GPEvent tempEventObject = new GPEvent(this.getJSONStringWithException(eventObject, "event_id"), 
													this.getJSONStringWithException(eventObject, "summary"),
													this.getJSONStringWithException(eventObject, "url"),
													this.getJSONStringWithException(eventObject, "start_time"));
			events[eventIter] = tempEventObject;
		}
		
		return events;
	}

	protected GPGeometry getParsedGeometry(final JSONObject jsonObjToBeParsed) {
		final JSONObject geometryObject = this.getJSONObjectWithException(jsonObjToBeParsed, "geometry");
		final JSONObject locationObject = this.getJSONObjectWithException(geometryObject, "location");
		
		final String longitude = this.getJSONStringWithException(locationObject, "lng");
		final String latitude = this.getJSONStringWithException(locationObject, "lat");
		final String viewport = this.getJSONStringWithException(geometryObject, "viewport");
		
		return new GPGeometry(longitude, latitude, viewport);
	}
}