package com.android.tabchecker;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.*;

public class TabChecker extends Activity {

	private double lat;
	private double lng;
	public static double latSaved;
	public static double lngSaved;
	
	private PendingIntent servicePendingIntent;

	private final LocationListener mylocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	    // TODO make sure this works
		// Look up the AdView as a resource and load a request.
	    AdView adView = (AdView)this.findViewById(R.id.adView);
	    adView.loadAd(new AdRequest());

		// set up Button saveLocationoButton which will save the location
		final Button saveButton = (Button) findViewById(R.id.saveLocationButton);
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// setting up location services
				LocationManager locationManager;
				String context = Context.LOCATION_SERVICE;
				locationManager = (LocationManager) getSystemService(context);
				
				// settings for location provider allowing the best choices (GPS
				// or cellular)
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setCostAllowed(true);
				criteria.setPowerRequirement(Criteria.POWER_LOW);
				String provider = locationManager.getBestProvider(criteria,
						true);

				Location location = locationManager
						.getLastKnownLocation(provider);
				updateWithNewLocation(location);
				
				// uses default location provider, updates every 500 ms, and 1 meter 
				// TODO change this my use too much power
				locationManager.requestLocationUpdates(provider, 500, 1,
						mylocationListener);
				
				// saves saved location so it is displayed on the screen 
				setSavedLocation(location);
				
				// This is the code to make MyAlarmService work, it will wake up the phone from sleeping
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				
				Intent myIntent = new Intent(TabChecker.this,
						MyAlarmService.class);
				servicePendingIntent = PendingIntent.getService(
						TabChecker.this, 0, myIntent, 0);

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.SECOND, 1);
				
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), servicePendingIntent);
				
				// tell the user what we did
				Toast.makeText(TabChecker.this, "Started TabChecker Alarm!",
						Toast.LENGTH_LONG).show();
			}
		});
		
		// stops tab checker service and gps
		final Button cancelButton = (Button) findViewById(R.id.cancelAlertButton);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// This is the code to cancel MyAlarmService  alarm once set off
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarmManager.cancel(servicePendingIntent);

				// Tell the user about what we did.
				Toast.makeText(TabChecker.this, "You have canceled the TabChecker Alarm!",
						Toast.LENGTH_LONG).show();
			}
		});
		
		setUpGPS();	
	}

	// sets up actions updating the location depending on phone status
	private void setUpGPS() {
		// setting up location services
		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);

		// settings for location provider allowing the best choices (GPS or
		// cellular)
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);

		Location location = locationManager.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		// uses default location provider, updates every 500 ms, and 1 meter
		// dont really need this updating every second
		locationManager.requestLocationUpdates(provider, 500, 1,
				mylocationListener);
	}

	// updates location on screen
	private void updateWithNewLocation(Location location) {
		String latLongString;
		TextView myLocationText;
		myLocationText = (TextView) findViewById(R.id.myLocationText);
		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		} else {
			latLongString = "No location found";
		}
		myLocationText.setText("Your Current Position is:\n" + latLongString);
	}
	
	// displays the saved location
	private void setSavedLocation(Location location) {		
		String savedLatLongString;
		TextView mySavedLocationText;
		mySavedLocationText = (TextView) findViewById(R.id.savedLocationText);
		if (location != null) {
			latSaved = location.getLatitude();
			lngSaved = location.getLongitude();
			savedLatLongString = "Lat:" + latSaved + "\nLong:" + lngSaved;
		} else {
			savedLatLongString = "No saved bar";
		}
		mySavedLocationText.setText("Your Saved Bar Location is:\n"
				+ savedLatLongString);
	}
	
	// App lifecycle shit
	@Override
    protected void onPause() {
        super.onPause();
    }
	
	@Override
    protected void onStart() {
        super.onStart();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
    }
    
	@Override
	protected void onStop() {
        super.onStop();
    }
}
