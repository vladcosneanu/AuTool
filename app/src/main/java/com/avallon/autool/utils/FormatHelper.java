package com.avallon.autool.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormatHelper {

	public static String getTime(int seconds, boolean fullMins) {
		int days = (int) Math.floor(seconds / (3600 * 24));
		int hours = (int) Math.floor((seconds - (days * 3600 * 24)) / 3600);
		int minutes = (int) Math.floor((seconds - (days * 3600 * 24) - (hours * 3600)) / 60);

		String returnString = "";
		if (days > 0) {
			returnString += days + " d";
			
			if (hours > 0) {
				returnString += " " + hours + " h ";
			}
		} else {
			if (hours > 0) {
				returnString += hours + " h ";
			}
			
			if (minutes > 0) {
				if (fullMins) {
					if (minutes == 1) {
						returnString += "1 min";
					} else {
						returnString += minutes + " mins";
					}
				} else {
					returnString += minutes + " m";
				}
			} else {
				returnString += "0 m";
			}
		}

		return returnString;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static String getTime(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.US);
		String currentTime = sdf.format(calendar.getTime());
		
		return currentTime;
	}
	
	public static String getDate(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", Locale.US);
		String currentTime = sdf.format(calendar.getTime());
		
		return currentTime;
	}

	public static String getDateWithoutYear(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("d MMMM", Locale.US);
		String currentTime = sdf.format(calendar.getTime());

		return currentTime;
	}
	
	public static Calendar getFutureDate(Calendar referenceCalendar, int secondsToAdd) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(referenceCalendar.getTime());
	    calendar.add(Calendar.SECOND, secondsToAdd);
	    
		return calendar;
	}
}
