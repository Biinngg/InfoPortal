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

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * 校园卡消费记录与基本信息API数据的下载与解析。
 * 继承自iBeiKeApi，提供了对ApiUrl的获取，获取API接口数据，
 * 对接口数据解析并保存到数据库中，将消费记录与学生信息格式化为键值对、对数据的过滤。
 * 
 */ 
public class CampusHandler extends iBeiKeApi {
	private Context context;
	private CampusHelper helper;
	private String apiUrl;
	private String spaFilter = "\\S";
	private String retFilter = "[^\\r]";
	private String intFilter = "\\d";
	
	public CampusHandler(Context context) {
		super(context);
		this.context = context;
	}
	
	public String getApiUrl(String type) {
		String userName, passWord, flag;
		LoginHelper login = new LoginHelper(context);
		ContentValues cv = login.getLoginData(Campus.campInfoTable);
		if(cv == null) {
			apiUrl = null;
		} else {
			userName = cv.getAsString("user");
			passWord = cv.getAsString("passwd");
			if(type==null) {
				flag = cv.getAsString("type");
			} else {
				flag = type;
			}
			apiUrl = getApiUrl(Campus.campInfoTable, userName, passWord, flag);
		}
		return apiUrl;
	}
	
	public String fetchData() {
		String htmlBody = "";
		try {
			htmlBody = fetchData(apiUrl);
		} catch (Exception e) {
			Log.e("CampusHandler.fetchData()", e.toString());
		}
		return htmlBody;
	}

	public boolean parseAndSave(String string) throws JSONException {
		JSONTokener jsonTokener = new JSONTokener(string);
		JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
		ContentValues cv = converter(jsonObject);
		jsonObject = (JSONObject)jsonTokener.nextValue();
		cv.putAll(converter(jsonObject));
		cv = parseInfo(cv);
		if(cv == null) {
			return false;
		}
		helper = new CampusHelper(context);
		helper.saveContentValues(cv, Campus.campInfoTable);
		JSONArray jsonArray = null;
		try{
			jsonArray = (JSONArray)jsonTokener.nextValue();
		} catch(ClassCastException e) {
			e.printStackTrace();
			return true;
		}
		int length = jsonArray.length();
		for(int i=0; i<length; i++) {
			jsonObject = jsonArray.getJSONObject(i);
			cv = converter(jsonObject);
			cv = parseDetail(cv);
			helper.saveContentValues(cv, Campus.campDetailTable);
		}
		return true;
	}
	
	private ContentValues parseInfo(ContentValues cv) {
		ContentValues result = new ContentValues();
		String[] keys = Campus.campInfoColumns;
		if(cv.getAsString(keys[1]) == "null") {
			return null;
		}
		for(int i=0; i<keys.length; i++) {
			String value = cv.getAsString(keys[i]);
			value = filter(value, spaFilter);
			value = filter(value, retFilter);
			result.put(keys[i], value);
		}
		return result;
	}
	
	private ContentValues parseDetail(ContentValues cv) {
		ContentValues result = new ContentValues();
		String[] keys = Campus.campDetailColumns;
		ComTimes cm = new ComTimes(context);
		String type = "yyyyMMddkkmmss";
		for(int i=0; i<keys.length; i++) {
			String value = cv.getAsString(keys[i]);
			value = filter(value, spaFilter);
			value = filter(value, retFilter);
			switch(i) {
			case 0:
				value = filter(value, intFilter);
				result.put(keys[i], cm.stringToMillis(type, value));
				break;
			case 1:
				result.put(keys[i], value);
				break;
			default:
				value = filter(value, intFilter);
				result.put(keys[i], Integer.parseInt(value));
				break;
			}
		}
		return result;
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
