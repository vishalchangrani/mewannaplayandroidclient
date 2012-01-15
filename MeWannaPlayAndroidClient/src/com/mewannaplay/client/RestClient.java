
package com.mewannaplay.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mewannaplay.model.Status;


public class RestClient {
    private boolean authentication;
    private final ArrayList<NameValuePair> headers;
    private String jsonBody;
    private String message;
    private final ArrayList<NameValuePair> params;
    private String response;
    private int responseCode;
    private final String url;
    // HTTP Basic Authentication
    private String username;
    private String password;
    private static final String TAG = "RestClient";
  
    
    //shared instance of httpclient
    private static HttpClient mHttpClient;
    public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms

    public RestClient(String url) {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    // Be warned that this is sent in clear text, don't use basic auth unless
    // you have to.

    public void addBasicAuthentication(String user, String pass) {
        authentication = true;
        username = user;
        password = pass;
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    //for convenience, since most of the calls will of type GET
    public Object execute(Class returnType, String jsonId, boolean exepctingArray) throws Exception
    {
    	return execute(RequestMethods.GET, returnType, jsonId, exepctingArray);
    }
    
    //TODO fix this to better use generics
	public Object execute(RequestMethods method, Class returnType, String jsonId, boolean expectingArray)
			throws Exception {
		String responseString = null;
		switch (method) {
		case GET: {
			HttpGet request = new HttpGet(url + addGetParams());
			request = (HttpGet) addHeaderParams(request);
			responseString = executeRequest(request, url);
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);
			request = (HttpPost) addHeaderParams(request);
			request = (HttpPost) addBodyParams(request);
			responseString = executeRequest(request, url);
			break;
		}
		case PUT: {
			HttpPut request = new HttpPut(url);
			request = (HttpPut) addHeaderParams(request);
			request = (HttpPut) addBodyParams(request);
			responseString = executeRequest(request, url);
			break;
		}
		case DELETE: {
			HttpDelete request = new HttpDelete(url);
			request = (HttpDelete) addHeaderParams(request);
			responseString = executeRequest(request, url);
		}
		}

		if (responseString == null)
			throw new Exception("Server response not recevieved for " + url);

	//	responseString = "{\"status\":{\"error\":\"No\",\"code\":200,\"description\":\"\",\"message\":\"Ok\"},\"tenniscourt\":[{\"tennis_id\":\"13604\",\"tennis_latitude\":\"32.807622\",\"tennis_longitude\":\"-85.971383\",\"tennis_subcourts\":\"4\",\"Occupied\":\"124\",\"tennis_facility_type\":\"Private\",\"tennis_name\":\"Willowpoint Golf & Country Club\",\"message_count\":\"124\"}]}";
		// First extract status
		final JSONTokener jsonTokener = new JSONTokener(responseString);
		JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
		final GsonBuilder gsonb = new GsonBuilder();
		final Gson gson = gsonb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
		final Status status = gson
				.fromJson(jsonObject.getJSONObject("status").toString(), Status.class);

		if (status == null)
			throw new Exception("Server response not recevieved for " + url);
		if (status.isNotSuccess())
			throw new Exception("Server reported error " + status.getMessage());

		// Now extract the thing we are really interested in
		Object o = null;
		//TODO make this smarter removed the need for this expectedArray variable all together
		if (expectingArray)
		 o = gson.fromJson(jsonObject.getJSONArray(jsonId).toString(), returnType);
		else
		 o = gson.fromJson(jsonObject.getJSONObject(jsonId).toString(), returnType);
		return o;
	}

    private HttpUriRequest addHeaderParams(HttpUriRequest request) throws Exception {
        for (NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }
        if (authentication) {
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
            request.addHeader(new BasicScheme().authenticate(creds, request));
        }
        return request;
    }

    private HttpUriRequest addBodyParams(HttpUriRequest request) throws Exception {
        if (jsonBody != null) {
            request.addHeader("Content-Type", "application/json");
            if (request instanceof HttpPost)
                ((HttpPost) request).setEntity(new StringEntity(jsonBody, "UTF-8"));
            else if (request instanceof HttpPut)
                ((HttpPut) request).setEntity(new StringEntity(jsonBody, "UTF-8"));
        } else if (!params.isEmpty()) {
            if (request instanceof HttpPost)
                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            else if (request instanceof HttpPut)
                ((HttpPut) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }
        return request;
    }

    private String addGetParams() throws Exception {
        StringBuffer combinedParams = new StringBuffer();
        if (!params.isEmpty()) {
            combinedParams.append("?");
            for (NameValuePair p : params) {
                combinedParams.append((combinedParams.length() > 1 ? "&" : "") + p.getName() + "="
                        + URLEncoder.encode(p.getValue(), "UTF-8"));
            }
        }
        return combinedParams.toString();
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

  
    public void setJSONString(String data) {
        jsonBody = data;
    }

    private String executeRequest(HttpUriRequest request, String url) throws IOException {
        HttpResponse httpResponse;
        try {
        	maybeCreateHttpClient();
            httpResponse = mHttpClient.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
            	return EntityUtils.toString(entity);
            }
            return "";
        } catch (IOException e) {
        	mHttpClient.getConnectionManager().shutdown();
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
    
    public static boolean isClassCollection(Class c) {
    	  return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c);
    	}

}
