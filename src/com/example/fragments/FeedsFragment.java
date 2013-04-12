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

import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.R;
import com.example.adapters.FeedsAdapter;
import com.example.constants.Constants;
import com.google.gson.Gson;

public class FeedsFragment extends SherlockFragment{
	Context applicationContext;
	ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();
	Boolean isInternetPresent = false;
	ProgressDialog pDialog;
	ListView lv;
	ListAdapter adapter;
	Feeds[] feeds;
	Boolean onDestroyCalled = false;
	ArrayList<HashMap<String, String>> feedsList = new ArrayList<HashMap<String,String>>();
	public static String KEY_DISH_NAME = "dishName";
	public static String KEY_DISH_REVIEW = "reviews";
	public static String KEY_RESTAURANT_NAME = "restaurantName";
	public static String KEY_IMAGE_URL = "imageURL";
	public static String KEY_WHO_RATED = "whoRated";
	public static String KEY_EMOTION_VALUE = "emotionValue";

	public static String GET_FEEDS_URL=Constants.GET_FEEDS_URL;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.feeds_fragment, container, false);
		applicationContext = this.getActivity().getApplicationContext();

		cd = new ConnectionDetector(this.getActivity().getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			alert.showAlertDialog(getActivity(), "Internet Connection Error","Please connect to working Internet connection", false);
			return container;
		}

		lv = (ListView) view.findViewById(R.id.feeds_listview);
		if(onDestroyCalled){
			lv.setAdapter(adapter);
			onDestroyCalled=false;
		}else{
			new LoadFeeds().execute();
		}
		return view;
	}
	@Override
	public void onDestroyView() {
		onDestroyCalled=true;
		super.onDestroy();
	}

	class LoadFeeds extends AsyncTask<String, String, String> {
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
		protected String doInBackground(String... params) {

			String feedsJsonStr = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(GET_FEEDS_URL);
			ResponseHandler<String> handler = new BasicResponseHandler();

			try {
				feedsJsonStr = httpclient.execute(request, handler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Gson gson = new Gson();
			feeds = gson.fromJson(feedsJsonStr, Feeds[].class);

			return null;
		}
		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			getActivity().runOnUiThread(new Runnable() {
				public void run() {

					if(feeds !=null && feeds.length > 0){
						for (int i=0; i<10 ; i++) { //feeds.length
							HashMap<String, String> map = new HashMap<String, String>();
							// Restaurant name
							map.put(KEY_RESTAURANT_NAME, feeds[i].getRestaurant().getName());
							//Dish Name
							map.put(KEY_DISH_NAME, feeds[i].getItem().getDishName());
							//Dish review
							map.put(KEY_DISH_REVIEW, feeds[i].getReview());
							//Dish image url
							map.put(KEY_IMAGE_URL, feeds[i].getImageURL());
							//Get user name
							map.put(KEY_WHO_RATED, feeds[i].getUser().getName());
							//get emotion value
							map.put(KEY_EMOTION_VALUE, displayEmotionMeterValue(feeds[i].getEmotionValue()));
							// adding HashMap to ArrayList
							feedsList.add(map);
						}
						adapter = new FeedsAdapter(applicationContext, feedsList,R.layout.list_feeds,
								new String[] { KEY_WHO_RATED,KEY_DISH_NAME, KEY_RESTAURANT_NAME,KEY_DISH_REVIEW,KEY_IMAGE_URL,KEY_EMOTION_VALUE}, new int[] {
								R.id.who_rated,R.id.dish_image,R.id.emotion_value,R.id.dish_review });

						// Adding data into listview
						lv.setAdapter(adapter);
					}
				}

			});
		}
		private String displayEmotionMeterValue(int val) {
			String emotion="";
			switch (val) {
			case 4:
				emotion = "Pathetic";
				break;
			case 3:
				emotion = "Nahh!!";
				break;
			case 2:
				emotion = "Good";
				break;
			case 1:
				emotion = "Yummyyy";
				break;
			default:
				break;
			}
			return emotion;
		}

	}
}
