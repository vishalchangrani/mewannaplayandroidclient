package com.mewannaplay.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class User {
	
	@SerializedName("username")
	private String userName;
	@SerializedName("password")
	private String password;
	
	public User(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public JSONObject toJSONObject() throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		JSONObject user =  new JSONObject(gson.toJson(this));
		JSONObject finalJsonObject = new JSONObject();
		return finalJsonObject.put("user", user);
	}
	
}
