package com.mewannaplay.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Build;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mewannaplay.Constants;
import com.mewannaplay.authenticator.AuthenticatorActivity;
import com.mewannaplay.model.NewUser;
import com.mewannaplay.model.Status;
import com.mewannaplay.model.User;

public class RestClient {


        private String message;
        private String response;
        private int responseCode;
        private final String url;

        private static final String TAG = "RestClient";
        private static boolean loggedIn = false;


        // Create a local instance of cookie store
        private static CookieManager cookieManager = new CookieManager();
 
        static
        {
        	// HTTP connection reuse which was buggy pre-froyo
            if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }
        }
      
        public RestClient(String url) {
                this.url = url;
        }

      

        // for convenience, since most of the calls will of type GET
        public JSONObject execute() throws IOException {
                return execute(RequestMethods.GET, null);
        }
     
        public JSONObject execute(RequestMethods method, JSONObject jsonObjectToSend)
                throws IOException {
        
        	InputStreamReader inputStreamReader = null;
        	BufferedReader bufferedReader = null;
        	InputStream inputStream = null;
        	try
        	{
        	inputStream = excuteAndReturnInputStream(method,jsonObjectToSend);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);   
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
            }
            
            
            String responseString = response.toString();
            if (responseString == null)
                    throw new IOException("Server response not recevieved for " + url);
            
            // responseString =
            // "{\"status\":{\"error\":\"No\",\"code\":200,\"description\":\"\",\"message\":\"Ok\"},\"tenniscourt\":[{\"tennis_id\":\"13604\",\"tennis_latitude\":\"32.807622\",\"tennis_longitude\":\"-85.971383\",\"tennis_subcourts\":\"4\",\"Occupied\":\"124\",\"tennis_facility_type\":\"Private\",\"tennis_name\":\"Willowpoint Golf & Country Club\",\"message_count\":\"124\"}]}";
            // First extract status
            final JSONTokener jsonTokener = new JSONTokener(responseString);
            Status status = null;
            JSONObject jsonObject = null;
            jsonObject = (JSONObject) jsonTokener.nextValue();
            final GsonBuilder gsonb = new GsonBuilder();
            final Gson gson = gsonb.setFieldNamingPolicy(
            FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            status = gson.fromJson(jsonObject.getJSONObject("status").toString(), Status.class);
          

            if (status == null)
                    throw new IOException("Server response not recevieved for " + url);
            if (status.isNotSuccess())
                    throw new IOException(status.getMessage());

            return jsonObject;
        	}
        	catch (Exception e) {
                throw new IOException("Conversion error " + url);
        	}
        	finally
        	{
        		if (bufferedReader != null)
        			bufferedReader.close();
                if (inputStreamReader != null)
                	inputStreamReader.close();
                if (inputStream != null)
                	inputStream.close();
        	}
        
        
        
        }
        
        public JsonReader excuteGetAndReturnStream() throws IOException 
        {
        	InputStream inputStream = excuteAndReturnInputStream(RequestMethods.GET,null);
        	InputStreamReader inputStreamReader = null;
        	BufferedReader bufferedReader = null;
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);       	
        	return new JsonReader(bufferedReader);
        }
        
        private InputStream excuteAndReturnInputStream(RequestMethods method, JSONObject jsonObjectToSend) throws IOException 
        {
        	 
        	HttpURLConnection conn = null;
        	InputStream inputStream = null;
        	   try
        	   {
        	   
        		URL urlObject = new URL(url);
       			conn = (HttpURLConnection) urlObject.openConnection();
       			conn.addRequestProperty("Accept-Encoding", "gzip");
       			if (cookieManager == null)
       				cookieManager = new CookieManager();
       			cookieManager.setCookies(conn);	   
        	   
       		switch(method)
        	   {
        	   case GET: 	
        			
        			break; 

        	   case POST:
        		   conn.setDoOutput(true);
        		   byte[] payload = jsonObjectToSend.toString().getBytes();
        		   conn.setFixedLengthStreamingMode(payload.length);
                   conn.getOutputStream().write(payload);
                   break;
                   
        	   case DELETE:
        		 //  conn.setDoOutput(true);
        		   conn.setRequestMethod("DELETE");
        		   break;
        	   }
       			
       			inputStream = conn.getInputStream();
       			responseCode = conn.getResponseCode();
			    message = conn.getResponseMessage();
			   
		
               String contentEncoding = conn.getContentEncoding();
              
               if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                       Log.d(TAG, " response is in gzip");
                       inputStream = new GZIPInputStream(inputStream);
               }
               
               return inputStream;
          
        	   }
        	   catch (Throwable t)
        	   {
        		   Log.e(TAG,"Exception in RestClient "+t.getMessage());
        		   throw new IOException("Exception in RestClient "+t.getMessage());
        	   }
        	   finally
        	   {
        		  if (conn != null)
        			  cookieManager.storeCookies(conn);
        		   Log.d(TAG," Done for request "+url+" response code is "+responseCode+" Message is "+message);
        	   }

        }
        
   
        public String getErrorMessage() {
                return message;
        }

        public String getResponse() {
                return response;
        }

        public int getResponseCode() {
                return responseCode;
        }


        public static void login(String username, String password) throws Exception {
                if (loggedIn) {

                        Log.e(TAG, " Already logged in. Logging out");
                        logout();

                } else {
                        
                        cookieManager = new CookieManager();
                }
                // Bind custom cookie store to the local context
              
                try {
                        // Login magic goes here
                        User user = new User(username,
                                        AuthenticatorActivity.encryptPassword(password));
                        RestClient restClient = new RestClient(Constants.LOGIN);
                        restClient.execute(RequestMethods.POST, user.toJSONObject());
                        Log.d(TAG, username + " logged in successfully");
                        loggedIn = true;
                } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        throw new Exception("Failed to login");
                } catch (JsonSyntaxException e) {
                        Log.e(TAG, e.getMessage());
                        throw new Exception("Failed to login");
                } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        throw e;
                } finally {
                        if (!loggedIn)
                        	cookieManager = null;
                }

        }

        public static void logout() {
                Log.d(TAG, "Doing logout...");
                if (!loggedIn)
                        Log.e(TAG, "Logout called when user not logged in");

                try {
                        RestClient restClient = new RestClient(Constants.LOGOUT);
                        restClient.execute(RequestMethods.POST, null);
                } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                } finally {
  
                        cookieManager = null;
                        loggedIn = false;
                }

        }

        public static void registerNewUser(String userName, String password,
                        String email) throws Exception{
                if (loggedIn) {
                        Log.e(TAG,
                                        " Already logged in! and creating a new user... Logging out");
                        logout();

                } else {
 
                        cookieManager = new CookieManager();
                }
                // Bind custom cookie store to the local context
                //localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                try {
                        // Login magic goes here
                        NewUser user = new NewUser(userName,
                                        AuthenticatorActivity.encryptPassword(password), email);
                        RestClient restClient = new RestClient(Constants.ADD_USER);
                        restClient.execute(RequestMethods.POST, user.toJSONObject());
                        Log.d(TAG, userName + " registered successfully");
                
                } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        throw new Exception("Failed to login");
                } catch (JsonSyntaxException e) {
                        Log.e(TAG, e.getMessage());
                        throw new Exception("Failed to login");
                } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        throw e;
                } 
        }

        public static boolean isLoggedIn() {
                return loggedIn;
        }
}
