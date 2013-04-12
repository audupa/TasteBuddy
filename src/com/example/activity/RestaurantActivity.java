package com.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.R;

public class RestaurantActivity extends SherlockActivity{
	public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
	private String reference;
	private String name;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant);
        Intent intent = getIntent();
        String id = intent.getStringExtra(KEY_REFERENCE);
        String name = intent.getStringExtra(KEY_NAME);
        setTitle(name);
    }
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
    {    
       switch (item.getItemId()) 
       {        
          case R.id.checkin:            
             Intent intent = new Intent(this, ShareDishActivity.class);            
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
             startActivity(intent);            
             return true;        
          default:            
             return super.onOptionsItemSelected(item);    
       }
    }
 
    /** Callback function */
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.restaurant_menu, menu);
        return super.onCreateOptionsMenu(menu);
        
    }
}
