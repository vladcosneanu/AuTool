package com.avallon.autool.requests;


import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.avallon.autool.interfaces.RouteDetailsListener;
import com.google.android.gms.maps.model.LatLng;

public class RequestWeather extends AsyncTask<List<LatLng>, Integer, List<JSONObject>> {

	private boolean done = false;
	private RouteDetailsListener fragment;

	public RequestWeather(RouteDetailsListener fragment) {
		this.fragment = fragment;
	}

	@Override
	protected List<JSONObject> doInBackground(List<LatLng>... params) {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		
		try {
			for (int i = 0; i < params[0].size(); i++) {
				publishProgress(i, params[0].size());
				byte[] result = null;
				JSONObject json = null;
				
				String url = "http://api.wunderground.com/api/468f8275a0e8182b/";
				url += "hourly10day/";
				url += "q/";
				url += params[0].get(i).latitude + "," + params[0].get(i).longitude;
				url += ".json";
				
				Log.e("request", url);
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				StatusLine statusLine = response.getStatusLine();
				Log.d("status", statusLine.toString());
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					String str = new String(result, "UTF-8");
					json = new JSONObject(str);
					jsonList.add(json);
				}
			}
			
			if (jsonList.size() == params[0].size()) {
				done = true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			
		}
		return jsonList;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		fragment.onWeatherElementReceived(values[0], values[1]);
	}

	@Override
	protected void onPostExecute(List<JSONObject> jsonList) {
		super.onPostExecute(jsonList);

		if (done) {
			fragment.onWeatherReceived(jsonList);
		}
	}
}
