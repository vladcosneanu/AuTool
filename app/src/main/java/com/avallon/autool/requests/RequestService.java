package com.avallon.autool.requests;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

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

public class RequestService extends AsyncTask<String, Integer, JSONObject> {

    public static final String GAS_STATION = "gas_station";
    public static final String CAR_REPAIR = "car_repair";
    public static final String CAR_WASH = "car_wash";
    public static final String PARKING = "parking";
    
	private boolean done = false;
	private ServicesFragment serviceFragment;
	private String type;
	private Coordinate currentLocation;
	private String nextPageToken;

	public RequestService(ServicesFragment serviceFragment, String type, Coordinate currentLocation, String nextPageToken) {
		this.serviceFragment = serviceFragment;
		this.type = type;
		this.currentLocation = currentLocation;
		this.nextPageToken = nextPageToken;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		
		String url = "";
		if (nextPageToken == null) {
		    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	        url += "location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
	        url += "&radius=3000";
	        url += "&types=" + type;
	        url += "&language=en-US";
	        url += "&sensor=true";
	        url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
		} else {
	        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            url += "pagetoken=" + nextPageToken;
            url += "&sensor=true";
            url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
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
			serviceFragment.onServicesReceived(json);
		}
	}
}
