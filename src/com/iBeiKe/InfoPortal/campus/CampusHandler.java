package com.iBeiKe.InfoPortal.campus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.common.LoginHelper;
import com.iBeiKe.InfoPortal.common.iBeiKeApi;
import com.iBeiKe.InfoPortal.database.Database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class CampusHandler extends iBeiKeApi {
	private Context context;
	private iBeiKeApi api;
	private String spaFilter = "\\S";
	private String retFilter = "[^\\r]";
	private String intFilter = "\\d";
	
	public CampusHandler(Context context) {
		super(context);
		this.context = context;
		api = new iBeiKeApi(context);
	}
	
	public String getApiUrl() {
		LoginHelper login = new LoginHelper(context);
		ContentValues cv = login.getLoginData("card_info");
		String userName = cv.getAsString("user");
		String passWord = cv.getAsString("passwd");
		String flag = cv.getAsString("type");
		return getApiUrl("camp_info", userName, passWord, flag);
	}
	
	public String fetchData() {
		String htmlBody = "";
		try {
			htmlBody = fetchData(getApiUrl());
		} catch (Exception e) {
			Log.e("CampusHandler.fetchData()", e.toString());
		}
		return htmlBody;
	}

	public void parseAndSave(String string) throws JSONException {
		Database db = new Database(context);
		db.write();
		JSONTokener jsonTokener = new JSONTokener(string);
		JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
		ContentValues cv = api.converter(jsonObject);
		jsonObject = (JSONObject)jsonTokener.nextValue();
		cv.putAll(api.converter(jsonObject));
		cv = parseInfo(cv);
		saveContentValues(cv, "camp_info");
		JSONArray jsonArray = (JSONArray)jsonTokener.nextValue();
		int length = jsonArray.length();
		for(int i=0; i<length; i++) {
			jsonObject = jsonArray.getJSONObject(i);
			cv = api.converter(jsonObject);
			cv = parseDetail(cv);
			saveContentValues(cv, "camp_detail");
		}
		db.close();
	}
	
	private ContentValues parseInfo(ContentValues cv) {
		String[] keys = new String[]{"name", "id", "cardid",
				"currentState", "disableState"};
		for(int i=0; i<keys.length; i++) {
			String value = cv.getAsString(keys[i]);
			value = filter(value, spaFilter);
			value = filter(value, retFilter);
			cv.put(keys[i], value);
		}
		return cv;
	}
	
	private ContentValues parseDetail(ContentValues cv) {
		String[] keys = new String[]{"time", "place", "cost", "left"};
		ComTimes cm = new ComTimes(context);
		String type = "yyyyMMddkkmmss";
		for(int i=0; i<keys.length; i++) {
			String value = cv.getAsString(keys[i]);
			value = filter(value, spaFilter);
			value = filter(value, retFilter);
			switch(i) {
			case 0:
				value = filter(value, intFilter);
				cv.put(keys[i], cm.stringToMillis(type, value));
				break;
			case 1:
				cv.put(keys[i], value);
				break;
			default:
				value = filter(value, intFilter);
				cv.put(keys[i], Integer.parseInt(value));
				break;
			}
		}
		return cv;
	}
	
	public void saveContentValues(ContentValues cv, String tableName) {
		Database db = new Database(context);
		db.write();
		db.clean(tableName);
		db.insert(tableName, cv);
		db.close();
	}
	
	public String filter(String src, String filter) {
		String result = "";
        Pattern pattern = Pattern.compile(filter);
        Matcher matcher  = pattern.matcher(src);
        while (matcher.find()) {
        	result += matcher.group();
		}
        return result;
	}
}
