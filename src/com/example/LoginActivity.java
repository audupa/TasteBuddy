package com.example;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.constants.Constants;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

@SuppressWarnings("deprecation")
public class LoginActivity extends SherlockActivity {
	private static String APP_ID = Constants.APP_ID;//398564260229994"; 
    private Facebook facebook;
    private AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        facebook = new Facebook(APP_ID);
        
        mAsyncRunner = new AsyncFacebookRunner(facebook);

        ImageButton fbLoginButton = (ImageButton)findViewById(R.id.loginButton);

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToFacebook();
            }
       });
    }

    public void loginToFacebook() {
        mPrefs = getSharedPreferences(Constants.PREFERENCE_FILENAME,MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        System.out.println("the access token is ***"+access_token);
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }

        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        if (!facebook.isSessionValid()) {
            facebook.authorize(this,
                    new String[] { "email", "publish_stream","publish_checkins", "user_checkins" , "user_status"},
                    new FacebookLoginDialogListener(mPrefs,facebook,getApplicationContext(),this));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    		super.onActivityResult(requestCode, resultCode, data);
    	    facebook.authorizeCallback(requestCode, resultCode, data);
    }
}