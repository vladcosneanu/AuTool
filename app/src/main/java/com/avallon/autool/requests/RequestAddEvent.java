package com.avallon.autool.requests;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import com.avallon.autool.R;
import com.avallon.autool.fragments.CarDetailsFragment;
import com.avallon.autool.utils.PreferenceHelper;

public class RequestAddEvent extends AsyncTask<String, String, Long> {

	public static final String INSPECTION = "INSPECTION";
	public static final String INSURANCE = "INSURANCE";
	public static final String ROAD_TAX = "ROAD_TAX";
	
	private boolean done = false;
	private CarDetailsFragment carDetailsFragment;
	private long calendarId;
	private String type;

	public RequestAddEvent(CarDetailsFragment carDetailsFragment, long calendarId, String type) {
		this.carDetailsFragment = carDetailsFragment;
		this.calendarId = calendarId;
		this.type = type;
	}

	@Override
	protected Long doInBackground(String... params) {
		Calendar calendar = Calendar.getInstance();
		Context context = carDetailsFragment.getActivity();
		
		String title = "";
		String description = "";
		String location = "";
		
		if (type.equals(INSPECTION)) {
			calendar.set(Calendar.DATE, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.INSPECTION_EXP_DAY)));
			calendar.set(Calendar.MONTH, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.INSPECTION_EXP_MONTH)));
			calendar.set(Calendar.YEAR, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.INSPECTION_EXP_YEAR)));
			
			title = carDetailsFragment.getString(R.string.vehicle_inspection_title);
			description = carDetailsFragment.getString(R.string.vehicle_inspection_description);
			location = carDetailsFragment.getString(R.string.vehicle_inspection_location);
		} else if (type.equals(INSURANCE)) {
			calendar.set(Calendar.DATE, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.INSURANCE_EXP_DAY)));
			calendar.set(Calendar.MONTH, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.INSURANCE_EXP_MONTH)));
			calendar.set(Calendar.YEAR, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.INSURANCE_EXP_YEAR)));
			
			title = carDetailsFragment.getString(R.string.vehicle_insurance_title);
			description = carDetailsFragment.getString(R.string.vehicle_insurance_description);
			location = carDetailsFragment.getString(R.string.vehicle_insurance_location);
		} else if (type.equals(ROAD_TAX)) {
			calendar.set(Calendar.DATE, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.ROAD_TAX_EXP_DAY)));
			calendar.set(Calendar.MONTH, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.ROAD_TAX_EXP_MONTH)));
			calendar.set(Calendar.YEAR, Integer.parseInt(PreferenceHelper.loadValue(context, PreferenceHelper.ROAD_TAX_EXP_YEAR)));
			
			title = carDetailsFragment.getString(R.string.vehicle_road_tax_title);
			description = carDetailsFragment.getString(R.string.vehicle_road_tax_description);
			location = carDetailsFragment.getString(R.string.vehicle_road_tax_location);
		}
		long time = calendar.getTimeInMillis();
		
		ContentResolver cr = carDetailsFragment.getActivity().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, time);
		values.put(Events.DTEND, time);
		values.put(Events.ALL_DAY, 1);
		values.put(Events.TITLE, title);
		values.put(Events.DESCRIPTION, description);
		values.put(Events.EVENT_LOCATION, location);
		values.put(Events.CALENDAR_ID, calendarId);
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

		Uri uri = cr.insert(Events.CONTENT_URI, values);

		// get the event ID that is the last element in the Uri
		long eventID = Long.parseLong(uri.getLastPathSegment());
		
		String reminderValue = PreferenceHelper.loadDefauldValue(context, "reminder");
		if (reminderValue.equals("")) {
			// reminder setting was not set
			reminderValue = "1 day";
		}
		
		int reminderMinutes = 0;
		if (reminderValue.equals("1 day")) {
			reminderMinutes = 60 * 24;
		} else if (reminderValue.equals("2 days")) {
			reminderMinutes = 60 * 24 * 2;
		} else if (reminderValue.equals("1 week")) {
			reminderMinutes = 60 * 24 * 7;
		} else if (reminderValue.equals("2 weeks")) {
			reminderMinutes = 60 * 24 * 14;
		}
		
		ContentValues valuesReminder = new ContentValues();
		valuesReminder.put(Reminders.MINUTES, reminderMinutes);
		valuesReminder.put(Reminders.EVENT_ID, eventID);
		valuesReminder.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		
		cr.insert(Reminders.CONTENT_URI, valuesReminder);
		
		if (type.equals(INSPECTION)) {
			PreferenceHelper.saveValue(context, PreferenceHelper.INSPECTION_EVENT_ID, String.valueOf(eventID));			
		} else if (type.equals(INSURANCE)) {
			PreferenceHelper.saveValue(context, PreferenceHelper.INSURANCE_EVENT_ID, String.valueOf(eventID));
		} else if (type.equals(ROAD_TAX)) {
			PreferenceHelper.saveValue(context, PreferenceHelper.ROAD_TAX_EVENT_ID, String.valueOf(eventID));
		}
		
		done = true;
		
		return eventID;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		
		if (done) {
			carDetailsFragment.onEventAdded();
		}
	}
}
