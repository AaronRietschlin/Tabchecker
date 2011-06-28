package com.android.tabchecker;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class MyService<MyAlarmService> extends Service implements LocationListener {

	String svcName = Context.NOTIFICATION_SERVICE;
	NotificationManager notificationManager;
	LocationManager locationManager;
	PendingIntent pendingIntent;
	AlarmManager alarmManager;
	
	ProximityIntentReceiver receiver = new ProximityIntentReceiver();
	
	public static double lat;
	public static double lng;
	public static double latSaved;
	public static double lngSaved;

	@Override
	public void onCreate() {
		// Toast.makeText(this, "MyAlarmService.onCreate()",
		// Toast.LENGTH_LONG).show();
		// test of service/notifications
		// setupNotifications();

		// setting up location services
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
		String provider = locationManager.getBestProvider(criteria, true);

		Location location = locationManager.getLastKnownLocation(provider);
		// give uses current location
		updateWithNewLocation(location);
		// sets the saved location
		setSavedLocation(location);

		// uses default location provider, updates every 1 sec, and 1 meter
		locationManager.requestLocationUpdates(provider, 1000, 1,
				this);

		// tell user about what
		Toast.makeText(this, "Started TabChecker Alarm!", Toast.LENGTH_LONG)
				.show();
		
		// starts proximity alert
		setupProximityAlert();
		
		// test notifications
		repeatingAlarmServices();
		
		// removes location services
		if (TabChecker.removeAllShit == true) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// Toast.makeText(this, "MyAlarmService.onBind()",
		// Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onDestroy() {
		//Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
		// Tell the user about what we did.
		Toast.makeText(MyService.this,
				"You have canceled the TabChecker Alarm!", Toast.LENGTH_LONG)
				.show();
		
		// Cancel Repeating Alarm when the service is closed
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent myAlarmIntent = new Intent(MyService.this, MyRepeatingAlarm.class);
		pendingIntent = PendingIntent.getService(MyService.this, 0, myAlarmIntent, 0);
		alarmManager.cancel(pendingIntent);

		// removes proximity alert
		//locationManager.removeProximityAlert(proximityIntent);
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		// Toast.makeText(this, "MyAlarmService.onStart()",
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// Toast.makeText(this, "MyAlarmService.onUnbind()",
		// Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);
	}

	// the alarm waring you that you are leaving the saved location
	class ProximityIntentReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {

			String key = LocationManager.KEY_PROXIMITY_ENTERING;
			Boolean entering = intent.getBooleanExtra(key, false);

			// if false you are leavign the area
			if (entering == false) {
				// set off repeating alarm
				repeatingAlarmServices();
				// unregisters proximity reciver
				//unregisterReceiver(receiver);

			} else {
				Toast.makeText(context, "Activated the alarm.",
						Toast.LENGTH_SHORT).show();
			}
			
			// unregisters proximity reciver
			if (TabChecker.removeAllShit == true) {
				unregisterReceiver(receiver);
			}
		}
	}

	// the proximity alert code
	private void setupProximityAlert() {

		String locService = Context.LOCATION_SERVICE;
		LocationManager locationManager = (LocationManager) getSystemService(locService);

		float radius = 50f; // in meters
		long expiration = -1; // alert will not expire
		// intent to trigger the reciver to be false or true
		Intent intent = new Intent(LocationManager.KEY_PROXIMITY_ENTERING);

		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, -1,
				intent, 0);
		// the actual function which is called to
		locationManager.addProximityAlert(latSaved, lngSaved, radius,
				expiration, proximityIntent);

		IntentFilter filter = new IntentFilter(
				LocationManager.KEY_PROXIMITY_ENTERING);
		registerReceiver(receiver, filter);
	}
	
	private void updateWithNewLocation(Location location) {

		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
		}
	}

	private void setSavedLocation(Location location) {

		if (location != null) {
			latSaved = location.getLatitude();
			lngSaved = location.getLongitude();
		} else {
			Toast.makeText(this, "Location Not Saved No Location Services Found, Start Again", Toast.LENGTH_LONG)
					.show();
		}
	}
	
	private void repeatingAlarmServices() {
		Intent myAlarmIntent = new Intent(MyService.this, MyRepeatingAlarm.class);
		pendingIntent = PendingIntent.getService(MyService.this, 0, myAlarmIntent, 0);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		// repeat alarm every 10 seconds
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10, pendingIntent);
		
		Toast.makeText(MyService.this, "Start Repeating Alarm", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		updateWithNewLocation(location);
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		updateWithNewLocation(null);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
}