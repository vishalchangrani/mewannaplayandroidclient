package com.mewannaplay.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.google.android.maps.GeoPoint;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
	@SerializedName("city_name")
	private String city;
	@SerializedName("state_name")
	private String state;
	@SerializedName("county_name")
	private String countyName;
	@SerializedName("state_abbreviation")
	private String abbreviation;
	
	

	public TennisCourt() {

	}

	public TennisCourt(int id, double latitude, double longitude,
			int subcourts, int occupied, String facilityType, String name,
			int messageCount, String city, String county, String state, String abbreviation) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.subcourts = subcourts;
		this.occupied = occupied;
		this.facilityType = facilityType;
		this.name = name;
		this.messageCount = messageCount;
		this.city = city;
		this.state = state;
		this.countyName = county;
		this.countyName = county;
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
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public void setOccupied(int occupied) {
		this.occupied = occupied;
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
		contentValues.put("city", city);
		contentValues.put("state", state);
		contentValues.put("county", countyName);
		contentValues.put("abbreviation", abbreviation);
		return contentValues;
	}
	
	public static TennisCourt[] fromJSONObjectArray(JSONObject jsonObject) throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		return (TennisCourt[])  (gson.fromJson(jsonObject.getJSONArray("tenniscourt").toString(), TennisCourt[].class));

	}
}