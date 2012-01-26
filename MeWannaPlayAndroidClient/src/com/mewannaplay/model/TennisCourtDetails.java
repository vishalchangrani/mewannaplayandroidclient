/**
 * 
 */
package com.mewannaplay.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author vishal
 *
 */
public class TennisCourtDetails {

	@SerializedName("tennis_id")
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
	@SerializedName("TennisActivity")
	private TennisActivity[] tennisActivity;
	public class TennisActivity
	{
		@SerializedName("activity_type_id")
		private int typeId;
		@SerializedName("tennis_activity_remarks")
		private int remarks;
	}
	
	
}
