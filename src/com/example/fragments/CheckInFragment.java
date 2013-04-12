package com.example.fragments;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.R;

public class CheckInFragment extends SherlockFragment {
	private ImageView imageView;
	private Uri imageUri;
	public static final String ARG_SECTION_NUMBER = "section_number";
	private static final int IMAGE_CAPTURE = 0;
	final int PICTURE_ACTIVITY = 1;
	Intent takePictureIntent;
	public CheckInFragment() {	
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.checkin,null);
    	ImageButton cameraButton = new ImageButton(getActivity());//(ImageButton)v.findViewById(R.id.camera_button);
    	//imageView = (ImageView)v.findViewById(R.id.imgView);
		cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				dispatchTakePictureIntent(PICTURE_ACTIVITY);
			}
		});
        return v;

    }
    private void dispatchTakePictureIntent(int actionCode) {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        ContentValues values = new ContentValues();
//        String fileName = "testphoto.jpg";
//        values.put(MediaStore.Images.Media.TITLE, fileName);
//        values.put(MediaStore.Images.Media.DESCRIPTION,
//                "Image capture by camera");
//        imageUri = this.getActivity().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        startActivityForResult(takePictureIntent, actionCode);
    }
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Bundle extras = takePictureIntent.getExtras();
//        Bitmap mImageBitmap = (Bitmap) extras.get("data");
//        imageView.setImageBitmap(mImageBitmap);
//    	if (requestCode == IMAGE_CAPTURE) {
//            if (resultCode == RESULT_OK){
//                imageView.setImageURI(imageUri);
//            }
//        }
    }    




	

}
