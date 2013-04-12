package com.example.activity;

import com.example.constants.Constants;
import com.flurry.android.FlurryAgent;

import android.app.Activity;

	public class FlurryBaseActivity extends Activity
	{
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
	}

