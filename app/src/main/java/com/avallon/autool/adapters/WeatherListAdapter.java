package com.avallon.autool.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avallon.autool.R;
import com.avallon.autool.items.WeatherItem;
import com.avallon.autool.utils.PreferenceHelper;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class WeatherListAdapter extends ArrayAdapter<WeatherItem> {

    private Context context;
    private ArrayList<WeatherItem> weatherItems;
    private int layoutResource;

    public WeatherListAdapter(Context context, int resource, ArrayList<WeatherItem> weatherItems) {
        super(context, resource, weatherItems);

        this.context = context;
        this.layoutResource = resource;
        this.weatherItems = weatherItems;
    }

    public WeatherItem getItem(int position) {
        return weatherItems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	boolean isMetric = PreferenceHelper.isMetric(context);
        final WeatherItem weatherItem = getItem(position);
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(layoutResource, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.weatherIcon = (ImageView) rowView.findViewById(R.id.weather_icon);
            viewHolder.weatherCity = (TextView) rowView.findViewById(R.id.weather_city);
            viewHolder.weatherTemp = (TextView) rowView.findViewById(R.id.weather_temp);
            viewHolder.weatherFeelsLike = (TextView) rowView.findViewById(R.id.weather_feels_like);
            viewHolder.weatherCondition = (TextView) rowView.findViewById(R.id.weather_condition);
            viewHolder.weatherWind = (TextView) rowView.findViewById(R.id.weather_wind);
            viewHolder.weatherHumidity = (TextView) rowView.findViewById(R.id.weather_humidity);
            viewHolder.weatherPrecip = (TextView) rowView.findViewById(R.id.weather_precipitation);
            viewHolder.weatherPressure = (TextView) rowView.findViewById(R.id.weather_pressure);
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        UrlImageViewHelper.setUrlDrawable(viewHolder.weatherIcon, weatherItem.getIconUrl());

        SimpleDateFormat hourFormat = new SimpleDateFormat("H:mm, d MMM y", Locale.US);
        String cityHourValue = String.format(context.getResources().getString(R.string.weather_city_format), weatherItem.getCity(),
                hourFormat.format(weatherItem.getWeatherTime().getTime()));
        viewHolder.weatherCity.setText(cityHourValue);

        String tempValue = "";
        if (isMetric) {
        	tempValue = String.format(context.getResources().getString(R.string.weather_temp_celsius_format),
                    weatherItem.getTemperatureCelsius());
        } else {
        	tempValue = String.format(context.getResources().getString(R.string.weather_temp_farenheit_format),
                    weatherItem.getTemperatureFahrenheit());
        }
        viewHolder.weatherTemp.setText(tempValue);

        String feelsLikeValue = "";
        if (isMetric) {
        	feelsLikeValue = String.format(context.getResources().getString(R.string.weather_feels_like_celsius_format),
                    weatherItem.getFeelsLikeCelsius());
        } else {
        	feelsLikeValue = String.format(context.getResources().getString(R.string.weather_feels_like_farenheit_format),
                    weatherItem.getFeelsLikeFahrenheit());
        }
        viewHolder.weatherFeelsLike.setText(feelsLikeValue);

        viewHolder.weatherCondition.setText(weatherItem.getCondition());

        String windValue = "";
        if (isMetric) {
        	windValue = String.format(context.getResources().getString(R.string.weather_wind_metric_format), weatherItem.getWindSpeedKM(),
                    weatherItem.getWindDirection());
        } else {
        	windValue = String.format(context.getResources().getString(R.string.weather_wind_imperial_format), weatherItem.getWindSpeedMI(),
                    weatherItem.getWindDirection());
        }
        viewHolder.weatherWind.setText(windValue);

        String humidityValue = String
                .format(context.getResources().getString(R.string.weather_humiditys_format), weatherItem.getHumidity());
        viewHolder.weatherHumidity.setText(humidityValue);

        String pressureValue = "";
        if (isMetric) {
        	pressureValue = String.format(context.getResources().getString(R.string.weather_pressure_metric_format),
                    weatherItem.getPressureMetric());
        } else {
        	pressureValue = String.format(context.getResources().getString(R.string.weather_pressure_imperial_format),
                    weatherItem.getPressureImperial());
        }
        viewHolder.weatherPressure.setText(pressureValue);

        String precipValue;
        String qpf = "";
        int stringPrecipResource;
        if (isMetric) {
        	qpf = weatherItem.getPrecipitationsMetric();
        	stringPrecipResource = R.string.weather_precip_metric_format;
        } else {
        	qpf = weatherItem.getPrecipitationsImperial();
        	stringPrecipResource = R.string.weather_precip_imperial_format;
        }
        if (qpf.equals("") || qpf.equals("0") || qpf.equals("0.00")) {
            precipValue = context.getResources().getString(R.string.weather_precip_none);
        } else {
            precipValue = String.format(context.getResources().getString(stringPrecipResource), qpf);
        }
        viewHolder.weatherPrecip.setText(precipValue);

        return rowView;
    }

    static class ViewHolder {
        private ImageView weatherIcon;
        private TextView weatherCity;
        private TextView weatherTemp;
        private TextView weatherFeelsLike;
        private TextView weatherCondition;
        private TextView weatherWind;
        private TextView weatherHumidity;
        private TextView weatherPrecip;
        private TextView weatherPressure;
    }
}
