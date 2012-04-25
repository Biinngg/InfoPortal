package com.iBeiKe.InfoPortal.campus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public class JsonHandler {
	public void parseData(String str) throws JSONException {
		JSONTokener jsonTokener = new JSONTokener(str);
		JSONObject jsonObjec = (JSONObject)jsonTokener.nextValue();
		Log.d("jsonObjec", jsonObjec.toString());
		JSONObject jsonObjec1 = (JSONObject)jsonTokener.nextValue();
		Log.d("jsonObjec", jsonObjec1.toString());
		JSONArray jsonObjec3 = (JSONArray)jsonTokener.nextValue();
		Log.d("jsonObjec", jsonObjec3.toString());
		int length = jsonObjec3.length();
		for(int i=0; i<length; i++) {
			JSONObject obj = jsonObjec3.getJSONObject(i);
			Log.d("obj", obj.toString());
		}
	}
}
