package com.example.fragments;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.GPSTracker;
import com.example.R;
import com.example.constants.Constants;

public class GooglePlaces extends SherlockFragment{
	 GPSTracker gps;
	 Activity activity;
	 String requesturl="https://maps.googleapis.com/maps/api/place/search/json";

	public GooglePlaces(){

	}
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    this.activity = activity;
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.places_container,null);
    	
    	double latitude = 0;
    	double longitude = 0;
    	gps = new GPSTracker(activity);
		if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();            
        }else{
            gps.showSettingsAlert();
        }
		requesturl = formURL(latitude,longitude);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(requesturl);
		HttpParams params = new BasicHttpParams();
		params.setParameter("types", "cafe|restaurant");
		params.setParameter("location", latitude+","+longitude);
		params.setParameter("radius","5000");
		params.setParameter("sensor","true");
		params.setParameter("key",Constants.GOOGLE_API_KEY);
		httpGet.setParams(params);
		try{
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity);
			JSONObject jsonObject = new JSONObject(json);
		}catch (Exception e) {
			System.out.println(e);
		}
        return v;

    }
    public String formURL(double lat,double lon){
    	String url = "?types=cafe|restaurant&location=";
    	url  = url+ lat+","+lon;
    	url = url + "&radius=5000";
    	url = url + "&sensor=false";
    	url = url + "&key="+Constants.GOOGLE_API_KEY;
    	url = requesturl+url;
    	return url;
    }

}
