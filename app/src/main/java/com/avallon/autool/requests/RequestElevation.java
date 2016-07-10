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

import com.avallon.autool.interfaces.RouteDetailsListener;

import android.os.AsyncTask;
import android.util.Log;

public class RequestElevation extends AsyncTask<String, Integer, JSONObject> {

	private boolean done = false;
	private RouteDetailsListener fragment;

	public RequestElevation(RouteDetailsListener fragment) {
		this.fragment = fragment;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		
		try {
			String url = "https://maps.googleapis.com/maps/api/elevation/json";
			url += "?sensor=true";
			url += "&samples=11";
			url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
			
			String pathEncoded = URLEncoder.encode(params[0], "UTF-8");
			url += "&path=" + pathEncoded;
			
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
			fragment.onElevationReceived(json);
		}
	}
}
