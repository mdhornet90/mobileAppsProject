package com.android.googleplaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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
			strings.add(this.getJSONStringWithException(array, arrayIter));
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
		
		return handledResult;
	}

	private String getJSONStringWithException(JSONArray jsonArrayToBeHandled, int indexOfString) {
		String handledResult = null;
		try {
			handledResult = jsonArrayToBeHandled.getString(indexOfString);
		} catch (JSONException JSONe) {
			Log.i("NO_OBJECT", "The object does not exist");
		}
		
		return handledResult;
	}

}