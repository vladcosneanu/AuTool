package com.avallon.autool.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.avallon.autool.R;
import com.avallon.autool.activities.MainActivity;
import com.avallon.autool.items.Coordinate;
import com.avallon.autool.items.ResizeAnimation;
import com.avallon.autool.items.ServiceItem;
import com.avallon.autool.requests.RequestService;
import com.avallon.autool.requests.RequestServiceDistance;
import com.avallon.autool.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

public class ServicesFragment extends Fragment implements OnClickListener {

	public static final String SERVICE_TYPE = "SERVICE_TYPE";
	public static final String SERVICES_LIST = "SERVICES_LIST";
	private View mView;
	private static RelativeLayout mapLayout;
	private Button gasStationsButton;
	private Button autoServiceButton;
	private Button carWashButton;
	private Button parkingButton;
	private View gasStationsButton2;
	private View autoServiceButton2;
	private View carWashButton2;
	private View parkingButton2;
	private View servicesLayout;
	private View servicesIntroLayout;
	private View servicesButtonsLayout;
	private View servicesButtonsLayout2;
    private View poweredByView;
	private static int servicesLayoutHeight = 0;
	private static int servicesIntroLayoutHeight = 0;
	private static int servicesButtonsLayoutHeight = 0;
    private static int poweredByLayoutHeight = 0;
	private static int targetHeight = 0;
	public static GoogleMap map;
	private SupportMapFragment mapFragment;
	public static boolean extended = false;
	private ImageButton fullScreenButton;
	public static List<ServiceItem> serviceItems;
	private static List<Marker> markerItems;
	public static String selectedType;
	private boolean receivedAllItems = true;
	private static boolean mapDisplayed = true;
	private ServicesListFragment servicesListFragment;
	private View collapseMapButton;
	public static ServiceItem selectedService;
	private static int selectedOrientation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}

		try {
			mView = inflater.inflate(R.layout.services_fragment, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mapLayout = (RelativeLayout) mView.findViewById(R.id.map_layout);
		gasStationsButton = (Button) mView.findViewById(R.id.gas_station_button);
		gasStationsButton.setOnClickListener(this);
		gasStationsButton2 = mView.findViewById(R.id.gas_station_button2);
		gasStationsButton2.setOnClickListener(this);
		autoServiceButton = (Button) mView.findViewById(R.id.auto_service_button);
		autoServiceButton.setOnClickListener(this);
		autoServiceButton2 = mView.findViewById(R.id.auto_service_button2);
		autoServiceButton2.setOnClickListener(this);
		carWashButton = (Button) mView.findViewById(R.id.car_wash_button);
		carWashButton.setOnClickListener(this);
		carWashButton2 = mView.findViewById(R.id.car_wash_button2);
		carWashButton2.setOnClickListener(this);
		parkingButton = (Button) mView.findViewById(R.id.parking_button);
		parkingButton.setOnClickListener(this);
		parkingButton2 = mView.findViewById(R.id.parking_button2);
		parkingButton2.setOnClickListener(this);
		servicesLayout = mView.findViewById(R.id.services_layout);
		servicesIntroLayout = mView.findViewById(R.id.services_intro_layout);
		servicesButtonsLayout = mView.findViewById(R.id.services_buttons_layout);
		servicesButtonsLayout2 = mView.findViewById(R.id.services_buttons_layout2);
        poweredByView = mView.findViewById(R.id.powered_by);
		mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
		map = mapFragment.getMap();
		if (map != null) {
		    map.setMyLocationEnabled(true);
	        map.getUiSettings().setAllGesturesEnabled(false);
		}
		fullScreenButton = (ImageButton) mView.findViewById(R.id.full_screen_map_button);
		fullScreenButton.setOnClickListener(this);

		if (extended) {
			mapFragment.getView().setVisibility(View.VISIBLE);
			fullScreenButton.setVisibility(View.VISIBLE);
		} else {
			mapFragment.getView().setVisibility(View.INVISIBLE);
			fullScreenButton.setVisibility(View.INVISIBLE);
		}
		collapseMapButton = mView.findViewById(R.id.collapseMapButton);
		collapseMapButton.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		selectedOrientation = getResources().getConfiguration().orientation;

		String serviceType = getActivity().getIntent().getStringExtra(ServicesFragment.SERVICE_TYPE);
	    if (targetHeight == 0 || (serviceType != null && !MainActivity.openedTabFromWidget)) {
			getMapHeight();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		int newOrientation = getResources().getConfiguration().orientation;
	    if (selectedOrientation != newOrientation){
	    	targetHeight = 0;
	    }
	    
	    servicesListFragment = (ServicesListFragment) MainActivity.wrActivity.get().getSupportFragmentManager().findFragmentByTag(SERVICES_LIST);
		if (servicesListFragment != null) {
            FragmentTransaction ft = MainActivity.wrActivity.get().getSupportFragmentManager().beginTransaction();
            mapDisplayed = true;
            ft.remove(servicesListFragment);
            servicesListFragment = null;
            ft.commit();
        }
		MainActivity.wrActivity.get().resetServiceListSwitch();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.gas_station_button:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (isInvalidHeights()) {
				break;
			}
			displayProgress();
			animateMap();
			getServices(RequestService.GAS_STATION);

			break;
		case R.id.auto_service_button:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (isInvalidHeights()) {
				break;
			}
			displayProgress();
			animateMap();
			getServices(RequestService.CAR_REPAIR);

			break;
		case R.id.car_wash_button:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (isInvalidHeights()) {
				break;
			}
			displayProgress();
			animateMap();
			getServices(RequestService.CAR_WASH);

			break;
		case R.id.parking_button:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (isInvalidHeights()) {
				break;
			}
			displayProgress();
			animateMap();
			getServices(RequestService.PARKING);

			break;
		case R.id.gas_station_button2:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (selectedType != RequestService.GAS_STATION && receivedAllItems) {
				map.clear();
				displayProgress();
				getServices(RequestService.GAS_STATION);

				break;
			} else {
				break;
			}
		case R.id.auto_service_button2:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (selectedType != RequestService.CAR_REPAIR && receivedAllItems) {
				map.clear();
				displayProgress();
				getServices(RequestService.CAR_REPAIR);

				break;
			} else {
				break;
			}
		case R.id.car_wash_button2:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (selectedType != RequestService.CAR_WASH && receivedAllItems) {
				map.clear();
				displayProgress();
				getServices(RequestService.CAR_WASH);

				break;
			} else {
				break;
			}
		case R.id.parking_button2:
			if (!Utils.isNetworkAvailable(getActivity())) {
				Utils.createNetErrorDialog(getActivity());
				break;
			}
			
			if (selectedType != RequestService.PARKING && receivedAllItems) {
				map.clear();
				displayProgress();
				getServices(RequestService.PARKING);

				break;
			} else {
				break;
			}
		case R.id.full_screen_map_button:
			if (receivedAllItems) {
				if (!map.getUiSettings().isScrollGesturesEnabled()) {
					maximizeMap();
				} else {
					minimizeMap();
				}
			}
			break;
		case R.id.collapseMapButton:
			animateMapUp();
			break;
		default:
			break;
		}
	}
	
	public void maximizeMap() {
	    toggleFullscreen(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        fullScreenButton.setImageResource(R.drawable.ic_action_return_from_full_screen);
        mapLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
	}
	
	public void minimizeMap() {
	    toggleFullscreen(false);
        getActivity().getActionBar().show();
        map.getUiSettings().setAllGesturesEnabled(false);
        fullScreenButton.setImageResource(R.drawable.ic_action_full_screen);
        mapLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, targetHeight));
	}
	
	private boolean isInvalidHeights() {
		if (servicesLayoutHeight == 0 || servicesIntroLayoutHeight == 0 || servicesButtonsLayoutHeight == 0 || poweredByLayoutHeight == 0) {
			return true;
		}
		
		return false;
	}

	private void displayProgress() {
		if (servicesListFragment != null) {
			servicesListFragment.displayProgress();
		}
	}

	private void getServices(String service) {
		MainActivity.wrActivity.get().displayProgress(true);
		
		selectedType = service;
		serviceItems = null;

		if (getActivity() != null) {
			Utils.updateCoordinate(new LocationInfo(getActivity()));
		}
		Coordinate location = Utils.getLocation();

		RequestService requestService = new RequestService(this, selectedType, location, null);
		requestService.execute(new String[]{});
	}

	private void addMap() {
		mapLayout.setVisibility(View.VISIBLE);
		mapFragment.getView().setVisibility(View.VISIBLE);
		fullScreenButton.setVisibility(View.VISIBLE);
	}

	private void removeMap() {
		mapLayout.setVisibility(View.INVISIBLE);
		mapFragment.getView().setVisibility(View.GONE);
		fullScreenButton.setVisibility(View.INVISIBLE);
	}

	private void onHeightReceived() {
		if (isInvalidHeights()) {
			return;
		}
		
		String serviceType = getActivity().getIntent().getStringExtra(ServicesFragment.SERVICE_TYPE);
		if (serviceType != null && !MainActivity.openedTabFromWidget) {
			MainActivity.openedTabFromWidget = true;
		    extended = false;
			getServiceFromWidget(serviceType);
		} else if (extended) {
			extended = false;
			animateMap();
		}
	}

	private void getMapHeight() {
		if (servicesLayout != null) {
			servicesLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					servicesLayoutHeight = servicesLayout.getHeight();
					onHeightReceived();

					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						servicesLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						servicesLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				}
			});
		}

		if (servicesIntroLayout != null) {
			servicesIntroLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					servicesIntroLayoutHeight = servicesIntroLayout.getHeight();
					onHeightReceived();

					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						servicesIntroLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						servicesIntroLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				}
			});
		}

		if (servicesButtonsLayout != null) {
			servicesButtonsLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					servicesButtonsLayoutHeight = servicesButtonsLayout.getHeight();
					onHeightReceived();

					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
						servicesButtonsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						servicesButtonsLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				}
			});
		}

        if (poweredByView != null) {
            poweredByView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    poweredByLayoutHeight = poweredByView.getHeight();
                    onHeightReceived();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        servicesButtonsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        servicesButtonsLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
	}

	public void onServicesReceived(JSONObject json) {
		receivedAllItems = false;
		if (serviceItems == null) {
			serviceItems = new ArrayList<ServiceItem>();
		}

		try {
			String status = json.getString("status");

			if (status.equals("OK")) {
				JSONArray results = json.getJSONArray("results");
				for (int i = 0; i < results.length(); i++) {
					JSONObject result = results.getJSONObject(i);
					ServiceItem serviceItem = new ServiceItem();
					serviceItem.createServiceItemFromJson(result);

					serviceItems.add(serviceItem);
				}
			}

			if (json.has("next_page_token")) {
				receivedAllItems = false;
				final String nextPageToken = json.getString("next_page_token");
				Timer mTimer = new Timer();
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						RequestService requestService = new RequestService(ServicesFragment.this, selectedType, null, nextPageToken);
						requestService.execute(new String[] {});
					}
				}, 2000);
			} else {
				receivedAllItems = true;
			}

			if (receivedAllItems) {
				String destinations = "";
				for (int i = 0; i < serviceItems.size(); i++) {
					ServiceItem serviceItem = serviceItems.get(i);

					if (i == (serviceItems.size() - 1)) {
						destinations += serviceItem.getLatitude() + "," + serviceItem.getLongitude();
					} else {
						destinations += serviceItem.getLatitude() + "," + serviceItem.getLongitude() + "|";
					}
				}

				if (getActivity() != null) {
					Utils.updateCoordinate(new LocationInfo(getActivity()));
				}
				Coordinate location = Utils.getLocation();

				RequestServiceDistance requestServiceDistance = new RequestServiceDistance(this, location, destinations);
				requestServiceDistance.execute(new String[] {});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onServicesDistanceReceived(JSONObject json) {
		Utils.checkReadyToRate(getActivity());
		
		MainActivity.wrActivity.get().displayProgress(false);
		if (serviceItems == null || getActivity() == null) {
			return;
		}
		
		if (extended) {
			((MainActivity) getActivity()).showServiceListSwitch();
		}
		
		try {
			String status = json.getString("status");
			if (status.equals("OK")) {
				JSONArray rows = json.getJSONArray("rows");
				JSONObject row = rows.getJSONObject(0);
				JSONArray elements = row.getJSONArray("elements");

				final LatLngBounds.Builder bc = new LatLngBounds.Builder();

				markerItems = new ArrayList<Marker>();
				for (int i = 0; i < serviceItems.size(); i++) {
					ServiceItem serviceItem = serviceItems.get(i);
					JSONObject element = elements.getJSONObject(i);

					serviceItem.setDistance(element.getJSONObject("distance").getInt("value"));
					serviceItem.setDuration(element.getJSONObject("duration").getInt("value"));

					LatLng markerLatLng = new LatLng(serviceItem.getLatitude(), serviceItem.getLongitude());

					BitmapDescriptor bitmapDescriptor = null;
					if (selectedType == RequestService.GAS_STATION) {
						bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.gas);
					} else if (selectedType == RequestService.CAR_REPAIR) {
						bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.settings);
					} else if (selectedType == RequestService.CAR_WASH) {
						bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.car_wash);
					} else if (selectedType == RequestService.PARKING) {
						bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.parking_sign_normal);
					}

					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(markerLatLng);
					markerOptions.title(serviceItem.getName());
					markerOptions.draggable(false);
					markerOptions.snippet(serviceItem.getVicinity());
					markerOptions.icon(bitmapDescriptor);
					Marker marker = map.addMarker(markerOptions);

					markerItems.add(marker);

					bc.include(markerLatLng);

					mapFragment.getView().post(new Runnable() {
						@Override
						public void run() {
							if (extended) {
								map.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
							}
						}
					});
				}

				Collections.sort(serviceItems);

				if (servicesListFragment != null) {
					servicesListFragment.loadData();
				}
			} else  {
				if (servicesListFragment != null) {
					servicesListFragment.loadData();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(final Marker marker) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.start_navigation_service)
                       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                        	   	String navigationUrl = "google.navigation:q=";
								navigationUrl += marker.getPosition().latitude + "," + marker.getPosition().longitude;
								Log.d("request", navigationUrl);
								Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl));
								getActivity().startActivity(navIntent);
                           }
                       })
                       .setNegativeButton(R.string.no, null);
                builder.show();
			}
		});
	}

	private void toggleFullscreen(boolean fullscreen) {
		if (getActivity() == null) {
			return;
		}
		
		WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
		if (fullscreen) {
			((MainActivity) getActivity()).hideActionBar();
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		} else {
			((MainActivity) getActivity()).showActionBar();
			attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		}
		getActivity().getWindow().setAttributes(attrs);
	}

	private void animateMap() {
		if (extended) {
			return;
		}
		
		targetHeight = servicesLayoutHeight - (servicesButtonsLayoutHeight / 2) - poweredByLayoutHeight;
		ResizeAnimation animationMap = new ResizeAnimation(mapLayout, 0, targetHeight, true);
		animationMap.setDuration(500);
		animationMap.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				map.clear();
				mapLayout.setVisibility(View.INVISIBLE);
				mapFragment.getView().setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				extended = true;
				addMap();
				
				if (serviceItems != null && serviceItems.size() > 0) {
					final LatLngBounds.Builder bc = new LatLngBounds.Builder();
					
					markerItems = new ArrayList<Marker>();
					for (int i = 0; i < serviceItems.size(); i++) {
						ServiceItem serviceItem = serviceItems.get(i);
						LatLng markerLatLng = new LatLng(serviceItem.getLatitude(), serviceItem.getLongitude());
						
						BitmapDescriptor bitmapDescriptor = null;
						if (selectedType == RequestService.GAS_STATION) {
							bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.gas);
						} else if (selectedType == RequestService.CAR_REPAIR) {
							bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.settings);
						} else if (selectedType == RequestService.CAR_WASH) {
							bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.car_wash);
						} else if (selectedType == RequestService.PARKING) {
							bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.parking_sign_normal);
						}

						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.position(markerLatLng);
						markerOptions.title(serviceItem.getName());
						markerOptions.draggable(false);
						markerOptions.snippet(serviceItem.getVicinity());
						markerOptions.icon(bitmapDescriptor);
						Marker marker = map.addMarker(markerOptions);

						markerItems.add(marker);

						bc.include(markerLatLng);

						mapFragment.getView().post(new Runnable() {
							@Override
							public void run() {
								if (extended) {
									map.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
								}
							}
						});
					}
				}
			}
		});
		mapLayout.startAnimation(animationMap);

		ResizeAnimation animationIntro = new ResizeAnimation(servicesIntroLayout, servicesIntroLayoutHeight, 0, true);
		animationIntro.setDuration(500);
		servicesIntroLayout.startAnimation(animationIntro);

		ResizeAnimation animationButtons = new ResizeAnimation(servicesButtonsLayout, servicesButtonsLayoutHeight, 0, true);
		animationButtons.setDuration(500);
		servicesButtonsLayout.startAnimation(animationButtons);

		ResizeAnimation animationButtons2 = new ResizeAnimation(servicesButtonsLayout2, 0, servicesButtonsLayoutHeight / 2, true);
		animationButtons2.setDuration(500);
		servicesButtonsLayout2.startAnimation(animationButtons2);

		if (getActivity() != null) {
			Utils.updateCoordinate(new LocationInfo(getActivity()));
		}
		Coordinate location = Utils.getLocation();
		CameraPosition.Builder currentPosition = new CameraPosition.Builder();
		currentPosition.bearing(0);
		currentPosition.tilt(0);
		currentPosition.target(new LatLng(location.getLatitude(), location.getLongitude()));
		currentPosition.zoom(12f);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition.build()));
	}

	private void animateMapUp() {
		((MainActivity) getActivity()).hideServiceListSwitch();
		targetHeight = servicesLayoutHeight - (servicesButtonsLayoutHeight / 2) - poweredByLayoutHeight;

		ResizeAnimation animationMap = new ResizeAnimation(mapLayout, targetHeight, 0, true);
		animationMap.setDuration(500);
		animationMap.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				map.clear();
				mapLayout.setVisibility(View.VISIBLE);
				mapFragment.getView().setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				extended = false;
				removeMap();
			}
		});
		mapLayout.startAnimation(animationMap);

		ResizeAnimation animationIntro = new ResizeAnimation(servicesIntroLayout, 0, servicesIntroLayoutHeight, true);
		animationIntro.setDuration(500);
		servicesIntroLayout.startAnimation(animationIntro);

		ResizeAnimation animationButtons = new ResizeAnimation(servicesButtonsLayout, 0, servicesButtonsLayoutHeight, true);
		animationButtons.setDuration(500);
		servicesButtonsLayout.startAnimation(animationButtons);

		ResizeAnimation animationButtons2 = new ResizeAnimation(servicesButtonsLayout2, servicesButtonsLayoutHeight / 2, 0, true);
		animationButtons2.setDuration(500);
		servicesButtonsLayout2.startAnimation(animationButtons2);
	}

	public void getServiceFromWidget(String serviceType) {
		if (serviceType.equals(RequestService.GAS_STATION)) {
			animateMap();
			getServices(RequestService.GAS_STATION);
		} else if (serviceType.equals(RequestService.CAR_REPAIR)) {
			animateMap();
			getServices(RequestService.CAR_REPAIR);
		} else if (serviceType.equals(RequestService.CAR_WASH)) {
			animateMap();
			getServices(RequestService.CAR_WASH);
		} else if (serviceType.equals(RequestService.PARKING)) {
			animateMap();
			getServices(RequestService.PARKING);
		}
	}

	public void toggleServicesMapList(ServiceItem serviceItem) {
        if (mapLayout.getVisibility() == View.VISIBLE) {
            if (servicesListFragment == null) {
                servicesListFragment = new ServicesListFragment();
            }
            FragmentTransaction ft = MainActivity.wrActivity.get().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (mapDisplayed) {
                mapDisplayed = false;
                ft.add(R.id.map_layout, servicesListFragment, SERVICES_LIST);
            } else {
                mapDisplayed = true;
                ft.remove(servicesListFragment);
            }
            ft.commit();
            
            if (mapDisplayed && serviceItem != null) {
            	for (int i = 0; i < markerItems.size(); i++) {
            		Marker marker = markerItems.get(i);
            		
					if (serviceItem.getName().equals(marker.getTitle()) && serviceItem.getVicinity().equals(marker.getSnippet())) {
						// found our marker
						marker.showInfoWindow();
						
						CameraPosition.Builder currentPosition = new CameraPosition.Builder();
						currentPosition.bearing(0);
						currentPosition.tilt(0);
						currentPosition.target(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
						currentPosition.zoom(16f);
						map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPosition.build()));
					}
				}
            }
        }
    }
}