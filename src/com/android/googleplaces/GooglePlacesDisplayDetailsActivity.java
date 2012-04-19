package com.android.googleplaces;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapActivity;

public class GooglePlacesDisplayDetailsActivity extends MapActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String reference = this.getIntent().getStringExtra("reference");
        Log.i("AFTER_NEW_ACTIVITY_STARTS", reference);
        setContentView(R.layout.google_places_display_details);
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
