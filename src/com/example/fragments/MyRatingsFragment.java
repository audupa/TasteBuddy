package com.example.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.AlertDialogManager;
import com.example.ConnectionDetector;
import com.example.Feeds;
import com.example.MyRatings;
import com.example.R;
import com.example.User;
import com.example.adapters.FeedsAdapter;
import com.example.adapters.MyRatingsAdapter;
import com.example.constants.Constants;
import com.facebook.android.Facebook;
import com.google.gson.Gson;

public class MyRatingsFragment extends SherlockFragment{
	Context applicationContext;
	ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();
	Boolean isInternetPresent=false;
	ProgressDialog pDialog;
	ListView lv;
	ListAdapter adapter=null;
	MyRatings[] myRatings;
	Boolean onDestroyCalled = false;
	ArrayList<HashMap<String, String>> myRatingsList = new ArrayList<HashMap<String,String>>();
	public static String GET_IRATED_URL=Constants.GET_IRATED_URL;
	public static String KEY_DISH_NAME = "dishName";
	public static String KEY_DISH_REVIEW = "reviews";
	public static String KEY_RESTAURANT_NAME = "restaurantName";
	public static String KEY_IMAGE_URL = "imageURL";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_ratings_fragment, container, false);
		applicationContext = this.getActivity().getApplicationContext();

		cd = new ConnectionDetector(this.getActivity().getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			alert.showAlertDialog(this.getActivity(), "Internet Connection Error","Please connect to working Internet connection", false);
			return container;
		}

		lv = (ListView) view.findViewById(R.id.myrating_listview);
		if(onDestroyCalled && adapter!=null){
			lv.setAdapter(adapter);
			onDestroyCalled=false;
		}else{
			new LoadMyRatings().execute();
		}
		return view;
	}
	@Override
	public void onDestroyView() {
		onDestroyCalled=true;
		super.onDestroy();
	}

	class LoadMyRatings extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			String myRatingsJsonStr = null;
			String url = GET_IRATED_URL;
			String facebookId = getFacebookId();
			url = url+facebookId;
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			ResponseHandler<String> handler = new BasicResponseHandler();
			try {
				myRatingsJsonStr = httpclient.execute(request, handler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(myRatingsJsonStr);
			//JSONArray json;
			Gson gson = new Gson();
			myRatings = gson.fromJson(myRatingsJsonStr, MyRatings[].class);

			return null;
		}
		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			getActivity().runOnUiThread(new Runnable() {
				public void run() {

					if(myRatings !=null && myRatings.length > 0){
						for (int i=0; i< myRatings.length ; i++) { //feeds.length
							HashMap<String, String> map = new HashMap<String, String>();
							// Restaurant name
							map.put(KEY_RESTAURANT_NAME, myRatings[i].getRestaurant().getName());
							//Dish Name
							map.put(KEY_DISH_NAME, myRatings[i].getItem().getDishName());
							//Dish review
							map.put(KEY_DISH_REVIEW, myRatings[i].getReview());
							//Dish image url
							map.put(KEY_IMAGE_URL, myRatings[i].getImageURL());
							// adding HashMap to ArrayList
							myRatingsList.add(map);
						}
						adapter = new MyRatingsAdapter(applicationContext, myRatingsList,R.layout.list_my_ratings,
								new String[] { KEY_DISH_NAME, KEY_RESTAURANT_NAME,KEY_DISH_REVIEW,KEY_IMAGE_URL}, new int[] {
								R.id.dish_name, R.id.restaurant_name,R.id.dish_review,R.id.dish_image });
						// Adding data into listview
						lv.setAdapter(adapter);
					}

				}
			});
		}
		private String getFacebookId(){
			User user = new User();
			String facebookId="";
			try {
				user = getUserObject();
				facebookId = user.getFaceboookId();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return facebookId;
		}
		private User getUserObject() throws JSONException, IOException {
			SharedPreferences mPrefs;
			User userObj = new User();
			Facebook facebook = new Facebook(Constants.APP_ID);
			mPrefs = applicationContext.getSharedPreferences(Constants.PREFERENCE_FILENAME,applicationContext.MODE_PRIVATE);
			String access_token = mPrefs.getString("access_token", null);
			long expires = mPrefs.getLong("access_expires", 0);
			if (access_token != null) {
				facebook.setAccessToken(access_token);
			}
			if (expires != 0) {
				facebook.setAccessExpires(expires);
			}
			JSONObject me = new JSONObject(facebook.request("me"));
			String id = me.getString("id");
			userObj.setName(me.getString("name"));
			userObj.setFaceboookId(id); // My ID
			userObj.setAccessToken(facebook.getAccessToken());
			userObj.setDeviceToken("12343465");
			return userObj;
		}
	}
}
