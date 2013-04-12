package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.activity.NearbyPlacesActivity;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookLoginDialogListener implements DialogListener {
	private SharedPreferences mPrefs;
	private Facebook facebook;
	private Context context;
	Activity activity;
	
	public FacebookLoginDialogListener(SharedPreferences mPrefs,Facebook facebook, Context context, Activity activity) {
		this.mPrefs = mPrefs;
		this.facebook = facebook;
		this.context = context;
		this.activity = activity;
	} 
	
	@SuppressWarnings("deprecation")
	@Override
	public void onComplete(Bundle values) {
        // Function to handle complete event
        // Edit Preferences and update facebook acess_token
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("access_token",facebook.getAccessToken());
        System.out.println("Access token is ****" +facebook.getAccessToken());
        editor.putLong("access_expires",facebook.getAccessExpires());
        editor.commit();
        Intent homePageActivity = new Intent(context, FragmentTabsPager.class);
        homePageActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(homePageActivity);
	}

	@Override
	public void onFacebookError(FacebookError e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(DialogError e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}
	
}