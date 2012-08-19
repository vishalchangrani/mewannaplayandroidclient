package com.mewannaplay.asynctask;

import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;

import com.mewannaplay.Constants;
import com.mewannaplay.CourtDetailsActivity;
import com.mewannaplay.client.RequestMethods;

public class MarkCourtOccupiedAsyncTask extends BasicAsyncTask {

	
	  public MarkCourtOccupiedAsyncTask(Activity context ,int courtId) {
          super(context ,Constants.MARK_COURT_OCCUPIED + courtId, RequestMethods.POST, "Marking court occupied...");
     
  }
	@Override
	protected void parseResponse(JSONObject jsonObject) throws IOException {
		return;

	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		((CourtDetailsActivity)ownerAcitivty).onPostExecuteMarkCourtOccupiedTask(result);
	}

}
