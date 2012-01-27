package com.mewannaplay.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class Message {
	
	@SerializedName("message_id")
	private int id;
	@SerializedName("additionalcomment")
	private String text;
	@SerializedName("postedbyuser")
	private String user;
	@SerializedName("level")
	private String level;
	@SerializedName("schedule_time")
	private String scheduleTime;
	@SerializedName("contact_info")
	private String contactInfo;
	@SerializedName("playersneeded")
	private String playerNeeded;
	@SerializedName("contact_type_id")
	private int contactTypeId;
	@SerializedName("timeposted")
	private String timeposted;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public String getScheduleTime() {
		return scheduleTime;
	}


	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}


	public String getContactInfo() {
		return contactInfo;
	}


	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}


	public String getPlayerNeeded() {
		return playerNeeded;
	}


	public void setPlayerNeeded(String playerNeeded) {
		this.playerNeeded = playerNeeded;
	}


	public int getContactTypeId() {
		return contactTypeId;
	}


	public void setContactTypeId(int contactTypeId) {
		this.contactTypeId = contactTypeId;
	}


	public String getTimeposted() {
		return timeposted;
	}


	public void setTimeposted(String timeposted) {
		this.timeposted = timeposted;
	}


	public static Message[] fromJSONObject(JSONObject jsonObject) throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();		
		JSONArray tennisCourtJsonObject = jsonObject.getJSONArray("courtmessages");
		Message[] messages = (Message[]) gson.fromJson(tennisCourtJsonObject.toString(), Message[].class);
		return messages;
	}
}
