package com.avallon.autool.requests;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract.Events;

import com.avallon.autool.fragments.PrefsFragment;

public class RequestDeleteEvents extends AsyncTask<String, String, Integer> {

	private boolean done = false;
	private PrefsFragment prefsFragment;
	private List<Long> eventsIds;

	public RequestDeleteEvents(PrefsFragment prefsFragment, List<Long> eventsIds) {
		this.prefsFragment = prefsFragment;
		this.eventsIds = eventsIds;
	}

	@Override
	protected Integer doInBackground(String... params) {
		int rowsDeleted = 0;
		done = false;
		if (eventsIds.size() > 0) {
			ContentResolver cr = prefsFragment.getActivity().getContentResolver();
			
			for (int i = 0; i < eventsIds.size(); i++) {
				long eventId = eventsIds.get(i);
				
				Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
				rowsDeleted += cr.delete(deleteUri, null, null);
				
				if (rowsDeleted >= 1) {
					done = true;		
				} else {
					done = false;
					break;
				}
			}
		} else {
			done = true;	
		}

		return rowsDeleted;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		if (done) {
			prefsFragment.onEventsDeleted();
		} else {
			prefsFragment.onEventsDeletedFailed();
		}
	}
}
