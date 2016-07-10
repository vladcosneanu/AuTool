package com.avallon.autool.fragments;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.avallon.autool.R;
import com.avallon.autool.activities.CarDetailsActivity;
import com.avallon.autool.activities.RoutsActivity;
import com.avallon.autool.utils.FormatHelper;
import com.avallon.autool.utils.PreferenceHelper;
import com.google.android.gms.maps.model.LatLng;

public class RoutsDetailsInfoFragment extends Fragment implements OnClickListener {
	
	private View mView;
	private TextView departureTextView;
	private View departureLayout;
	private TextView waypoint1TextView;
	private View waypoint1Layout;
	private TextView waypoint2TextView;
	private View waypoint2Layout;
	private TextView waypoint3TextView;
	private View waypoint3Layout;
	private TextView waypoint4TextView;
	private View waypoint4Layout;
	private TextView destinationTextView;
	private View destinationLayout;
	private TextView distanceTextView;
	private TextView durationTextView;
	private TextView averageSpeedTextView;
	private TextView gasConsumptionTextView;
	private TextView gasPriceTextView;
	private View fuelPercentageLayout;
    private TextView fuelPercentageValue;
    private TextView moreThanOneTank;
    private static int fuelIndicatorWidth;
    private static int fuelIndicatorHeight;
    private ImageView fuelIndicator;
    private View routeCitiesLayout;
    private View fuelIndicatorLayout;
    private View routeDetailsLayout;
    private View routeDetailsProgressLayout;
    private boolean isMetric; 
    private View gasConsumptionLayout;
    private View gasPriceLayout;
    private View routeDetailsMainLayout;
    private View noResultsLayout;
	private View routeDetailsContainer;
	
	public static int elevationDistanceUnit;
	public static int waypoint1TimeDistance = 0;
	public static int waypoint2TimeDistance = 0;
	public static int waypoint3TimeDistance = 0;
	public static int waypoint4TimeDistance = 0;
	public static int destinationTimeDistance = 0;
	
	public static String departureCity;
	public static String waypoint1City;
	public static String waypoint2City;
	public static String waypoint3City;
	public static String waypoint4City;
	public static String destinationCity;
	
	public static LatLng departureLocation;
	public static LatLng waypoint1Location;
	public static LatLng waypoint2Location;
	public static LatLng waypoint3Location;
	public static LatLng waypoint4Location;
    public static LatLng destinationLocation;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.routs_details_info_fragment, container, false);

        return mView;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		departureTextView = (TextView) mView.findViewById(R.id.departure_value);
		waypoint1TextView = (TextView) mView.findViewById(R.id.waypoint1_value);
		waypoint1Layout = mView.findViewById(R.id.waypoint1_layout);
		waypoint1Layout.setVisibility(View.GONE);
		waypoint2TextView = (TextView) mView.findViewById(R.id.waypoint2_value);
		waypoint2Layout = mView.findViewById(R.id.waypoint2_layout);
		waypoint2Layout.setVisibility(View.GONE);
		waypoint3TextView = (TextView) mView.findViewById(R.id.waypoint3_value);
		waypoint3Layout = mView.findViewById(R.id.waypoint3_layout);
		waypoint3Layout.setVisibility(View.GONE);
		waypoint4TextView = (TextView) mView.findViewById(R.id.waypoint4_value);
		waypoint4Layout = mView.findViewById(R.id.waypoint4_layout);
		waypoint4Layout.setVisibility(View.GONE);
		destinationTextView = (TextView) mView.findViewById(R.id.destination_value);
		departureLayout = mView.findViewById(R.id.departure_layout);
        departureLayout.setOnClickListener(this);
        destinationLayout = mView.findViewById(R.id.destination_layout);
        destinationLayout.setOnClickListener(this);
		distanceTextView = (TextView) mView.findViewById(R.id.distance_value);
		durationTextView = (TextView) mView.findViewById(R.id.duration_value);
		averageSpeedTextView = (TextView) mView.findViewById(R.id.average_speed_value);
		gasConsumptionTextView = (TextView) mView.findViewById(R.id.gas_consumption_value);
		gasPriceTextView = (TextView) mView.findViewById(R.id.gas_price_value);
		fuelPercentageLayout = mView.findViewById(R.id.fuel_percentage_layout);
        fuelPercentageValue = (TextView) mView.findViewById(R.id.fuel_percentage_value);
        moreThanOneTank = (TextView) mView.findViewById(R.id.more_than_one_tank);
        fuelIndicator = (ImageView) mView.findViewById(R.id.fuel_indicator);
        setFuelMeasurements();
        
        routeCitiesLayout = mView.findViewById(R.id.route_cities_layout);
        routeCitiesLayout.setVisibility(View.INVISIBLE);
        fuelIndicatorLayout = mView.findViewById(R.id.fuel_indicator_layout);
        fuelIndicatorLayout.setVisibility(View.INVISIBLE);
        routeDetailsLayout = mView.findViewById(R.id.route_details_layout);
        routeDetailsLayout.setVisibility(View.INVISIBLE);
        routeDetailsProgressLayout = mView.findViewById(R.id.route_details_progress_layout);
        gasConsumptionLayout = mView.findViewById(R.id.gas_consumption_layout);
		gasPriceLayout = mView.findViewById(R.id.gas_price_layout);
		routeDetailsMainLayout = mView.findViewById(R.id.route_details_main_layout);
		noResultsLayout = mView.findViewById(R.id.no_results_layout);
		routeDetailsContainer = mView.findViewById(R.id.route_details_container);
	}
	
	@Override
	public void onResume() {
		if (RoutsActivity.routeJson != null && RoutsDetailsTabsFragment.elevationLocations != null) {
			onResultsReceived(RoutsActivity.routeJson, RoutsDetailsTabsFragment.elevationLocations);
		}
		
		if (RoutsDetailsTabsFragment.noResults) {
		    noResultsReceived();
		}
		
		super.onResume();
	}
	
	public void onResultsReceived(final JSONObject json, List<LatLng> elevationLocations) {
		Log.d("Vlad", "onResultsReceived");
		isMetric = PreferenceHelper.isMetric(getActivity());
		routeDetailsProgressLayout.setVisibility(View.GONE);
        routeDetailsLayout.setVisibility(View.VISIBLE);
        fuelIndicatorLayout.setVisibility(View.VISIBLE);
        routeCitiesLayout.setVisibility(View.VISIBLE);
		
		try {
			String status = json.getString("status");
			if (status.equals("OK")) {
				JSONArray routes = json.getJSONArray("routes");
				JSONObject route = routes.getJSONObject(0);
				JSONArray legs = route.getJSONArray("legs");
				JSONObject leg1 = legs.getJSONObject(0);
				departureCity = leg1.getString("start_address");
				departureTextView.setText(departureCity);
				
				JSONObject departureLoc = leg1.getJSONObject("start_location");
                departureLocation = new LatLng(departureLoc.getDouble("lat"), departureLoc.getDouble("lng"));
				
				int totalDuration = 0;
				int totalDistance = 0;

				for (int i = 0; i < legs.length(); i++) {
					JSONObject leg = legs.getJSONObject(i);
					
					switch (i) {
					case 1:
						waypoint1City = leg.getString("start_address");
						waypoint1TextView.setText(waypoint1City);
						waypoint1Layout.setVisibility(View.VISIBLE);
						waypoint1Layout.setOnClickListener(this);
						
						JSONObject waypoint1Loc = leg.getJSONObject("start_location");
                        waypoint1Location = new LatLng(waypoint1Loc.getDouble("lat"), waypoint1Loc.getDouble("lng"));
						break;
					case 2:
						waypoint2City = leg.getString("start_address");
						waypoint2TextView.setText(waypoint2City);
						waypoint2Layout.setVisibility(View.VISIBLE);
						waypoint2Layout.setOnClickListener(this);
						
						JSONObject waypoint2Loc = leg.getJSONObject("start_location");
                        waypoint2Location = new LatLng(waypoint2Loc.getDouble("lat"), waypoint2Loc.getDouble("lng"));
						break;
					case 3:
						waypoint3City = leg.getString("start_address");
						waypoint3TextView.setText(waypoint3City);
						waypoint3Layout.setVisibility(View.VISIBLE);
						waypoint3Layout.setOnClickListener(this);
						
						JSONObject waypoint3Loc = leg.getJSONObject("start_location");
                        waypoint3Location = new LatLng(waypoint3Loc.getDouble("lat"), waypoint3Loc.getDouble("lng"));
						break;
					case 4:
						waypoint4City = leg.getString("start_address");
						waypoint4TextView.setText(waypoint4City);
						waypoint4Layout.setVisibility(View.VISIBLE);
						waypoint4Layout.setOnClickListener(this);
						
						JSONObject waypoint4Loc = leg.getJSONObject("start_location");
                        waypoint4Location = new LatLng(waypoint4Loc.getDouble("lat"), waypoint4Loc.getDouble("lng"));
						break;
					default:
						break;
					}
					
					JSONObject lastLeg = legs.getJSONObject(legs.length() - 1);
					destinationCity = lastLeg.getString("end_address");
					destinationTextView.setText(destinationCity);
					
					JSONObject destinationLoc = leg.getJSONObject("end_location");
                    destinationLocation = new LatLng(destinationLoc.getDouble("lat"), destinationLoc.getDouble("lng"));
					
					JSONObject duration = leg.getJSONObject("duration");
					int durationValue = duration.getInt("value");
					totalDuration += durationValue;
					
					JSONObject distance = leg.getJSONObject("distance");
					int distanceValue = distance.getInt("value");
					totalDistance+= distanceValue;
					
					// setup time distance between destinations
					if (legs.length() > 1) {
						if (legs.length() == 2) {
							if (i == 0) {
								waypoint1TimeDistance = durationValue;
							} else {
								destinationTimeDistance = waypoint1TimeDistance + durationValue;
							}
						} else if (legs.length() == 3) {
							if (i == 0) {
								waypoint1TimeDistance = durationValue;
							} else if (i == 1){
								waypoint2TimeDistance = waypoint1TimeDistance + durationValue;
							} else {
								destinationTimeDistance = waypoint2TimeDistance + durationValue;
							}
						} else if (legs.length() == 4) {
							if (i == 0) {
								waypoint1TimeDistance = durationValue;
							} else if (i == 1){
								waypoint2TimeDistance = waypoint1TimeDistance + durationValue;
							} else if (i == 2){
								waypoint3TimeDistance = waypoint2TimeDistance + durationValue; 
							} else {
								destinationTimeDistance = waypoint3TimeDistance + durationValue;
							}
						} else if (legs.length() == 5) {
							if (i == 0) {
								waypoint1TimeDistance = durationValue;
							} else if (i == 1){
								waypoint2TimeDistance = waypoint1TimeDistance + durationValue;
							} else if (i == 2){
								waypoint3TimeDistance = waypoint2TimeDistance + durationValue; 
							} else if (i == 3){
								waypoint4TimeDistance = waypoint3TimeDistance + durationValue;
							} else {
								destinationTimeDistance = waypoint4TimeDistance + durationValue;
							}
						}
					} else {
						// only one leg
						destinationTimeDistance = durationValue;
					}
				}
				
				// set route total distance
				NumberFormat formatter = new DecimalFormat("#.#");
				String totalDistanceValue = "";
				if (isMetric) {
					totalDistanceValue = String.format(getResources().getString(R.string.distance_value_km), formatter.format((float)totalDistance/1000));
				} else {
					float totalDistanceMiles = (float) ((totalDistance / 1000) * 0.621371192);
					totalDistanceValue = String.format(getResources().getString(R.string.distance_value_mi), formatter.format((float)totalDistanceMiles));
				}
				distanceTextView.setText(totalDistanceValue);
				
				elevationDistanceUnit = (int)(totalDistance / 10000);
				
				//set route total duration
				durationTextView.setText(FormatHelper.getTime(totalDuration, false));
				
				// set route average speed
				String averageSpeedValue = "";
				if (isMetric) {
					averageSpeedValue = String.format(getResources().getString(R.string.speed_km), formatter.format(((float)totalDistance / totalDuration) * 3.6));
				} else {
					float averageSpeedMiles = (float) (((float) totalDistance / totalDuration) * 3.6 * 0.621371192);
					averageSpeedValue = String.format(getResources().getString(R.string.speed_mi), formatter.format(averageSpeedMiles));
				}
				averageSpeedTextView.setText(averageSpeedValue);
				
				// set route fuel consumption
				float finalconsumptionValue = 0;
				String consumption = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_CONSUMPTION);
				if (!consumption.equals("")) {
					float consumptionValue = Float.parseFloat(consumption);
					finalconsumptionValue = (consumptionValue * totalDistance) / (1000 * 100);
					String consumptionText = "";
					if (isMetric) {
						consumptionText = String.format(getResources().getString(R.string.fuel_consumption_litres2), formatter.format(finalconsumptionValue));	
					} else {
						consumptionText = String.format(getResources().getString(R.string.fuel_consumption_gallons2), formatter.format(finalconsumptionValue));
					}
					gasConsumptionTextView.setText(consumptionText);
					gasConsumptionLayout.setOnClickListener(null);
				} else {
					gasConsumptionTextView.setText(R.string.value_not_set);
					gasConsumptionLayout.setOnClickListener(this);
				}
				
				// set route fuel price
				String gasPrice = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_PRICE);
				String currency = PreferenceHelper.loadDefauldValue(getActivity(), PreferenceHelper.CURRENCY);
				if (currency.equals("")) {
				    currency = "USD";
				}
				
				if (!consumption.equals("")) {
					if (!gasPrice.equals("")) {
						float gasPriceValue = Float.parseFloat(gasPrice);
						float finalGasPriceValue = finalconsumptionValue * gasPriceValue;
						String gasPriceText = String.format(getResources().getString(R.string.price_format), formatter.format(finalGasPriceValue), currency);
						gasPriceTextView.setText(gasPriceText);
						gasPriceLayout.setOnClickListener(null);
					} else {
						gasPriceTextView.setText(R.string.value_not_set);
						gasPriceLayout.setOnClickListener(this);
					}
				} else {
					gasPriceTextView.setText(R.string.value_not_set);
					gasPriceLayout.setOnClickListener(this);
				}
				
				// set fuel indicator
				String fuelTank = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_TANK_CAPACITY);
				if (!consumption.equals("")) {
					if (!fuelTank.equals("")) {
						int fuelTankValue = Integer.parseInt(fuelTank);
						float degrees = 0 - (finalconsumptionValue * 116) / fuelTankValue;
						if (degrees < -116) {
							degrees = -116;
						}
						
						int fuelPercentage = Math.round(finalconsumptionValue * 100 / fuelTankValue);
						
						if (fuelPercentage > 100) {
						    fuelPercentageLayout.setVisibility(View.GONE);
						    moreThanOneTank.setVisibility(View.VISIBLE);
						    moreThanOneTank.setText(R.string.more_than_one_tank);
						} else {
						    fuelPercentageLayout.setVisibility(View.VISIBLE);
						    moreThanOneTank.setVisibility(View.GONE);
						    fuelPercentageValue.setText(String.format(getResources().getString(R.string.fuel_percent_format), fuelPercentage));
						}
						
						Animation animation = new RotateAnimation(0, degrees, fuelIndicatorWidth/2, fuelIndicatorHeight/2);
						animation.setDuration(2000);
						animation.setFillEnabled(true);
						animation.setFillAfter(true);
						animation.setInterpolator(new DecelerateInterpolator());
						animation.setRepeatCount(0);
						fuelIndicator.startAnimation(animation);
					} else {
						fuelPercentageLayout.setVisibility(View.GONE);
					    moreThanOneTank.setVisibility(View.VISIBLE);
					    moreThanOneTank.setText(R.string.additional_info_required);
					    moreThanOneTank.setOnClickListener(this);
					}
				} else {
					fuelPercentageLayout.setVisibility(View.GONE);
				    moreThanOneTank.setVisibility(View.VISIBLE);
				    moreThanOneTank.setText(R.string.additional_info_required);
				    moreThanOneTank.setOnClickListener(this);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @Override
    public void onClick(View v) {
    	Intent intent;
    	
        switch (v.getId()) {
        case R.id.departure_layout:
            ((RoutsActivity) getActivity()).moveToMap(departureLocation);
            break;
        case R.id.waypoint1_layout:
            ((RoutsActivity) getActivity()).moveToMap(waypoint1Location);       
            break;
        case R.id.waypoint2_layout:
            ((RoutsActivity) getActivity()).moveToMap(waypoint2Location);
            break;
        case R.id.waypoint3_layout:
            ((RoutsActivity) getActivity()).moveToMap(waypoint3Location);
            break;
        case R.id.waypoint4_layout:
            ((RoutsActivity) getActivity()).moveToMap(waypoint4Location);
            break;
        case R.id.destination_layout:
            ((RoutsActivity) getActivity()).moveToMap(destinationLocation);
            break;
        case R.id.gas_consumption_layout:
			intent = new Intent(getActivity(), CarDetailsActivity.class);
			startActivity(intent);
			break;
		case R.id.gas_price_layout:
			intent = new Intent(getActivity(), CarDetailsActivity.class);
			startActivity(intent);
			break;
		case R.id.more_than_one_tank:
			intent = new Intent(getActivity(), CarDetailsActivity.class);
			startActivity(intent);
			break;
        default:
            break;
        }
    }
    
    private void setFuelMeasurements() {
        if (fuelIndicator != null) {
            fuelIndicator.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    fuelIndicatorWidth = fuelIndicator.getWidth();
                    fuelIndicatorHeight = fuelIndicator.getHeight();
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        fuelIndicator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        fuelIndicator.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }
    
    public void noResultsReceived() {
        routeDetailsContainer.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.VISIBLE);
    }
}
