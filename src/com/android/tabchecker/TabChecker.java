package com.android.tabchecker;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TabChecker extends Activity {

	PendingIntent servicePendingIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// set up Button saveLocationoButton which will save the location
		final Button saveButton = (Button) findViewById(R.id.saveLocationButton);

		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Start the MyAlarmService
				startService();
			}
		});

		// stops tab checker service and gps
		final Button cancelButton = (Button) findViewById(R.id.cancelAlertButton);

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Cancel the service
				stopService();
			}
		});

		// setup button to display the map showing saved location and current
		// location
		final Button mapButton = (Button) findViewById(R.id.mapButton);

		mapButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// launch the map actitiy
				startMap();

				// Tell the user what was done
				Toast.makeText(TabChecker.this, "You have launched the map",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void startService() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		Intent serviceIntent = new Intent(TabChecker.this, MyAlarmService.class);
		servicePendingIntent = PendingIntent.getService(TabChecker.this, 0,
				serviceIntent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 1);

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				servicePendingIntent);
	}

	public void stopService() {
		Intent myStopServiceIntent = new Intent(TabChecker.this,
				MyAlarmService.class);
		stopService(myStopServiceIntent);
	}

	public void startMap() {
		Intent mapIntent = new Intent(TabChecker.this, MyMap.class);
		startActivity(mapIntent);
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
