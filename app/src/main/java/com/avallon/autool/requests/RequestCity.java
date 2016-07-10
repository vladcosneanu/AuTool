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

import com.avallon.autool.fragments.RoutsFragment;

public class RequestCity extends AsyncTask<String, Integer, JSONObject> {

	private boolean done = false;
	private RoutsFragment routsFragment;

	public RequestCity(RoutsFragment routsFragment) {
		this.routsFragment = routsFragment;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;

		String url = "https://maps.googleapis.com/maps/api/geocode/json?";
		url += "sensor=true";
		url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
		url += "&result_type=route";
		url += "&latlng=" + params[0];

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
			routsFragment.onCityReceived(json);
		}
	}
}
