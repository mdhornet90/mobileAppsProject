package com.android.googleplaces;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class GooglePlacesSearchResponseAdapter extends ArrayAdapter<GooglePlacesSearchResult> {
	private GooglePlacesSearchActivity googlePlacesSearchActivity;
	private ArrayList<GooglePlacesSearchResult> items;
	private HashMap<String, Bitmap> iconHashtable = new HashMap<String, Bitmap>();
    public GooglePlacesSearchResponseAdapter(GooglePlacesSearchActivity googlePlacesSearchActivity, Context context, int textViewResourceId, ArrayList<GooglePlacesSearchResult> items) {
            super(context, textViewResourceId, items);
			this.googlePlacesSearchActivity = googlePlacesSearchActivity;
            this.items = items;
    }
    
    public void setGooglePlacesSearchResult (ArrayList<GooglePlacesSearchResult> items) {
    	this.items.clear();
    	this.items.addAll(items);
    }
    	
    private void refreshDataSet(Void...params) {
    	GooglePlacesSearchResult[] tempItems = this.items.toArray(new GooglePlacesSearchResult[this.items.size()]);
    	items.clear();
    	ArrayList<GooglePlacesSearchResult> newlist = new ArrayList<GooglePlacesSearchResult>();
    	for (int i = 0; i < tempItems.length; i++) {
    		newlist.add(tempItems[i]);
    	}
    	items.addAll(newlist);
    	this.notifyDataSetChanged();
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.googlePlacesSearchActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.custom_gp_result_row, null);
        }
        GooglePlacesSearchResult gpSearchResult = items.get(position);
    	
        if (gpSearchResult != null) {
    		ImageView iconImageView = (ImageView) v.findViewById(R.id.icon);
            TextView nameTextView = (TextView) v.findViewById(R.id.name_text_view);
            TextView vicinityTextView = (TextView) v.findViewById(R.id.vicinity_text_view);
            TextView ratingTextView = (TextView) v.findViewById(R.id.rating_text_view);
            if (iconImageView != null) { //http://stackoverflow.com/questions/3996702/display-imagefrom-url-in-imageview
                Bitmap bitmap = this.iconHashtable.get(gpSearchResult.getIconURL());
                if (bitmap == null) {
                	String url = gpSearchResult.getIconURL();
                	ImageFetchTask task = new ImageFetchTask();
                	task.execute(url);	
                	bitmap = task.getBitmapToBeStored();
                }
                iconImageView.setImageBitmap(bitmap);    
            }
            if(nameTextView != null){
            	nameTextView.setText(gpSearchResult.getName());
            }
            if (vicinityTextView != null) {
            	vicinityTextView.setText(gpSearchResult.getVicinity());                            
            }
            if(ratingTextView != null){
            	ratingTextView.setText("Rating: " + gpSearchResult.getRating());
            }
        }
        return v;
    }
    
    private class ImageFetchTask extends AsyncTask<String, Void, Bitmap> {
    	private Bitmap bitmapToBeStored;
    	private String url;
		@Override
		protected Bitmap doInBackground(String... url) {
			try {
				this.url = url[0];
    	        URL iconUrl = new URL(this.url);
    	        HttpGet httpRequest = new HttpGet(iconUrl.toURI());

    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

    	        HttpEntity entity = response.getEntity();
    	        BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
    	        InputStream input = b_entity.getContent();
    	        Bitmap bmp = BitmapFactory.decodeStream(input);
    	        setBitmapToBeStored(bmp);
    	        return bmp;
    	    } catch (MalformedURLException e) {
    	        Log.e("log", "bad url", e);
    	    } catch (IOException e) {
    	        Log.e("log", "io error", e);
    	    } catch (URISyntaxException e) {
    	    	Log.e("log", "syntax error", e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap bmp) {
			if (bmp != null) {
				iconHashtable.put(url, bmp);
            	this.cancel(true);
				GooglePlacesSearchResponseAdapter.this.refreshDataSet((Void) null);
			}
		}
		public Bitmap getBitmapToBeStored() {
			return bitmapToBeStored;
		}

		public void setBitmapToBeStored(Bitmap bitmapToBeStored) {
			this.bitmapToBeStored = bitmapToBeStored;
		}
    }
}