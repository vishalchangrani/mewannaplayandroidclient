package com.mewannaplay.test.client;


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
	
	
}
