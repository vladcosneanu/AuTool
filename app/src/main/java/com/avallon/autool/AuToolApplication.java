package com.avallon.autool;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import android.app.Application;
import android.util.Log;

public class AuToolApplication extends Application {

	private static AuToolApplication singleton;

	public static AuToolApplication getInstance() {
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		singleton = this;
		
		try {
			LocationLibrary.initialiseLibrary(getBaseContext(), "com.avallon.autool");
			LocationLibrary.useFineAccuracyForRequests(true);
		} catch (Exception e) {
			Log.e("AuToolApplication", "Could not setup the location library: " + e.getMessage());
		}
	}

}
