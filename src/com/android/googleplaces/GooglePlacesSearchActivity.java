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
import android.os.AsyncTask;
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
	private ProgressDialog searchResultsDialog;
		
	private GooglePlacesSearchResponse gpSearchResponse = null;
	private GooglePlacesSearchResponseAdapter gpAdapter;
	private Button gpsActivateButton;
	
	private LocationManager locationManager;
	private Handler gpsTimeoutHandler;
	private long startTime;
	private Location location;
	private PlacesSearchTask placesSearchTask;
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
        placesSearchTask = new PlacesSearchTask();
        
        gpSearchResponse = new GooglePlacesSearchResponse();
    	gpAdapter = new GooglePlacesSearchResponseAdapter(this, this, R.layout.custom_gp_result_row, gpSearchResponse.getGpSearchResults());
    	setListAdapter(gpAdapter);
        
    	this.getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
			public void onItemClick(AdapterView<?> listView, View customRow, int rowPosition, long rowId) {
            	GooglePlacesSearchActivity.this.vibrator.vibrate(100);
				String referenceToBeSentToDetails = (String) gpAdapter.getItem(rowPosition).getReference();
				double longitudeToBeSentToDetails = location.getLongitude();
				double latitudeToBeSentToDetails = location.getLatitude();
				Intent startDetails = new Intent(GooglePlacesSearchActivity.this.getApplicationContext(), GooglePlacesDetailActivity.class);
				startDetails.putExtra("reference", referenceToBeSentToDetails);
				startDetails.putExtra("longitude", longitudeToBeSentToDetails);
				startDetails.putExtra("latitude", latitudeToBeSentToDetails);
				startActivity(startDetails);
			}
        });
    	
    	Location location = getLocationUpdateOfType("LAST_KNOWN");
		placesSearchTask = new PlacesSearchTask();
		placesSearchTask.execute(location);
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
		
	@Override
	public void onLocationChanged(final Location location) {
		placesSearchTask = new PlacesSearchTask();
		placesSearchTask.execute(location);
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	public Location getLocationUpdateOfType(String updateType) {
		startTime = System.currentTimeMillis();
		if (updateType.equalsIgnoreCase("LAST_KNOWN")) {
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			} else {
				return location;
			}
		} else if (updateType.equalsIgnoreCase("CURRENT")) {
			String providerType = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;	
			locationManager.requestSingleUpdate(providerType, this, getMainLooper());
			locationManager.requestSingleUpdate(providerType, this, getMainLooper());
		}
		
		return null;
	}
	
	private class PlacesSearchTask extends AsyncTask<Location, Void, Void> {
		@Override
		protected Void doInBackground(Location... params) {
			gpsTimeoutHandler.removeCallbacks(checkGPSTimeout);
			locationManager.removeUpdates(GooglePlacesSearchActivity.this);
			location = params[0];
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
			} catch (Exception e) { 
	        	Log.e("BACKGROUND_PROC", ProjectUtils.makeEmptyStringFromNull(e.getMessage()));
	        }
	        return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			searchResultsDialog.setMessage("Populating results...");
            if (gpSearchResponse.getGpSearchResults() != null && gpSearchResponse.getGpSearchResults().size() > 0) {
                gpAdapter.setGooglePlacesSearchResult(gpSearchResponse.getGpSearchResults());
            }
            gpAdapter.notifyDataSetChanged();
            searchResultsDialog.dismiss();
		}
	}
}