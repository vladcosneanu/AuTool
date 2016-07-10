package com.avallon.autool.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avallon.autool.R;
import com.avallon.autool.activities.MainActivity;
import com.avallon.autool.activities.RoutsActivity;
import com.avallon.autool.items.Coordinate;
import com.avallon.autool.requests.RequestCity;
import com.avallon.autool.requests.RequestPlace;
import com.avallon.autool.utils.PreferenceHelper;
import com.avallon.autool.utils.Utils;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

public class RoutsFragment extends Fragment implements OnClickListener, TextWatcher {

	private View mView;
	private AutoCompleteTextView departureCity;
	private AutoCompleteTextView destinationCity;
	private AutoCompleteTextView waypointCity1;
	private AutoCompleteTextView waypointCity2;
	private AutoCompleteTextView waypointCity3;
	private AutoCompleteTextView waypointCity4;
	private Button addWaypoint;
	private Button clearWaypoint1;
	private Button clearWaypoint2;
	private Button clearWaypoint3;
	private Button clearWaypoint4;
	private Button getRouteButton;
	private Button currentLocationBtn;
	private ProgressBar currentLocationProgress;
	private Button disclaimerBtn;
	private RequestPlace requestPlace;
	private int currentFocusId;
	private ArrayAdapter<String> adapter;
	private List<String> places;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.routs_fragment, container, false);

		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		departureCity = (AutoCompleteTextView) mView.findViewById(R.id.departureText);
		departureCity.addTextChangedListener(this);
		destinationCity = (AutoCompleteTextView) mView.findViewById(R.id.destinationText);
		destinationCity.addTextChangedListener(this);
		waypointCity1 = (AutoCompleteTextView) mView.findViewById(R.id.waypointText1);
		waypointCity1.addTextChangedListener(this);
		waypointCity2 = (AutoCompleteTextView) mView.findViewById(R.id.waypointText2);
		waypointCity2.addTextChangedListener(this);
		waypointCity3 = (AutoCompleteTextView) mView.findViewById(R.id.waypointText3);
		waypointCity3.addTextChangedListener(this);
		waypointCity4 = (AutoCompleteTextView) mView.findViewById(R.id.waypointText4);
		waypointCity4.addTextChangedListener(this);
		clearWaypoint1 = (Button) mView.findViewById(R.id.clear_waypoint1);
		clearWaypoint1.setOnClickListener(this);
		clearWaypoint2 = (Button) mView.findViewById(R.id.clear_waypoint2);
		clearWaypoint2.setOnClickListener(this);
		clearWaypoint3 = (Button) mView.findViewById(R.id.clear_waypoint3);
		clearWaypoint3.setOnClickListener(this);
		clearWaypoint4 = (Button) mView.findViewById(R.id.clear_waypoint4);
		clearWaypoint4.setOnClickListener(this);
		addWaypoint = (Button) mView.findViewById(R.id.add_waypoint_button);
		addWaypoint.setOnClickListener(this);
		getRouteButton = (Button) mView.findViewById(R.id.get_route_button);
		getRouteButton.setOnClickListener(this);
		currentLocationBtn = (Button) mView.findViewById(R.id.currentLocationBtn);
		currentLocationBtn.setOnClickListener(this);
		currentLocationProgress = (ProgressBar) mView.findViewById(R.id.currentLocation_progress);
		disclaimerBtn = (Button) mView.findViewById(R.id.disclaimerBtn);
		disclaimerBtn.setOnClickListener(this);
		
		if (PreferenceHelper.loadBooleanDefauldValue(getActivity(), PreferenceHelper.NEW_FEATURES_1)) {
			PreferenceHelper.saveBooleanDefauldValue(getActivity(), PreferenceHelper.NEW_FEATURES_1, false);
			showNewFeatures();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_waypoint_button:
			if (waypointCity1.getVisibility() == View.GONE) {
				waypointCity1.setVisibility(View.VISIBLE);
				clearWaypoint1.setVisibility(View.VISIBLE);
			} else if (waypointCity2.getVisibility() == View.GONE) {
				waypointCity2.setVisibility(View.VISIBLE);
				clearWaypoint1.setVisibility(View.GONE);
				clearWaypoint2.setVisibility(View.VISIBLE);
			} else if (waypointCity3.getVisibility() == View.GONE) {
				waypointCity3.setVisibility(View.VISIBLE);
				clearWaypoint2.setVisibility(View.GONE);
				clearWaypoint3.setVisibility(View.VISIBLE);
			} else if (waypointCity4.getVisibility() == View.GONE) {
				waypointCity4.setVisibility(View.VISIBLE);
				clearWaypoint3.setVisibility(View.GONE);
				clearWaypoint4.setVisibility(View.VISIBLE);
				addWaypoint.setVisibility(View.GONE);
			}
			break;
		case R.id.clear_waypoint1:
			clearWaypoint1.setVisibility(View.GONE);
			waypointCity1.setText("");
			waypointCity1.setVisibility(View.GONE);
			break;
		case R.id.clear_waypoint2:
			clearWaypoint1.setVisibility(View.VISIBLE);
			clearWaypoint2.setVisibility(View.GONE);
			waypointCity2.setText("");
			waypointCity2.setVisibility(View.GONE);
			break;
		case R.id.clear_waypoint3:
			clearWaypoint2.setVisibility(View.VISIBLE);
			clearWaypoint3.setVisibility(View.GONE);
			waypointCity3.setText("");
			waypointCity3.setVisibility(View.GONE);
			break;
		case R.id.clear_waypoint4:
			clearWaypoint3.setVisibility(View.VISIBLE);
			clearWaypoint4.setVisibility(View.GONE);
			waypointCity4.setText("");
			waypointCity4.setVisibility(View.GONE);
			addWaypoint.setVisibility(View.VISIBLE);
			break;
		case R.id.get_route_button:
			if (Utils.isNetworkAvailable(getActivity())) {
			    String departureCityText = departureCity.getText().toString().trim();
			    String destinationCityText = destinationCity.getText().toString().trim();
			    if (!departureCityText.equals("") && !destinationCityText.equals("")) {
			        Intent intent = new Intent(getActivity(), RoutsActivity.class);
	                intent.putExtra(RoutsActivity.ROUTES_LINK, getRequestLink());
	                getActivity().startActivity(intent);
			    } else {
			        Toast.makeText(getActivity(), getString(R.string.empty_cities_error), Toast.LENGTH_SHORT).show();
			    }
			} else {
				Utils.createNetErrorDialog(getActivity());
			}
			break;
		case R.id.currentLocationBtn:
			if (Utils.isNetworkAvailable(getActivity())) {
				currentLocationProgress.setVisibility(View.VISIBLE);
				currentLocationBtn.setVisibility(View.GONE);
	
				// force a location update
				LocationLibrary.forceLocationUpdate(getActivity());
	
				if (getActivity() != null) {
					Utils.updateCoordinate(new LocationInfo(getActivity()));
				}
				Coordinate location = Utils.getLocation();
	
				RequestCity requestCity = new RequestCity(this);
				String cityCoordinates = location.getLatitude() + "," + location.getLongitude();
				requestCity.execute(new String[] { cityCoordinates });
			} else {
				Utils.createNetErrorDialog(getActivity());
			}
			break;
		case R.id.disclaimerBtn:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.routs_feature_info)).setTitle(R.string.app_name).setIcon(R.drawable.ic_launcher)
					.setCancelable(false).setPositiveButton(getString(R.string.ok), null);
			AlertDialog alert = builder.create();
			alert.show();
			break;
		default:
			break;
		}
	}
	
	private void showNewFeatures() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.new_feaures).setTitle(R.string.app_name);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton(R.string.ok, null);
		builder.show();
	}

	public String getRequestLink() {
		String requestLink = "https://maps.googleapis.com/maps/api/directions/json";
		requestLink += "?sensor=true";
		requestLink += "&mode=driving";
		requestLink += "&alternatives=false";
		requestLink += "&units=metric";
		requestLink += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
		try {
			String departureCityText = departureCity.getText().toString().trim();
			String departureCityTextEncoded = URLEncoder.encode(departureCityText, "UTF-8");

			String destinationCityText = destinationCity.getText().toString().trim();
			String destinationCityTextEncoded = URLEncoder.encode(destinationCityText, "UTF-8");

			requestLink += "&origin=" + departureCityTextEncoded;
			requestLink += "&destination=" + destinationCityTextEncoded;

			String waypoints = "&waypoints=";
			if (!waypointCity1.getText().toString().trim().equals("")) {
				String waypointCity1Text = waypointCity1.getText().toString().trim();
				String waypointCity1TextEncoded = URLEncoder.encode(waypointCity1Text, "UTF-8");
				waypoints += waypointCity1TextEncoded;
			}

			if (!waypointCity2.getText().toString().trim().equals("")) {
				String waypointCity2Text = "|" + waypointCity2.getText().toString().trim();
				String waypointCity2TextEncoded = URLEncoder.encode(waypointCity2Text, "UTF-8");
				waypoints += waypointCity2TextEncoded;
			}

			if (!waypointCity3.getText().toString().trim().equals("")) {
				String waypointCity3Text = "|" + waypointCity3.getText().toString().trim();
				String waypointCity3TextEncoded = URLEncoder.encode(waypointCity3Text, "UTF-8");
				waypoints += waypointCity3TextEncoded;
			}

			if (!waypointCity4.getText().toString().trim().equals("")) {
				String waypointCity4Text = "|" + waypointCity4.getText().toString().trim();
				String waypointCity4TextEncoded = URLEncoder.encode(waypointCity4Text, "UTF-8");
				waypoints += waypointCity4TextEncoded;
			}

			if (!waypoints.equals("&waypoints=")) {
				requestLink += waypoints;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestLink;
	}

	public void onCityReceived(JSONObject json) {
		try {
			if (json.getString("status").equals("OK")) {
				MainActivity.currentLocationSelected = true;
				JSONObject addressComponents = (JSONObject) json.getJSONArray("results").get(0);
				String formattedAddress = addressComponents.getString("formatted_address");
				setCurrentLocation(formattedAddress);
			} else {
				MainActivity.currentLocationSelected = false;
				displayNoCityError();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setCurrentLocation(final String location) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				currentLocationProgress.setVisibility(View.GONE);
				currentLocationBtn.setVisibility(View.VISIBLE);
				departureCity.setText(location);
			}
		});
	}

	private void displayNoCityError() {
		if (getActivity() == null) {
			return;
		}
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				currentLocationProgress.setVisibility(View.GONE);
				currentLocationBtn.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity(), getResources().getString(R.string.no_current_address), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void onPlacesReceived(JSONObject json) {
	    try {
	        if (json.getString("status").equals("OK")) {
	            JSONArray predictions = json.getJSONArray("predictions");
	            places = new ArrayList<String>();
	            
	            for (int i = 0; i < predictions.length(); i++) {
                    JSONObject prediction = predictions.getJSONObject(i);
                    places.add(prediction.getString("description"));
                }
	            
	            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, places);
	            
	            switch (currentFocusId) {
                case R.id.departureText:
                    Log.d("Vlad", adapter.getCount() + " " + places.toString());
                    departureCity.setAdapter(adapter);
                    departureCity.showDropDown();
                    destinationCity.dismissDropDown();
                    waypointCity1.dismissDropDown();
                    waypointCity2.dismissDropDown();
                    waypointCity3.dismissDropDown();
                    waypointCity4.dismissDropDown();
                    break;
                case R.id.destinationText:
                    departureCity.dismissDropDown();
                    destinationCity.setAdapter(adapter);
                    destinationCity.showDropDown();
                    waypointCity1.dismissDropDown();
                    waypointCity2.dismissDropDown();
                    waypointCity3.dismissDropDown();
                    waypointCity4.dismissDropDown();
                    break;
                case R.id.waypointText1:
                    departureCity.dismissDropDown();
                    destinationCity.dismissDropDown();
                    waypointCity1.setAdapter(adapter);
                    waypointCity1.showDropDown();
                    waypointCity2.dismissDropDown();
                    waypointCity3.dismissDropDown();
                    waypointCity4.dismissDropDown();
                    break;
                case R.id.waypointText2:
                    departureCity.dismissDropDown();
                    destinationCity.dismissDropDown();
                    waypointCity1.dismissDropDown();
                    waypointCity2.setAdapter(adapter);
                    waypointCity2.showDropDown();
                    waypointCity3.dismissDropDown();
                    waypointCity4.dismissDropDown();
                    break;
                case R.id.waypointText3:
                    departureCity.dismissDropDown();
                    destinationCity.dismissDropDown();
                    waypointCity1.dismissDropDown();
                    waypointCity2.dismissDropDown();
                    waypointCity3.setAdapter(adapter);
                    waypointCity3.showDropDown();
                    waypointCity4.dismissDropDown();
                    break;
                case R.id.waypointText4:
                    departureCity.dismissDropDown();
                    destinationCity.dismissDropDown();
                    waypointCity1.dismissDropDown();
                    waypointCity2.dismissDropDown();
                    waypointCity3.dismissDropDown();
                    waypointCity4.setAdapter(adapter);
                    waypointCity4.showDropDown();
                    break;
                default:
                    departureCity.dismissDropDown();
                    destinationCity.dismissDropDown();
                    waypointCity1.dismissDropDown();
                    waypointCity2.dismissDropDown();
                    waypointCity3.dismissDropDown();
                    waypointCity4.dismissDropDown();
                    break;
                }
	        }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

    @Override
    public void afterTextChanged(Editable arg0) {
        if (getActivity() == null || getActivity().getCurrentFocus() == null || 
        		!PreferenceHelper.loadBooleanDefauldValue(getActivity(), PreferenceHelper.AUTOCOMPLETE)) {
            return;
        }
        
        currentFocusId = getActivity().getCurrentFocus().getId();
        if (requestPlace != null) {
            requestPlace.cancel(true);
        }
        
        String input = "";
        
        switch (currentFocusId) {
        case R.id.departureText:
            input = departureCity.getText().toString();
            break;
        case R.id.destinationText:
            input = destinationCity.getText().toString();
            break;
        case R.id.waypointText1:
            input = waypointCity1.getText().toString();
            break;
        case R.id.waypointText2:
            input = waypointCity2.getText().toString();
            break;
        case R.id.waypointText3:
            input = waypointCity3.getText().toString();
            break;
        case R.id.waypointText4:
            input = waypointCity4.getText().toString();
            break;
        default:
            break;
        }
        
        if (!input.trim().equals("") && input.length() > 2) {
            requestPlace = new RequestPlace(RoutsFragment.this, input);
            requestPlace.execute(new String[] {});
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
