package com.mewannaplay.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class NewUser {
	
	@SerializedName("user_name")
	private String userName;
	@SerializedName("user_password")
	private String password;
	@SerializedName("user_email")
	private String email;
	
	public NewUser(String userName, String password, String email)
	{
		this.userName = userName;
		this.password = password;
		this.email = email;
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
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	/*
	 * curl -i -v --cookie-jar newcookies.txt Ê-X POST -d '{"data":{"User":{"user_name":"vishal3","user_password": "fbd6f1ccYW1pdGdhbmRoaQ==","user_email":"vishal@vishal3.com"}}}' --location http://api.mewannaplay.com/v1/users/add
	 */
	public JSONObject toJSONObject() throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		JSONObject user =  new JSONObject(gson.toJson(this));
		JSONObject userJsonObject = new JSONObject();
		userJsonObject.put("User", user);
		JSONObject finalJsonObject = new JSONObject();
		return finalJsonObject.put("data",userJsonObject);
	}
	
}
