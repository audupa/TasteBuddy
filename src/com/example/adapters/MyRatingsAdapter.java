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

public class MyRatingsAdapter extends SimpleAdapter{
	private Context ctx;
	private String[] values;
	private int resource;
	private int rID[];
	private List<? extends Map<String, ?>> data;
	String from[];
	private static ImageManager imageManager;
	LoaderSettings settings;
	public MyRatingsAdapter(Context context, List<? extends Map<String, ?>> data,int resource, String[] from, int[] to) {
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
		//ImageView image = (ImageView)rowView.findViewById(rID[0]);
		TextView dishName = (TextView)rowView.findViewById(rID[0]);
		TextView restaurantName = (TextView)rowView.findViewById(rID[1]);
		TextView review = (TextView)rowView.findViewById(rID[2]);
		ImageView image = (ImageView)rowView.findViewById(rID[3]);
		
		Map<String, ?> map = data.get(position);
		if(map.get(from[0])!=null){
			dishName.setText((CharSequence) map.get(from[0]));
		}
		if(map.get(from[1])!=null){
			restaurantName.setText((CharSequence) map.get(from[1]));
		}
		if(map.get(from[2])!=null){
			review.setText((CharSequence) map.get(from[2]));
		}
		
		ImageTagFactory imageTagFactory = ImageTagFactory.getInstance(ctx, R.drawable.no_image_available);
		ImageTag tag = imageTagFactory.build((String) map.get(from[3]),ctx);
		image.setTag(tag);
		imageManager.getLoader().load(image);
		
		return rowView;
	}

}
