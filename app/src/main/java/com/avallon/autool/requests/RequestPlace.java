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

import com.avallon.autool.fragments.RoutsFragment;

public class RequestPlace extends AsyncTask<String, Integer, JSONObject> {

	private boolean done = false;
	private RoutsFragment routsFragment;
	private String input;

	public RequestPlace(RoutsFragment routsFragment, String input) {
		this.routsFragment = routsFragment;
		this.input = input;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		try {
		    input = URLEncoder.encode(input, "UTF-8");;
		    
		    String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
	        url += "input=" + input;
	        url += "&offset=" + input.length();
	        url += "&language=en-US";
	        url += "&sensor=true";
	        url += "&types=(cities)";
	        url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
		    
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
		    routsFragment.onPlacesReceived(json);
		}
	}
}
