package com.android.googleplaces;

public class GooglePlacesDetailResponse extends GooglePlacesGenericResponse {
	private GooglePlacesDetailResult gpDetailResult;
	
	public GooglePlacesDetailResult getGpDetailResult() {
		return gpDetailResult;
	}
	public void setGpDetailResult(GooglePlacesDetailResult gpDetailResult) {
		this.gpDetailResult = gpDetailResult;
	}
}
