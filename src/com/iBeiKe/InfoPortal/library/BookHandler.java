package com.iBeiKe.InfoPortal.library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.iBeiKe.InfoPortal.common.iBeiKeApi;

/**
 * 图书搜索API数据的下载与解析。
 * 继承自iBeiKeApi，提供了对ApiUrl的更新，
 * 用以对图书搜索结果翻页，获取API接口数据，
 * 对接口数据解析并保存到数据库中，
 * 对数据进行格式化、转换为Unicode以及对数据组织为供数据库存储的键值对格式。
 *
 */
public class BookHandler extends iBeiKeApi {
	private Context context;
	private BookHelper helper;
	private int curPageNum = 0;
	private String apiUrl;
	private String spaFilter = "\\S";
	private String retFilter = "[^\\r]";
	
	public BookHandler(Context context) {
		super(context);
		this.context = context;
	}

	public String updateApiUrl(String nameOrISBN) {
		String baseUrl;
		baseUrl = getBaseUrl("lib_search") + "?";
		if(nameOrISBN.matches("\\d{13}")) {
			apiUrl = baseUrl + "ISBN=" + nameOrISBN;
		} else {
			apiUrl = baseUrl + "bookName=" + toHexString(nameOrISBN);
		}
		return apiUrl;
	}
	public String updateApiUrl(long ISBN) {
		String baseUrl;
		baseUrl = getBaseUrl("lib_search") + "?";
		apiUrl = baseUrl + "ISBN=" + ISBN;
		return apiUrl;
	}
    
    private String toHexString(String s) {
    	String str= "";
    	for(byte b: s.getBytes()) {
    		str += "%" + Integer.toHexString(b & 0XFF);
    	}
    	return str;
    }
	
	public String fetchData(int pageNum) {
		String htmlBody = "";
		this.curPageNum = pageNum;
		String page = "&page=" + pageNum;
		try {
			htmlBody = fetchData(apiUrl + page);
		} catch (Exception e) {
			Log.e("CampusHandler.fetchData()", e.toString());
		}
		return htmlBody;
	}

	public int parseAndSave(String string) throws JSONException {
		ContentValues cv;
		int pageNum;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		JSONTokener jsonTokener = new JSONTokener(decodeUnicode(string).toString());
		Object object = jsonTokener.nextValue();
		if(string.indexOf("[") != 0) {
			jsonObject = (JSONObject)object;
			if(jsonObject == null) {
				return -1;
			}
			cv = converter(jsonObject);
			pageNum = cv.getAsInteger("pageNum:");
			jsonArray = (JSONArray)jsonTokener.nextValue();
		} else {
			jsonArray = (JSONArray)object;
			pageNum = 1;
		}
		if(curPageNum > pageNum) {
			return 0;
		}
		if(curPageNum == 1) {
			helper = new BookHelper(context);
		}
		int length = jsonArray.length();
		for(int i=0; i<length; i++) {
			jsonObject = jsonArray.getJSONObject(i);
			cv = converter(jsonObject);
			cv = parseList(cv);
			helper.saveContentValues(cv, Book.tableList);
		}
		if(curPageNum == pageNum) {
			return 0;
		}
		return 1;
	}

    
	public static StringBuffer decodeUnicode(String dataStr) {
		dataStr=dataStr.replace("&#x", "\\u");
		final StringBuffer buffer = new StringBuffer();
		String tempStr = "";
		String operStr = dataStr;
		if (operStr != null && operStr.indexOf("\\u") == -1)
			return buffer.append(operStr);
		if (operStr != null && !operStr.equals("")
				&& !operStr.startsWith("\\u")) {
			tempStr = operStr.substring(0, operStr.indexOf("\\u"));
			operStr = operStr.substring(operStr.indexOf("\\u"), operStr
					.length());
		}
		buffer.append(tempStr);
		while (operStr != null && !operStr.equals("")
				&& operStr.startsWith("\\u")) {
			tempStr = operStr.substring(0, 6);
			operStr = operStr.substring(7, operStr.length());
			String charStr = "";
			charStr = tempStr.substring(2, tempStr.length());
			char letter = (char) Integer.parseInt(charStr, 16);
			buffer.append(new Character(letter).toString());
			if (operStr.indexOf("\\u") == -1) {
				buffer.append(operStr);
			} else { 
				tempStr = operStr.substring(0, operStr.indexOf("\\u"));
				operStr = operStr.substring(operStr.indexOf("\\u"), operStr
						.length());
				buffer.append(tempStr);
			}
		}
		return buffer;
	}
	
	private ContentValues parseList(ContentValues cv) {
		ContentValues result = new ContentValues();
		String[] keys = Book.columnsList;
		if(cv.getAsString(keys[1]) == "null") {
			return null;
		}
		for(int i=0; i<keys.length; i++) {
			String value = cv.getAsString(keys[i]);
			value = filter(value, spaFilter);
			value = filter(value, retFilter);
			value = value.replace("&nbsp;", " ");
			switch(i) {
			case 3:
			case 4:
				result.put(keys[i], Integer.parseInt(value));
				break;
			default:
				result.put(keys[i], value);
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
