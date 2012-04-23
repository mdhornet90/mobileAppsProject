package com.android.googleplaces;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class GooglePlacesDetailActivity extends Activity {
	private ProgressDialog detailResultsDialog;
	private String reference;
	private String placeLongitude;
	private String placeLatitude;
	private WebView mapView;
	private double currentLongitude;
	private double currentLatitude;
	private TextView nameView;
	private TextView ratingView;
	private TextView addressView;
	private TextView phoneNumberView;
	private TextView websiteView;
	private TextView placesWebsiteView;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = this.getIntent().getStringExtra("reference");
        currentLongitude = this.getIntent().getDoubleExtra("longitude", 0.0);
        currentLatitude = this.getIntent().getDoubleExtra("latitude", 0.0);
        
        setContentView(R.layout.google_places_display_details);
        mapView = (WebView) findViewById(R.id.map_webview);
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        nameView = (TextView) findViewById(R.id.name);
        ratingView = (TextView) findViewById(R.id.rating);
        addressView = (TextView) findViewById(R.id.address);
        phoneNumberView = (TextView) findViewById(R.id.phone_number);
        websiteView = (TextView) findViewById(R.id.website);
        placesWebsiteView = (TextView) findViewById(R.id.places_website);
        
        DetailsRequestTask task = new DetailsRequestTask();
        task.execute(reference);
    }
    
    private class DetailsRequestTask extends AsyncTask<String, Void, GooglePlacesDetailResult> {
    	@Override
    	protected void onPreExecute() {
    		detailResultsDialog = ProgressDialog.show(GooglePlacesDetailActivity.this, "Please wait", "Loading your results");
    	}
		@Override
		protected GooglePlacesDetailResult doInBackground(String... params) {
			String detailsReference = params[0];
			GooglePlacesDetailResponse responseToBeSent = null;
			try {
				GooglePlacesRequestsHandler handler = new GooglePlacesRequestsHandler();
				String FINAL_URL = Constants.PLACES_DETAILS_URL + Constants.FORMAT + "reference=" + detailsReference + "&sensor=true&key=" + Constants.API_KEY;
				JSONObject json = handler.getJSONFromUrl(FINAL_URL); // getting JSON
				Log.i("URL", FINAL_URL);
				GooglePlacesDetailResponseParser parser = new GooglePlacesDetailResponseParser();
				try {
					responseToBeSent = parser.parseResults(json);
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			} catch (Exception e) { 
	        	Log.e("BACKGROUND_PROC", e.getMessage());
	        }
			
			return responseToBeSent.getGpDetailResult();
		}
		
		protected void onPostExecute(GooglePlacesDetailResult result) {
			nameView.setText(result.getName());
			ratingView.setText("Rating: " + result.getRating());
			addressView.setText("Address: " + result.getFormattedAddress());
			phoneNumberView.setText("Phone Number: " + result.getFormattedPhoneNumber());
			websiteView.setText("Official Website: " + result.getWebsite());
			placesWebsiteView.setText("Places website: " + result.getPlacesPageUrl());
			placeLatitude = result.getGeometry().getLocation().getLatitude();
			placeLongitude = result.getGeometry().getLocation().getLongitude();
			setupWebView();
			detailResultsDialog.dismiss();
			Toast toast = Toast.makeText(GooglePlacesDetailActivity.this, "This page is only displayed in portrait mode", 5000);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
    	
    }
    
    private void setupWebView(){
        mapView.setWebViewClient(new WebViewClient());
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.loadUrl(Constants.MAP_URL + "?q=" + placeLatitude + "," + placeLongitude + "&t=k&z=18&f=q");
        //Log.i("MAP_VIEW_URL", Constants.MAP_URL + "?q=" + placeLatitude + "," + placeLongitude + "&t=k&z=18");
    }
}
