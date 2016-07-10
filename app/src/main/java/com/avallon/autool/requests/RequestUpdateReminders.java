package com.avallon.autool.requests;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.provider.CalendarContract.Reminders;

import com.avallon.autool.fragments.PrefsFragment;
import com.avallon.autool.utils.PreferenceHelper;

public class RequestUpdateReminders extends AsyncTask<String, String, Boolean> {

	private boolean done = false;
	private PrefsFragment prefsFragment;
	private int reminderMinutes;

	public RequestUpdateReminders(PrefsFragment prefsFragment, int reminderMinutes) {
		this.prefsFragment = prefsFragment;
		this.reminderMinutes = reminderMinutes;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		ContentValues values = new ContentValues();
		values.put(Reminders.MINUTES, reminderMinutes);

		// update inspection event reminder
		boolean inspectionEventUpdated = false;
		if (!PreferenceHelper.loadValue(prefsFragment.getActivity(), PreferenceHelper.INSPECTION_EVENT_ID).equals("")) {
			String eventID = PreferenceHelper.loadValue(prefsFragment.getActivity(), PreferenceHelper.INSPECTION_EVENT_ID);
			int rows = prefsFragment.getActivity().getContentResolver()
					.update(Reminders.CONTENT_URI, values, Reminders.EVENT_ID + " = ?", new String[] { eventID });

			if (rows > 0) {
				inspectionEventUpdated = true;
			} else {
				inspectionEventUpdated = false;
			}
		} else {
			inspectionEventUpdated = true;
		}

		// update insurance event reminder
		boolean insuranceEventUpdated = false;
		if (!PreferenceHelper.loadValue(prefsFragment.getActivity(), PreferenceHelper.INSURANCE_EVENT_ID).equals("")) {
			String eventID = PreferenceHelper.loadValue(prefsFragment.getActivity(), PreferenceHelper.INSURANCE_EVENT_ID);
			int rows = prefsFragment.getActivity().getContentResolver()
					.update(Reminders.CONTENT_URI, values, Reminders.EVENT_ID + " = ?", new String[] { eventID });

			if (rows > 0) {
				insuranceEventUpdated = true;
			} else {
				insuranceEventUpdated = false;
			}
		} else {
			insuranceEventUpdated = true;
		}

		// update road tax event reminder
		boolean roadTaxEventUpdated = false;
		if (!PreferenceHelper.loadValue(prefsFragment.getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID).equals("")) {
			String eventID = PreferenceHelper.loadValue(prefsFragment.getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID);
			int rows = prefsFragment.getActivity().getContentResolver()
					.update(Reminders.CONTENT_URI, values, Reminders.EVENT_ID + " = ?", new String[] { eventID });

			if (rows > 0) {
				roadTaxEventUpdated = true;
			} else {
				roadTaxEventUpdated = false;
			}
		} else {
			roadTaxEventUpdated = true;
		}

		done = true;

		if (inspectionEventUpdated && insuranceEventUpdated && roadTaxEventUpdated) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (done) {
			if (result) {
				prefsFragment.onRemindersUpdateSuccessful();
			} else {
				prefsFragment.onRemindersUpdateFailed();
			}
		}
	}
}
