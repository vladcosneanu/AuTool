package com.avallon.autool.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.avallon.autool.R;
import com.avallon.autool.requests.RequestDeleteEvents;
import com.avallon.autool.requests.RequestUpdateReminders;
import com.avallon.autool.utils.PreferenceHelper;

public class PrefsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		final ListPreference measurement = (ListPreference) findPreference("measurement");

		measurement.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final int index = measurement.findIndexOfValue(newValue.toString());
				if (index >= 0) {
					final String summary = (String) measurement.getEntries()[index];
					measurement.setSummary(summary);
				}

				return true;
			}
		});

		final ListPreference currency = (ListPreference) findPreference("currency");

		currency.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final int index = currency.findIndexOfValue(newValue.toString());
				if (index >= 0) {
					final String summary = (String) currency.getEntries()[index];
					currency.setSummary(summary);
				}

				return true;
			}
		});

		final ListPreference reminder = (ListPreference) findPreference("reminder");

		reminder.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				final int index = reminder.findIndexOfValue(newValue.toString());
				if (index >= 0) {
					final String summary = (String) reminder.getEntries()[index];
					reminder.setSummary(summary);
				}

				String reminderValue = newValue.toString();

				int reminderMinutes = 60 * 24;
				if (new String(reminderValue).equals("1 day")) {
					reminderMinutes = 60 * 24;
				} else if (new String(reminderValue).equals("2 days")) {
					reminderMinutes = 60 * 24 * 2;
				} else if (new String(reminderValue).equals("1 week")) {
					reminderMinutes = 60 * 24 * 7;
				} else if (new String(reminderValue).equals("2 weeks")) {
					reminderMinutes = 60 * 24 * 14;
				}

				RequestUpdateReminders requestUpdateReminders = new RequestUpdateReminders(PrefsFragment.this, reminderMinutes);
				requestUpdateReminders.execute(new String[] {});

				return true;
			}
		});
		
		final CheckBoxPreference autocompletePref = (CheckBoxPreference) findPreference("autocomplete");
		
		autocompletePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Log.d("Vlad", newValue.toString());
				return true;
			}
		});

		final Preference clear = (Preference) findPreference("reset");

		clear.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(getString(R.string.reset_app)).setTitle(R.string.app_name).setIcon(R.drawable.ic_launcher)
						.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								List<Long> eventsIds = new ArrayList<Long>();
								String inspectionEventIdValue = PreferenceHelper.loadValue(getActivity(),
										PreferenceHelper.INSPECTION_EVENT_ID);
								if (!inspectionEventIdValue.equals("")) {
									eventsIds.add(Long.parseLong(inspectionEventIdValue));
								}

								String insuranceEventIdValue = PreferenceHelper.loadValue(getActivity(),
										PreferenceHelper.INSURANCE_EVENT_ID);
								if (!insuranceEventIdValue.equals("")) {
									eventsIds.add(Long.parseLong(insuranceEventIdValue));
								}

								String roadTaxEventIdValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID);
								if (!roadTaxEventIdValue.equals("")) {
									eventsIds.add(Long.parseLong(roadTaxEventIdValue));
								}

								RequestDeleteEvents deleteEvents = new RequestDeleteEvents(PrefsFragment.this, eventsIds);
								deleteEvents.execute(new String[] {});

								PreferenceHelper.clearSavedData(getActivity());
							}
						}).setNegativeButton("Cancel", null);
				AlertDialog alert = builder.create();
				alert.show();

				return true;
			}
		});

		final Preference contact = (Preference) findPreference("contact");

		contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);

				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "cosneanu_vlad@yahoo.com" });
				intent.putExtra(Intent.EXTRA_SUBJECT, "Autool feedback");

				startActivity(intent);

				return false;
			}
		});

		final Preference about = (Preference) findPreference("about");

		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(getString(R.string.about_message)).setTitle(R.string.app_name).setIcon(R.drawable.ic_launcher)
						.setCancelable(false).setPositiveButton(R.string.rate_it, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								final String appPackageName = getActivity().getPackageName();
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
											+ appPackageName)));
								}
							}
						});
				builder.setNegativeButton(R.string.close, null);
				AlertDialog alert = builder.create();
				alert.show();

				return false;
			}
		});
	}

	public void onRemindersUpdateSuccessful() {
	    if (isAdded()) {
	        Toast.makeText(getActivity(), getString(R.string.reminders_updated), Toast.LENGTH_LONG).show();
	    }
	}

	public void onRemindersUpdateFailed() {
	    if (isAdded()) {
	        Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
	    }
	}

	public void onEventsDeleted() {
	    if (isAdded()) {
	        Toast.makeText(getActivity(), getString(R.string.data_deleted), Toast.LENGTH_LONG).show();
	    }
	}

	public void onEventsDeletedFailed() {
	    if (isAdded()) {
	        Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
	    }
	}
}
