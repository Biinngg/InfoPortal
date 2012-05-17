package com.iBeiKe.InfoPortal.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.iBeiKe.InfoPortal.database.Database;

import android.content.ContentValues;
import android.content.Context;

/**
 * 从api_url数据表中获取基本的api url，
 * 提供完整的url获取方法，将JSONObject转换为键值对，
 * 对指定url数据的获取以及大致格式化以剥离出纯净JSON数据。
 *
 */
public class iBeiKeApi {
	private Context context;
	private String table = "api_urls";
	private String baseUrl;
	
	public iBeiKeApi(Context context) {
		this.context = context;
		baseUrl = getBaseUrl();
	}
	
	private String getBaseUrl() {
		Database db = new Database(context);
		db.read();
		String[] result = db.getString(table, "value", "name=\'api\'", null, 0);
		db.close();
		return result[0];
	}

	public String getBaseUrl(String apiName) {
		String sel = "name=\'" + apiName + "\'";
		Database db = new Database(context);
		db.read();
		String[] result = db.getString(table, "value", sel, null, 0);
		db.close();
		String apiUrl = baseUrl + result[0];
		return apiUrl;
	}
	public String getApiUrl(String apiName, String userName, String passWord, String flag) {
		String apiUrl = getBaseUrl(apiName) + "?";
		apiUrl += "username=" + userName + "&passwd=" + passWord + "&flag=" + flag;
		return apiUrl;
	}
	
	public ContentValues converter(JSONObject obj) throws JSONException {
		Iterator iterator = obj.keys();
		ContentValues cv = new ContentValues();
		while(iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = obj.getString(key);
			cv.put(key, value);
		}
		return cv;
	}

	public String fetchData(String url) throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
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
        	} else if(tmp.contains("{")) {
        		htmlRet += tmp;
        		mark = true;
        	}
        }
        htmlRet.replace("</body>", "");
        String htmlBody = new String(htmlRet.getBytes("UTF-8"), "UTF-8");
        reader.close();
        return htmlBody;
	}
}
