package com.avallon.autool.fragments;

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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avallon.autool.R;
import com.avallon.autool.utils.PreferenceHelper;

public class RoutsDetailsElevationFragment extends Fragment{
	
	private View mView;
	private LinearLayout chartLayout;
	private boolean needsRepaint = false;
	private boolean isMetric;
	private View routeElevationProgressLayout;
    private View noResultsLayout;
    private TextView highestAlt;
    private TextView lowestAlt;
    private TextView departureAlt;
    private TextView destinationAlt;
    private View elevationsLayout;
    private ScrollView scrollView;
	private View routeElevationContainer;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.routs_details_elevation_fragment, container, false);
        
        return mView;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		chartLayout = (LinearLayout) mView.findViewById(R.id.chart);
		routeElevationProgressLayout = mView.findViewById(R.id.route_elevation_progress_layout);
        noResultsLayout = mView.findViewById(R.id.no_results_layout);
        highestAlt = (TextView) mView.findViewById(R.id.highest_altitude);
        lowestAlt = (TextView) mView.findViewById(R.id.lowest_altitude);
        departureAlt = (TextView) mView.findViewById(R.id.departure_altitude);
        destinationAlt = (TextView) mView.findViewById(R.id.destination_altitude);
        elevationsLayout = mView.findViewById(R.id.elevations_layout);
        scrollView = (ScrollView) mView.findViewById(R.id.elevation_scrollview);
		routeElevationContainer = mView.findViewById(R.id.route_elevation_container);
	}
	
	@Override
	public void onResume() {
		if (RoutsDetailsTabsFragment.elevationJson != null) {
			onElevationReceived(RoutsDetailsTabsFragment.elevationJson);
		}
		
		if (RoutsDetailsTabsFragment.noResults) {
            noResultsReceived();
        }
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		needsRepaint = true;
	}

	public void onElevationReceived(JSONObject json) {
		Log.d("Vlad", "onElevationReceived");
		isMetric = PreferenceHelper.isMetric(getActivity());
		routeElevationProgressLayout.setVisibility(View.GONE);
		elevationsLayout.setVisibility(View.VISIBLE);
		
		String status;
		try {
			status = json.getString("status");
			
			if (status.equals("OK")) {
				XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
				XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
				XYSeriesRenderer currentRenderer = new XYSeriesRenderer();
				XYSeries xySeries = new XYSeries(RoutsDetailsInfoFragment.departureCity + 
						" - " + RoutsDetailsInfoFragment.destinationCity);
				
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
						xySeries.add(i * RoutsDetailsInfoFragment.elevationDistanceUnit, (int)result.getDouble("elevation"));
					} else {
						xySeries.add(i * RoutsDetailsInfoFragment.elevationDistanceUnit * 0.621371192, (int)result.getDouble("elevation") * 3.2808399);
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
	
	public void noResultsReceived() {
		routeElevationContainer.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.VISIBLE);
    }
}
