package com.avallon.autool.interfaces;

import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public interface RouteDetailsListener {

	public void onResultsReceived(final JSONObject json, List<LatLng> elevationLocations);
	
	public void onElevationReceived(JSONObject json);
	
	public void onWeatherReceived(List<JSONObject> jsonList);
	
	public void onWeatherElementReceived(int position, int total);
}
