package com.android.googleplaces;

public class GooglePlacesGenericResponse {

	private String[] htmlAttributions;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String[] getHtmlAttribution() {
		return htmlAttributions;
	}

	public void setHtmlAttribution(String[] htmlAttributions) {
		this.htmlAttributions = htmlAttributions;
	}

}