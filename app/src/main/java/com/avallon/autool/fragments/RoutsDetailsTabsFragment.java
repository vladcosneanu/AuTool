package com.avallon.autool.fragments;


import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.OnTabChangeListener;

import com.avallon.autool.R;
import com.avallon.autool.activities.RoutsActivity;
import com.avallon.autool.interfaces.RouteDetailsListener;
import com.avallon.autool.requests.RequestElevation;
import com.avallon.autool.requests.RequestWeather;
import com.avallon.autool.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

public class RoutsDetailsTabsFragment extends Fragment implements OnTabChangeListener, RouteDetailsListener{
	
	private FragmentTabHost mTabHost;
	public static List<JSONObject> weatherListJson;
	public static JSONObject elevationJson;
	public static List<LatLng> elevationLocations;
	public static boolean noResults = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.fragment1);

        mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("Info"),
                RoutsDetailsInfoFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("elevation").setIndicator("Elevation"),
        		RoutsDetailsElevationFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("weather").setIndicator("Weather"),
        		RoutsDetailsWeatherFragment.class, null);
        
        mTabHost.setOnTabChangedListener(this);

        return mTabHost;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
    
	public void onTabChanged(String tabId) {
		if (tabId.equals("info")) {
//			RoutsDetailsInfoFragment infoFragment = (RoutsDetailsInfoFragment) getChildFragmentManager().findFragmentByTag("info");
		} else if (tabId.equals("elevation")) {
//			RoutsDetailsElevationFragment elevationFragment = (RoutsDetailsElevationFragment) getChildFragmentManager().findFragmentByTag("elevation");
		} else if (tabId.equals("weather")) {
//			RoutsDetailsWeatherFragment weatherFragment = (RoutsDetailsWeatherFragment) getChildFragmentManager().findFragmentByTag("weather");
		}
	}
	
	public void onResultsReceived(final JSONObject json, List<LatLng> elevationLocations) {
		Utils.checkReadyToRate(getActivity());
		RoutsActivity.routeJson = json;
		RoutsDetailsTabsFragment.elevationLocations = elevationLocations; 
		
		try {
		    String status = json.getString("status");
		    if (!status.equals("OK")) {
		        // no results received
		        noResults = true;
		        RoutsDetailsInfoFragment infoFragment = (RoutsDetailsInfoFragment) getChildFragmentManager().findFragmentByTag("info");
		        if (infoFragment != null) {
		            infoFragment.noResultsReceived();
		        }
		        
		        return;
		    }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
    	RoutsDetailsInfoFragment infoFragment = (RoutsDetailsInfoFragment) getChildFragmentManager().findFragmentByTag("info");
    	if (infoFragment != null) {
    		infoFragment.onResultsReceived(json, elevationLocations);
    	}
    	
    	// setup the elevation request
		String elevationLocationsString = "";
		for (int i = 0; i < elevationLocations.size(); i++) {
			elevationLocationsString += elevationLocations.get(i).latitude + "," + elevationLocations.get(i).longitude;
			if (i != (elevationLocations.size() - 1)) {
				elevationLocationsString += "|";
			}
		}
    	
		if (elevationJson != null) {
			onElevationReceived(elevationJson);
		} else {
			// request elevations
			RequestElevation requestElevation = new RequestElevation(this);
			requestElevation.execute(new String []{ elevationLocationsString });
		}
		
		if (weatherListJson != null && weatherListJson.size() > 0) {
			onWeatherReceived(weatherListJson);
		} else {
			RequestWeather requestWeather = new RequestWeather(this);
			requestWeather.execute(elevationLocations);
		}
    }

	public void onElevationReceived(JSONObject json) {
		elevationJson = json;
		
		RoutsDetailsElevationFragment elevationFragment = (RoutsDetailsElevationFragment) getChildFragmentManager().findFragmentByTag("elevation");
    	if (elevationFragment != null) {
    		elevationFragment.onElevationReceived(json);
    	}
	}

	public void onWeatherReceived(List<JSONObject> jsonList) {
		weatherListJson = jsonList;
		
		RoutsDetailsWeatherFragment weatherFragment = (RoutsDetailsWeatherFragment) getChildFragmentManager().findFragmentByTag("weather");
    	if (weatherFragment != null) {
    		weatherFragment.onWeatherReceived(jsonList);
    	}
	}

	public void onWeatherElementReceived(int position, int total) {
	    RoutsDetailsWeatherFragment weatherFragment = (RoutsDetailsWeatherFragment) getChildFragmentManager().findFragmentByTag("weather");
        if (weatherFragment != null) {
            weatherFragment.onWeatherElementReceived(position, total);
        }
	}
}