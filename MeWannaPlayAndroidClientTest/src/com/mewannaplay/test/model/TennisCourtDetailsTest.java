package com.mewannaplay.test.model;

import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.mewannaplay.model.Message;
import com.mewannaplay.model.TennisCourtDetails;

public class TennisCourtDetailsTest extends AndroidTestCase {

	public void testTennisCourtDetailsFromJSONObject() {

		try {
			TennisCourtDetails tennisCourtDetails = TennisCourtDetails
					.fromJSONObject(new JSONObject(tennisCourtDetailsJSON));
			Message[] messages = Message
					.fromJSONObject(new JSONObject(messagesJSON));
		} catch (Throwable e)
		{
			fail(e.getMessage());
		}
		
	}

	public static String tennisCourtDetailsJSON = "{    \"status\": {        \"error\": \"No\",        \"code\": 200,        \"description\": \"\",        \"message\": \"Ok\"    },    \"tenniscourt\": {        \"tennis_court_id\": \"13604\",        \"tennis_name\": \"Willowpoint Golf & Country Club\",        \"tennis_address\": \"2544 Willow Point Rd,\nAlexander City, \nAL 35010-6264\",        \"tennis_zipcode\": \"35010\",        \"tennis_url\": \"http://www.willowpoint.com/Default.aspx?p=DynamicModule&pageid=257426&ssid=120128&vnf=1\",        \"tennis_facility_type\": \"Private\",        \"tennis_subcourts\": \"4\",        \"tennis_timings\": \"\",        \"city_name\": \"Alexander City\",        \"state_name\": \"Alabama\",        \"state_abbreviation\": \" AL \",        \"TennisActivity\": [            {                \"activity_type_id\": \"0\",                \"tennis_activity_remarks\": \"\"            },            {                \"activity_type_id\": \"5\",                \"tennis_activity_remarks\": \"\"            }        ],        \"TennisAmenity\": [            {                \"amenity_type_id\": \"3\",                \"tennis_amenity_remarks\": \"\"            },            {                \"amenity_type_id\": \"4\",                \"tennis_amenity_remarks\": \"\"            }        ],        \"TennisContact\": [            {                \"tennis_contact_details\": \"256-212-1407\",                \"contact_type_id\": \"0\"            },            {                \"tennis_contact_details\": \"lsharpe@russelllands.com\",                \"contact_type_id\": \"1\"            }        ],        \"Message\": [            {                \"message_id\": \"1\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"2\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"3\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"4\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"5\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"6\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"7\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"8\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"9\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"10\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"11\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"12\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"13\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"14\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"15\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"16\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"17\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"18\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"19\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"20\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"21\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"22\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"23\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"24\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"25\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"26\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"27\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"28\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"29\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"30\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"31\",                \"message_text\": \"This is amit testing for web-service 4\",                \"user_login_id\": \"1\",                \"player_type\": \"beginner\",                \"schedule_time\": \"2011-08-23 08:38:36\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2011-08-23 07:31:33\"            },            {                \"message_id\": \"37\",                \"message_text\": \"Amit wants to reserve\",                \"user_login_id\": \"5\",                \"player_type\": \"Beginner\",                \"schedule_time\": \"1970-01-01 00:00:00\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2012-01-21 07:28:29\"            },            {                \"message_id\": \"38\",                \"message_text\": \"Amit wants to reserve\",                \"user_login_id\": \"5\",                \"player_type\": \"\",                \"schedule_time\": \"2012-12-12 01:02:03\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2012-01-21 07:53:32\"            },            {                \"message_id\": \"39\",                \"message_text\": \"Amit wants to reserve\",                \"user_login_id\": \"5\",                \"player_type\": \"\",                \"schedule_time\": \"2012-12-12 01:02:03\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2012-01-21 07:54:39\"            },            {                \"message_id\": \"40\",                \"message_text\": \"Amit wants to reserve\",                \"user_login_id\": \"5\",                \"player_type\": \"\",                \"schedule_time\": \"2012-12-12 01:02:03\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2012-01-21 07:55:11\"            },            {                \"message_id\": \"41\",                \"message_text\": \"Amit wants to reserve\",                \"user_login_id\": \"5\",                \"player_type\": \"\",                \"schedule_time\": \"2012-12-12 01:02:03\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2012-01-21 07:56:00\"            },            {                \"message_id\": \"42\",                \"message_text\": \"Amit wants to reserve\",                \"user_login_id\": \"5\",                \"player_type\": \"Beginner\",                \"schedule_time\": \"2012-12-12 01:02:03\",                \"contact_info\": \"qu0253@gmail.com\",                \"player_needed\": \"2\",                \"contact_type_id\": \"1\",                \"created\": \"2012-01-21 07:57:32\"            }        ],        \"TennisSurface\": [            {                \"tennis_surface_id\": \"0\",                \"surface_type_id\": \"2318\"            },            {                \"tennis_surface_id\": \"1\",                \"surface_type_id\": \"331\"            }        ],        \"TennisSubcourtStatus\": [ ]    }}";
	public static String messagesJSON = "{    \"status\": {        \"error\": \"No\",        \"code\": 200,        \"description\": \"\",        \"message\": \"Ok\"    },    \"tennis_id\": \"13604\",    \"courtmessages\": [        {            \"message_id\": \"42\",            \"timeposted\": \"2012-01-21 07:57:32\",            \"schedule_time\": \"2012-12-12 01:02:03\",            \"postedbyuser\": \"5\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"Beginner\",            \"additionalcomment\": \"Amit wants to reserve\"        },        {            \"message_id\": \"41\",            \"timeposted\": \"2012-01-21 07:56:00\",            \"schedule_time\": \"2012-12-12 01:02:03\",            \"postedbyuser\": \"5\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"\",            \"additionalcomment\": \"Amit wants to reserve\"        },        {            \"message_id\": \"40\",            \"timeposted\": \"2012-01-21 07:55:11\",            \"schedule_time\": \"2012-12-12 01:02:03\",            \"postedbyuser\": \"5\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"\",            \"additionalcomment\": \"Amit wants to reserve\"        },        {            \"message_id\": \"39\",            \"timeposted\": \"2012-01-21 07:54:39\",            \"schedule_time\": \"2012-12-12 01:02:03\",            \"postedbyuser\": \"5\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"\",            \"additionalcomment\": \"Amit wants to reserve\"        },        {            \"message_id\": \"38\",            \"timeposted\": \"2012-01-21 07:53:32\",            \"schedule_time\": \"2012-12-12 01:02:03\",            \"postedbyuser\": \"5\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"\",            \"additionalcomment\": \"Amit wants to reserve\"        },        {            \"message_id\": \"37\",            \"timeposted\": \"2012-01-21 07:28:29\",            \"schedule_time\": \"1970-01-01 00:00:00\",            \"postedbyuser\": \"5\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"Beginner\",            \"additionalcomment\": \"Amit wants to reserve\"        },        {            \"message_id\": \"1\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"2\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"3\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"4\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"5\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"6\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"7\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"8\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"9\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"10\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"11\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"12\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"13\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        },        {            \"message_id\": \"14\",            \"timeposted\": \"2011-08-23 07:31:33\",            \"schedule_time\": \"2011-08-23 08:38:36\",            \"postedbyuser\": \"1\",            \"contact_type_id\": \"1\",            \"contact_info\": \"qu0253@gmail.com\",            \"playersneeded\": \"2\",            \"level\": \"beginner\",            \"additionalcomment\": \"This is amit testing for web-service 4\"        }    ]}";

}
