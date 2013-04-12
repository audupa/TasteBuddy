package com.example.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.R;
import com.example.activity.NearbyPlacesActivity;
import com.example.fragments.CheckInFragment;
import com.example.fragments.GooglePlaces;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
 * sections of the app.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public Context context;
	public final int NUM_OF_SCREENS = 3;
	
	public SectionsPagerAdapter(FragmentManager fm,Context ctx) {
         super(fm);
         this.context = ctx;
     }

	@Override
	public Fragment getItem(int arg0) {
        switch (arg0) {
        case 0: 
            Fragment fragment = new CheckInFragment();
            Bundle args = new Bundle();
            args.putInt(CheckInFragment.ARG_SECTION_NUMBER, 1234);
            fragment.setArguments(args);
            return fragment;
        case 1:
            Fragment fragment1 = new GooglePlaces();
            Bundle args1 = new Bundle();
            args1.putInt(CheckInFragment.ARG_SECTION_NUMBER, 223);
            fragment1.setArguments(args1);
            return fragment1;        	
        case 2:
            Fragment fragment2 = new CheckInFragment();
            Bundle args2 = new Bundle();
            args2.putInt(CheckInFragment.ARG_SECTION_NUMBER, 3987);
            fragment2.setArguments(args2);
            return fragment2;
        }
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return NUM_OF_SCREENS;
	}
	
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return context.getString(R.string.checkin_title).toUpperCase();
            case 1: return context.getString(R.string.search_title).toUpperCase();
            case 2: return context.getString(R.string.review_title).toUpperCase();
        }
        return null;
    }

}
