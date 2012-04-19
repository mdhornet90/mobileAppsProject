package com.android.googleplaces;

public class GooglePlacesDetailResult extends GooglePlacesGenericResult {
	AddressComponent[] addressComponentArray;
	String formattedAddress;
	String formattedPhoneNumber;
	String internationalPhoneNumber;
	String placesPageUrl;
	String website;
	
	public static class AddressComponent {
		String[] addressComponentType;
		String longName;
		String shortName;
	}
}
