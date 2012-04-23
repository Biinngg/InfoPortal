package com.iBeiKe.InfoPortal.campus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class JsonHandler {
	private Context context;
	
	public JsonHandler(Context context) {
		this.context = context;
		try {
			parseData(fetchData());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String fetchData() throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://api.ibeike.com/eapi.php?username=40950187&passwd=225331&flag=lastmon");
        // Create a response handler
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String tmp = null;
        String htmlRet="";
        boolean mark = false;
        while((tmp=reader.readLine())!=null) {
        	if(mark)
        		htmlRet += tmp;
        	if(tmp.contains("<body>")) {
        		mark = true;
        	} else if(tmp.contains("</body>")) {
        		mark = false;
        	}
        }
        htmlRet.replace("</body>", "");
        String htmlBody = new String(htmlRet.getBytes("UTF-8"), "UTF-8");
        reader.close();
        Log.d("body", htmlBody);
        return htmlBody;
	}
	
	public void parseData(String str) throws JSONException {
		JSONTokener jsonTokener = new JSONTokener(str);
		JSONObject jsonObjec = (JSONObject)jsonTokener.nextValue();
		Log.d("jsonObjec", jsonObjec.toString());
		JSONObject jsonObjec1 = (JSONObject)jsonTokener.nextValue();
		Log.d("jsonObjec", jsonObjec1.toString());
		JSONArray jsonObjec3 = (JSONArray)jsonTokener.nextValue();
		Log.d("jsonObjec", jsonObjec3.toString());
		length = arrayList.length();
		for(int i=0; i<length; i++) {
			JSONObject obj = arrayList.getJSONObject(i);
			Log.d("obj", obj.toString());
		}
	}
}
