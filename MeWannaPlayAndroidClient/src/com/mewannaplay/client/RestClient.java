package com.mewannaplay.client;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mewannaplay.Constants;
import com.mewannaplay.authenticator.AuthenticatorActivity;
import com.mewannaplay.model.Status;
import com.mewannaplay.model.User;

public class RestClient {
	
	private final static ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
	private String message;
	private String response;
	private int responseCode;
	private final String url;

	private static final String TAG = "RestClient";
	private static boolean loggedIn = false;

	// shared instance of httpclient
	private static HttpClient mHttpClient;
	public static final int REGISTRATION_TIMEOUT = 120 * 1000; // ms

	// Create a local instance of cookie store
	private final static CookieStore cookieStore = new BasicCookieStore();

	// Create local HTTP context
	private final static HttpContext localContext = new BasicHttpContext();

	public RestClient(String url) {
		this.url = url;
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	// for convenience, since most of the calls will of type GET
	public JSONObject execute() throws IOException  {
		return execute(RequestMethods.GET, null);
	}

	// TODO fix this to better use generics
	public JSONObject execute(RequestMethods method, JSONObject jsonObjectToSend) throws IOException
			{
		String responseString = null;
		switch (method) {
		case GET: {
			HttpGet request = new HttpGet(url);
		//	request = (HttpGet) addHeaderParams(request);
			responseString = executeRequest(request, url);
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);
		//	request = (HttpPost) addHeaderParams(request);
			if (jsonObjectToSend != null) {
				StringEntity se = new StringEntity(jsonObjectToSend.toString());
				request.setEntity(se);
				Log.d(TAG, jsonObjectToSend.toString());
			}
			responseString = executeRequest(request, url);
			break;
		}
		case DELETE: {
			HttpDelete request = new HttpDelete(url);
			responseString = executeRequest(request, url);
			break;
		}
		/*
		 * case PUT: { HttpPut request = new HttpPut(url); request = (HttpPut)
		 * addHeaderParams(request); request = (HttpPut) addBodyParams(request);
		 * responseString = executeRequest(request, url); break; } case DELETE:
		 * { HttpDelete request = new HttpDelete(url); request = (HttpDelete)
		 * addHeaderParams(request); responseString = executeRequest(request,
		 * url); }
		 */
		}

		if (responseString == null)
			throw new IOException("Server response not recevieved for " + url);

		// responseString =
		// "{\"status\":{\"error\":\"No\",\"code\":200,\"description\":\"\",\"message\":\"Ok\"},\"tenniscourt\":[{\"tennis_id\":\"13604\",\"tennis_latitude\":\"32.807622\",\"tennis_longitude\":\"-85.971383\",\"tennis_subcourts\":\"4\",\"Occupied\":\"124\",\"tennis_facility_type\":\"Private\",\"tennis_name\":\"Willowpoint Golf & Country Club\",\"message_count\":\"124\"}]}";
		// First extract status
		final JSONTokener jsonTokener = new JSONTokener(responseString);
		Status status = null;
		JSONObject jsonObject = null;
		try 
		{
		jsonObject = (JSONObject) jsonTokener.nextValue();
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(
				FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		status = gson.fromJson(jsonObject.getJSONObject("status")
				.toString(), Status.class);
		}
		catch(Exception e)
		{
			throw new IOException("Conversion error " + url);
		}

		if (status == null)
			throw new IOException("Server response not recevieved for " + url);
		if (status.isNotSuccess())
			throw new IOException(status.getMessage());

		return jsonObject;

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

	private String executeRequest(HttpUriRequest request, String url)
			throws IOException {
		HttpResponse httpResponse;
		try {
			maybeCreateHttpClient();
			httpResponse = mHttpClient.execute(request, localContext);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
			return "";
		} catch (IOException e) {
			mHttpClient.getConnectionManager().shutdown();
			mHttpClient = null;
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Configures the httpClient to connect to the URL provided.
	 */
	public static void maybeCreateHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params,
					REGISTRATION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
			ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		}
	}

	public static void login(String username, String password) throws Exception {
		if (loggedIn) {

					Log.e(TAG, " Already logged in. Logging out");
					logout();
				
		}
		else
		{
			headers.clear();

			cookieStore.clear();
		}
			// Bind custom cookie store to the local context
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
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
					cookieStore.clear();
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
			headers.clear();

			cookieStore.clear();

			loggedIn = false;
		}

	}

	public static boolean isLoggedIn()
	{
		return loggedIn;
	}
}
