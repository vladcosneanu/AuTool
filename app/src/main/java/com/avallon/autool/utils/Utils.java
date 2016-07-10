package com.avallon.autool.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.Settings;
import android.util.Log;

import com.avallon.autool.R;
import com.avallon.autool.activities.RoutsActivity;
import com.avallon.autool.fragments.RoutsDetailsFragment;
import com.avallon.autool.fragments.RoutsDetailsInfoFragment;
import com.avallon.autool.fragments.RoutsDetailsTabsFragment;
import com.avallon.autool.fragments.RoutsDetailsWeatherFragment;
import com.avallon.autool.items.Coordinate;
import com.avallon.autool.items.InstalledCalendar;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

public class Utils {

	private static Coordinate coordinate;

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] CALENDAR_PROJECTION = new String[] { Calendars._ID, // 0
			Calendars.ACCOUNT_NAME, // 1
			Calendars.CALENDAR_DISPLAY_NAME, // 2
			Calendars.OWNER_ACCOUNT // 3
	};

	// The indices for the projection array above.
	public static final int PROJECTION_ID_INDEX = 0;
	public static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	public static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	public static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

	public static final String[] EVENTS_PROJECTION = new String[] { Events._ID };

	public static void resetRequest() {
		RoutsActivity.allLocations = null;
		RoutsActivity.elevationLocations = null;
		RoutsActivity.routeJson = null;
		RoutsDetailsFragment.weatherListJson = null;
		RoutsDetailsFragment.elevationJson = null;
		RoutsDetailsFragment.noResults = false;

		RoutsDetailsTabsFragment.elevationJson = null;
		RoutsDetailsTabsFragment.elevationLocations = null;
		RoutsDetailsTabsFragment.weatherListJson = null;
		RoutsDetailsWeatherFragment.selectedDate = null;
		RoutsDetailsFragment.selectedDate = null;
		RoutsDetailsInfoFragment.departureCity = null;
		RoutsDetailsInfoFragment.waypoint1City = null;
		RoutsDetailsInfoFragment.waypoint2City = null;
		RoutsDetailsInfoFragment.waypoint3City = null;
		RoutsDetailsInfoFragment.waypoint4City = null;
		RoutsDetailsInfoFragment.destinationCity = null;
		RoutsDetailsTabsFragment.noResults = false;
	}

	public static Coordinate getLocation() {
		if (coordinate == null) {
			coordinate = new Coordinate();
			coordinate.setValid(false);
		}
		return coordinate;
	}

	public static void updateCoordinate(LocationInfo locationInfo) {
		coordinate = new Coordinate();
		Log.i("Location", "Location update at:" + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true)
				+ ", accuracy: " + locationInfo.lastAccuracy + ", lat: " + locationInfo.lastLat + ", lng: " + locationInfo.lastLong);
		coordinate.setLatitude(locationInfo.lastLat);
		coordinate.setLongitude(locationInfo.lastLong);
		coordinate.setValid(true);
		coordinate.setAccuracy(locationInfo.lastAccuracy);
	}

	public static Calendar getMaxWeatherDate(int destinationTimeDistance) {
		Calendar currentCal = Calendar.getInstance();

		currentCal.add(Calendar.HOUR, 240);
		currentCal.add(Calendar.SECOND, (-1 * destinationTimeDistance));
		currentCal.set(Calendar.MINUTE, 0);
		currentCal.set(Calendar.SECOND, 0);
		currentCal.set(Calendar.MILLISECOND, 0);

		return currentCal;
	}

	public static int getScreelLayout(Context context) {
		int screenLayout = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		switch (screenLayout) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
			return Configuration.SCREENLAYOUT_SIZE_SMALL;
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			return Configuration.SCREENLAYOUT_SIZE_NORMAL;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
			return Configuration.SCREENLAYOUT_SIZE_LARGE;
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			return Configuration.SCREENLAYOUT_SIZE_XLARGE;
			// return Configuration.SCREENLAYOUT_SIZE_NORMAL;
		default:
			return Configuration.SCREENLAYOUT_SIZE_UNDEFINED;
		}
	}

	public static List<InstalledCalendar> getAvailableCalendars(Context context) {
		// Run query
		Cursor cursor = null;
		ContentResolver cr = context.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;

		String selection = "((" + Calendars.VISIBLE + " = ?))";
		String[] selectionArgs = new String[] { "1" };
		// Submit the query and get a Cursor object back.
		cursor = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);

		List<InstalledCalendar> calendars = new ArrayList<InstalledCalendar>();

		// Use the cursor to step through the returned records
		while (cursor.moveToNext()) {
			InstalledCalendar calendar = new InstalledCalendar();
			calendar.createInstalledCalendar(cursor);

			calendars.add(calendar);
		}

		return calendars;
	}

	public static boolean eventExists(String eventID, Activity activity) {
		String selection = "(( " + Events._ID + " = ?) AND ( deleted != 1 ))";
		String[] selectionArgs = new String[] { eventID };

		ContentResolver cr = activity.getContentResolver();
		Cursor cursor = cr.query(Events.CONTENT_URI, EVENTS_PROJECTION, selection, selectionArgs, null);

		if (cursor.getCount() >= 1) {
			// the event exists
			return true;
		} else {
			// the event does not exist
			return false;
		}
	}

	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Prompt the user to connect the device to the Internet
	 */
	public static void createNetErrorDialog(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(R.drawable.ic_launcher).setMessage(R.string.net_dialog_message).setTitle(R.string.net_dialog_title)
				.setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
						activity.startActivity(i);
					}
				}).setNegativeButton(R.string.cancel, null);

		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void checkReadyToRate(final Activity activity) {
		if (activity == null) {
			return;
		}
		
		String featuresUsedValue = PreferenceHelper.loadValue(activity, PreferenceHelper.FEATURES_USED);
		if (featuresUsedValue.equals("")){
			PreferenceHelper.saveValue(activity, PreferenceHelper.FEATURES_USED, "1");
			return;
		} else {
			int featuresUsed = Integer.parseInt(featuresUsedValue);
			featuresUsed++;
			PreferenceHelper.saveValue(activity, PreferenceHelper.FEATURES_USED, String.valueOf(featuresUsed));
			
			if (featuresUsed == PreferenceHelper.FEATURES_USED_NEEDED) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage(activity.getString(R.string.rate_it_mesage)).setTitle(R.string.app_name).setIcon(R.drawable.ic_launcher)
						.setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PreferenceHelper.saveValue(activity, PreferenceHelper.FEATURES_USED, "11");
								final String appPackageName = activity.getPackageName();
								try {
									activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
								} catch (android.content.ActivityNotFoundException anfe) {
									activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
											+ appPackageName)));
								}
							}
						});
				builder.setNeutralButton(R.string.later, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						PreferenceHelper.saveValue(activity, PreferenceHelper.FEATURES_USED, "0");
					}
				});
				builder.setNegativeButton(R.string.close, null);
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				return;
			}
		}
	}
}
