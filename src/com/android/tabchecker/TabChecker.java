package com.android.tabchecker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TabChecker extends Activity {

	private double lat;
	private double lng;
	private double latSaved;
	private double lngSaved;

	// Used for notifications:
	String svcName = Context.NOTIFICATION_SERVICE;
	NotificationManager notificationManager;

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

	class ProximityIntentReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {

			String key = LocationManager.KEY_PROXIMITY_ENTERING;
			Boolean entering = intent.getBooleanExtra(key, false);

			if (entering == false) {
				Toast.makeText(context,
						"Alarm has been set off, did you pay your tab?",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "	 Activated the alarm.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


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
				// uses default location provider, updates every 1/2 second, and
				// 1 meter
				locationManager.requestLocationUpdates(provider, 500, 1,
						mylocationListener);

				// saves location
				setSavedLocation(location);

				// start the proximity Alert
				setProximityAlert();
				
				setupNotifications();

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
		// uses default location provider, updates every 1/2 second, and 1 meter
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

	// Shows and saves the location which will trigger the alarm
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

	// the alarm waring you that you are leaving the saved location
	// does not work
	private void setProximityAlert() {

		String locService = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) getSystemService(locService);

		float radius = 50f; // in meters
		long expiration = -1; // alert will not expire

		Intent intent = new Intent(LocationManager.KEY_PROXIMITY_ENTERING);

		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, -1,
				intent, 0);

		locationManager.addProximityAlert(latSaved, lngSaved, radius,
				expiration, proximityIntent);

		IntentFilter filter = new IntentFilter(
				LocationManager.KEY_PROXIMITY_ENTERING);
		registerReceiver(new ProximityIntentReceiver(), filter);
	}

	public void setupNotifications() {
		// Gets a reference to the notification manager
		notificationManager = (NotificationManager) getSystemService(svcName);
		// Choose a drawable to display as the status bar icon
		int icon = R.drawable.icon;
		// Text to display in the status bar when the notification is launched
		CharSequence tickerText = "Reminder: Close your tab!"; //this.getString(R.string.tickerNotificationText);
		// The extended status bar orders notification in time order
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		// Text to display in the extended status window
		String expandedText = "You are out of range of the GPS and did forgot to close your tab!"; //R.string.expandedNotificationText
		// Title for the expanded status
		String expandedTitle = "Did you forget to close your tab?"; //R.string.expandedNotificationTitle
		// Intent to launch an activity when the extended text is clicked
		
		//TODO: Launch into a new activity where you can say you closed your tab!
		Intent intent = new Intent(this, TabChecker.class);
		PendingIntent launchIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, expandedTitle, expandedText,
				launchIntent);
		
		//Causes the phone to ring based on the default notification sound
		Uri ringURI =
			RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.sound = ringURI;
		
		//Causes the phone to vibrate
		long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
		notification.vibrate = vibrate;
		
		int notificationRef = 1;
		notificationManager.notify(notificationRef, notification);
	}

}