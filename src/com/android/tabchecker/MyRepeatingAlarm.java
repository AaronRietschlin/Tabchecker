package com.android.tabchecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

public class MyRepeatingAlarm extends Service {
	
	NotificationManager notificationManager;
	String repeatingALarmService = Context.NOTIFICATION_SERVICE;
	
	@Override
	public void onCreate() {
		//Toast.makeText(this, "MyRepeatingService.onCreate()", Toast.LENGTH_LONG).show();
		
		// Gets a reference to the notification manager
		notificationManager = (NotificationManager) getSystemService(repeatingALarmService);
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
	
	@Override
	public IBinder onBind(Intent intent) {
		//Toast.makeText(this, "MyRepeatingService.onBind()", Toast.LENGTH_LONG).show();
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "MyRepeatingService.onDestroy()", Toast.LENGTH_LONG).show();
		
		// stop the myrepeatingalarm service
		Intent myServiceIntent = new Intent(this,
				MyRepeatingAlarm.class);
		stopService(myServiceIntent);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//Toast.makeText(this, "MyRepeatingService.onStart()", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		//Toast.makeText(this, "MyRepeatingService.onUnbind()", Toast.LENGTH_LONG).show();
		return super.onUnbind(intent);
	}
}