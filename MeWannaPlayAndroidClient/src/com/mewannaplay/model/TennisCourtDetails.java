/**
 * 
 */
package com.mewannaplay.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.mewannaplay.Constants;

/**
 * @author vishal
 *
 */
public class TennisCourtDetails {

	@SerializedName("tennis_court_id")
	private int id;
	@SerializedName("tennis_name")
	private String name;
	@SerializedName("tennis_address")
	private String address;
	@SerializedName("tennis_zipcode")
	private String zipcode;
	@SerializedName("tennis_url")
	private String url;
	@SerializedName("tennis_facility_type")
	private String facilityType;
	@SerializedName("tennis_subcourts")
	private int subcourts;
	@SerializedName("tennis_timings")
	private String timings;
	@SerializedName("city_name")
	private String city;
	@SerializedName("state_name")
	private String state;
	@SerializedName("state_abbreviation")
	private String abbreviation;
	
	private String surfaceType;
	
	private String phone;
	
	 
	/*private boolean[] activites = new boolean[Constants.ACTIVITY.values().length];
	private String[] activity_remarks = new String[Constants.ACTIVITY.values().length];
	private boolean[] ameneties = new boolean[Constants.AMENITY.values().length];
	private String[]amenity_remarks = new String[Constants.AMENITY.values().length];*/

	TennisActivity[] tennisActivities = new TennisActivity[Constants.ACTIVITY.values().length];
	TennisAmenity[] tennisAmeneties = new TennisAmenity[Constants.AMENITY.values().length];
	
	public class TennisActivity
	{
		@SerializedName("activity_type_id")
		private int typeId;
		@SerializedName("tennis_activity_remarks")
		private String remarks;
		public int getTypeId() {
			return typeId;
		}
		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}
		public String getRemarks() {
			return remarks;
		}
		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}
		
		public ContentValues toContentValue()
		{
			ContentValues contentValues = new ContentValues(8);
			contentValues.put("_id", typeId);
			contentValues.put("remark", nonNullString(remarks));
			contentValues.put("tennis_court", TennisCourtDetails.this.getId());
			return contentValues;
		}
		
		public  TennisActivity fromCursor(Cursor cursor)
		{
			TennisActivity tdc = new TennisActivity();
			tdc.setTypeId(cursor.getInt(cursor.getColumnIndex("_id")));
			tdc.setRemarks(cursor.getString(cursor.getColumnIndex("remark")));
			return tdc;
		}
		
	}
	
	public class TennisAmenity
	{
		@SerializedName("amenity_type_id")
		private int typeId;
		@SerializedName("tennis_amenity_remarks")
		private String remarks;
		public int getTypeId() {
			return typeId;
		}
		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}
		public String getRemarks() {
			return remarks;
		}
		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}
		public ContentValues toContentValue()
		{
			ContentValues contentValues = new ContentValues(8);
			contentValues.put("_id", typeId);
			contentValues.put("remark", TennisCourtDetails.nonNullString(remarks));
			contentValues.put("tennis_court", TennisCourtDetails.this.id);
			return contentValues;
		}
		public TennisAmenity fromCursor(Cursor cursor)
		{
			TennisAmenity tdc = new TennisAmenity();
			tdc.setTypeId(cursor.getInt(cursor.getColumnIndex("_id")));
			tdc.setRemarks(cursor.getString(cursor.getColumnIndex("remark")));
			return tdc;
		}
	}
	
	private class TennisContact
	{
		@SerializedName("contact_type_id")
		private int typeId;
		@SerializedName("tennis_contact_details")
		private String contactDetails;
		public int getTypeId() {
			return typeId;
		}
		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}
		public String getContactDetails() {
			return contactDetails;
		}
		public void setContactDetails(String contactDetails) {
			this.contactDetails = contactDetails;
		}
	}
	
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	public int getSubcourts() {
		return subcourts;
	}
	public void setSubcourts(int subcourts) {
		this.subcourts = subcourts;
	}
	public String getTennisTimings() {
		return timings;
	}
	public void setTennisTimings(String tennisTimings) {
		this.timings = tennisTimings;
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
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
public TennisActivity[] getTennisActivities() {
		return tennisActivities;
	}
	public void setTennisActivities(TennisActivity[] tennisActivities) {
		this.tennisActivities = tennisActivities;
	}
	public TennisAmenity[] getTennisAmeneties() {
		return tennisAmeneties;
	}
	public void setTennisAmeneties(TennisAmenity[] tennisAmeneties) {
		this.tennisAmeneties = tennisAmeneties;
	}
	public String getTimings() {
		return timings;
	}
	public void setTimings(String timings) {
		this.timings = timings;
	}
	public String getSurfaceType() {
		return surfaceType;
	}
	public void setSurfaceType(String surfaceType) {
		this.surfaceType = surfaceType;
	}
	

	
	/*	public boolean[] getActivites() {
		return activites;
	}
	public void setActivites(boolean[] activites) {
		this.activites = activites;
	}
	public String[] getActivity_remarks() {
		return activity_remarks;
	}
	public void setActivity_remarks(String[] activity_remarks) {
		this.activity_remarks = activity_remarks;
	}
	public boolean[] getAmeneties() {
		return ameneties;
	}
	public void setAmeneties(boolean[] ameneties) {
		this.ameneties = ameneties;
	}
	public String[] getAmenity_remarks() {
		return amenity_remarks;
	}
	public void setAmenity_remarks(String[] amenity_remarks) {
		this.amenity_remarks = amenity_remarks;
	}*/
	public static TennisCourtDetails fromJSONObject(JSONObject jsonObject) throws JsonSyntaxException, JSONException
	{
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		
		JSONObject tennisCourtJsonObject = jsonObject.getJSONObject("tenniscourt");
		TennisCourtDetails tennisCourtDetails = (TennisCourtDetails)  (gson.fromJson(tennisCourtJsonObject.toString(), TennisCourtDetails.class));
		JSONArray tennisActivityJsonArray = tennisCourtJsonObject.getJSONArray("TennisActivity");
		TennisActivity[] tennisActivities = (TennisActivity[]) gson.fromJson(tennisActivityJsonArray.toString(), TennisActivity[].class);
	
		for (TennisActivity tennisActivity : tennisActivities)
		{
			tennisCourtDetails.getTennisActivities()[tennisActivity.getTypeId()-1] = tennisActivity;
		}
	
		JSONArray tennisAmenityJsonArray = tennisCourtJsonObject.getJSONArray("TennisAmenity");
		TennisAmenity[] tennisAms = (TennisAmenity[]) gson.fromJson(tennisAmenityJsonArray.toString(), TennisAmenity[].class);
		for (TennisAmenity tennisAmenity : tennisAms)
		{
			tennisCourtDetails.getTennisAmeneties()[tennisAmenity.getTypeId()-1] = tennisAmenity;
			
		}
		
		
		JSONArray tennisContactArray = tennisCourtJsonObject.getJSONArray("TennisContact");
		TennisContact[] tennisContacts = (TennisContact[]) gson.fromJson(tennisContactArray.toString(), TennisContact[].class);
		for (TennisContact tennisContact : tennisContacts)
		{
			if (tennisContact.getTypeId() == 1) //only interested in phone number
			{
				tennisCourtDetails.setPhone(tennisContact.getContactDetails());
				break;
			}
		}
		
		JSONArray surfaceTypeArray = tennisCourtJsonObject.getJSONArray("SurfaceType");
		if (surfaceTypeArray != null && surfaceTypeArray.length() > 0 )

		

		{
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<surfaceTypeArray.length();i++)
		{
			if (i!=0)
				sb.append(",");
			sb.append(surfaceTypeArray.get(i));		
		}
			tennisCourtDetails.setSurfaceType(sb.toString());
		}
		else
			tennisCourtDetails.setSurfaceType("N/A");
		
		return tennisCourtDetails;
	}
	
	
	public ContentValues toContentValue()
	{
		ContentValues contentValues = new ContentValues(8);
		contentValues.put("_id", id);
		contentValues.put("name", nonNullString(name));
		contentValues.put("address", nonNullString(address));
		contentValues.put("zipcode", nonNullString(zipcode));
		contentValues.put("url", nonNullString(url));
		contentValues.put("facility_type", nonNullString(facilityType));
		contentValues.put("subcourts",  subcourts);
		contentValues.put("timings", nonNullString(timings));
		contentValues.put("city", nonNullString(city));
		contentValues.put("state", nonNullString(state));
		contentValues.put("abbreviation", nonNullString(abbreviation));
		contentValues.put("phone", nonNullString(phone));
		contentValues.put("surface_type", nonNullString(surfaceType));
		return contentValues;
	}
	

	
	public static TennisCourtDetails fromCursor(Cursor cursor, Cursor activityCursor, Cursor amenityCursor)
	{
		TennisCourtDetails tdc = new TennisCourtDetails();
		tdc.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		tdc.setName(cursor.getString(cursor.getColumnIndex("name")));
		tdc.setAddress(cursor.getString(cursor.getColumnIndex("address")));
		tdc.setZipcode(cursor.getString(cursor.getColumnIndex("zipcode")));
		tdc.setUrl(cursor.getString(cursor.getColumnIndex("url")));
		tdc.setFacilityType(cursor.getString(cursor.getColumnIndex("facility_type")));
		tdc.setSubcourts(cursor.getInt(cursor.getColumnIndex("subcourts")));
		tdc.setTennisTimings(cursor.getString(cursor.getColumnIndex("timings")));
		tdc.setCity(cursor.getString(cursor.getColumnIndex("city")));
		tdc.setState(cursor.getString(cursor.getColumnIndex("state")));
		tdc.setAbbreviation(cursor.getString(cursor.getColumnIndex("abbreviation")));
		tdc.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
		tdc.setSurfaceType(cursor.getString(cursor.getColumnIndex("surface_type")));
		
		for (boolean hasItem = activityCursor.moveToFirst(); hasItem; hasItem = activityCursor.moveToNext()) {
		    int type = activityCursor.getInt(activityCursor.getColumnIndex("_id"));
		    TennisActivity tennisActivity = tdc.new TennisActivity();
		    tennisActivity.setTypeId(type);
		    tennisActivity.setRemarks(activityCursor.getString(activityCursor.getColumnIndex("remark")));
		    tdc.getTennisActivities()[type-1] = tennisActivity;
		}
		for (boolean hasItem = amenityCursor.moveToFirst(); hasItem; hasItem = amenityCursor.moveToNext()) {
		    int type = amenityCursor.getInt(amenityCursor.getColumnIndex("_id"));
		    TennisAmenity tennisam =  tdc.new TennisAmenity();
		    tennisam.setTypeId(type);
		    tennisam.setRemarks(amenityCursor.getString(amenityCursor.getColumnIndex("remark")));
		    tdc.getTennisAmeneties()[type-1] = tennisam;
		}

		
		return tdc;
		
	}
	public final static String nonNullString(String inputString)
	{
		if (inputString == null || inputString.trim().equals(""))
			return "";
		return inputString;
	}
	
	public ContentValues[] contentValuesForActivity()
	{
		List<ContentValues> contentValues = new ArrayList<ContentValues>();
	
		for (int i = 0;i<tennisActivities.length;i++)
		{
			TennisActivity ta = tennisActivities[i];
			if (ta == null)
				continue;
			ContentValues cs = new ContentValues(3); 
			cs.put("_id", ta.getTypeId());
			cs.put("remark", nonNullString(ta.getRemarks()));
			cs.put("tennis_court", this.id);
			contentValues.add(cs);
		}
		return contentValues.toArray(new ContentValues[contentValues.size()]);
	}
	
	public ContentValues[] contentValuesForAmenity()
	{
		List<ContentValues> contentValues = new ArrayList<ContentValues>();
		
		for (int i = 0;i<tennisAmeneties.length;i++)
		{
			TennisAmenity ta = tennisAmeneties[i];
			if (ta == null)
				continue;
			ContentValues cs = new ContentValues(3); 
			cs.put("_id", ta.getTypeId());
			cs.put("remark", nonNullString(ta.getRemarks()));
			cs.put("tennis_court", this.id);
			contentValues.add(cs);
		}
		return contentValues.toArray(new ContentValues[contentValues.size()]);
	}
	
	
}
