package com.android.googleplaces;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class GooglePlacesSearchActivity extends ListActivity implements LocationListener {
	private Vibrator vibrator;
	private ProgressDialog searchResultsDialog = null; //Threading and UI stuff credited to http://www.softwarepassion.com/android-series-custom-listview-items-and-adapters/
	
		
	private GooglePlacesSearchResponse gpSearchResponse = null;
	private GooglePlacesSearchResponseAdapter gpAdapter;
	private Runnable viewGooglePlacesSearchResults;
	private Button gpsActivateButton;
	
	private LocationManager locationManager;
	private Handler gpsTimeoutHandler;
	private long startTime;
	//Location location;
	
	Runnable checkGPSTimeout = new Runnable() {
		@Override
		public void run() {
			long start = startTime;
			long millis = System.currentTimeMillis() - start;
			int seconds = (int) (millis / 1000) % 60;
			
			if (seconds >= 30) { //If the GPS still hasn't updated after 30 seconds, the app switches over to network locator mode
				locationManager.removeUpdates(GooglePlacesSearchActivity.this);
				searchResultsDialog.setMessage("GPS Timed out, using network to find your location...");
				locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, GooglePlacesSearchActivity.this, getMainLooper());
				gpsTimeoutHandler.removeCallbacks(this);
			} else {
				gpsTimeoutHandler.postDelayed(this, 1000);
			}
		}
	};
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        gpsTimeoutHandler = new Handler();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
        gpSearchResponse = new GooglePlacesSearchResponse();
    	gpAdapter = new GooglePlacesSearchResponseAdapter(this, this, R.layout.custom_gp_result_row, gpSearchResponse.getGpSearchResults());
    	setListAdapter(gpAdapter);
        
    	this.getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> listView, View customRow, int rowPosition, long rowId) {
            	GooglePlacesSearchActivity.this.vibrator.vibrate(100);
				String referenceToBeSentToDetails = (String) gpAdapter.getItem(rowPosition).getReference();
				Intent startDetails = new Intent(GooglePlacesSearchActivity.this.getApplicationContext(), GooglePlacesDisplayDetailsActivity.class);
				startDetails.putExtra("reference", referenceToBeSentToDetails);
				startActivity(startDetails);
			}
        });
    	
    	viewGooglePlacesSearchResults = new Runnable() {
    		@Override
            public void run() {
    			Location location = getLocationUpdateOfType("LAST_KNOWN");
				gpsTimeoutHandler.removeCallbacks(checkGPSTimeout);
				gpsTimeoutHandler.postDelayed(checkGPSTimeout, 1000);
				getGooglePlacesSearchResults(location);  			
            }
    	};
    	Thread thread =  new Thread(null, viewGooglePlacesSearchResults, "GooglePlacesBackground");
        thread.start();
        searchResultsDialog = ProgressDialog.show(GooglePlacesSearchActivity.this, "Please wait...", "Getting your last known location...", true);
	}
    
	@Override
    public void onStart() {
    	super.onStart();
    	vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	gpsActivateButton = (Button) findViewById(R.id.gps_activate_button);
    	gpsActivateButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				vibrator.vibrate(100);
				getLocationUpdateOfType("CURRENT");
				gpsTimeoutHandler.removeCallbacks(checkGPSTimeout);
				gpsTimeoutHandler.postDelayed(checkGPSTimeout, 1000);
				searchResultsDialog = ProgressDialog.show(GooglePlacesSearchActivity.this, "Please wait...", "Getting your current location...", true);
            }
    	});
    }
	//TODO Convert this to an AsyncTask at some point
	private void getGooglePlacesSearchResults(Location location) {
		gpsTimeoutHandler.removeCallbacks(this.checkGPSTimeout);
		locationManager.removeUpdates(this);
		try {
			GooglePlacesRequestsHandler handler = new GooglePlacesRequestsHandler();
			String FINAL_URL = Constants.PLACES_SEARCH_URL + Constants.FORMAT + "location=" + location.getLatitude() + "," + location.getLongitude() +"&radius=5000&types=bar%7Cmovie_theater%7Cnight_club%7Crestaurant%7Cshopping_mall&sensor=true&key=" + Constants.API_KEY;
			JSONObject json = handler.getJSONFromUrl(FINAL_URL); // getting JSON
			Log.i("URL", FINAL_URL);
			GooglePlacesSearchResponseParser parser = new GooglePlacesSearchResponseParser();
			try {
				gpSearchResponse = parser.parseResults(json);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
	        Thread.sleep(5000);
	        Log.i("ARRAY", ""+ gpSearchResponse.getGpSearchResults().size());
		} catch (Exception e) { 
        	Log.e("BACKGROUND_PROC", e.getMessage());
        }
        runOnUiThread(returnResults);
	}
	
	private Runnable returnResults = new Runnable() {
        @Override
        public void run() {
        	searchResultsDialog.setMessage("Populating results...");
            if (gpSearchResponse.getGpSearchResults() != null && gpSearchResponse.getGpSearchResults().size() > 0) {
                gpAdapter.setGooglePlacesSearchResult(gpSearchResponse.getGpSearchResults());
            }
            gpAdapter.notifyDataSetChanged();
            searchResultsDialog.dismiss();
        }
	};
		
	@Override
	public void onLocationChanged(final Location location) {
		locationManager.removeUpdates(this);
		gpsTimeoutHandler.removeCallbacks(this.checkGPSTimeout);
		viewGooglePlacesSearchResults = new Runnable() {
    		@Override
            public void run() {
    			getGooglePlacesSearchResults(location);
            }
    	};
    	Thread thread =  new Thread(null, viewGooglePlacesSearchResults, "GooglePlacesBackground");
        thread.start();
		Log.i("GPSUpdate", "got location!");
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	public Location getLocationUpdateOfType(String updateType) {
		String providerType = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;	
		locationManager.requestSingleUpdate(providerType, this, getMainLooper());
		startTime = System.currentTimeMillis();
		if (updateType.equalsIgnoreCase("LAST_KNOWN")) {
			return locationManager.getLastKnownLocation(providerType);
		} else if (updateType.equalsIgnoreCase("CURRENT")) {
			locationManager.requestSingleUpdate(providerType, this, getMainLooper());
		}
		
		return null;
	}
}