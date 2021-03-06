package com.mewannaplay;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mewannaplay.model.City;
import com.mewannaplay.providers.ProviderContract;

public class StateCityChoiceDialog extends Dialog {

        
        Spinner citySpinner;
        Spinner stateSpinner;
        private int stateSpinnerCurrentPos = -1;
        protected LocationManager locationManager;
        TextView search,errormsg;
        
        Typeface bold,heavy,light,normal;
        
          public StateCityChoiceDialog(Context context) {
                super(context);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.state_city_choice_layout);
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                search=(TextView)findViewById(R.id.txtsearch);
            bold=Typeface.createFromAsset(context.getAssets(),"Folks-Bold.ttf");
                                 heavy=Typeface.createFromAsset(context.getAssets(),"Folks-Heavy.ttf");
                                 light=Typeface.createFromAsset(context.getAssets(),"Folks-Light.ttf");
                                 normal=Typeface.createFromAsset(context.getAssets(),"Folks-Normal.ttf");
                                 search.setTypeface(bold);
                
                 
                
        }
        
        

        public void init()
        {
                if (MapViewActivity.mapViewActivity == null)
                        return;
                City city = MapViewActivity.mapViewActivity.getCurrentCity();
                if (city != null)
                {

                        SimpleCursorAdapter adapter = ((SimpleCursorAdapter)(stateSpinner.getAdapter()));
                        
                        for(int i=0; i < adapter.getCount(); i++) {
                                Cursor cursor = (Cursor) adapter.getItem(i);
                                  if(city.getAbbreviation().equals(cursor.getString(cursor.getColumnIndex("abbreviation")))){
                                          stateSpinnerCurrentPos = i;
                                          stateSpinner.setSelection(i);
                                    break;
                                  }
                                }
                        

                        /*if (cursor.moveToFirst()) {
                                while (cursor.isAfterLast() == false) {
                                        if (cursor.getString(cursor.getColumnIndex("abbreviation")).equals(city.getAbbreviation()))
                                        {
                                                int initialPosition = cursor.getPosition();
                                                stateSpinnerCurrentPos = initialPosition;
                                                initialPosition = (int) adapter.getItemId(initialPosition);
                                                stateSpinner.setSelection(initialPosition);
                                                break;
                                        }
                                        cursor.moveToNext();
                                }
                        }*/

                        adapter = ((SimpleCursorAdapter)(citySpinner.getAdapter()));
                 

                        if (adapter.getCursor() == null)
                        {
                                initCursorForCitySpinner(city.getAbbreviation());
                        }
                        
                        for(int i=0; i < adapter.getCount(); i++) {
                                Cursor cursor = (Cursor) adapter.getItem(i);
                                  if(cursor.getString(cursor.getColumnIndex("name")).equals(city.getName()) && cursor.getString(cursor.getColumnIndex("abbreviation")).equals(city.getAbbreviation())){
                                          citySpinner.setSelection(i);
                                    break;
                                  }
                                }
                        
        
                }
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                
                super.onCreate(savedInstanceState);
                 
                stateSpinner = (Spinner) findViewById(R.id.state_spinner);
                citySpinner = (Spinner) findViewById(R.id.city_spinner);
                citySpinner.setEnabled(false);
                
                Cursor cur = getContext().getContentResolver().query(ProviderContract.Cities.CONTENT_URI, new String[]{"_id","abbreviation"} , " _id=_id) GROUP BY (abbreviation", null, " abbreviation asc");
                MapViewActivity.mapViewActivity.startManagingCursor(cur);
                SimpleCursorAdapter stateAdapter = new SimpleCursorAdapter(this.getContext(),
                    android.R.layout.simple_spinner_item, // Use a template
                                                          // that displays a
                                                          // text view
                    cur, // Give the cursor to the list adapter
                    new String[] {"abbreviation"}, // Map the NAME column in the
                                                         // people database to...
                    new int[] {android.R.id.text1}); // The "text1" view defined in
                                                     // the XML template
                                                         
                stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateSpinner.setAdapter(stateAdapter);
                
                stateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                        int arg2, long rowId) {
                                if (arg2 == stateSpinnerCurrentPos & citySpinner.isEnabled()) //This is like previous choice and City spinner has been initialized 
                                        return;
                                stateSpinnerCurrentPos = arg2;
                                Cursor stateCursor = (Cursor) arg0.getItemAtPosition(arg2);	//Else retrieve the state selected..we need it
                            	String state = stateCursor.getString(stateCursor.getColumnIndex("abbreviation"));    
                                if (state != null)
                                   initCursorForCitySpinner(state); //This wil enable the city spinner
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub
                                
                        }
                        
                });
                
              
                cur = null; //getContext().getContentResolver().query(ProviderContract.Cities.CONTENT_URI, new String[]{"_id","name"} , null, null, " name asc");
                SimpleCursorAdapter cityAdapter = new SimpleCursorAdapter(this.getContext(),
                            android.R.layout.simple_spinner_item, // Use a template
                                                                  // that displays a
                                                                  // text view
                            cur, // Give the cursor to the list adapter
                            new String[] {"name"}, // Map the NAME column in the
                                                                 // people database to...
                            new int[] {android.R.id.text1}); // The "text1" view defined in
                                                             // the XML template
                                                                 
                        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        citySpinner.setAdapter(cityAdapter);

                Button button = (Button) findViewById(R.id.okstatecity);
                button.setOnClickListener(new View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                                onOK(v);
                                
                        }
                });
                

                init();
                
                getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }

    	public void setCurrentLocationButton() {
    		errormsg = (TextView) findViewById(R.id.txterror);
    		Button button = (Button) findViewById(R.id.choose_current_location);
    		
//    		Location currentLocation = locationManager
  //  				.getLastKnownLocation(getBestProvider());
    	
    		
    		Location currentLocation = MapViewActivity.mapViewActivity
    				.getMyCurrentLocation();

    		if (currentLocation != null && currentLocation.getLatitude() != 0
    				&& currentLocation.getLongitude() != 0) {

    			button.setBackgroundResource(R.drawable.buttons);
    			errormsg.setText("");
    			button.setOnClickListener(new View.OnClickListener() {

    				@Override
    				public void onClick(View v) {
    					chooseCurrentLocation(v);
    				}
    			});
    		} else {

    			errormsg.setText("Current Location is Not Available");
    			button.setBackgroundResource(R.drawable.disablestate);
    		}
    	}
    	public String getBestProvider() {
//    		locationManager = (LocationManager) context.
//    				.getSystemService(Context.LOCATION_SERVICE);
    		Criteria criteria = new Criteria();
    		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
    		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
    		String bestProvider = locationManager.getBestProvider(criteria, true);
    		if(bestProvider!=null){
    		return bestProvider;}else{
    			return LocationManager.GPS_PROVIDER;
    		}
    	}

        private void onOK(View v)
        {
                
                        int position = citySpinner.getSelectedItemPosition();
                        SimpleCursorAdapter sca = (SimpleCursorAdapter)(citySpinner.getAdapter());
                        Cursor c = (Cursor) sca.getItem(position);
                        City city = new City();
                        city.setId(c.getInt(c.getColumnIndex("_id")));
                        city.setName(c.getString(c.getColumnIndex("name")));
                        city.setAbbreviation(c.getString(c.getColumnIndex("abbreviation")));
                        city.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
                        city.setLongitude(c.getDouble(c.getColumnIndex("longitude")));
                        MapViewActivity.mapViewActivity.setCurrentCity(city);
                        ((TextView) (MapViewActivity.mapViewActivity.findViewById(R.id.dropdown_city))).setText(city.getName()+","+city.getAbbreviation());
                        this.dismiss();
        }

        private void initCursorForCitySpinner(String state)
        {
                Cursor cur = getContext().getContentResolver().query(ProviderContract.Cities.CONTENT_URI, null , " abbreviation like ? ", new String[]{state}, " name asc");
                MapViewActivity.mapViewActivity.startManagingCursor(cur);
                ((SimpleCursorAdapter)(citySpinner.getAdapter())).changeCursor(cur);
                citySpinner.setEnabled(true);
        }
        
        public void chooseCurrentLocation(View v)
        {
                MapViewActivity.mapViewActivity.setCurrentCity(null);
                ((TextView) (MapViewActivity.mapViewActivity.findViewById(R.id.dropdown_city))).setText(R.string.current_location);
                this.dismiss();
        }

}
