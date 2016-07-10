package com.avallon.autool.items;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherItem {
	
	private String city;
	private String iconUrl;
	private Calendar weatherTime;
	private String temperatureCelsius;
	private String temperatureFahrenheit;
	private String feelsLikeCelsius;
	private String feelsLikeFahrenheit;
	private String condition;
	private String windSpeedKM;
	private String windSpeedMI;
	private String windDirection;
	private String humidity;
	private String pressureMetric;
	private String pressureImperial;
	private String precipitationsMetric;
	private String precipitationsImperial;

	public void createWeatherFromJson(JSONObject json) {
		try {
			iconUrl = json.getString("icon_url").replace("/k/", "/a/");
			temperatureCelsius = json.getJSONObject("temp").getString("metric");
			temperatureFahrenheit = json.getJSONObject("temp").getString("english");
			feelsLikeCelsius = json.getJSONObject("feelslike").getString("metric");
			feelsLikeFahrenheit = json.getJSONObject("feelslike").getString("english");
			condition = json.getString("condition");
			windSpeedKM = json.getJSONObject("wspd").getString("metric");
			windSpeedMI = json.getJSONObject("wspd").getString("english");
			windDirection = json.getJSONObject("wdir").getString("dir");
			humidity = json.getString("humidity");
			pressureMetric = json.getJSONObject("mslp").getString("metric");
			pressureImperial = json.getJSONObject("mslp").getString("english");
			precipitationsMetric = json.getJSONObject("qpf").getString("metric");
			precipitationsImperial = json.getJSONObject("qpf").getString("english");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}

	public Calendar getWeatherTime() {
		return weatherTime;
	}
	
	public void setWeatherTime(Calendar weatherTime, boolean firstDeparture) {
	    if (firstDeparture) {
	        weatherTime.add(Calendar.HOUR, -1);
	    }
		this.weatherTime = weatherTime;
	}

	public String getTemperatureCelsius() {
		return temperatureCelsius;
	}
	
	public String getTemperatureFahrenheit() {
		return temperatureFahrenheit;
	}

	public String getFeelsLikeCelsius() {
		return feelsLikeCelsius;
	}
	
	public String getFeelsLikeFahrenheit() {
		return feelsLikeFahrenheit;
	}

	public String getCondition() {
		return condition;
	}

	public String getWindSpeedKM() {
		return windSpeedKM;
	}
	
	public String getWindSpeedMI() {
		return windSpeedMI;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getPressureMetric() {
		return pressureMetric;
	}
	
	public String getPressureImperial() {
		return pressureImperial;
	}

	public String getPrecipitationsMetric() {
		return precipitationsMetric;
	}
	
	public String getPrecipitationsImperial() {
		return precipitationsImperial;
	}

	public String getIconUrl() {
		return iconUrl;
	}
}
