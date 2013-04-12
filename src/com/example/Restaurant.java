package com.example;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Restaurant implements Serializable {
private static final long serialVersionUID = 1L;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("placeId")
	private String placeId;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("discoveredBy")
	private String discoveredBy;
	
	@SerializedName("longitude")
	private double longitude;
	
	@SerializedName("lattitude")
	private double lattitude;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDiscoveredBy() {
		return discoveredBy;
	}
	public void setDiscoveredBy(String discoveredBy) {
		this.discoveredBy = discoveredBy;
	}
	
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLattitude() {
		return lattitude;
	}
	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
