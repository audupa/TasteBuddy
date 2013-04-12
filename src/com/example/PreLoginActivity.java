package com.example;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.activity.ShareDishActivity;
import com.example.constants.Constants;

public class PreLoginActivity extends SherlockActivity{
	private SharedPreferences mPrefs;
	ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();
	Boolean isInternetPresent = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prelogin_activity);
		TextView errMsg = (TextView)findViewById(R.id.prelogin_err_msg);
		
		cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            alert.showAlertDialog(this, "Internet Connection Error","Please connect to working Internet connection", false);
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
		mPrefs = getSharedPreferences(Constants.PREFERENCE_FILENAME, MODE_PRIVATE);
		String authtoken = mPrefs.getString("access_token", null);
		if(authtoken == null){
			Intent loginPageActivity = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(loginPageActivity);
		}
		else {
			//Intent homePageActivity = new Intent(getApplicationContext(), HomePageActivity.class);
			Intent homePageActivity = new Intent(getApplicationContext(), FragmentTabsPager.class);
			//Intent homePageActivity = new Intent(getApplicationContext(), ShareDishActivity.class);
			startActivity(homePageActivity);
			//Toast.makeText(getBaseContext(), "Hi there!"+authtoken, Toast.LENGTH_LONG).show();
		}
	}

}
