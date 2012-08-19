package com.mewannaplay.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.mewannaplay.CourtDetailsActivity;

public class CourtDetailsActivityTest extends ActivityInstrumentationTestCase2<CourtDetailsActivity> {

	CourtDetailsActivity activity;
	public CourtDetailsActivityTest() {
		super("com.mewannaplay",CourtDetailsActivity.class);
		// TODO Auto-generated constructor stub
	}
	
	@UiThreadTest
	@Override
	  protected void setUp() throws Exception {
	    super.setUp();
	    setActivityInitialTouchMode(false);
	    activity = getActivity();
	

	}

	public void testPreConditions() {

	  } 

}
