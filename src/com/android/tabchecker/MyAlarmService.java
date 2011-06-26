package com.android.tabchecker;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class MyAlarmService extends Service {

	String svcName = Context.NOTIFICATION_SERVICE;
	NotificationManager notificationManager;
	LocationManager locationManager;
	PendingIntent pendingIntent;

	public static double lat;
	public static double lng;
	public static double latSaved;
	public static double lngSaved;

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
		// starts proximity alert
		setupProximityAlert();
		// sets the saved location
		setSavedLocation(location);

		// uses default location provider, updates every 1 sec, and 1 meter
		// TODO change this my use too much power
		locationManager.requestLocationUpdates(provider, 1000, 1,
				mylocationListener);

		// tell user about what
		Toast.makeText(this, "Started TabChecker Alarm!", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// Toast.makeText(this, "MyAlarmService.onBind()",
		// Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Toast.makeText(this, "MyAlarmService.onDestroy()",
		// Toast.LENGTH_LONG).show();
		// Tell the user about what we did.
		Toast.makeText(MyAlarmService.this,
				"You have canceled the TabChecker Alarm!", Toast.LENGTH_LONG)
				.show();
		// Cancel Repeating Alarm when the service is closed
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

		// removes proximity alert
		// locationManager.removeProximityAlert(proximityIntent);

		// TODO removes location services
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
				// set off notifications
				//setupNotifications();

			} else {
				Toast.makeText(context, "Activated the alarm.",
						Toast.LENGTH_SHORT).show();
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
		registerReceiver(new ProximityIntentReceiver(), filter);
	}

	// notifcation alarms
	// TODO use tickerText to close the tab click on it and it will take you
	// back to the app
	public void setupNotifications() {
		// Gets a reference to the notification manager
		notificationManager = (NotificationManager) getSystemService(svcName);
		// Choose a drawable to display as the status bar icon
		int icon = R.drawable.icon;
		// Text to display in the status bar when the notification is launched
		CharSequence tickerText = "Reminder: Close your tab!"; // this.getString(R.string.tickerNotificationText);
		// The extended status bar orders notification in time order
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		// Text to display in the extended status window
		String expandedText = "You are out of range of the GPS and did forgot to close your tab!"; // R.string.expandedNotificationText
		// Title for the expanded status
		String expandedTitle = "Did you forget to close your tab?"; // R.string.expandedNotificationTitle
		// Intent to launch an activity when the extended text is clicked

		// Launch into a new activity where you can say you closed your tab and
		// cancled the alarm!
		Intent intent = new Intent(this, TabChecker.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, expandedTitle, expandedText,
				pendingIntent);

		// Causes the phone to vibrate
		long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
		notification.vibrate = vibrate;

		// Causes the phone to ring based on the default notification sound
		Uri ringURI = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.sound = ringURI;

		int notificationRef = 1;
		notificationManager.notify(notificationRef, notification);
	}

	private void updateWithNewLocation(Location location) {

		if (location != null) {
			lat = location.getLatitude();
			lng = location.getLongitude();
		} else {
			Toast.makeText(this,
					"No Location Services Found Start Service Again",
					Toast.LENGTH_LONG).show();
		}
	}

	private void setSavedLocation(Location location) {

		if (location != null) {
			latSaved = location.getLatitude();
			lngSaved = location.getLongitude();
		} else {
			Toast.makeText(this, "Location Not Saved", Toast.LENGTH_LONG)
					.show();
		}
	}
	
	private void repeatingAlarmServices() {
		Intent myAlarmIntent = new Intent(MyAlarmService.this, MyRepeatingAlarm.class);
		pendingIntent = PendingIntent.getService(MyAlarmService.this, 0, myAlarmIntent, 0);
		
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 10);
		// repeat alarm every 10 seconds
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		
		Toast.makeText(MyAlarmService.this, "Start Repeating Alarm", Toast.LENGTH_LONG).show();
	}
}