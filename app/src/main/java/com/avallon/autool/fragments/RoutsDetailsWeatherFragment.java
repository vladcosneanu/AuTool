package com.avallon.autool.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.avallon.autool.R;
import com.avallon.autool.adapters.WeatherListAdapter;
import com.avallon.autool.items.WeatherItem;
import com.avallon.autool.utils.FormatHelper;
import com.avallon.autool.utils.Utils;

public class RoutsDetailsWeatherFragment extends Fragment implements OnDateSetListener, OnTimeSetListener {
	private View mView;
	public static Calendar selectedDate;

	private List<JSONObject> departureWeatherList;
	private List<JSONObject> waypoint1WeatherList;
	private List<JSONObject> waypoint2WeatherList;
	private List<JSONObject> waypoint3WeatherList;
	private List<JSONObject> waypoint4WeatherList;
	private List<JSONObject> destinationWeatherList;

	private TextView departureDateTextView;
	private TextView departureTimeTextView;
	private View departureDateLayout;
	private View departureTimeLayout;
	private ProgressBar weatherProgressBar;
    private View weatherProgressLayout;
    private TextView weatherProgressText;
	private ListView weatherList;
	private ArrayList<WeatherItem> weatherItems;
	private WeatherListAdapter weatherListAdapter;
	private ProgressBar weatherListProgress;
	private View routeWeatherMainLayout;
    private View noResultsLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.routs_details_weather_fragment,
				container, false);

		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	    if (selectedDate == null) {
            selectedDate = Calendar.getInstance();
        }
	    
		departureDateTextView = (TextView) mView.findViewById(R.id.departure_date_value);
		departureDateTextView.setText(FormatHelper.getDateWithoutYear(selectedDate));
		departureTimeTextView = (TextView) mView.findViewById(R.id.departure_time_value);
		departureTimeTextView.setText(FormatHelper.getTime(selectedDate));
		departureDateLayout = mView.findViewById(R.id.departure_date_layout);
		departureDateLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RoutsDetailsTabsFragment.weatherListJson != null && RoutsDetailsTabsFragment.weatherListJson.size() > 0) {
					createDepartureDateDialog();
				}
			}
		});
		departureTimeLayout = mView.findViewById(R.id.departure_time_layout);
		departureTimeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RoutsDetailsTabsFragment.weatherListJson != null && RoutsDetailsTabsFragment.weatherListJson.size() > 0) {
					createDepartureTimeDialog();
				}
			}
		});
		
		weatherList = (ListView) mView.findViewById(R.id.weatherList);
		weatherProgressBar = (ProgressBar) mView.findViewById(R.id.progressbar_weather);
        weatherProgressLayout = mView.findViewById(R.id.weather_progress_layout);
        weatherProgressText = (TextView) mView.findViewById(R.id.weather_progress_text);
        if (RoutsDetailsInfoFragment.departureCity != null) {
        	String weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), 
        			RoutsDetailsInfoFragment.departureCity);
        	weatherProgressText.setText(weatherProgressValue);
        }
        weatherListProgress = (ProgressBar) mView.findViewById(R.id.weatherList_progress);
        
        routeWeatherMainLayout = mView.findViewById(R.id.route_weather_main_layout);
        noResultsLayout = mView.findViewById(R.id.no_results_layout);
	}
	
	@Override
	public void onResume() {
		if (RoutsDetailsTabsFragment.weatherListJson != null && RoutsDetailsTabsFragment.weatherListJson.size() > 0) {
			onWeatherReceived(RoutsDetailsTabsFragment.weatherListJson);
		}
		
		if (RoutsDetailsTabsFragment.noResults) {
		    noResultsReceived();
		}
		
		super.onResume();
	}

	public void onWeatherReceived(List<JSONObject> jsonList) {
		Log.d("Vlad", "onWeatherReceived");
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
				} else if (jsonList.size() > 2 && i == 1) {
					// waypoint 1 weather list
					waypoint1WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint1WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 2) {
					// waypoint 2 weather list
					waypoint2WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint2WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 3) {
					// waypoint 3 weather list
					waypoint3WeatherList = new ArrayList<JSONObject>();
					for (int j = 0; j < hourlyForecast.length(); j++) {
						waypoint3WeatherList.add(hourlyForecast.getJSONObject(j));
					}
				} else if (jsonList.size() > 2 && i == 4) {
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
                    selectWeatherFromList(currentTime, departureWeatherList, RoutsDetailsInfoFragment.departureCity, true);
                } else {
                    selectWeatherFromList(receivedCalendar, departureWeatherList, RoutsDetailsInfoFragment.departureCity, false);
                }
                
                if (RoutsDetailsInfoFragment.waypoint1TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, RoutsDetailsInfoFragment.waypoint1TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint1WeatherList, RoutsDetailsInfoFragment.waypoint1City, false);
                }
                
                if (RoutsDetailsInfoFragment.waypoint2TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, RoutsDetailsInfoFragment.waypoint2TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint2WeatherList, RoutsDetailsInfoFragment.waypoint2City, false);
                }
                
                if (RoutsDetailsInfoFragment.waypoint3TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, RoutsDetailsInfoFragment.waypoint3TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint3WeatherList, RoutsDetailsInfoFragment.waypoint3City, false);
                }
                
                if (RoutsDetailsInfoFragment.waypoint4TimeDistance != 0) {
                    Calendar waypoint1Time = FormatHelper.getFutureDate(receivedCalendar, RoutsDetailsInfoFragment.waypoint4TimeDistance);
                    selectWeatherFromList(waypoint1Time, waypoint4WeatherList, RoutsDetailsInfoFragment.waypoint4City, false);
                }
                
                Calendar destinationTime = FormatHelper.getFutureDate(receivedCalendar, RoutsDetailsInfoFragment.destinationTimeDistance);
                selectWeatherFromList(destinationTime, destinationWeatherList, RoutsDetailsInfoFragment.destinationCity, false);
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
				    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a zzz MMMM dd, yyyy", Locale.US);
                    weatherTime.setTime(sdf.parse(weatherTimeObject.getString("pretty").replace("on ", "").trim()));
				    
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weatherListAdapter = new WeatherListAdapter(getActivity(), R.layout.weather_item, weatherItems);
                weatherList.setAdapter(weatherListAdapter);
                weatherListProgress.setVisibility(View.GONE);
                weatherList.setVisibility(View.VISIBLE);
            }
        });
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
	    dummyCalendar.set(Calendar.SECOND, 0);
	    dummyCalendar.set(Calendar.MILLISECOND, 0);
	    Calendar maxCalendar = Utils.getMaxWeatherDate(RoutsDetailsInfoFragment.destinationTimeDistance);
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
	    dummyCalendar.set(Calendar.SECOND, 0);
	    dummyCalendar.set(Calendar.MILLISECOND, 0);
	    Calendar maxCalendar = Utils.getMaxWeatherDate(RoutsDetailsInfoFragment.destinationTimeDistance);
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
	
	public void onWeatherElementReceived(int position, int total) {
	    String weatherProgressValue = "";
	    if (isAdded()) {
	        if (position == 0) {
	            // departure weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsInfoFragment.departureCity);    
	        } else if (position == 1 && position < (total - 1)) {
	            // waypoint 1 weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsInfoFragment.waypoint1City);
	        } else if (position == 2 && position < (total - 1)) {
	            // waypoint 2 weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsInfoFragment.waypoint2City);
	        } else if (position == 3 && position < (total - 1)) {
	            // waypoint 3 weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsInfoFragment.waypoint3City);
	        } else if (position == 4 && position < (total - 1)) {
	            // waypoint 4 weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsInfoFragment.waypoint4City);
	        } else {
	            // destination weather request
	            weatherProgressValue = String.format(getResources().getString(R.string.weather_progress_text), RoutsDetailsInfoFragment.destinationCity);
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
        routeWeatherMainLayout.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.VISIBLE);
    }
}
