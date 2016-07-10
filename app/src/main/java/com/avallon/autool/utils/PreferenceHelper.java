package com.avallon.autool.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    public static final String AUTOOL = "AUTOOL";
    public static final String FUEL_CONSUMPTION = "FUEL_CONSUMPTION";
    public static final String FUEL_PRICE = "FUEL_PRICE";
    public static final String FUEL_TYPE = "FUEL_TYPE";
    public static final String FUEL_TANK_CAPACITY = "FUEL_TANK_CAPACITY";
    public static final String MEASUREMENT = "measurement";
    public static final String CURRENCY = "currency";
    public static final String REMINDER = "reminder";
    public static final String FEATURES_USED = "features_used";
    public static final int FEATURES_USED_NEEDED = 10;
    public static final String AUTOCOMPLETE = "autocomplete";
    public static final String NEW_FEATURES_1 = "new_features_1";

    public static final String INSURANCE_EXP_DAY = "INSURANCE_EXP_DAY";
    public static final String INSURANCE_EXP_MONTH = "INSURANCE_EXP_MONTH";
    public static final String INSURANCE_EXP_YEAR = "INSURANCE_EXP_YEAR";
    public static final String INSURANCE_EVENT_ID = "INSURANCE_EVENT_ID";

    public static final String INSPECTION_EXP_DAY = "INSPECTION_EXP_DAY";
    public static final String INSPECTION_EXP_MONTH = "INSPECTION_EXP_MONTH";
    public static final String INSPECTION_EXP_YEAR = "INSPECTION_EXP_YEAR";
    public static final String INSPECTION_EVENT_ID = "INSPECTION_EVENT_ID";

    public static final String ROAD_TAX_EXP_DAY = "ROAD_TAX_EXP_DAY";
    public static final String ROAD_TAX_EXP_MONTH = "ROAD_TAX_EXP_MONTH";
    public static final String ROAD_TAX_EXP_YEAR = "ROAD_TAX_EXP_YEAR";
    public static final String ROAD_TAX_EVENT_ID = "ROAD_TAX_EVENT_ID";

    public static void saveValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUTOOL, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String loadValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUTOOL, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static String loadDefauldValue(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }
    
    public static boolean loadBooleanDefauldValue(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, true);
    }
    
    public static void saveBooleanDefauldValue(Context context, String key, boolean value) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean isMetric(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String units = sharedPreferences.getString(MEASUREMENT, "metric");

        if (units.equals("metric")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void clearSavedData(Context context) {
    	SharedPreferences sharedPreferences = context.getSharedPreferences(AUTOOL, Context.MODE_PRIVATE);
    	Editor editor = sharedPreferences.edit();
    	editor.clear().commit();
    	
    	SharedPreferences sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	Editor editorDefault = sharedDefaultPreferences.edit();
    	editorDefault.clear().commit();
    }
}
