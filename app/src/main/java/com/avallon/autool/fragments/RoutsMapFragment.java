package com.avallon.autool.fragments;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.avallon.autool.R;
import com.avallon.autool.activities.MainActivity;
import com.avallon.autool.activities.RoutsActivity;
import com.avallon.autool.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class RoutsMapFragment extends Fragment {

	private View mView;
	public static GoogleMap map;
	private SupportMapFragment mapFragment;
	private ProgressBar progressBar;
	private ImageButton fullScreenButton;
	private ImageButton directionsButton;
	private View mapLayout;
	private View noResultsLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.routs_map_fragment, container, false);

		progressBar = (ProgressBar) mView.findViewById(R.id.progressBar_map);

		mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);

		map.getUiSettings().setAllGesturesEnabled(false);
		fullScreenButton = (ImageButton) mView.findViewById(R.id.full_screen_map_button);
		fullScreenButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!map.getUiSettings().isScrollGesturesEnabled()) {
					maximizeMap();
				} else {
					minimizeMap();
				}
			}
		});

		directionsButton = (ImageButton) mView.findViewById(R.id.directions_map_button);
		directionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.start_navigation).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent navIntent;
						if (MainActivity.currentLocationSelected) {
							String navigationUrl = "google.navigation:q=";
							if (Utils.getScreelLayout(getActivity()) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
								navigationUrl += RoutsDetailsFragment.destinationLocation.latitude + ","
										+ RoutsDetailsFragment.destinationLocation.longitude;
							} else {
								navigationUrl += RoutsDetailsInfoFragment.destinationLocation.latitude + ","
										+ RoutsDetailsInfoFragment.destinationLocation.longitude;
							}

							Log.d("request", navigationUrl);
							navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl));
						} else {
							String navigationUrl = "http://maps.google.com/maps";
							if (Utils.getScreelLayout(getActivity()) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
								navigationUrl += "?saddr=" + RoutsDetailsFragment.departureLocation.latitude + ","
										+ RoutsDetailsFragment.departureLocation.longitude;
								navigationUrl += "&daddr=" + RoutsDetailsFragment.destinationLocation.latitude + ","
										+ RoutsDetailsFragment.destinationLocation.longitude;
							} else {
								navigationUrl += "?saddr=" + RoutsDetailsInfoFragment.departureLocation.latitude + ","
										+ RoutsDetailsInfoFragment.departureLocation.longitude;
								navigationUrl += "&daddr=" + RoutsDetailsInfoFragment.destinationLocation.latitude + ","
										+ RoutsDetailsInfoFragment.destinationLocation.longitude;
							}

							Log.d("request", navigationUrl);
							navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl));
						}

						getActivity().startActivity(navIntent);
					}
				}).setNegativeButton(R.string.no, null);
				builder.show();
			}
		});

		mapLayout = mView.findViewById(R.id.map_layout);
		noResultsLayout = mView.findViewById(R.id.no_results_layout);

		if (RoutsActivity.routeJson != null && RoutsActivity.allLocations != null) {
			onResultsReceived(RoutsActivity.routeJson, RoutsActivity.allLocations);
		}

		return mView;
	}
	
	public void maximizeMap() {
	    toggleFullscreen(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        fullScreenButton.setImageResource(R.drawable.ic_action_return_from_full_screen);
	}
	
	public void minimizeMap() {
	    toggleFullscreen(false);
        getActivity().getActionBar().show();
        map.getUiSettings().setAllGesturesEnabled(false);
        fullScreenButton.setImageResource(R.drawable.ic_action_full_screen);
	}

	public void onResultsReceived(JSONObject json, final List<LatLng> allLocations) {
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}

		final LatLngBounds.Builder bc = new LatLngBounds.Builder();

		try {
			String status = json.getString("status");
			if (status.equals("OK")) {
			    if (!isAdded()) {
			        return;
			    }
				noResultsLayout.setVisibility(View.GONE);
				map.addPolyline(new PolylineOptions().addAll(allLocations).width(5).color(getResources().getColor(R.color.holo_blue_light)));

				for (int k = 0; k < allLocations.size(); k++) {
					bc.include(allLocations.get(k));
				}

				mapFragment.getView().post(new Runnable() {
					@Override
					public void run() {
					    if (allLocations.size() > 0) {
					        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
					    }
					}
				});
			} else {
				mapLayout.setVisibility(View.GONE);
				noResultsLayout.setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void moveMapToLocation(LatLng latLng) {
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	}

	private void toggleFullscreen(boolean fullscreen) {
		if (getActivity() == null) {
			return;
		}
		WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
		if (fullscreen) {
			((RoutsActivity) getActivity()).hideActionBar();
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		} else {
			((RoutsActivity) getActivity()).showActionBar();
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		getActivity().getWindow().setAttributes(attrs);
	}

}
