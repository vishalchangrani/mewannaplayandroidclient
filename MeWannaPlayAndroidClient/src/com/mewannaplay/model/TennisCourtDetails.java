/**
 * 
 */
package com.mewannaplay.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private String tennisTimings;
	@SerializedName("city_name")
	private String city;
	@SerializedName("state_name")
	private String state;
	@SerializedName("state_abbreviation")
	private String abbreviation;
	
	private String phone;
	
	 
	private boolean[] activites = new boolean[Constants.ACTIVITY.values().length];
	private String[] activity_remarks = new String[Constants.ACTIVITY.values().length];
	private boolean[] ameneties = new boolean[Constants.ACTIVITY.values().length];
	private String[]amenity_remarks = new String[Constants.ACTIVITY.values().length];
	
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
	}
	
	public class TennisContact
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
		return tennisTimings;
	}
	public void setTennisTimings(String tennisTimings) {
		this.tennisTimings = tennisTimings;
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
	public boolean[] getActivites() {
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
	}
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
			tennisCourtDetails.getActivites()[tennisActivity.getTypeId()] = true;
			tennisCourtDetails.getActivity_remarks()[tennisActivity.getTypeId()] = tennisActivity.getRemarks();
		}
	
		JSONArray tennisAmenityJsonArray = tennisCourtJsonObject.getJSONArray("TennisAmenity");
		TennisAmenity[] tennisAmeneties = (TennisAmenity[]) gson.fromJson(tennisAmenityJsonArray.toString(), TennisAmenity[].class);
		for (TennisAmenity tennisAmenity : tennisAmeneties)
		{
			tennisCourtDetails.getAmeneties()[tennisAmenity.getTypeId()] = true;
			tennisCourtDetails.getAmenity_remarks()[tennisAmenity.getTypeId()] = tennisAmenity.getRemarks();
		}
		
		
		JSONArray tennisContactArray = tennisCourtJsonObject.getJSONArray("TennisContact");
		TennisContact[] tennisContacts = (TennisContact[]) gson.fromJson(tennisContactArray.toString(), TennisContact[].class);
		for (TennisContact tennisContact : tennisContacts)
		{
			if (tennisContact.getTypeId() == 0) //only interested in phone number
			{
				tennisCourtDetails.setPhone(tennisContact.getContactDetails());
				break;
			}
		}
		return tennisCourtDetails;
	}
	
}
