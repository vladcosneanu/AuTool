package com.avallon.autool.requests;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.avallon.autool.fragments.ServicesFragment;
import com.avallon.autool.items.Coordinate;

public class RequestServiceDistance extends AsyncTask<String, Integer, JSONObject> {

    public static final String GAS_STATION = "gas_station";
    public static final String CAR_REPAIR = "car_repair";
    public static final String CAR_WASH = "car_wash";
    public static final String PARKING = "parking";
    
	private boolean done = false;
	private ServicesFragment serviceFragment;
	private Coordinate currentLocation;
	private String destinations;

	public RequestServiceDistance(ServicesFragment serviceFragment, Coordinate currentLocation, String destinations) {
		this.serviceFragment = serviceFragment;
		this.currentLocation = currentLocation;
		this.destinations = destinations;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		
		String url = "";
		try {
			url += "https://maps.googleapis.com/maps/api/distancematrix/json?";
			url += "origins=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
			url += "&destinations=" + URLEncoder.encode(destinations, "UTF-8");
			url += "&sensor=true";
			url += "&mode=driving";
			url += "&units=metric";
			url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Log.e("request", url);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			Log.d("status", statusLine.toString());
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				result = EntityUtils.toByteArray(response.getEntity());
				publishProgress(result.length);
				String str = new String(result, "UTF-8");
				json = new JSONObject(str);
			}

			if (json != null) {
				done = true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		return json;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Log.d("size", values[0].toString());
	}

	@Override
	protected void onPostExecute(JSONObject json) {
		super.onPostExecute(json);
		
		if (done) {
			serviceFragment.onServicesDistanceReceived(json);
		}
	}
}
