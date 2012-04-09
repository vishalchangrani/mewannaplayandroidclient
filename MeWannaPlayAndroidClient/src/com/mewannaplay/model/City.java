package com.mewannaplay.model;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class City implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("city_id")
	private int id;
	@SerializedName("city_name")
	private String name;
	@SerializedName("state_abbreviation")
	private String abbreviation;
	@SerializedName("city_latitude")
	private double latitude;
	@SerializedName("city_longitude")
	private double longitude;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
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
	
	public static City[] fromJSONObjectArray(JSONObject jsonObject) throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();		
		JSONArray cities = jsonObject.getJSONArray("cities");
		City[] citiesArr = (City[]) gson.fromJson(cities.toString(), City[].class);
		return citiesArr;
	}
	
	
	public ContentValues toContentValue()
	{
		ContentValues contentValues = new ContentValues(5);
		contentValues.put("_id", id);
		contentValues.put("latitude", latitude);
		contentValues.put("longitude", longitude);
		contentValues.put("name", name);
		contentValues.put("abbreviation", abbreviation);
		return contentValues;
	}


}
