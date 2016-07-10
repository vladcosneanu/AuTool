package com.avallon.autool.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.avallon.autool.R;
import com.avallon.autool.activities.MainActivity;
import com.avallon.autool.fragments.ServicesFragment;
import com.avallon.autool.requests.RequestService;

public class AutoolAppWidgetProvider41 extends AppWidgetProvider {

	public static final String ACTION_WIDGET_GAS_STATION = "ACTION_WIDGET_GAS_STATION";
	public static final String ACTION_WIDGET_AUTO_SERVICE = "ACTION_WIDGET_AUTO_SERVICE";
	public static final String ACTION_WIDGET_CAR_WASH = "ACTION_WIDGET_CAR_WASH";
	public static final String ACTION_WIDGET_PARKING = "ACTION_WIDGET_PARKING";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_4_1);

			// Create an Intent for Gas Stations
			Intent intent = new Intent(context, MainActivity.class);
			intent.putExtra(ServicesFragment.SERVICE_TYPE, RequestService.GAS_STATION);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setAction(ACTION_WIDGET_GAS_STATION);
			PendingIntent pendingIntentGasStations = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.gas_station_view, pendingIntentGasStations);

			// Create an Intent for Auto Services
			intent = new Intent(context, MainActivity.class);
			intent.putExtra(ServicesFragment.SERVICE_TYPE, RequestService.CAR_REPAIR);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setAction(ACTION_WIDGET_AUTO_SERVICE);
			PendingIntent pendingIntentAutoServices = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.auto_service_view, pendingIntentAutoServices);

			// Create an Intent for Car Washes
			intent = new Intent(context, MainActivity.class);
			intent.putExtra(ServicesFragment.SERVICE_TYPE, RequestService.CAR_WASH);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setAction(ACTION_WIDGET_CAR_WASH);
			PendingIntent pendingIntentCarWashes = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.car_wash_view, pendingIntentCarWashes);

			// Create an Intent for Parking
			intent = new Intent(context, MainActivity.class);
			intent.putExtra(ServicesFragment.SERVICE_TYPE, RequestService.PARKING);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setAction(ACTION_WIDGET_PARKING);
			PendingIntent pendingIntentParking = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.parking_view, pendingIntentParking);

			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}
