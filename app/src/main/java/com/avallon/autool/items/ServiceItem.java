package com.avallon.autool.items;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceItem implements Comparable<ServiceItem> {

	private double latitude;
	private double longitude;
	private String name;
	private String vicinity;
	private List<String> types;
	private int distance = 0;
	private int duration = 0;

	public void createServiceItemFromJson(JSONObject json) {
		try {
			JSONObject geometry = json.getJSONObject("geometry");
			JSONObject location = geometry.getJSONObject("location");
			setLatitude(location.getDouble("lat"));
			setLongitude(location.getDouble("lng"));

			setName(json.getString("name"));

			JSONArray typesJson = json.getJSONArray("types");
			List<String> types = new ArrayList<String>();
			for (int i = 0; i < typesJson.length(); i++) {
				types.add(typesJson.getString(i));
			}
			setTypes(types);

			setVicinity(json.getString("vicinity"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public int compareTo(ServiceItem compareServiceItem) {
		int compareDistance = compareServiceItem.getDistance();

		return this.getDistance() - compareDistance;
	}
}
