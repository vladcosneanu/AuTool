package com.avallon.autool.fragments;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avallon.autool.R;
import com.avallon.autool.items.InstalledCalendar;
import com.avallon.autool.requests.RequestAddEvent;
import com.avallon.autool.requests.RequestUpdateEvent;
import com.avallon.autool.utils.FormatHelper;
import com.avallon.autool.utils.PreferenceHelper;
import com.avallon.autool.utils.Utils;

public class CarDetailsFragment extends Fragment implements OnClickListener, OnDateSetListener {

	private View mView;
	private LinearLayout gasConsumptionLayout;
	private TextView gasConsumptionText;
	private LinearLayout gasPriceLayout;
	private TextView gasPriceText;
	private LinearLayout fuelTypeLayout;
	private TextView fuelTypeText;
	private LinearLayout fuelTankLayout;
	private TextView fuelTankText;
	private LinearLayout inspectionExpLayout;
	private TextView inspectionExpText;
	private LinearLayout insuranceExpLayout;
	private TextView insuranceExpText;
	private LinearLayout roadTaxExpLayout;
	private TextView roadTaxExpText;
	private int dateType;
	private boolean isMetric;
	private ImageButton inspectionAddCalendar;
	private ImageButton insuranceAddCalendar;
	private ImageButton roadTaxAddCalendar;
	private String selectedEventType;
	private String selectedCalendarName;
	private ImageButton inspectionViewCalendar;
	private ImageButton insuranceViewCalendar;
	private ImageButton roadTaxViewCalendar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.car_details_fragment, container, false);

		return mView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		gasConsumptionLayout = (LinearLayout) mView.findViewById(R.id.gas_consumption_layout);
		gasConsumptionLayout.setOnClickListener(this);
		gasPriceLayout = (LinearLayout) mView.findViewById(R.id.gas_price_layout);
		gasPriceLayout.setOnClickListener(this);
		fuelTypeLayout = (LinearLayout) mView.findViewById(R.id.fuel_type_layout);
		fuelTypeLayout.setOnClickListener(this);
		fuelTankLayout = (LinearLayout) mView.findViewById(R.id.fuel_tank_layout);
		fuelTankLayout.setOnClickListener(this);
		inspectionExpLayout = (LinearLayout) mView.findViewById(R.id.inspection_exp_layout);
		inspectionExpLayout.setOnClickListener(this);
		insuranceExpLayout = (LinearLayout) mView.findViewById(R.id.insurance_exp_layout);
		insuranceExpLayout.setOnClickListener(this);
		roadTaxExpLayout = (LinearLayout) mView.findViewById(R.id.road_tax_layout);
		roadTaxExpLayout.setOnClickListener(this);
		gasConsumptionText = (TextView) mView.findViewById(R.id.gas_consumption_value);
		gasPriceText = (TextView) mView.findViewById(R.id.gas_price_value);
		fuelTypeText = (TextView) mView.findViewById(R.id.fuel_type_value);
		fuelTankText = (TextView) mView.findViewById(R.id.fuel_capacity_value);
		inspectionExpText = (TextView) mView.findViewById(R.id.inspection_exp_value);
		insuranceExpText = (TextView) mView.findViewById(R.id.insurance_exp_value);
		roadTaxExpText = (TextView) mView.findViewById(R.id.road_tax_value);
		inspectionAddCalendar = (ImageButton) mView.findViewById(R.id.inspection_add_calendar);
		inspectionAddCalendar.setOnClickListener(this);
		insuranceAddCalendar = (ImageButton) mView.findViewById(R.id.insurance_add_calendar);
		insuranceAddCalendar.setOnClickListener(this);
		roadTaxAddCalendar = (ImageButton) mView.findViewById(R.id.road_tax_add_calendar);
		roadTaxAddCalendar.setOnClickListener(this);
		inspectionViewCalendar = (ImageButton) mView.findViewById(R.id.inspection_view_calendar);
		inspectionViewCalendar.setOnClickListener(this);
		insuranceViewCalendar = (ImageButton) mView.findViewById(R.id.insurance_view_calendar);
		insuranceViewCalendar.setOnClickListener(this);
		roadTaxViewCalendar = (ImageButton) mView.findViewById(R.id.road_tax_view_calendar);
		roadTaxViewCalendar.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		setupEventButtons();

		isMetric = PreferenceHelper.isMetric(getActivity());

		String gasConsumptionSavedValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_CONSUMPTION);
		if (!gasConsumptionSavedValue.equals("")) {
			if (isMetric) {
				gasConsumptionText.setText(String.format(getResources().getString(R.string.fuel_consumption_litres),
						gasConsumptionSavedValue));
			} else {
				gasConsumptionText.setText(String.format(getResources().getString(R.string.fuel_consumption_gallons),
						gasConsumptionSavedValue));
			}
		} else {
			gasConsumptionText.setText(getString(R.string.empty_value2));
		}

		String gasPriceSavedValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_PRICE);
		if (!gasPriceSavedValue.equals("")) {
			String currency = PreferenceHelper.loadDefauldValue(getActivity(), PreferenceHelper.CURRENCY);
			if (currency.equals("")) {
				currency = "USD";
			}
			String gasPriceTextValue = "";
			if (isMetric) {
				gasPriceTextValue = String.format(getResources().getString(R.string.price_per_litre_format), gasPriceSavedValue, currency);
			} else {
				gasPriceTextValue = String.format(getResources().getString(R.string.price_per_gal_format), gasPriceSavedValue, currency);
			}
			gasPriceText.setText(gasPriceTextValue);
		} else {
			gasPriceText.setText(getString(R.string.empty_value2));
		}

		String fuelTypeSavedValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_TYPE);
		if (!fuelTypeSavedValue.equals("")) {
			fuelTypeText.setText(fuelTypeSavedValue);
		} else {
			fuelTypeText.setText(getString(R.string.empty_value2));
		}
		

		String fuelTankSavedValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_TANK_CAPACITY);
		if (!fuelTankSavedValue.equals("")) {
			String fuelTankValue = "";
			if (isMetric) {
				fuelTankValue = String.format(getResources().getString(R.string.fuel_consumption_litres2), fuelTankSavedValue);
			} else {
				fuelTankValue = String.format(getResources().getString(R.string.fuel_consumption_gallons2), fuelTankSavedValue);
			}
			fuelTankText.setText(fuelTankValue);
		} else {
			fuelTankText.setText(getString(R.string.empty_value2));
		}

		String inspectionExpDay = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_DAY);
		String inspectionExpMonth = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_MONTH);
		String inspectionExpYear = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_YEAR);
		if (!inspectionExpDay.equals("") || !inspectionExpMonth.equals("") || !inspectionExpYear.equals("")) {
			String monthValue = "";
			DateFormatSymbols dfs = new DateFormatSymbols();
			monthValue = dfs.getMonths()[Integer.parseInt(inspectionExpMonth)];

			String inspectionExpTextValue = String.format(getResources().getString(R.string.date_format),
					Integer.parseInt(inspectionExpDay), monthValue, Integer.parseInt(inspectionExpYear));
			inspectionExpText.setText(inspectionExpTextValue);
		} else {
			inspectionExpText.setText(getString(R.string.empty_value2));
		}

		String insuranceExpDay = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_DAY);
		String insuranceExpMonth = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_MONTH);
		String insuranceExpYear = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_YEAR);
		if (!insuranceExpDay.equals("") || !insuranceExpMonth.equals("") || !insuranceExpYear.equals("")) {
			String monthValue = "";
			DateFormatSymbols dfs = new DateFormatSymbols();
			monthValue = dfs.getMonths()[Integer.parseInt(insuranceExpMonth)];

			String insuranceExpTextValue = String.format(getResources().getString(R.string.date_format), Integer.parseInt(insuranceExpDay),
					monthValue, Integer.parseInt(insuranceExpYear));
			insuranceExpText.setText(insuranceExpTextValue);
		} else {
			insuranceExpText.setText(getString(R.string.empty_value2));
		}

		String roadTaxExpDay = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_DAY);
		String roadTaxExpMonth = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_MONTH);
		String roadTaxExpYear = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_YEAR);
		if (!roadTaxExpDay.equals("") || !roadTaxExpMonth.equals("") || !roadTaxExpYear.equals("")) {
			String monthValue = "";
			DateFormatSymbols dfs = new DateFormatSymbols();
			monthValue = dfs.getMonths()[Integer.parseInt(roadTaxExpMonth)];

			String roadTaxExpTextValue = String.format(getResources().getString(R.string.date_format), Integer.parseInt(roadTaxExpDay),
					monthValue, Integer.parseInt(roadTaxExpYear));
			roadTaxExpText.setText(roadTaxExpTextValue);
		} else {
			roadTaxExpText.setText(getString(R.string.empty_value2));
		}
	}

	private void setupEventButtons() {
		String inspectionEventID = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EVENT_ID);
		if (!inspectionEventID.equals("") && Utils.eventExists(inspectionEventID, getActivity())) {
			// this events still exists in the device Calendar application
			inspectionAddCalendar.setVisibility(View.GONE);
			inspectionViewCalendar.setVisibility(View.VISIBLE);
		} else {
			// the event was deleted from the device Calendar application, or was not set
			inspectionAddCalendar.setVisibility(View.VISIBLE);
			inspectionViewCalendar.setVisibility(View.GONE);
		}

		String insuranceEventID = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EVENT_ID);
		if (!insuranceEventID.equals("") && Utils.eventExists(insuranceEventID, getActivity())) {
			// this events still exists in the device Calendar application
			insuranceAddCalendar.setVisibility(View.GONE);
			insuranceViewCalendar.setVisibility(View.VISIBLE);
		} else {
			// the event was deleted from the device Calendar application, or was not set
			insuranceAddCalendar.setVisibility(View.VISIBLE);
			insuranceViewCalendar.setVisibility(View.GONE);
		}

		String roadtaxEventID = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID);
		if (!roadtaxEventID.equals("") && Utils.eventExists(roadtaxEventID, getActivity())) {
			// this events still exists in the device Calendar application
			roadTaxAddCalendar.setVisibility(View.GONE);
			roadTaxViewCalendar.setVisibility(View.VISIBLE);
		} else {
			// the event was deleted from the device Calendar application, or was not set
			roadTaxAddCalendar.setVisibility(View.VISIBLE);
			roadTaxViewCalendar.setVisibility(View.GONE);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gas_consumption_layout:
			createFuelConsumptionDialog();
			break;
		case R.id.gas_price_layout:
			createFuelPriceDialog();
			break;
		case R.id.fuel_type_layout:
			createFuelTypeDialog();
			break;
		case R.id.fuel_tank_layout:
			createFuelTankCapacityDialog();
			break;
		case R.id.inspection_exp_layout:
			createInspectionExpDateDialog();
			break;
		case R.id.insurance_exp_layout:
			createInsuranceExpDateDialog();
			break;
		case R.id.road_tax_layout:
			createRoadTaxExpDateDialog();
			break;
		case R.id.inspection_add_calendar:
			String inspectionExpDay = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_DAY);
			String inspectionExpMonth = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_MONTH);
			String inspectionExpYear = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_YEAR);
			if (!inspectionExpDay.equals("") || !inspectionExpMonth.equals("") || !inspectionExpYear.equals("")) {
				selectedEventType = RequestAddEvent.INSPECTION;
				createAddEventConfirmDialog();
			}
			break;
		case R.id.insurance_add_calendar:
			String insuranceExpDay = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_DAY);
			String insuranceExpMonth = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_MONTH);
			String insuranceExpYear = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_YEAR);
			if (!insuranceExpDay.equals("") || !insuranceExpMonth.equals("") || !insuranceExpYear.equals("")) {
				selectedEventType = RequestAddEvent.INSURANCE;
				createAddEventConfirmDialog();
			}
			break;
		case R.id.road_tax_add_calendar:
			String roadTaxExpDay = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_DAY);
			String roadTaxExpMonth = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_MONTH);
			String roadTaxExpYear = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_YEAR);
			if (!roadTaxExpDay.equals("") || !roadTaxExpMonth.equals("") || !roadTaxExpYear.equals("")) {
				selectedEventType = RequestAddEvent.ROAD_TAX;
				createAddEventConfirmDialog();
			}
			break;
		case R.id.inspection_view_calendar:
			openCalendarEvent(Long.parseLong(PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EVENT_ID)));
			break;
		case R.id.insurance_view_calendar:
			openCalendarEvent(Long.parseLong(PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EVENT_ID)));
			break;
		case R.id.road_tax_view_calendar:
			openCalendarEvent(Long.parseLong(PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID)));
			break;
		default:
			break;
		}
	}

	private void openCalendarEvent(long eventID) {
		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
		startActivity(intent);
	}

	private void createFuelConsumptionDialog() {
		View dialoglayout = getActivity().getLayoutInflater().inflate(R.layout.alert_edit, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		TextView titleView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.dialog_title, null);
		titleView.setText(getResources().getString(R.string.set_fuel_consumption_title));
		builder.setCustomTitle(titleView);

		if (isMetric) {
			builder.setMessage(getResources().getString(R.string.set_fuel_consumption_message_metric));
		} else {
			builder.setMessage(getResources().getString(R.string.set_fuel_consumption_message_imperial));
		}

		final EditText textEntryView = (EditText) dialoglayout.findViewById(R.id.value);
		String gasConsumptionTextValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_CONSUMPTION);
		if (!gasConsumptionTextValue.equals("")) {
			textEntryView.setText(gasConsumptionTextValue);
		}

		builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String value = textEntryView.getText().toString().trim();
				if (value.equals("")) {
					Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
					return;
				}

				Double valueDouble = FormatHelper.round(Double.parseDouble(value), 1);
				String formattedValue = String.valueOf(valueDouble);
				if (valueDouble.intValue() == 0) {
					Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
					return;
				}

				PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_CONSUMPTION, formattedValue);

				String gasConsumptionTextValue = "";
				if (isMetric) {
					gasConsumptionTextValue = String.format(getResources().getString(R.string.fuel_consumption_litres), formattedValue);
				} else {
					gasConsumptionTextValue = String.format(getResources().getString(R.string.fuel_consumption_gallons), formattedValue);
				}
				gasConsumptionText.setText(gasConsumptionTextValue);

				Toast.makeText(getActivity(), R.string.fuel_consumption_saved, Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		builder.setView(dialoglayout);
		AlertDialog alert = builder.show();

		// place cursor at the end of texr, must call sthis after display
		textEntryView.setSelection(textEntryView.getText().length());

		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}

	private void createFuelPriceDialog() {
		View dialoglayout = getActivity().getLayoutInflater().inflate(R.layout.alert_edit, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		TextView titleView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.dialog_title, null);
		titleView.setText(getResources().getString(R.string.set_fuel_price_title));
		builder.setCustomTitle(titleView);

		if (isMetric) {
			builder.setMessage(String.format(getResources().getString(R.string.set_fuel_price_message_metric), getCurrency()));
		} else {
			builder.setMessage(String.format(getResources().getString(R.string.set_fuel_price_message_imperial), getCurrency()));
		}

		final EditText textEntryView = (EditText) dialoglayout.findViewById(R.id.value);
		String gasPriceTextValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_PRICE);
		if (!gasPriceTextValue.equals("")) {
			textEntryView.setText(gasPriceTextValue);
		}

		builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String value = textEntryView.getText().toString().trim();
				if (value.equals("")) {
					Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
					return;
				}

				Double valueDouble = FormatHelper.round(Double.parseDouble(value), 2);
				String formattedValue = String.valueOf(valueDouble);
				if (valueDouble.intValue() == 0) {
					Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
					return;
				}

				PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_PRICE, formattedValue);

				String gasPriceTextValue = "";
				if (isMetric) {
					gasPriceTextValue = String.format(getResources().getString(R.string.price_per_litre_format), formattedValue,
							getCurrency());
				} else {
					gasPriceTextValue = String.format(getResources().getString(R.string.price_per_gal_format), formattedValue,
							getCurrency());
				}

				gasPriceText.setText(gasPriceTextValue);

				Toast.makeText(getActivity(), R.string.fuel_price_saved, Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		builder.setView(dialoglayout);
		AlertDialog alert = builder.show();

		// place cursor at the end of texr, must call sthis after display
		textEntryView.setSelection(textEntryView.getText().length());

		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}

	private String getCurrency() {
		String currency = PreferenceHelper.loadDefauldValue(getActivity(), PreferenceHelper.CURRENCY);
		if (currency.equals("")) {
			currency = "USD";
		}
		return currency;
	}

	private void createFuelTypeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		TextView titleView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.dialog_title, null);
		titleView.setText(getResources().getString(R.string.set_fuel_type_title));
		builder.setCustomTitle(titleView);

		builder.setItems(R.array.fuel_type_provider, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TYPE, getResources().getString(R.string.petrol));
					break;
				case 1:
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TYPE, getResources().getString(R.string.diesel));
					break;
				case 2:
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TYPE, getResources().getString(R.string.biodiesel));
					break;
				case 3:
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TYPE, getResources().getString(R.string.autogas));
					break;
				case 4:
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TYPE, getResources().getString(R.string.ethanol));
					break;
				case 5:
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TYPE, getResources().getString(R.string.hybrid));
					break;
				default:
					break;
				}

				fuelTypeText.setText(PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_TYPE));
				Toast.makeText(getActivity(), R.string.fuel_type_saved, Toast.LENGTH_SHORT).show();
			}
		});

		builder.show();
	}

	private void createFuelTankCapacityDialog() {
		View dialoglayout = getActivity().getLayoutInflater().inflate(R.layout.alert_edit, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		TextView titleView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.dialog_title, null);
		titleView.setText(getResources().getString(R.string.set_fuel_tank_capacity_title));
		builder.setCustomTitle(titleView);

		if (isMetric) {
			builder.setMessage(getResources().getString(R.string.set_fuel_tank_capacity_message_metric));
		} else {
			builder.setMessage(getResources().getString(R.string.set_fuel_tank_capacity_message_imperial));
		}

		final EditText textEntryView = (EditText) dialoglayout.findViewById(R.id.value);
		String fuelTankCapacityTextValue = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.FUEL_TANK_CAPACITY);
		if (!fuelTankCapacityTextValue.equals("")) {
			textEntryView.setText(fuelTankCapacityTextValue);
		}

		builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String value = textEntryView.getText().toString().trim();
				if (value.equals("")) {
					Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
					return;
				}

				Double valueDouble = FormatHelper.round(Double.parseDouble(value), 0);
				String formattedValue = String.valueOf(valueDouble.intValue());
				if (valueDouble.intValue() == 0) {
					Toast.makeText(getActivity(), R.string.incorrect_value, Toast.LENGTH_SHORT).show();
					return;
				}

				PreferenceHelper.saveValue(getActivity(), PreferenceHelper.FUEL_TANK_CAPACITY, formattedValue);

				String fuelTankTextValue = "";
				if (isMetric) {
					fuelTankTextValue = String.format(getResources().getString(R.string.fuel_consumption_litres2), formattedValue);
				} else {
					fuelTankTextValue = String.format(getResources().getString(R.string.fuel_consumption_gallons2), formattedValue);
				}
				fuelTankText.setText(fuelTankTextValue);

				Toast.makeText(getActivity(), R.string.fuel_tank_capacity_saved, Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		builder.setView(dialoglayout);
		AlertDialog alert = builder.show();

		// place cursor at the end of texr, must call sthis after display
		textEntryView.setSelection(textEntryView.getText().length());

		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}

	private void createInspectionExpDateDialog() {
		dateType = 0;
		DatePickerFragment date = new DatePickerFragment();
		Calendar calendar = Calendar.getInstance();

		String day = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_DAY);
		String month = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_MONTH);
		String year = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EXP_YEAR);

		Bundle args = new Bundle();
		if (day.equals("") || month.equals("") || year.equals("")) {
			args.putInt("year", calendar.get(Calendar.YEAR));
			args.putInt("month", calendar.get(Calendar.MONTH));
			args.putInt("day", calendar.get(Calendar.DATE));
		} else {
			args.putInt("year", Integer.parseInt(year));
			args.putInt("month", Integer.parseInt(month));
			args.putInt("day", Integer.parseInt(day));
		}
		date.setArguments(args);

		// Set Call back to capture selected date
		date.setCallBack(this);
		date.show(getActivity().getSupportFragmentManager(), "Date Picker");
	}

	private void createInsuranceExpDateDialog() {
		dateType = 1;
		DatePickerFragment date = new DatePickerFragment();
		Calendar calendar = Calendar.getInstance();

		String day = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_DAY);
		String month = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_MONTH);
		String year = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EXP_YEAR);

		Bundle args = new Bundle();
		if (day.equals("") || month.equals("") || year.equals("")) {
			args.putInt("year", calendar.get(Calendar.YEAR));
			args.putInt("month", calendar.get(Calendar.MONTH));
			args.putInt("day", calendar.get(Calendar.DATE));
		} else {
			args.putInt("year", Integer.parseInt(year));
			args.putInt("month", Integer.parseInt(month));
			args.putInt("day", Integer.parseInt(day));
		}
		date.setArguments(args);

		// Set Call back to capture selected date
		date.setCallBack(this);
		date.show(getActivity().getSupportFragmentManager(), "Date Picker");
	}

	private void createRoadTaxExpDateDialog() {
		dateType = 2;
		DatePickerFragment date = new DatePickerFragment();
		Calendar calendar = Calendar.getInstance();

		String day = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_DAY);
		String month = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_MONTH);
		String year = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_YEAR);

		Bundle args = new Bundle();
		if (day.equals("") || month.equals("") || year.equals("")) {
			args.putInt("year", calendar.get(Calendar.YEAR));
			args.putInt("month", calendar.get(Calendar.MONTH));
			args.putInt("day", calendar.get(Calendar.DATE));
		} else {
			args.putInt("year", Integer.parseInt(year));
			args.putInt("month", Integer.parseInt(month));
			args.putInt("day", Integer.parseInt(day));
		}
		date.setArguments(args);

		// Set Call back to capture selected date
		date.setCallBack(this);
		date.show(getActivity().getSupportFragmentManager(), "Date Picker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		switch (dateType) {
		case 0:
			// inspection set
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date inspectionDate = sdf.parse(year + "-" + (month + 1) + "-" + day);
				Date today = sdf.parse(sdf.format(new Date()));

				if (inspectionDate.compareTo(today) > 0) {
					// selected day is after today
					String monthValue = "";
					DateFormatSymbols dfs = new DateFormatSymbols();
					monthValue = dfs.getMonths()[month];

					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.INSPECTION_EXP_DAY, String.valueOf(day));
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.INSPECTION_EXP_MONTH, String.valueOf(month));
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.INSPECTION_EXP_YEAR, String.valueOf(year));

					String inspectionExpTextValue = String.format(getResources().getString(R.string.date_format), day, monthValue, year);
					inspectionExpText.setText(inspectionExpTextValue);

					if (!PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSPECTION_EVENT_ID).equals("")) {
						Calendar newTime = Calendar.getInstance();
						newTime.set(year, month, day);
						RequestUpdateEvent requestUpdateEvent = new RequestUpdateEvent(this, Long.parseLong(PreferenceHelper.loadValue(
								getActivity(), PreferenceHelper.INSPECTION_EVENT_ID)), newTime);
						requestUpdateEvent.execute(new String[] {});
					}
					Toast.makeText(getActivity(), R.string.inspection_date_saved, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.date_invalid, Toast.LENGTH_SHORT).show();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			// insurance set
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date insuranceDate = sdf.parse(year + "-" + (month + 1) + "-" + day);
				Date today = sdf.parse(sdf.format(new Date()));

				if (insuranceDate.compareTo(today) > 0) {
					// selected day is after today
					String monthValue = "";
					DateFormatSymbols dfs = new DateFormatSymbols();
					monthValue = dfs.getMonths()[month];

					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.INSURANCE_EXP_DAY, String.valueOf(day));
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.INSURANCE_EXP_MONTH, String.valueOf(month));
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.INSURANCE_EXP_YEAR, String.valueOf(year));

					String insuranceExpTextValue = String.format(getResources().getString(R.string.date_format), day, monthValue, year);
					insuranceExpText.setText(insuranceExpTextValue);
					
					if (!PreferenceHelper.loadValue(getActivity(), PreferenceHelper.INSURANCE_EVENT_ID).equals("")) {
						Calendar newTime = Calendar.getInstance();
						newTime.set(year, month, day);
						RequestUpdateEvent requestUpdateEvent = new RequestUpdateEvent(this, Long.parseLong(PreferenceHelper.loadValue(
								getActivity(), PreferenceHelper.INSURANCE_EVENT_ID)), newTime);
						requestUpdateEvent.execute(new String[] {});
					}

					Toast.makeText(getActivity(), R.string.insurance_date_saved, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.date_invalid, Toast.LENGTH_SHORT).show();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			// road tax set
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date roadTaxDate = sdf.parse(year + "-" + (month + 1) + "-" + day);
				Date today = sdf.parse(sdf.format(new Date()));

				if (roadTaxDate.compareTo(today) > 0) {
					// selected day is after today
					String monthValue = "";
					DateFormatSymbols dfs = new DateFormatSymbols();
					monthValue = dfs.getMonths()[month];

					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_DAY, String.valueOf(day));
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_MONTH, String.valueOf(month));
					PreferenceHelper.saveValue(getActivity(), PreferenceHelper.ROAD_TAX_EXP_YEAR, String.valueOf(year));

					String roadTaxExpTextValue = String.format(getResources().getString(R.string.date_format), day, monthValue, year);
					roadTaxExpText.setText(roadTaxExpTextValue);
					
					if (!PreferenceHelper.loadValue(getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID).equals("")) {
						Calendar newTime = Calendar.getInstance();
						newTime.set(year, month, day);
						RequestUpdateEvent requestUpdateEvent = new RequestUpdateEvent(this, Long.parseLong(PreferenceHelper.loadValue(
								getActivity(), PreferenceHelper.ROAD_TAX_EVENT_ID)), newTime);
						requestUpdateEvent.execute(new String[] {});
					}

					Toast.makeText(getActivity(), R.string.road_tax_date_saved, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.date_invalid, Toast.LENGTH_SHORT).show();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	public void onEventAdded() {
		if (selectedEventType.equals(RequestAddEvent.INSPECTION)) {
			inspectionAddCalendar.setVisibility(View.GONE);
			inspectionViewCalendar.setVisibility(View.VISIBLE);
			Toast.makeText(getActivity(), String.format(getString(R.string.inspection_event_added), selectedCalendarName), Toast.LENGTH_SHORT).show();
		} else if (selectedEventType.equals(RequestAddEvent.INSURANCE)) {
			insuranceAddCalendar.setVisibility(View.GONE);
			insuranceViewCalendar.setVisibility(View.VISIBLE);
			Toast.makeText(getActivity(), String.format(getString(R.string.insurance_event_added), selectedCalendarName), Toast.LENGTH_SHORT).show();
		} else if (selectedEventType.equals(RequestAddEvent.ROAD_TAX)) {
			roadTaxAddCalendar.setVisibility(View.GONE);
			roadTaxViewCalendar.setVisibility(View.VISIBLE);
			Toast.makeText(getActivity(), String.format(getString(R.string.road_tax_event_added), selectedCalendarName), Toast.LENGTH_SHORT).show();
		}
	}

	public void onEventUpdateSuccessful() {

	}

	public void onEventUpdateFailed() {

	}
	
	private void createAddEventConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_add_event)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   createAddEventDialog();
                   }
               })
               .setNegativeButton(R.string.no, null);
        builder.show();
	}

	private void createAddEventDialog() {
		final List<InstalledCalendar> calendars = Utils.getAvailableCalendars(getActivity());

		String[] calendarNames = new String[calendars.size()];
		for (int i = 0; i < calendars.size(); i++) {
			calendarNames[i] = calendars.get(i).getDisplayName();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.event_calendar_select_title);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setItems(calendarNames, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InstalledCalendar calendar = calendars.get(which);
				selectedCalendarName = calendar.getDisplayName();

				RequestAddEvent addEvent = new RequestAddEvent(CarDetailsFragment.this, calendar.getId(), selectedEventType);
				addEvent.execute(new String[] {});
			}
		});

		builder.show();
	}
}
