package com.mewannaplay.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import static com.mewannaplay.model.TennisCourtDetails.nonNullString;

public class Message {
	
	@SerializedName("message_id")
	private int id;
	@SerializedName("message_text")
	private String text;
	@SerializedName("user_login_id")
	private String user;
	@SerializedName("level")
	private String level;
	@SerializedName("schedule_time")
	private String scheduleTime;
	@SerializedName("contact_info")
	private String contactInfo;
	@SerializedName("player_needed")
	private String playerNeeded;
	@SerializedName("contact_type_id")
	private int contactTypeId;
	@SerializedName("created")
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
	
	public JSONObject toJSONObject()
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();		
		try {
			return new JSONObject(gson.toJson(this));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(this.getClass().toString(), e.getMessage());
			return null;
		}
	}
	
	public ContentValues toContentValue()
	{
		ContentValues contentValues = new ContentValues(9);
		contentValues.put("_id", id);
		contentValues.put("text", nonNullString(text));
		contentValues.put("user", nonNullString(user));
		contentValues.put("level", nonNullString(level));
		contentValues.put("scheduled_time", nonNullString(scheduleTime));
		contentValues.put("contact_info", nonNullString(contactInfo));
		contentValues.put("contact_type", contactTypeId);
		contentValues.put("players_needed", nonNullString(playerNeeded));
		contentValues.put("time_posted", nonNullString(timeposted));
		return contentValues;
	}
}
