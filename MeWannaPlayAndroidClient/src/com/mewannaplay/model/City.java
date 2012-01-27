package com.mewannaplay.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class City {

	@SerializedName("city_id")
	private int id;
	@SerializedName("city_name")
	private String name;
	@SerializedName("state_abbreviation")
	private String abbreviation;
	@SerializedName("city_latitude")
	private String latitude;
	@SerializedName("city_longitude")
	private String longitude;
	
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
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public static City[] fromJSONObject(JSONObject jsonObject) throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();		
		JSONArray cities = jsonObject.getJSONArray("cities");
		City[] citiesArr = (City[]) gson.fromJson(cities.toString(), City[].class);
		return citiesArr;
	}

}
