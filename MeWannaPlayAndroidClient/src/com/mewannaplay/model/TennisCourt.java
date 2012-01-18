package com.mewannaplay.model;

import android.content.ContentValues;

import com.google.android.maps.GeoPoint;
import com.google.gson.annotations.SerializedName;

public class TennisCourt {
	@SerializedName("tennis_id")
	private int id;
	@SerializedName("tennis_latitude")
	private double latitude;
	@SerializedName("tennis_longitude")
	private double longitude;
	@SerializedName("tennis_subcourts")
	private int subcourts;
	// TODO fix 'Occupied' on server to 'occupied' to get rid of the following
	// annotation
	@SerializedName("Occupied")
	private int occupied;
	@SerializedName("tennis_facility_type")
	private String facilityType;
	@SerializedName("tennis_name")
	private String name;
	@SerializedName("tennis_message_count")
	private int messageCount;

	public TennisCourt() {

	}

	public TennisCourt(int id, double latitude, double longitude,
			int subcourts, int occupied, String facilityType, String name,
			int messageCount) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.subcourts = subcourts;
		this.occupied = occupied;
		this.facilityType = facilityType;
		this.name = name;
		this.messageCount = messageCount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getSubcourts() {
		return subcourts;
	}

	public void setSubcourts(int subcourts) {
		this.subcourts = subcourts;
	}

	public int getOccpied() {
		return occupied;
	}

	public void setOccpied(int occpied) {
		this.occupied = occpied;
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

	public boolean isPrivate() {
		return facilityType.equalsIgnoreCase("Private");
	}

	public int getOccupied() {
		return occupied;
	}

	public boolean isOccupied() {
		return occupied == subcourts;
	}
	
	public boolean isPartiallyOccupied() {
		return occupied < subcourts && !isNotOccupied();
	}
	

	public boolean isNotOccupied() {
		return occupied == 0;
	}
	
	public GeoPoint getGeoPoint()
	{
	 return new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
	}
	
	public ContentValues toContentValue()
	{
		ContentValues contentValues = new ContentValues(8);
		contentValues.put("_id", id);
		contentValues.put("latitude", latitude);
		contentValues.put("longitude", longitude);
		contentValues.put("subcourts", subcourts);
		contentValues.put("occupied", occupied);
		contentValues.put("facility_type", facilityType);
		contentValues.put("name", name);
		contentValues.put("message_count", messageCount);
		return contentValues;
	}
}
