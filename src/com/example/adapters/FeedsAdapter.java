package com.example.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.R;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.LoaderSettings.SettingsBuilder;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;

public class FeedsAdapter extends SimpleAdapter{
	private Context ctx;
	private String[] values;
	private int resource;
	private int rID[];
	private List<? extends Map<String, ?>> data;
	String from[];
	private static ImageManager imageManager;
	LoaderSettings settings;
	public FeedsAdapter(Context context, List<? extends Map<String, ?>> data,int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.ctx = context;
		this.resource = resource;
		rID = to;
		this.data = data;
		this.from = from;
		settings = new SettingsBuilder().withDisconnectOnEveryCall(true).build(ctx);
		//settings.setCacheManager(new LruBitmapCache(ctx));
        imageManager = new ImageManager(settings);
	}
	public static ImageManager getImageLoader() {
        return imageManager;
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(resource, parent, false);
		String dishName="";
		String restaurantName="";
		String userName="";
		String rev="";
		String emotionVal="";
		
		TextView whoRated = (TextView)rowView.findViewById(rID[0]);
		ImageView image = (ImageView)rowView.findViewById(rID[1]);
		TextView emotion = (TextView)rowView.findViewById(rID[2]);
		TextView review = (TextView)rowView.findViewById(rID[3]);
		
		
		Map<String, ?> map = data.get(position);
		userName = map.get(from[0]).toString();
		if(map.get(from[1])!=null){
			dishName = map.get(from[1]).toString();
		}
		if(map.get(from[2])!=null){
			restaurantName = map.get(from[2]).toString();
		}
		if(map.get(from[3])!=null){
			rev = map.get(from[3]).toString();
		}
		if(map.get(from[5])!=null){
			emotionVal = map.get(from[5]).toString();
		}
		
		//dishName.setText((CharSequence) map.get(from[0]));
		//restaurantName.setText((CharSequence) map.get(from[1]));
		
		
		String title = userName + " rated "+dishName+" at "+restaurantName;
		
		whoRated.setText((CharSequence)title);
		review.setText((CharSequence) rev);
		emotion.setText((CharSequence) emotionVal);
		
		
		ImageTagFactory imageTagFactory = ImageTagFactory.getInstance(ctx, R.drawable.no_image_available);
		ImageTag tag = imageTagFactory.build((String) map.get(from[4]),ctx);
		image.setTag(tag);
		imageManager.getLoader().load(image);
		//SimpleLoader loader = new SimpleLoader(settings);
		//loader.load(image);
		
		//Bitmap myImage = new DirectLoader().download((String) map.get(from[3]));
		//image.setImageBitmap(myImage);
		return rowView;
		
	}
	
}
