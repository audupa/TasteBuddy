package com.example.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.AlertDialogManager;
import com.example.ConnectionDetector;
import com.example.FoodItem;
import com.example.FragmentTabsPager;
import com.example.HomePageActivity;
import com.example.R;
import com.example.Restaurant;
import com.example.SharedItem;
import com.example.User;
import com.example.constants.Constants;
import com.facebook.android.Facebook;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ShareDishActivity extends SherlockActivity implements AnimationListener {
	final int PICTURE_ACTIVITY = 1;
	final int NEARBY_PLACES_ACTIVITY = 2;
	final int REVIEW_ACTIVITY = 3;
	private String uuid="";
	Intent takePictureIntent;
	//private String listview_array[] = new String[20];
	private ArrayList<String> listview_array = new ArrayList<String>();
	private String itemSelected = "";
	private String itemIdSelected;
	private int emotionValue=-1;
	private int healthValue;
	private String placeSelected="";
	private String placeSelectedReference;
	private String reviews="";
	private AutoCompleteTextView autoComplete;
	HashMap<String, String> map = new HashMap<String, String>();
	private Facebook facebook;
	private String GET_DISHES_URL = Constants.GET_DISHES_URL;
	private String SAVE_ITEM_URL = Constants.SAVE_ITEM_URL;
	private String UPLOAD_IMAGE_URL = Constants.UPLOAD_IMAGE_URL;
	public static String KEY_RESTAURANT = "restaurant";
	public static String KEY_REVIEW = "review";
	private Restaurant restaurant = null;
	ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();
	Boolean isInternetPresent = false;
	SharedItem sharedItem = new SharedItem();
	ProgressDialog pDialog;
	Activity act;
	
	AlertDialog.Builder error;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_dish_activity);
		//setContentView(R.layout.activity_maps);
		act = this;
		cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            alert.showAlertDialog(this, "Internet Connection Error","Please connect to working Internet connection", false);
            return;
        }
        
//		if (android.os.Build.VERSION.SDK_INT > 9) {
//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//					.permitAll().build();
//			StrictMode.setThreadPolicy(policy);
//		}
		new GetDishes().execute();
//		AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.autocomplete_dishes_list);
//		new GetDishes().execute();
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_dropdown_item_1line, listview_array);
//		autoComplete.setAdapter(adapter);
//		autoComplete
//				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//					public void onItemClick(AdapterView<?> parent, View view,
//							int position, long id) {
//						itemSelected = parent.getItemAtPosition(position)
//								.toString();
//						itemIdSelected = map.get(itemSelected);
//					}
//				});

		ImageView mImageView = (ImageView) findViewById(R.id.dish_pic);
		mImageView.setVisibility(View.INVISIBLE);

		ImageView cameraButton = (ImageView) findViewById(R.id.camera_button);
		cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent(PICTURE_ACTIVITY);
			}
		});

		ImageView emotionMeter = (ImageView) findViewById(R.id.taste);
		ImageView emotion1 = (ImageView) findViewById(R.id.emo1);
		ImageView emotion2 = (ImageView) findViewById(R.id.emo2);
		ImageView emotion3 = (ImageView) findViewById(R.id.emo3);
		ImageView emotion4 = (ImageView) findViewById(R.id.emo4);
		List<ImageView> emotionList = new ArrayList<ImageView>();
		emotionList.add(emotion1);
		emotionList.add(emotion2);
		emotionList.add(emotion3);
		emotionList.add(emotion4);
		emotionMeter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showEmotionMeter();
			}
		});
		for (int i = 0; i < emotionList.size(); i++) {
			ImageView emotion = emotionList.get(i);
			final int j = i;
			emotion.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					emotionValue = getEmotionMeterValue(view);
					displayEmotionMeterValue(emotionValue);
				}
			});
		}
		
		//Health Meter handler
		ImageView healthMeter = (ImageView) findViewById(R.id.health);
		ImageView health1 = (ImageView) findViewById(R.id.health1);
		ImageView health2 = (ImageView) findViewById(R.id.health2);
		ImageView health3 = (ImageView) findViewById(R.id.health3);
		ImageView health4 = (ImageView) findViewById(R.id.health4);
		List<ImageView> healthList = new ArrayList<ImageView>();
		healthList.add(health1);
		healthList.add(health2);
		healthList.add(health3);
		healthList.add(health4);
		healthMeter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showHealthMeter();
			}
		});
		for (int i = 0; i < healthList.size(); i++) {
			ImageView health = healthList.get(i);
			final int j = i;
			health.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					healthValue = getHealthMeterValue(view);
					displayHealthMeterValue(healthValue);
				}
			});
		}


		ImageView location = (ImageView) findViewById(R.id.location);
		location.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getNearbyPlaces();
			}
		});

		ImageButton shareButton = (ImageButton) findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareOnFacebook();
			}
		});
		//Review
		ImageView reviewButton = (ImageView)findViewById(R.id.reviewButton);
		reviewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getReview();
			}
		});
		error = new AlertDialog.Builder(this);
		error.setIcon(R.drawable.info_icon);
		error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     });
	}

	public void onStart()
    {
       super.onStart();
       FlurryAgent.onStartSession(this,Constants.FLURRY_API_KEY);
       // your code
    }

    public void onStop()
    {
       super.onStop();
       FlurryAgent.onEndSession(this);
       // your code
    }
    
	public ArrayList<String> getDishesItems() {
		String result = callGetDishesSerivice();
		String list[][] = new String[1000][2];
		try {
			JSONArray jsonArray = new JSONArray(result);
			int i = 0;
			for (i = 0; i < jsonArray.length(); i++) {
				JSONObject objJson = jsonArray.getJSONObject(i);
				String name = objJson.getString("name");
				String id = objJson.getString("id");
				// TO DO populate the ids''s
				list[i][0] = name;
				list[i][1] = id;
			}
			//String list1[] = new String[i];
			ArrayList<String> list1 = new ArrayList<String>();

			for (int j = 0; j < i; j++) {
				//list1[j] = list[j][0];
				list1.add(list[j][0]);
				map.put(list[j][0], list[j][1]);
			}
			return list1;

		} catch (JSONException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		return null;
	}

	// Get list of all the dishes
	public String callGetDishesSerivice() {
		String dishes = null;
		try {

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(GET_DISHES_URL);
			ResponseHandler<String> handler = new BasicResponseHandler();
			dishes = httpclient.execute(request, handler);
			System.out.println(dishes);

			// Toast.makeText(this, returned, Toast.LENGTH_LONG).show();

		} catch (ClientProtocolException e) {
			Toast.makeText(this, "There was an issue Try again later",
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, "There was an IO issue Try again later",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		return dishes.toString();
	}

	private void dispatchTakePictureIntent(int actionCode) {
		takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, actionCode);
	}

	private void displayDishImage(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap mImageBitmap = (Bitmap) extras.get("data");

		ImageView cameraButton = (ImageView) findViewById(R.id.camera_button);
		cameraButton.setVisibility(View.INVISIBLE);

		ImageView mImageView = (ImageView) findViewById(R.id.dish_pic);
		mImageView.setVisibility(View.VISIBLE);

		mImageView.setImageBitmap(mImageBitmap);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		uuid = UUID.randomUUID().toString();
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + uuid + ".jpg");
		try {
			file.createNewFile();
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case NEARBY_PLACES_ACTIVITY:
				showSelectedLocation(data);
				break;
			case PICTURE_ACTIVITY:
				displayDishImage(data);
				break;
			case REVIEW_ACTIVITY:
				if (data.getExtras().containsKey(KEY_REVIEW)) {
					reviews = data.getExtras().get(KEY_REVIEW).toString();
				} 
				break;
			}
		}
	}

	private void showEmotionMeter() {
		hideHealthMeter();
		LinearLayout emotionMeter = (LinearLayout) findViewById(R.id.emotion_meter);
		emotionMeter.setVisibility(View.VISIBLE);
		Animation movement;
		movement = AnimationUtils.loadAnimation(this,
				R.drawable.animation_top_to_bottom);
		movement.reset();
		movement.setFillAfter(true);
		movement.setAnimationListener(this);
		emotionMeter.startAnimation(movement);
		movement.setFillAfter(false);
	}
	private void showHealthMeter() {
		hideEmotionMeter();
		LinearLayout healthMeter = (LinearLayout) findViewById(R.id.health_meter);
		healthMeter.setVisibility(View.VISIBLE);
		Animation movement;
		movement = AnimationUtils.loadAnimation(this,
				R.drawable.animation_top_to_bottom);
		movement.reset();
		movement.setFillAfter(true);
		movement.setAnimationListener(this);
		healthMeter.startAnimation(movement);
		movement.setFillAfter(false);
	}

	private void hideEmotionMeter() {
		LinearLayout emotionMeter = (LinearLayout) findViewById(R.id.emotion_meter);
		emotionMeter.setVisibility(View.INVISIBLE);
	}
	private void hideHealthMeter() {
		LinearLayout emotionMeter = (LinearLayout) findViewById(R.id.health_meter);
		emotionMeter.setVisibility(View.INVISIBLE);
	}

	private int getEmotionMeterValue(View view) {
		hideEmotionMeter();
		switch (view.getId()) {
		case R.id.emo1:
			return 4;
		case R.id.emo2:
			return 3;
		case R.id.emo3:
			return 2;
		case R.id.emo4:
			return 1;
		default:
			return -1;
		}
	}
	private int getHealthMeterValue(View view) {
		hideHealthMeter();
		switch (view.getId()) {
		case R.id.health1:
			return 4;
		case R.id.health2:
			return 3;
		case R.id.health3:
			return 2;
		case R.id.health4:
			return 1;
		default:
			return -1;
		}
	}

	private void displayEmotionMeterValue(int val) {
		TextView emotion = (TextView) findViewById(R.id.emotion_value);
		String expression = "is ";
		switch (val) {
		case 4:
			expression = expression + "Pathetic";
			emotion.setText(expression);
			break;
		case 3:
			expression = expression + "Nahh!!";
			emotion.setText(expression);
			break;
		case 2:
			expression = expression + "Good";
			emotion.setText(expression);
			break;
		case 1:
			expression = expression + "Yummyyy";
			emotion.setText(expression);
			break;
		default:
			break;
		}
	}
	
	private void displayHealthMeterValue(int val) {
		TextView emotion = (TextView) findViewById(R.id.health_value);
		switch (val) {
		case 4:
			emotion.setText("A morning walk should do");
			break;
		case 3:
			emotion.setText("Ah! need a 5km run");
			break;
		case 2:
			emotion.setText("Aaah! Need a 10km uphill run");
			break;
		case 1:
			emotion.setText("I dont care.I just hog");
			break;
		default:
			break;
		}
	}
	
	private void getReview() {
		Intent reviewActivity = new Intent(this, ReviewActivity.class);
		reviewActivity.putExtra(KEY_REVIEW, reviews);
		startActivityForResult(reviewActivity, REVIEW_ACTIVITY);
	}

	private void getNearbyPlaces() {
		Intent nearbyPlaces = new Intent(this, NearbyPlacesActivity.class);
		startActivityForResult(nearbyPlaces, NEARBY_PLACES_ACTIVITY);
	}

	private void showSelectedLocation(Intent data) {
		if (data.getExtras().containsKey("reference")) {
			restaurant = (Restaurant) data.getExtras().get(KEY_RESTAURANT);
			placeSelectedReference = restaurant.getPlaceId();
			placeSelected = restaurant.getName();// data.getStringExtra("name");
			TextView place = (TextView) findViewById(R.id.placeSelected);
			place.setText("@ " + placeSelected);
		}
	}

	private void shareOnFacebook() {
		try {
			sharedItem = getSharedObject();
			if(sharedItem != null){
				new SaveItem().execute();
				Intent homepage = new Intent(this, FragmentTabsPager.class);
				homepage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homepage);
			}else{
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveItem() throws JSONException, Exception {
		Context context = getApplicationContext();
		CharSequence text = "Your item is saved!!!";
		int duration = Toast.LENGTH_SHORT;
		boolean isSuccess = false;

		if(uuid != ""){
			String imageFilename = Environment.getExternalStorageDirectory()
					.getPath() + "/" + uuid + ".jpg";
			int responseValue = uploadImage(imageFilename);
		}
		
		// Construct the shared object and post it
		
		if(sharedItem == null){
			return;
		}
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(sharedItem, SharedItem.class);

		Bundle postParams = new Bundle();
		postParams.putString("title", sharedItem.getItem().getName());
		postParams.putString("description", sharedItem.getDescription());
		postParams.putString("url", "Put your own URL to the object here");
		String actionUrl = "me/sakathootame:rate?fooditem="
				+ URLEncoder
						.encode("http://ec2-23-20-146-25.compute-1.amazonaws.com/oota.php?fb:app_id=509255055792188&og:type=sakathootame:fooditem&og:title="
								+ sharedItem.getItem().getName()
								+ "&og:description="
								+ sharedItem.getDescription()
								+ "&og:image="
								+ sharedItem.getImageUrl()
								+ "&body=Put your mesage here");
		String encodedUrl = URLEncoder.encode(actionUrl, "UTF-8");
		String responseOfAction = facebook.request(actionUrl, postParams,
				"POST");
		Log.i("Fb post action responce", responseOfAction);
		org.apache.http.HttpResponse response = postItemObject(SAVE_ITEM_URL,
				json);

		if (response != null && response.getStatusLine().getStatusCode() == 200) {
			isSuccess = true;

		} else
			isSuccess = false;
		Log.i("MyApp*******", "AfterResponse");

		if (isSuccess)
			text = "Your item is saved!!!";
		else
			text = "Sorry! there was an error saving item";

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	private SharedItem getSharedObject() throws JSONException, Exception {
		SharedItem sharedItem = new SharedItem();
		FoodItem item = new FoodItem();
		
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) findViewById(R.id.autocomplete_dishes_list);
		String itemName = autoComplete.getText().toString();
		
		if(itemName == null || itemName.equals("")){
			error.setTitle("Please enter the dish name");
			error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	
		        }
		     });
			error.show();
			return null;
		}
		if(!itemName.equals(itemSelected)){
			item.setName(itemName);
		}else{
			item.setId(itemIdSelected);
			item.setName(itemSelected);
		}
		
		sharedItem.setItem(item);

		if(emotionValue == -1){
			error.setTitle("Please select the emotion");
			error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	showEmotionMeter();
		        }
		     });
			error.show();
			
			return null;
		}else{
			sharedItem.setEmotionId(emotionValue);
		}
		
		if(uuid != ""){
			sharedItem.setImageUrl("http://ec2-23-20-146-25.compute-1.amazonaws.com/images/"+ uuid + ".jpg");
		}else{
			sharedItem.setImageUrl("http://ec2-23-20-146-25.compute-1.amazonaws.com/images/"+ "D9E4840F-8F0F-422D-B95A-C3175D74D75F" + ".jpg");
		}
		sharedItem.setDescription(reviews);

		if(restaurant != null){
			sharedItem.setRestaurant(restaurant);
		}else{
			error.setTitle("Please select the location");
			error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	getNearbyPlaces();
		        }
		     });
			error.show();
			return null;
		}
		Date date = new Date();
		sharedItem.setDate(null);

		User user = new User();
		user = getUserObject();

		sharedItem.setUser(user);

		return sharedItem;
	}

	private org.apache.http.HttpResponse postItemObject(String uri, String json) {
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			return new DefaultHttpClient().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Failure");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			System.out.println("Failure");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failure");
			e.printStackTrace();

		}
		return null;
	}

	private User getUserObject() throws JSONException, IOException {
		SharedPreferences mPrefs;
		User userObj = new User();
		facebook = new Facebook(Constants.APP_ID);
		mPrefs = getSharedPreferences(Constants.PREFERENCE_FILENAME,
				MODE_PRIVATE);
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

	public int uploadImage(String sourceFileUri) {
		String fileName = sourceFileUri;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 124 * 124;
		File sourceFile = new File(sourceFileUri);
		int serverResponseCode = 0;

		if (!sourceFile.isFile()) {
			Log.e("uploadFile", "Source File Does not exist");
			return 0;
		}
		try { // open a URL connection to the Servlet
			String correctFileName[] = new String[4];
			correctFileName = sourceFileUri.split("/");
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(UPLOAD_IMAGE_URL + uuid);
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
																// connection to
																// the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("imageName", correctFileName[3]);

			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
					+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			bytesAvailable = fileInputStream.available(); // create a buffer of
															// maximum size
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();
			Log.e("server response codde", "" + serverResponseCode);
			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
					+ ": " + serverResponseCode);
			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}
		return serverResponseCode;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}
    class SaveItem extends AsyncTask<String, String, String> {
    	
        @Override
        protected String doInBackground(String... arg0) {
               try {
                      saveItem();
               } catch (JSONException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
               } catch (Exception e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
               }
               return null;
        }

    }
    class GetDishes extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(act);
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
    	@Override
        protected ArrayList<String> doInBackground(ArrayList<String>... arg0) {
               try {
                      return getDishesItems();
               } catch (Exception e) {
                      e.printStackTrace();
               }
               return null;
        }
        @Override
        protected void onPostExecute(ArrayList<String> foodItems) {
        	pDialog.dismiss();
        	super.onPostExecute(foodItems);
        	listview_array = foodItems;
        	autoComplete = (AutoCompleteTextView) findViewById(R.id.autocomplete_dishes_list);
    		ArrayAdapter<String> adapter = new ArrayAdapter<String>(act,
    				android.R.layout.simple_dropdown_item_1line, listview_array);
    		autoComplete.setAdapter(adapter);
    		autoComplete
    				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    					public void onItemClick(AdapterView<?> parent, View view,
    							int position, long id) {
    						itemSelected = parent.getItemAtPosition(position)
    								.toString();
    						itemIdSelected = map.get(itemSelected);
    					}
    				});
        }

    }

}
