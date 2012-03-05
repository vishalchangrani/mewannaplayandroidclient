package com.mewannaplay.test.client;


import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.test.AndroidTestCase;

import com.mewannaplay.Constants;
import com.mewannaplay.client.RestClient;
import com.mewannaplay.model.TennisCourt;

public class RestClientTest extends AndroidTestCase {

	public void testExecuteGetAllCourts() throws Exception
	{
		RestClient restClient = new RestClient(Constants.GET_ALL_TENNISCOURTS);
		//Parse Response into our object
   //     Type collectionType = new TypeToken<List<TennisCourt>>(){}.getType();
        TennisCourt[] tennisCourts = TennisCourt.fromJSONObjectArray(restClient.execute());
        assertEquals(1843,tennisCourts.length);		
	}
	
	public void testLogin() throws Exception
	{
		

		RestClient.login("amit","amitgandhi");
		RestClient restClient = new RestClient(Constants.PARTNERFOUND);
		restClient.execute();
		
	}
	
	public void testLogout() throws Exception
	{

		RestClient.login("amit","amitgandhi");
		RestClient restClient = new RestClient(Constants.PARTNERFOUND);
		restClient.execute();
		RestClient.logout();
		restClient = new RestClient(Constants.PARTNERFOUND);
		try
		{
		restClient.execute();
		fail(" no exception on logout and partner found");
		}
		catch(Exception e)
		{
	
		}
		
	}
	
	public void testHttpClient() throws ClientProtocolException, IOException
	{
		HttpClient httpclient = new DefaultHttpClient();

	    // Create a local instance of cookie store
	    CookieStore cookieStore = new BasicCookieStore();

	    // Create local HTTP context
	    HttpContext localContext = new BasicHttpContext();
	    // Bind custom cookie store to the local context
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

	    HttpGet httpget = new HttpGet("http://api.mewannaplay.com/v1/tenniscourts");
        // Pass local context as a parameter
        HttpResponse response = httpclient.execute(httpget, localContext);
        HttpEntity entity = response.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(response.getStatusLine());
        if (entity != null) {
            System.out.println("Response content length: " + entity.getContentLength());
        }
        List<Cookie> cookies = cookieStore.getCookies();
        for (int i = 0; i < cookies.size(); i++) {
            System.out.println("Local cookie: " + cookies.get(i));
        }

        // Consume response content
      //  EntityUtils.consume(entity);

        System.out.println("----------------------------------------");

	}
}
