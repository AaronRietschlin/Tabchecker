package com.android.tabchecker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
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
				stopServices();
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
		// starts the service
		Intent myServiceIntent = new Intent(TabChecker.this, MyAlarmService.class);
		startService(myServiceIntent);
	}

	public void stopServices() {
//		myServiceIntent = new Intent(TabChecker.this,
//				MyAlarmService.class);
//		stopService(myServiceIntent);

		// stops MyAlarmService using services name you need to start 
		// a service and when you cancel you cancel them all
		ComponentName serviceMyAlarm = startService(new Intent(this, MyAlarmService.class));
		stopService(new Intent(this, serviceMyAlarm.getClass()));
		try {
			Class serviceClass = Class.forName(serviceMyAlarm.getClassName());
			stopService(new Intent(this, serviceClass));
		} catch (ClassNotFoundException e) {}
		
		stopService(new Intent(this, MyRepeatingAlarm.class));
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
