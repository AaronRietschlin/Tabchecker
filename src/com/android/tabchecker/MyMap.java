package com.android.tabchecker;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyMap extends MapActivity {

	private MapController mapController;
	List<Overlay> mapOverlays;
	MyItemizedOverlay itemizedOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to map.xml
		setContentView(R.layout.map);

		MapView mapView = (MapView) findViewById(R.id.myMapView);
		// mapview view shit
		mapView.setBuiltInZoomControls(true);

		// Get the Map View's controller
		mapController = mapView.getController();
		// Zoom in
		mapController.setZoom(17);

		// set points
		// set saved point on the map
		Double geoSavedLat = MyService.latSaved * 1E6;
		Double geoSavedLng = MyService.lngSaved * 1E6;

		// This sets the an overlay item on the map (the map marker)

		mapOverlays = mapView.getOverlays();
		Drawable drawablesaved = this.getResources().getDrawable(
				R.drawable.markersaved);

		itemizedOverlay = new MyItemizedOverlay(drawablesaved);
		GeoPoint savedPoint = new GeoPoint(geoSavedLat.intValue(),
				geoSavedLng.intValue());
		OverlayItem overlayItemSaved = new OverlayItem(savedPoint, "Saved Bar",
				"Saved Bar");
		itemizedOverlay.addOverlay(overlayItemSaved);
		mapOverlays.add(itemizedOverlay);

		// set users current location on map
		Double geoLat = MyService.lat * 1E6;
		Double geoLng = MyService.lng * 1E6;
		mapOverlays = mapView.getOverlays();
		Drawable drawablecurrent = this.getResources().getDrawable(
				R.drawable.markercurrent);

		itemizedOverlay = new MyItemizedOverlay(drawablecurrent);
		GeoPoint currentPoint = new GeoPoint(geoLat.intValue(),
				geoLng.intValue());
		OverlayItem overlayItemCurrent = new OverlayItem(currentPoint,
				"Current Location", "Current Location");
		itemizedOverlay.addOverlay(overlayItemCurrent);
		mapOverlays.add(itemizedOverlay);

		// shit to center and move the map
		mapController.setCenter(savedPoint);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}