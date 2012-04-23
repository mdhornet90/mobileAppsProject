package com.android.googleplaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.googleplaces.GooglePlacesDetailResult.AddressComponent;
import com.android.googleplaces.GooglePlacesGenericResult.GPEvent;
import com.android.googleplaces.GooglePlacesGenericResult.GPGeometry;

public class GooglePlacesDetailResponseParser extends GooglePlacesRequestsParser {

	@Override
	public GooglePlacesDetailResponse parseResults(JSONObject jsonToBeParsed) throws JSONException {
		GooglePlacesDetailResponse gpDetailResponse = new GooglePlacesDetailResponse();
		final String[] htmlAttributions = this.getParsedStringArray(jsonToBeParsed, "html_attributions");
		final String statusCheck = this.getJSONStringWithException(jsonToBeParsed, "status");
		
		if (statusCheck == null || !statusCheck.equals("OK")) {
			Log.i("STATUS_NOT_OK", "Looks like the search was unsuccessful");
		} else {
			GooglePlacesDetailResult currentResult = new GooglePlacesDetailResult();
			final JSONObject jsonResultObject = this.getJSONObjectWithException(jsonToBeParsed, "result");
			
			final AddressComponent[] addressComponents = this.getAddressComponents(jsonResultObject);
			final GPEvent[] events = this.getParsedEvents(jsonResultObject);
			final String formattedAddress = this.getJSONStringWithException(jsonResultObject, "formatted_address");
			final String formattedPhoneNumber = this.getJSONStringWithException(jsonResultObject, "formatted_phone_number");
			final GPGeometry geometry = this.getParsedGeometry(jsonResultObject);
			final String iconURL = this.getJSONStringWithException(jsonResultObject, "icon");
			final String id = this.getJSONStringWithException(jsonResultObject, "id");
			final String internationalPhoneNumber = this.getJSONStringWithException(jsonResultObject, "international_phone_number");
			final String name = this.getJSONStringWithException(jsonResultObject, "name");
			final String rating = this.getJSONStringWithException(jsonResultObject, "rating");
			final String referenceObject = this.getJSONStringWithException(jsonResultObject, "reference");
			final String[] types = this.getParsedStringArray(jsonResultObject, "types");
			final String url = this.getJSONStringWithException(jsonResultObject, "url");
			final String vicinityObject = this.getJSONStringWithException(jsonResultObject, "vicinity");
			final String website = this.getJSONStringWithException(jsonResultObject, "website");
			
			currentResult.setAddressComponentArray(addressComponents);
			currentResult.setEvents(events);
			currentResult.setFormattedAddress(formattedAddress);
			currentResult.setFormattedPhoneNumber(formattedPhoneNumber);
			currentResult.setGeometry(geometry);
			currentResult.setIconURL(iconURL);
			currentResult.setId(id);
			currentResult.setInternationalPhoneNumber(internationalPhoneNumber);
			currentResult.setName(name);
			currentResult.setRating(rating);
			currentResult.setReference(referenceObject);
			currentResult.setTypes(types);
			currentResult.setPlacesPageUrl(url);
			currentResult.setVicinity(vicinityObject);
			currentResult.setWebsite(website);
			
			gpDetailResponse.setGpDetailResult(currentResult);
			gpDetailResponse.setHtmlAttribution(htmlAttributions);
			gpDetailResponse.setStatus(statusCheck);
			
			return gpDetailResponse;
		}
		return null;
	}
	
	private AddressComponent[] getAddressComponents(final JSONObject jsonObjToBeParsed) {
		final JSONArray addressComponentsArray = this.getJSONArrayWithException(jsonObjToBeParsed, "address_components");
		if (addressComponentsArray == null) {
			return null; //Short-circuit that avoids the hazard of a NullPointerException
		}
		
		final int addressComponentCount = addressComponentsArray.length();
		final AddressComponent[] addressComponents = new AddressComponent[addressComponentCount];
		
		for (int addressComponentsIter = 0; addressComponentsIter < addressComponentCount; addressComponentsIter++) {
			JSONObject addressComponentObject = this.getJSONObjectWithException(addressComponentsArray, addressComponentsIter);
			AddressComponent tempAddressComponent = new AddressComponent(this.getParsedStringArray(addressComponentObject, "types"),
																		this.getJSONStringWithException(addressComponentObject, "long_name"),
																		this.getJSONStringWithException(addressComponentObject, "short_name"));
			addressComponents[addressComponentsIter] = tempAddressComponent;
		}
		
		return addressComponents;
	}
}
