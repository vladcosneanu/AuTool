package com.avallon.autool.fragments;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine.Type;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.avallon.autool.R;
import com.avallon.autool.activities.CarDetailsActivity;
import com.avallon.autool.activities.RoutsActivity;
import com.avallon.autool.adapters.WeatherListAdapter;
import com.avallon.autool.interfaces.RouteDetailsListener;
import com.avallon.autool.items.WeatherItem;
import com.avallon.autool.requests.RequestElevation;
import com.avallon.autool.requests.RequestWeather;
import com.avallon.autool.utils.FormatHelper;
import com.avallon.autool.utils.PreferenceHelper;
import com.avallon.autool.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

public class RoutsDetailsFragment extends Fragment implements OnClickListener, OnDateSetListener, 
		OnTimeSetListener, RouteDetailsListener {
	
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
	private ImageView fuelIndicator;
	private static int fuelIndicatorWidth;
	private static int fuelIndicatorHeight;
	private LinearLayout chartLayout;
	private int elevationDistanceUnit;
	private TextView departureDateTextView;
	private TextView departureTimeTextView;
	private View departureDateLayout;
	private View departureTimeLayout;
	public static Calendar selectedDate;
	public static JSONObject elevationJson;
	private View fuelPercentageLayout;
	private TextView fuelPercentageValue;
	private TextView moreThanOneTank;
	private ProgressBar weatherProgressBar;
	private View weatherProgressLayout;
	private TextView weatherProgressText;
	private View routeCitiesLayout;
	private View fuelIndicatorLayout;
	private View routeDetailsLayout;
	private View routeDetailsProgressLayout;
	private View routeWeatherLayout;
	private ProgressBar weatherListProgress;
	private boolean isMetric; 
	private ListView weatherList;
    private ArrayList<WeatherItem> weatherItems;
    private WeatherListAdapter weatherListAdapter;
    private boolean needsRepaint = false;
    private View gasConsumptionLayout;
    private View gasPriceLayout;
    private View routeDetailsMainLayout;
    private View noResultsLayout;
    private TextView highestAlt;
    private TextView lowestAlt;
    private TextView departureAlt;
    private TextView destinationAlt;
    private View elevationsLayout;
    private View dividerLine;
	private View routeDetailsContainer;
	
	public static List<JSONObject> weatherListJson;
	private List<JSONObject> departureWeatherList;
	private List<JSONObject> waypoint1WeatherList;
	private int waypoint1TimeDistance = 0;
	private List<JSONObject> waypoint2WeatherList;
	private int waypoint2TimeDistance = 0;
	private List<JSONObject> waypoint3WeatherList;
	private int waypoint3TimeDistance = 0;
	private List<JSONObject> waypoint4WeatherList;
	private int waypoint4TimeDistance = 0;
	private List<JSONObject> destinationWeatherList;
	private int destinationTimeDistance = 0;
	
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
    
    public static boolean noResults = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.routs_details_fragment, container, false);
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
		fuelIndicator = (ImageView) mView.findViewById(R.id.fuel_indicator);
		setFuelMeasurements();
		
		if (selectedDate == null) {
            selectedDate = Calendar.getInstance();
        }
		
		departureDateTextView = (TextView) mView.findViewById(R.id.departure_date_value);
		departureDateTextView.setText(FormatHelper.getDateWithoutYear(selectedDate));
		departureTimeTextView = (TextView) mView.findViewById(R.id.departure_time_value);
		departureTimeTextView.setText(FormatHelper.getTime(selectedDate));
		departureDateLayout = mView.findViewById(R.id.departure_date_layout);
		departureDateLayout.setOnClickListener(this);
		departureTimeLayout = mView.findViewById(R.id.departure_time_layout);
		departureTimeLayout.setOnClickListener(this);
		weatherList = (ListView) mView.findViewById(R.id.weatherList);
		chartLayout = (LinearLayout) mView.findViewById(R.id.chart);
		fuelPercentageLayout = mView.findViewById(R.id.fuel_percentage_layout);
		fuelPercentageLayout.setVisibility(View.INVISIBLE);
		fuelPercentageValue = (TextView) mView.findViewById(R.id.fuel_percentage_value);
		moreThanOneTank = (TextView) mView.findViewById(R.id.more_than_one_tank);
		weatherProgressBar = (ProgressBar) mView.findViewById(R.id.progressbar_weather);
		weatherProgressLayout = mView.findViewById(R.id.weather_progress_layout);
		weatherProgressText = (TextView) mView.findViewById(R.id.weather_progress_text);
		routeCitiesLayout = mView.findViewById(R.id.route_cities_layout);
		routeCitiesLayout.setVisibility(View.INVISIBLE);
		fuelIndicatorLayout = mView.findViewById(R.id.fuel_indicator_layout);
		fuelIndicatorLayout.setVisibility(View.INVISIBLE);
		routeDetailsLayout = mView.findViewById(R.id.route_details_layout);
		routeDetailsLayout.setVisibility(View.INVISIBLE);
		routeDetailsProgressLayout = mView.findViewById(R.id.route_details_progress_layout);
		routeWeatherLayout = mView.findViewById(R.id.route_weather_layout);
		routeWeatherLayout.setVisibility(View.INVISIBLE);
		weatherListProgress = (ProgressBar) mView.findViewById(R.id.weatherList_progress);
		gasConsumptionLayout = mView.findViewById(R.id.gas_consumption_layout);
		gasPriceLayout = mView.findViewById(R.id.gas_price_layout);
		routeDetailsMainLayout = mView.findViewById(R.id.route_details_main_layout);
        noResultsLayout = mView.findViewById(R.id.no_results_layout);
        highestAlt = (TextView) mView.findViewById(R.id.highest_altitude);
        lowestAlt = (TextView) mView.findViewById(R.id.lowest_altitude);
        departureAlt = (TextView) mView.findViewById(R.id.departure_altitude);
        destinationAlt = (TextView) mView.findViewById(R.id.destination_altitude);
        elevationsLayout = mView.findViewById(R.id.elevations_layout);
        dividerLine = mView.findViewById(R.id.divider_line);
        routeDetailsContainer = mView.findViewById(R.id.route_details_container);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (RoutsActivity.routeJson != null && RoutsActivity.elevationLocations != null) {
			Log.d("Vlad", "received");
			onResultsReceived(RoutsActivity.routeJson, RoutsActivity.elevationLocations);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		needsRepaint = true;
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

	public void onResultsReceived(final JSONObject json, List<LatLng> elevationLocations) {
		isMetric = PreferenceHelper.isMetric(getActivity());
		routeDetailsProgressLayout.setVisibility(View.GONE);
		
		try {
			String status = json.getString("status");
			if (status.equals("OK")) {
			    routeDetailsLayout.setVisibility(View.VISIBLE);
		        fuelIndicatorLayout.setVisibility(View.VISIBLE);
		        routeCitiesLayout.setVisibility(View.VISIBLE);
		        fuelPercentageLayout.setVisibility(View.VISIBLE);
		        routeWeatherLayout.setVisibility(View.VISIBLE);
		        
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
					// request weather conditions
					RequestWeather requestWeather = new RequestWeather(this);
					requestWeather.execute(elevationLocations);
				}
			} else {
			    noResultsReceived();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onElevationReceived(JSONObject json) {
		elevationJson = json;
		elevationsLayout.setVisibility(View.VISIBLE);
		dividerLine.setVisibility(View.VISIBLE);
		
		String status;
		try {
			status = json.getString("status");
			if (status.equals("OK")) {
				XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
				XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
				XYSeriesRenderer currentRenderer = new XYSeriesRenderer();
				XYSeries xySeries = new XYSeries(departureCity + " - " + destinationCity);
				
				double minElevation = 10000;
                double maxElevation = -10000;
				
				JSONArray results = json.getJSONArray("results");
				for (int i = 0; i < results.length(); i++) {
					JSONObject result = results.getJSONObject(i);
					
					if (minElevation > result.getDouble("elevation")) {
                        minElevation = result.getDouble("elevation");
                    }
                    
                    if (maxElevation < result.getDouble("elevation")) {
                        maxElevation = result.getDouble("elevation");
                    }
					
					if (isMetric) {
						xySeries.add(i * elevationDistanceUnit, (int)result.getDouble("elevation"));
					} else {
						xySeries.add(i * elevationDistanceUnit * 0.621371192, (int)result.getDouble("elevation") * 3.2808399);
					}
//					xySeries.addAnnotation("Vlad", i * elevationDistanceUnit, (int)result.getDouble("elevation"));
				}
				
				int resource;
                double multiplier;
                if (isMetric) { 
                    resource = R.string.elevation_meters;
                    multiplier = 1;
                } else {
                    resource = R.string.elevation_feet;
                    multiplier = 3.2808399;
                }
                Double departureAltValue = results.getJSONObject(0).getDouble("elevation") * multiplier;
                departureAlt.setText(String.format(getString(resource), String.valueOf(departureAltValue.intValue())));
                Double destinationAltValue = results.getJSONObject(results.length() - 1).getDouble("elevation") * multiplier;
                destinationAlt.setText(String.format(getString(resource), String.valueOf(destinationAltValue.intValue())));
                Double highestAltValue = maxElevation * multiplier;
                highestAlt.setText(String.format(getString(resource), String.valueOf(highestAltValue.intValue())));
                Double lowestAltValue = minElevation * multiplier;
                lowestAlt.setText(String.format(getString(resource), String.valueOf(lowestAltValue.intValue())));
				
				renderer.setApplyBackgroundColor(false);
				renderer.setAxisTitleTextSize(16);
				renderer.setChartTitleTextSize(18);
				renderer.setLabelsTextSize(15);
				renderer.setLegendTextSize(15);
				renderer.setMargins(new int[] { 40, 40, 20, 30 }); // top, left, bottom, right
				renderer.setMarginsColor(getResources().getColor(R.color.activity_background_color));
				renderer.setPointSize(5);
				renderer.setZoomEnabled(false);
				renderer.setYAxisMin(0);
				renderer.setChartTitle(getResources().getString(R.string.elevation_chart_title));
				if (isMetric) {
					renderer.setXTitle(getResources().getString(R.string.elevation_x_km));
					renderer.setYTitle(getResources().getString(R.string.elevation_y_m));
				} else {
					renderer.setXTitle(getResources().getString(R.string.elevation_x_mi));
					renderer.setYTitle(getResources().getString(R.string.elevation_y_feet));
				}
			    renderer.setPanEnabled(false);
				
				currentRenderer.setColor(getResources().getColor(R.color.rectangle_border));
				currentRenderer.setLineWidth(3);
				FillOutsideLine outsideFillLine = new FillOutsideLine(Type.BELOW);
				outsideFillLine.setColor(getResources().getColor(R.color.rectangle_background));
				currentRenderer.addFillOutsideLine(outsideFillLine);

				dataset.addSeries(xySeries);
				renderer.addSeriesRenderer(currentRenderer);
				GraphicalView chart = ChartFactory.getCubeLineChartView(getActivity(), dataset, renderer, 0.3f);
				chart.setBackgroundColor(getResources().getColor(android.R.color.transparent));
				chartLayout.addView(chart);
				
				if (needsRepaint) {
					chartLayout.removeAllViews();
					chartLayout.addView(chart);
					needsRepaint = false;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onWeatherReceived(List<JSONObject> jsonList) {
		weatherListJson = jsonList;
		weatherProgressLayout.setVisibility(View.GONE);
		
		for (int i = 0; i < jsonList.size(); i++) {
			JSONObject json = jsonList.get(i);
			
			try {
				JSONArray hourlyForecast = json.getJSONArray("hourly_forecast");
				
				if (i == 0) {
					// departure weather list
					departureWeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						departureWeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (i == (jsonList.size() - 1)) {
					// destination weather list
					destinationWeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						destinationWeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 1){
					// waypoint 1 weather list
					waypoint1WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint1WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 2){
					// waypoint 2 weather list
					waypoint2WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint2WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 3){
					// waypoint 3 weather list
					waypoint3WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint3WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 4){
					// waypoint 4 weather list
					waypoint4WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint4WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		displayWeatherListProgress();
		weatherItems = new ArrayList<WeatherItem>();
        selectWeatherbyTime(selectedDate);
	}
	
	private void selectWeatherbyTime(final Calendar calendar) {
	    Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar receivedCalendar = Calendar.getInstance();
                receivedCalendar.setTime(calendar.getTime());
                
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(Calendar.HOUR, 1);
                if (receivedCalendar.compareTo(currentTime) < 0) {
                    selectWeatherFromList(currentTime, departureWeatherList, RoutsDetailsFragment.departureCity, true);
                } else {
                    selectWeatherFromList(receivedCalendar, departureWeatherList, RoutsDetailsFragment.departureCity, false);
                }
                
                if (waypoint1TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, waypoint1TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint1WeatherList, RoutsDetailsFragment.waypoint1City, false);
                }
                
                if (waypoint2TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, waypoint2TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint2WeatherList, RoutsDetailsFragment.waypoint2City, false);
                }
                
                if (waypoint3TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, waypoint3TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint3WeatherList, RoutsDetailsFragment.waypoint3City, false);
                }
                
                if (waypoint4TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, waypoint4TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint4WeatherList, RoutsDetailsFragment.waypoint4City, false);
                }
                
                Calendar destinationTime = FormatHelper.getFutureDate(receivedCalendar, destinationTimeDistance);
                selectWeatherFromList(destinationTime, destinationWeatherList, RoutsDetailsFragment.destinationCity, false);
                displayWeatherList();
            }
        });
	    thread.start();
	}
	
	private void selectWeatherFromList(Calendar calendar, List<JSONObject> weatherList, String city, boolean firstDeparture) {
        Calendar receivedCalendar = Calendar.getInstance();
        receivedCalendar.setTime(calendar.getTime());
        
        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(calendar.getTime());
        
        compareCalendar.set(Calendar.MINUTE, 0);
        compareCalendar.set(Calendar.SECOND, 0);
        compareCalendar.set(Calendar.MILLISECOND, 0);
        
        if (weatherList != null) {
	        for (int i = 0; i < weatherList.size(); i++) {
	            JSONObject weather = weatherList.get(i);
	            JSONObject weatherTimeObject;
	            try {
	                weatherTimeObject = weather.getJSONObject("FCTTIME");
	                Calendar weatherTime = Calendar.getInstance();
	                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a z MMMM d, y", Locale.US);
	                weatherTime.setTime(sdf.parse(weatherTimeObject.getString("pretty").replace("on ", "")));
	                
	                if (compareCalendar.compareTo(weatherTime) == 0) {
	                    WeatherItem weatherItem = new WeatherItem();
	                    weatherItem.createWeatherFromJson(weather);
	                    weatherItem.setCity(city);
	                    weatherItem.setWeatherTime(receivedCalendar, firstDeparture);
	                    weatherItems.add(weatherItem);
	                }
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (ParseException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
        }
    }
	
	private void displayWeatherList() {
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                weatherListAdapter = new WeatherListAdapter(getActivity(), R.layout.weather_item, weatherItems);
	                weatherList.setAdapter(weatherListAdapter);
	                weatherListProgress.setVisibility(View.GONE);
	                weatherList.setVisibility(View.VISIBLE);
	                setListViewHeightBasedOnChildren(weatherList);
	            }
	        });
		}
	}
	
	private void createDepartureDateDialog() {
		DatePickerFragment date = new DatePickerFragment();
		if (selectedDate == null) {
		    selectedDate = Calendar.getInstance();
		}
		
		Bundle args = new Bundle();
		args.putInt("year", selectedDate.get(Calendar.YEAR));
		args.putInt("month", selectedDate.get(Calendar.MONTH));
		args.putInt("day", selectedDate.get(Calendar.DATE));
		date.setArguments(args);
		
		// Set Call back to capture selected date
		date.setCallBack(this);
		date.show(getActivity().getSupportFragmentManager(), "Date Picker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
	    if (selectedDate.get(Calendar.YEAR) == year && selectedDate.get(Calendar.MONTH) == month && selectedDate.get(Calendar.DATE) == day) {
	        return;
	    }
	    
	    Calendar currentCalendar = Calendar.getInstance();
	    Calendar dummyCalendar = Calendar.getInstance();
	    dummyCalendar.setTime(selectedDate.getTime());
	    dummyCalendar.set(Calendar.YEAR, year);
	    dummyCalendar.set(Calendar.MONTH, month);
	    dummyCalendar.set(Calendar.DATE, day);
	    Calendar maxCalendar = Utils.getMaxWeatherDate(destinationTimeDistance);
	    if (dummyCalendar.compareTo(maxCalendar) > 0) {
	    	Toast.makeText(getActivity(), R.string.weather_date_select_error, Toast.LENGTH_SHORT).show();
	    	return;
	    } else if (dummyCalendar.compareTo(currentCalendar) < 0 ) {
	    	Toast.makeText(getActivity(), R.string.weather_date_select_error2, Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    
	    selectedDate.setTime(dummyCalendar.getTime());
	    
	    departureDateTextView.setText(FormatHelper.getDateWithoutYear(selectedDate));
	    
	    displayWeatherListProgress();
	    weatherItems = new ArrayList<WeatherItem>();
	    selectWeatherbyTime(selectedDate);
	}
	
	private void createDepartureTimeDialog() {
		TimePickerFragment time = new TimePickerFragment();
		if (selectedDate == null) {
            selectedDate = Calendar.getInstance();
        }
		
		Bundle args = new Bundle();
		args.putInt("hour", selectedDate.get(Calendar.HOUR_OF_DAY));
		args.putInt("minute", selectedDate.get(Calendar.MINUTE));
		time.setArguments(args);
		
		// Set Call back to capture selected time
		time.setCallBack(this);
		time.show(getActivity().getSupportFragmentManager(), "Time Picker");
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    if (selectedDate.get(Calendar.HOUR_OF_DAY) == hourOfDay && selectedDate.get(Calendar.MINUTE) == minute) {
	        return;
	    }
	    
	    Calendar currentCalendar = Calendar.getInstance();
	    Calendar dummyCalendar = Calendar.getInstance();
	    dummyCalendar.setTime(selectedDate.getTime());
	    dummyCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
	    dummyCalendar.set(Calendar.MINUTE, minute);
	    Calendar maxCalendar = Utils.getMaxWeatherDate(destinationTimeDistance);
	    if (dummyCalendar.compareTo(maxCalendar) > 0) {
	    	Toast.makeText(getActivity(), R.string.weather_date_select_error, Toast.LENGTH_SHORT).show();
	    	return;
	    } else if (dummyCalendar.compareTo(currentCalendar) < 0 ) {
	    	Toast.makeText(getActivity(), R.string.weather_date_select_error2, Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    
	    selectedDate.setTime(dummyCalendar.getTime());
		
		departureTimeTextView.setText(FormatHelper.getTime(selectedDate));
		
		displayWeatherListProgress();
		weatherItems = new ArrayList<WeatherItem>();
		selectWeatherbyTime(selectedDate);
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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
		case R.id.departure_date_layout:
			if (weatherListJson != null && weatherListJson.size() > 0) {
				createDepartureDateDialog();
			}
			break;
		case R.id.departure_time_layout:
			if (weatherListJson != null && weatherListJson.size() > 0) {
				createDepartureTimeDialog();
			}
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

	@Override
	public void onWeatherElementReceived(int position, int total) {
	    String weatherProgressValue = "";
	    if (isAdded()) {
	    	if (position == 0) {
		        // departure weather request
		        weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsFragment.departureCity);    
		    } else if (position == 1 && position < (total - 1)) {
		        // waypoint 1 weather request
		        weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsFragment.waypoint1City);
		    } else if (position == 2 && position < (total - 1)) {
		        // waypoint 2 weather request
		        weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsFragment.waypoint2City);
		    } else if (position == 3 && position < (total - 1)) {
		        // waypoint 3 weather request
		        weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsFragment.waypoint3City);
	        } else if (position == 4 && position < (total - 1)) {
	            // waypoint 4 weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsFragment.waypoint4City);
	        } else {
	            // destination weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsFragment.destinationCity);
	        }
		    
		    weatherProgressLayout.setVisibility(View.VISIBLE);
		    weatherProgressText.setText(weatherProgressValue);
			weatherProgressBar.setProgress(position * (100 / total));
	    }
	}
	
	private void displayWeatherListProgress() {
		if (getActivity() == null) {
			return;
		}
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				weatherList.setVisibility(View.GONE);
				weatherListProgress.setVisibility(View.VISIBLE);
			}
		});
	}
	
	public void noResultsReceived() {
        routeDetailsContainer.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.VISIBLE);
    }
}
