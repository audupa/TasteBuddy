package com.example ;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable{
	/**
	 * u
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("name")
	private String name;
	
	@SerializedName("faceboookId")
	private String faceboookId;
	
	@SerializedName("accessToken")
	private String accessToken;
	
	@SerializedName("deviceToken")
	private String deviceToken;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFaceboookId() {
		return faceboookId;
	}
	public void setFaceboookId(String faceboookId) {
		this.faceboookId = faceboookId;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public boolean equals(Object arg0) {
		User user= (User)arg0;
		return user.getFaceboookId().equals(this.faceboookId);
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", faceboookId=" + faceboookId
				+ ", accessToken=" + accessToken + ", deviceToken="
				+ deviceToken + "]";
	}
	
}
