package com.example;

import java.io.Serializable;

import com.example.Feeds.Item;
import com.google.gson.annotations.SerializedName;

public class MyRatings implements Serializable{
	@SerializedName("id")
	private String id;
	
	@SerializedName("emotionId")
	private int emotionId;

	@SerializedName("healthId")
	private int healthId;

	@SerializedName("imageUrl")
	private String imageURL;

	@SerializedName("description")
	private String description;

	private Restaurant restaurant;
	private User user;
	private Item item;
	
	public String getFeedId(){
		return id;
	}
	public int getEmotionValue(){
		return emotionId;
	}
	public int getHealthValue(){
		return healthId;
	}
	public String getImageURL(){
		return imageURL;
	}
	public String getReview(){
		return description;
	}
	public Restaurant getRestaurant(){
		return restaurant;
	}
	public User getUser(){
		return user;
	}
	public Item getItem(){
		return item;
	}
	
	public static class Item implements Serializable{
		@SerializedName("id")
	    public String id;
		
		@SerializedName("name")
	    public String name;
		
		public String getItemId(){
			return id;
		}
		public String getDishName(){
			return name;
		}
	}
	

}
