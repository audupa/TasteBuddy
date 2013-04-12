package com.example;

import java.io.Serializable;
import java.util.Date;

//import org.springframework.data.annotation.Id;

public class SharedItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//@Id
	private String id;
	private FoodItem item;
	private int emotionId;
	private String imageUrl;
	private String description;
	private Restaurant restaurant;
	private Date date=new Date();
	private User user;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public FoodItem getItem() {
		return item;
	}
	public void setItem(FoodItem item) {
		this.item = item;
	}
	public int getEmotionId() {
		return emotionId;
	}
	public void setEmotionId(int emotionId) {
		this.emotionId = emotionId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Restaurant getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
