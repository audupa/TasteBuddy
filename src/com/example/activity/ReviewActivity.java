package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.R;
import com.example.constants.Constants;
import com.flurry.android.FlurryAgent;

public class ReviewActivity extends SherlockActivity{
	EditText review;
	public static String KEY_REVIEW = "review";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_activity);
		review = (EditText)findViewById(R.id.write_review);
		ImageButton saveReviewButton = (ImageButton)findViewById(R.id.save_review_button);
		Intent i = getIntent();
		if(i.getExtras().containsKey(KEY_REVIEW)){
			if(!i.getExtras().get(KEY_REVIEW).toString().equals("")){
				review.setText(i.getExtras().get(KEY_REVIEW).toString());
			}
		}
		saveReviewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = getIntent();
                i.putExtra(KEY_REVIEW,review.getText());
                setResult(RESULT_OK, i);
                finish();
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
}
