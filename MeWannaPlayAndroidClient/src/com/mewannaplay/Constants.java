/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mewannaplay;

public class Constants {

    /**
     * Account type string.
     */
    public static final String ACCOUNT_TYPE = "com.mewannaplay";

    /**
     * Authtoken type string.
     */
    public static final String AUTHTOKEN_TYPE =
        "com.mewannaplay";
    
    public static final String ANONYMOUS_USER = "anonymous";
    
    public static final String BASE_URL =
            "http://api.mewannaplay.com/v1/";
    public static final String GET_ALL_TENNISCOURTS =
            BASE_URL+"tenniscourts";
    public static final String GET_TENNISCOURT_DETAILS =
    		BASE_URL+"tenniscourtdetails/index/";
    public static final String GET_TENNISCOURT_MESSAGES =
    		BASE_URL+"tenniscourtmessages/index/"; //here index to be passed is courtid 
    public static final String ADD_USER  =
    		BASE_URL+"users/add/";
    public static final String LOGIN  =
    		BASE_URL+"users/login/";
    public static final String LOGOUT  =
    		BASE_URL+"users/logout/";
    public static final String PARTNERFOUND  =
    		BASE_URL+"partnerfound/";
    public static final String GET_ALL_CITIES =
            BASE_URL+"cities";
    public static final String POST_MESSAGE =
            BASE_URL+"tenniscourtmessages/add";
    public static final String DELETE_MESSAGE =
            BASE_URL+"tenniscourtmessage/index/?/partnerfound/"; //e.g.  http://api.mewannaplay.com/V1/tenniscourtmessage/index/13604/partnerfound/true
    //NOTE in delete message index is message id
    public static final String MARK_COURT_OCCUPIED  =
    		BASE_URL+"markoccupied/id/";
    public static final String GET_OCCUPIED_COURT_AND_POSTED_MSG  =
    		BASE_URL+"user";
    public static final String GET_TENNISCOURT_MESSAGE_BY_COURTID =
            BASE_URL+"tenniscourtmessage/index/";
    public static final String GET_TENNIS_COURT_STATS =
            BASE_URL+"tenniscourtstats";  //Tennis court stats is information about the court which changes over time (message count and occupied count)
    
    public final static int password_salt_length = 8; //MUST MATCH SERVER

	public static final float PROXIMITY = 50f; //METERS
    
    public static enum ACTIVITY
    {
    	NEW_PLAYER_ADULT_PROGRAM,
    	NEW_PLAYER_JUNIOR_PROGRAM,
    	TEAM_TENNIS,
    	TOURNAMENTS,
    	LADDERS,
    	ROUND_ROBINS,
    	SOCIAL_MIXERS,
    	SENIORS,
    	OTHERS
    };
    
    public static enum AMENITY
    {
    	LOCKER_ROOM,
    	PARKING,
    	LESSONS,
    	LIGHTS,
    	SNACK_BAR,
    	SHOWER_ROOM,
    	SHOP
    }
   

}
