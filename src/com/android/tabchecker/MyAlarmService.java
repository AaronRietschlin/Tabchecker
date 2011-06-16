package com.android.tabchecker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

public class MyAlarmService extends Service {
	
	String svcName = Context.NOTIFICATION_SERVICE;
	NotificationManager notificationManager;
	
	AlarmManager alarms;
	PendingIntent alarmIntent;
	
	@Override
	public void onCreate() {
		//Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
		setupProximityAlert();
		//setupNotifications();	
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		//Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		alarms.cancel(alarmIntent);
		//Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		//Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);
	}
	
	// the alarm waring you that you are leaving the saved location
	class ProximityIntentReceiver extends BroadcastReceiver {
		
		public void onReceive(Context context, Intent intent) {

			String key = LocationManager.KEY_PROXIMITY_ENTERING;
			Boolean entering = intent.getBooleanExtra(key, false);
			
			// if false you are leavign the area
			if (entering == false) {				
				// set off notifications
				// TODO change this into an alarm that repeats
				//setupRepeatingAlarm;
				setupNotifications();

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
		locationManager.addProximityAlert(TabChecker.latSaved, TabChecker.lngSaved, radius,
				expiration, proximityIntent);

		IntentFilter filter = new IntentFilter(
				LocationManager.KEY_PROXIMITY_ENTERING);
		registerReceiver(new ProximityIntentReceiver(), filter);
	}
	
	// notifcation alarms
	// TODO  use tickerText to close the tab click on it and it will take you back to the app
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
		
		//Launch into a new activity where you can say you closed your tab and cancled the alarm!
		Intent intent = new Intent(this, TabChecker.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, expandedTitle, expandedText,
				pendingIntent);
		
		//Causes the phone to vibrate
		long[] vibrate = new long[] { 1000, 1000, 1000, 1000, 1000 };
		notification.vibrate = vibrate;
		
		//Causes the phone to ring based on the default notification sound
		Uri ringURI =
			RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.sound = ringURI;
		
		int notificationRef = 1;
		notificationManager.notify(notificationRef, notification);
		
		// repeating alarm
/*		int updateFreq = 1;
		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		long timeToRefresh = SystemClock.elapsedRealtime() + 
	                           updateFreq*10*1000;
	      
	    alarms.setRepeating(alarmType, timeToRefresh, 
	                          updateFreq*10*1000, alarmIntent); */ 
	}
}