package com.example.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.AlertDialogManager;
import com.example.ConnectionDetector;
import com.example.GPSTracker;
import com.example.GooglePlaces;
import com.example.R;
import com.example.Restaurant;
import com.example.RestaurantList;
import com.example.activity.NearbyPlacesActivity;
import com.example.activity.RestaurantActivity;
import com.example.activity.ShareDishActivity;
import com.example.adapters.NearByPlacesAdapter;
import com.example.constants.Constants;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class NearbyPlacesFragment  extends SherlockFragment {
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    // Connection detector class
    ConnectionDetector cd;
 
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Google Places
    GooglePlaces googlePlaces;
 
    // Places List
    RestaurantList nearPlaces;
 
    // GPS Location
    GPSTracker gps;
 
    // Button
    Button btnShowOnMap;
 
    // Progress dialog
    ProgressDialog pDialog;
 
    // Places Listview
    ListView lv;
 
    // ListItems data
    ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
    private SharedPreferences mPrefs;
    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_DISCOVEREDBY="user";
    public static String KEY_VICINITY = "vicinity"; // Place area name
    public static String GET_PLACES_URL="https://graph.facebook.com/search?type=place";
    public static String GET_PLACES_FROM_TB_URL = "http://ec2-23-20-146-25.compute-1.amazonaws.com/sakath-oota-backend/rest/items/nearby?";
	private double _latitude;
	private double _longitude;
	private double _radius;
	
	Context applicationContext;
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nearby_places_fragment, container, false);
        
        applicationContext = this.getActivity().getApplicationContext();
        mPrefs = this.getActivity().getSharedPreferences(Constants.PREFERENCE_FILENAME, applicationContext.MODE_PRIVATE);
		String authtoken = mPrefs.getString("access_token", null);
        cd = new ConnectionDetector(this.getActivity().getApplicationContext());
 
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(this.getActivity(), "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return container;
        }
 
        // creating GPS Class object
        gps = new GPSTracker(applicationContext);
 
        // check if GPS location can get
        if (gps.canGetLocation()) {
            _latitude=gps.getLatitude();
            _longitude=gps.getLongitude();
        } else {
            // Can't get user's current location
            alert.showAlertDialog(this.getActivity(), "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return view;
        }
 
        // Getting listview
        lv = (ListView) view.findViewById(R.id.list);
 
        // button show on map
        //btnShowOnMap = (Button) findViewById(R.id.btn_show_map);
 
        // calling background Async task to load Google Places
        // After getting places from Google all the data is shown in listview
        new LoadPlaces().execute();
 
        /** Button click event for shown on map */
//        btnShowOnMap.setOnClickListener(new View.OnClickListener() {
// 
//            @Override
//            public void onClick(View arg0) {
//                Intent i = new Intent(getApplicationContext(),
//                        PlacesMapActivity.class);
//                // Sending user current geo location
//                i.putExtra("user_latitude", Double.toString(gps.getLatitude()));
//                i.putExtra("user_longitude", Double.toString(gps.getLongitude()));
// 
//                // passing near places to map activity
//                i.putExtra("near_places", nearPlaces);
//                // staring activity
//                startActivity(i);
//            }
//        });
 
        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
 
                // Starting new intent
                //Intent in = new Intent(applicationContext,RestaurantActivity.class);
                Intent in = new Intent(applicationContext,ShareDishActivity.class);
 
                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                in.putExtra(KEY_REFERENCE, reference);
                in.putExtra(KEY_NAME, name);
                startActivity(in);
            }
        });
        return view;
 
    }
    
    class LoadPlaces extends AsyncTask<String, String, String> {
    	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        public RestaurantList callGetPlacesSerivice(){
            String places = null;
            String tbPlaces = null;
            try {
            	String finalURL=GET_PLACES_URL+"&center="+_latitude+","+_longitude+"&distance=1000&method=GET&format=json&access_token="+mPrefs.getString("access_token", null);
            	
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(finalURL);
                ResponseHandler<String> handler = new BasicResponseHandler();
                places = httpclient.execute(request, handler);
                System.out.println(places);
                
                //Toast.makeText(this, returned, Toast.LENGTH_LONG).show();
//            	HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
//                HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(GET_PLACES_URL));
//                
//                request.getUrl().put("type", "place");
//                request.getUrl().put("distance","2500"); // in meters
//                request.getUrl().put("center", "12.9833,77.5833");
//                request.getUrl().put("format", "json");
//                request.getUrl().put("access_token", "BAACEdEose0cBAHnJnfcJnk1qYyswHX1BirHAMf1105SAeTAqrHEBxY1J99rP57TzMJalJ314rsRN6TU4ZBQ5ER3y3G3L26NaZCxZAlFTB8oPGs8ndFW");
//               
     
//                RestaurantList list = request.execute().parseAs(RestaurantList.class);
                // Check log cat for places response status
//                Log.d("Places Status", "" + list.status);
                
                String tbURL = GET_PLACES_FROM_TB_URL +"latitude=" +_latitude + "&longitude=" +_longitude;
                HttpClient httpclient1 = new DefaultHttpClient();
                HttpGet request1 = new HttpGet(tbURL);
                ResponseHandler<String> handler1 = new BasicResponseHandler();
                tbPlaces = httpclient1.execute(request1, handler1);
                
                Restaurant list[] = new Restaurant[1000];
                try {
                    JSONObject json = new JSONObject(places) ;
                    JSONArray jsonArray=json.getJSONArray("data");
                    JSONArray jsonArray1=new JSONArray(tbPlaces); 
                    
                   // JSONObject objJson=json.getJSONObject(0);
                    
                 //   JSONArray json1=new JSONArray(objJson.toString());
                    
                    int k=0;
                    for(k=0;k < jsonArray1.length();k++){
                    	Restaurant restaurant = new Restaurant();
                    	JSONObject userJson = null;
                        JSONObject objJson = jsonArray1.getJSONObject(k);
                        restaurant.setPlaceId(objJson.getString("id"));
                        restaurant.setName(objJson.getString("name"));
                        if(objJson.isNull("user") == false){
                            userJson = objJson.getJSONObject("user");
                            if(userJson!=null)
                            	if(userJson.getString("name")!=null)
                            		restaurant.setDiscoveredBy(userJson.getString("name"));
                        }
                        list[k]=restaurant;    
                    }
                    
                    int i=0;
                    for(i=0;i < jsonArray.length();i++){
                        JSONObject objJson = jsonArray.getJSONObject(i);
                        
                        Restaurant restaurant = new Restaurant();
                        restaurant.setPlaceId(objJson.getString("id"));
                        restaurant.setName(objJson.getString("name"));
                        list[k+i]=restaurant;
                        
                    }
                    
                    
                    List<Restaurant>  list1=new ArrayList<Restaurant>(i);
                    for(int j=0;j<i+k;j++){
                    	list1.add(list[j]);
                    }
                    RestaurantList restaurantList=new RestaurantList();
                    restaurantList.status="OK";
                    restaurantList.results=list1;
                    return restaurantList;

                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                
                return null;

            }
            catch (ClientProtocolException e) {
                //Toast.makeText(this, "safdasf", 2);
            }
            catch (IOException e) {
                //Toast.makeText(this, "There was an IO issue Try again later", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
            
        }
        
 
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            
 
            try {
                
//                String types = "cafe|restaurant"; // Listing places only cafes, restaurants
//                double radius = 2500; // 1000 meters 
// 
//                // get nearest places
//                nearPlaces = googlePlaces.search(gps.getLatitude(),
//                        gps.getLongitude(), radius, types);
            	nearPlaces=callGetPlacesSerivice();
            	
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            
        	pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    
                    
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;
 
                    // Check for all possible status
                    if(status.equals("OK")){
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            // loop through each place
                            for (Restaurant p : nearPlaces.results) {
                                HashMap<String, String> map = new HashMap<String, String>();
 
                                // Place reference won't display in listview - it will be hidden
                                // Place reference is used to get "place full details"
                                map.put(KEY_REFERENCE, p.getPlaceId());
 
                                // Place name
                                map.put(KEY_NAME, p.getName());
                                map.put(KEY_DISCOVEREDBY,p.getDiscoveredBy());
                                // adding HashMap to ArrayList
                                placesListItems.add(map);
                            }
                            // list adapter
                            ListAdapter adapter = new NearByPlacesAdapter(applicationContext, placesListItems,
                                    R.layout.list_places,
                                    new String[] { KEY_REFERENCE, KEY_NAME,KEY_DISCOVEREDBY}, new int[] {
                                            R.id.reference, R.id.name, R.id.discoveredBy });
 
                            // Adding data into listview
                            lv.setAdapter(adapter);
                        }
                    }
                    else if(status.equals("ZERO_RESULTS")){
                        // Zero results found
                        alert.showAlertDialog(applicationContext, "Near Places",
                                "Sorry no places found. Try to change the types of places",
                                false);
                    }
                    else if(status.equals("UNKNOWN_ERROR"))
                    {
                        alert.showAlertDialog(applicationContext, "Places Error",
                                "Sorry unknown error occured.",
                                false);
                    }
                    else if(status.equals("OVER_QUERY_LIMIT"))
                    {
                        alert.showAlertDialog(applicationContext, "Places Error",
                                "Sorry query limit to google places is reached",
                                false);
                    }
                    else if(status.equals("REQUEST_DENIED"))
                    {
                        alert.showAlertDialog(applicationContext, "Places Error",
                                "Sorry error occured. Request is denied",
                                false);
                    }
                    else if(status.equals("INVALID_REQUEST"))
                    {
                        alert.showAlertDialog(applicationContext, "Places Error",
                                "Sorry error occured. Invalid Request",
                                false);
                    }
                    else
                    {
                        alert.showAlertDialog(applicationContext, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });
 
        }
 
    }
}
