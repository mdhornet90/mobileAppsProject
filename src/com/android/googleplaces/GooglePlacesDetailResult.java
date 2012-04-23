package com.android.googleplaces;

public class GooglePlacesDetailResult extends GooglePlacesGenericResult {
	AddressComponent[] addressComponentArray;
	String formattedAddress;
	String formattedPhoneNumber;
	String internationalPhoneNumber;
	String placesPageUrl;
	String website;
	
	public AddressComponent[] getAddressComponentArray() {
		return addressComponentArray;
	}

	public void setAddressComponentArray(AddressComponent[] addressComponentArray) {
		this.addressComponentArray = addressComponentArray;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public String getFormattedPhoneNumber() {
		return formattedPhoneNumber;
	}

	public void setFormattedPhoneNumber(String formattedPhoneNumber) {
		this.formattedPhoneNumber = formattedPhoneNumber;
	}

	public String getInternationalPhoneNumber() {
		return internationalPhoneNumber;
	}

	public void setInternationalPhoneNumber(String internationalPhoneNumber) {
		this.internationalPhoneNumber = internationalPhoneNumber;
	}

	public String getPlacesPageUrl() {
		return placesPageUrl;
	}

	public void setPlacesPageUrl(String placesPageUrl) {
		this.placesPageUrl = placesPageUrl;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public static class AddressComponent {
		String[] addressComponentTypes;
		String longName;
		String shortName;
		
		public AddressComponent(String[] addressComponentTypes, String longName, String shortName) {
			this.addressComponentTypes = addressComponentTypes;
			this.longName = longName;
			this.shortName = shortName;
		}
		
		public String[] getAddressComponentType() {
			return addressComponentTypes;
		}
		public void setAddressComponentType(String[] addressComponentType) {
			this.addressComponentTypes = addressComponentType;
		}
		public String getLongName() {
			return longName;
		}
		public void setLongName(String longName) {
			this.longName = longName;
		}
		public String getShortName() {
			return shortName;
		}
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
	}
}
