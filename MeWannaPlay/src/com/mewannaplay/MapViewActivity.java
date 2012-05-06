
package com.mewannaplay;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.mewannaplay.providers.ProviderContract.TennisCourts;
import com.mewannaplay.view.MapLocationViewer;

public class MapViewActivity extends MapActivity {

    ProgressDialog dialog;
    private MapViewActivity activity;


    // private String[] cityList;

    private ArrayAdapter cityListAdapter;
    private Spinner spinner;
    // private final CityListSpinner cityList[] = new CityListSpinner[3];
    private List<CityListSpinner> cityList;

    @Override
    public void onCreate(Bundle icicle) {
    	
    	  final Account account = new Account("anonymous", Constants.ACCOUNT_TYPE);
    	ContentResolver.requestSync(account, "com.mewannaplay.providers.TennisCourtProvider", new Bundle());

    	activity = this;
        super.onCreate(icicle);

        setContentView(R.layout.mapviewlayout);

        getCourtListOnMap();
        bindService();

        bindList();

        cityList = new ArrayList<CityListSpinner>();

        spinner = (Spinner) findViewById(R.id.dropdown_city);
        spinner.setBackgroundResource(R.drawable.semi_transparent_dropdown);

        cityList.add(new CityListSpinner("-1", "Select City", "AL", "0",
                "0"));

        cityListAdapter =
                new ArrayAdapter<CityListSpinner>(
                        this,
                        R.layout.simple_spinner_layout,
                        cityList);

        cityListAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        cityListAdapter.setNotifyOnChange(true);

        spinner.setAdapter(cityListAdapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

        Button infoImageButton = (Button) findViewById(R.id.ImageInfoButton01);
        infoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp3();

            }
        });

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(TennisCourts.CONTENT_URI, null, null, null, null);
    }

    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Log.e("MeWannaPlay", "MyOnItemSelectedListener ++++++++++++ ");

            CityListSpinner d = cityList.get(pos);// (CityListSpinner)
            // parent.getAdapter();
            if (d == null)
                return;
            if (d.getCityId().equals("-1"))
                return;

            // MapLocationViewer mapLocViewer = new
            // MapLocationViewer(getBaseContext());
            //
            // Location selLocation = new Location(d.getCityLatitude());
            double Sellatitude = convertStringToDouble(d.getCityLatitude());
            double Sellongitude = convertStringToDouble(d.getCityLongitude());
            // Log.e("MeWannaPlay", "MyOnItemSelectedListener ++++++++++++ 0 ");
            // selLocation.setLatitude(latitude);
            // selLocation.setLongitude(longitude);
            // mapLocViewer.locationResult.gotLocation(selLocation);

            Log.e("MeWannaPlay",
                    "MyOnItemSelectedListener ++++++++++++ lat  " + d.getCityLatitude());
            Log.e("MeWannaPlay",
                    "MyOnItemSelectedListener ++++++++++++ long " + d.getCityLongitude());
            Toast.makeText(parent.getContext(),
                    "The planet is " + d.getCityName().toString(), Toast.LENGTH_LONG)
                    .show();

            Log.e("MeWannaPlay", "MyOnItemSelectedListener ++++++++++++ 2 ");

            // MapLocationViewer mView = new MapLocationViewer(activity); ;

            MapView mapView = MapLocationViewer.getMapView();
            GeoPoint curLocGeoPoint;
            curLocGeoPoint = new GeoPoint((int) (Sellatitude * 1e6), (int) (Sellongitude * 1e6));

            Log.e("MeWannaPlay", "MyOnItemSelectedListener ++++++++++++ 2 Sellatitude "
                    + Sellatitude * 1e6);

            Log.e("MeWannaPlay", "MyOnItemSelectedListener ++++++++++++ Sellongitude "
                    + Sellongitude * 1e6);

            mapView.getController().setZoom(14);
            mapView.getController().setCenter(curLocGeoPoint);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }

    }

    private double convertStringToDouble(String str) {
        Float f = new Float(str);
        double d = f.doubleValue();
        return d;
    }

    private void bindService() {
        Log.e("MeWannaPlay", "bindService 1 ");
        Intent service = new Intent(this, BackgroundWebSerive.class);
        service.putExtra(BackgroundWebSerive.EXTRAS_CALLER_ID, "getCityListForSpinner");
        service.putExtra(BackgroundWebSerive.EXTRAS_URL, "http://api.mewannaplay.com/v1/cities");

        // need to use this instead of startService();
        Log.e("MeWannaPlay", "bindService 2 ");
        getApplicationContext().startService(service);

        // Binding ..this block can also start service if not started already
        Intent bindIntent = new Intent(this, BackgroundWebSerive.class);
     //   bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        // Register Broadcast Receiver
    //    IntentFilter filter = new IntentFilter(BackgroundWebSerive.WEB_INTENT_FILTER);
    //    registerReceiver(myReceiver, filter);
    }

    private void getCourtListOnMap() {
        Log.e("MeWannaPlay", "getCourtListOnMap 1 ");
       /* Intent service = new Intent(this, MWPBgWebSerive.class);
        service.putExtra(MWPBgWebSerive.EXTRAS_CALLER_ID, "getCourtListOnMap");
        service.putExtra(
                MWPBgWebSerive.EXTRAS_URL,
                "http://api.mewannaplay.com/v1/tenniscourts/radius/30/lon/0.572601022995435/lat/-1.50048369412853/");

        // need to use this instead of startService();
        Log.e("MeWannaPlay", "getCourtListOnMap 2 ");
        getApplicationContext().startService(service);

        // Binding ..this block can also start service if not started already
        Intent bindIntent = new Intent(this, MWPBgWebSerive.class);
        bindService(bindIntent, serviceConnection1, Context.BIND_AUTO_CREATE);
        // Register Broadcast Receiver
        IntentFilter filter = new IntentFilter(MWPBgWebSerive.WEB_INTENT_FILTER1);
        registerReceiver(myReceiver1, filter); */
    }

    private void bindList() {
        try {
            // loadData("Select City");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_list_item_1, data);
        // adapter.setNotifyOnChange(true);
        // setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapoptionmenu, menu);
        menu.setGroupVisible(BIND_AUTO_CREATE, true);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.mapview:

                return true;
            case R.id.listview:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void showPopUp3() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Information");

        LayoutInflater inflater = getLayoutInflater();
        View checkboxLayout = inflater.inflate(R.layout.popuplayout, null);
        helpBuilder.setView(checkboxLayout);

        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.setIcon(R.drawable.help_information);
        helpDialog.show();

    }

    public void showSearchPopUp() {
        // getCurrentLocation();

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Zip Code");

        LayoutInflater inflater = getLayoutInflater();
        View searchPopLayout = inflater.inflate(R.layout.searchpopuplayout, null);
        helpBuilder.setView(searchPopLayout);
        helpBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
            }
        });

        helpBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.setIcon(R.drawable.search_zipcode);
        helpDialog.show();

    }

    /**
     * Must let Google know that a route will not be displayed
     */
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    // Using JSON since it serializes so nicely
    // Could also create a custom adapter extended from BaseAdapter but thats
    // outside the scope of this tutorial.
    private void loadData(String parseCityList) throws Exception {
        JSONObject json = new JSONObject(parseCityList);

        JSONArray nameArray = json.getJSONArray("cities");
        cityList.remove(0);
        String cityName;
        String cityId;
        String stateAbbr;
        String cityLongitude;
        String cityLatitude;

        int i = 0;

        cityName = nameArray.toString();

        for (i = 0; i < nameArray.length(); i++) {

            cityId = nameArray.getJSONObject(i).getString("city_id").toString();

            cityName = nameArray.getJSONObject(i).getString("city_name").toString();

            stateAbbr = nameArray.getJSONObject(i).getString("state_abbreviation")
                    .toString();

            cityLatitude = nameArray.getJSONObject(i).getString("city_latitude")
                    .toString();

            cityLongitude = nameArray.getJSONObject(i).getString("city_longitude")
                    .toString();

            cityList.add(new CityListSpinner(cityId, cityName, stateAbbr, cityLatitude,
                    cityLongitude));

        }

        cityListAdapter.setNotifyOnChange(true);

    }

    private void loadCourtsOnMap(String parseCourtLists) throws JSONException {
        JSONObject json = new JSONObject(parseCourtLists);

        JSONArray tennisCourtArray = json.getJSONArray("tenniscourt");

        String tennis_id;
        String tennis_latitude;
        String tennis_longitude;
        String tennis_subcourts;
        String Occupied;
        String tennis_facility_type;
        String tennis_name;
        String message_count;

        int i = 0;

        for (i = 0; i < tennisCourtArray.length(); i++) {

            tennis_id = tennisCourtArray.getJSONObject(i).getString("tennis_id").toString();
            tennis_latitude = tennisCourtArray.getJSONObject(i).getString("tennis_latitude")
                    .toString();
            tennis_longitude = tennisCourtArray.getJSONObject(i).getString("tennis_longitude")
                    .toString();
            tennis_subcourts = tennisCourtArray.getJSONObject(i).getString("tennis_subcourts")
                    .toString();
            Occupied = tennisCourtArray.getJSONObject(i).getString("Occupied").toString();
            tennis_facility_type = tennisCourtArray.getJSONObject(i)
                    .getString("tennis_facility_type").toString();
            tennis_name = tennisCourtArray.getJSONObject(i).getString("tennis_name").toString();
            message_count = tennisCourtArray.getJSONObject(i).getString("message_count").toString();

            Log.e("MeWannaPlay", "run 4--------------- " + tennis_id);
            MapLocationViewer.addMapLocation(tennis_id, tennis_latitude, tennis_longitude,
                    tennis_subcourts, Occupied, tennis_facility_type, tennis_name, message_count);

        }
    }


    class CityListSpinner {
        public CityListSpinner(String city_id, String city_name, String state_abbreviation,
                String city_latitude, String city_longitude) {
            this.city_id = city_id;
            this.city_name = city_name;
            this.state_abbreviation = state_abbreviation;
            this.city_latitude = city_latitude;
            this.city_longitude = city_longitude;

        }

        public String getCityId() {
            return city_id;
        }

        public String getCityName() {
            return city_name;
        }

        public String getCityStateAbbr() {
            return state_abbreviation;
        }

        public String getCityLatitude() {
            return city_latitude;
        }

        public String getCityLongitude() {
            return city_longitude;
        }

        @Override
        public String toString() {
            return city_name;
        }

        String city_id;
        String city_name;
        String state_abbreviation;
        String city_latitude;
        String city_longitude;

    }

}
