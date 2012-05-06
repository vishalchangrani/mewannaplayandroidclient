package com.mewannaplay.model;

import com.google.gson.annotations.SerializedName;

public class TennisCourt {

	private int id;
	private float latitude;
	private float longitude;
	private int subcourts;
	//TODO fix 'Occupied' on server to 'occupied' to get rid of the following annotation
	@SerializedName("Occupied")
	private int occpied;
	private String facilityType;
	private String name;
	private int messageCount;
	

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public int getSubcourts() {
		return subcourts;
	}
	public void setSubcourts(int subcourts) {
		this.subcourts = subcourts;
	}
	public int getOccpied() {
		return occpied;
	}
	public void setOccpied(int occpied) {
		this.occpied = occpied;
	}
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	
}
