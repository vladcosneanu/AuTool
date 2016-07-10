package com.avallon.autool.requests;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract.Events;

import com.avallon.autool.fragments.CarDetailsFragment;

public class RequestUpdateEvent extends AsyncTask<String, String, Integer> {

	private boolean done = false;
	private CarDetailsFragment carDetailsFragment;
	private long eventID;
	private Calendar newTime;

	public RequestUpdateEvent(CarDetailsFragment carDetailsFragment, long eventID, Calendar newTime) {
		this.carDetailsFragment = carDetailsFragment;
		this.eventID = eventID;
		this.newTime = newTime;
	}

	@Override
	protected Integer doInBackground(String... params) {
		ContentResolver cr = carDetailsFragment.getActivity().getContentResolver();
		ContentValues values = new ContentValues();

		values.put(Events.DTSTART, newTime.getTimeInMillis());
		values.put(Events.DTEND, newTime.getTimeInMillis());

		Uri updateUri = null;
		updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		int rows = cr.update(updateUri, values, null, null);

		done = true;

		return rows;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		if (done) {
			if (result > 0) {
				carDetailsFragment.onEventUpdateSuccessful();
			} else {
				carDetailsFragment.onEventUpdateFailed();
			}
		}
	}
}
