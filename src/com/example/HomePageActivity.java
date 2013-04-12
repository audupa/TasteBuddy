package com.example;

//import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.activity.ShareDishActivity;
import com.example.fragments.FeedsFragment;
import com.example.fragments.MyRatingsFragment;
import com.example.fragments.NearbyPlacesFragment;
import com.tastebuddy.tab.TabListener;

public class HomePageActivity extends SherlockActivity {
   

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        // Notice that setContentView() is not used, because we use the root
        // android.R.id.content as the container for each fragment

        // setup action bar for tabs
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(true);
    	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//        ActionBar.Tab feedsTab = actionBar.newTab().setText("Feeds");
//        ActionBar.Tab nearbyPlacesTab = actionBar.newTab().setText("Nearby Places");
//        ActionBar.Tab iRatedTab = actionBar.newTab().setText("My Ratings");
    	ActionBar.Tab feedsTab = getSupportActionBar().newTab().setText("Feeds");
        ActionBar.Tab nearbyPlacesTab = getSupportActionBar().newTab().setText("Nearby Places");
        ActionBar.Tab iRatedTab = getSupportActionBar().newTab().setText("My Ratings");
       
        feedsTab.setTabListener(new TabListener<FeedsFragment>(this,"feeds",FeedsFragment.class));
        nearbyPlacesTab.setTabListener(new TabListener<NearbyPlacesFragment>(this,"nearBy",NearbyPlacesFragment.class));
        iRatedTab.setTabListener(new TabListener<MyRatingsFragment>(this,"iRated",MyRatingsFragment.class));
        
        getSupportActionBar().addTab(feedsTab);
        getSupportActionBar().addTab(nearbyPlacesTab);
        getSupportActionBar().addTab(iRatedTab);
        setTitle(R.string.app_name);
        //getActionBar().setTitle(R.string.app_name);
        
        
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {    
       switch (item.getItemId()) 
       {        
          case R.id.shareDish:            
             Intent intent = new Intent(this, ShareDishActivity.class);            
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
             startActivity(intent);            
             return true;        
          default:            
             return super.onOptionsItemSelected(item);    
       }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.homepage_menu, menu);
        //SearchView searchView = (SearchView) menu.findItem(R.id.dishSearch).getActionView();
        //searchView.setQueryHint("Find a place to go");
         
        
        return true;
    }

}