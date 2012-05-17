package com.iBeiKe.InfoPortal.news;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

import com.iBeiKe.InfoPortal.database.Database;

/**
 * 意见建议与信息通知的数据库辅助类。
 * 提供对数据库的访问，为listView中的cursor提供更新，
 * 获取数据库中的意见建议与信息通知内容条数，
 * 获取存储的意见建议、信息通知数据以及提供对从服务器获取到的数据的保存功能。
 *
 */
public class NewsHelper {
	private Context context;
	private SQLiteCursor cursor;
	int num = 0;

	public NewsHelper(Context context) {
		this.context = context;
	}
    
    public SQLiteCursor updateNewsCursor(String table, String[] columns) {
    	Database db = new Database(context);
    	db.read();
    	cursor = db.getCursor(table, columns, null, null);
    	Log.d("cursor count", cursor.getCount() + "");
    	db.close();
    	return cursor;
    }
    
    public Map<String,String> getNewsData(int position, String[] columns) {
    	cursor.moveToPosition(position);
    	Map<String,String> map = new HashMap<String,String>();
    	int length = columns.length;
    	for(int i=0;i<length;i++) {
    		String value = cursor.getString(i);
    		map.put(columns[i], value);
    	}
    	return map;
    }
    
    public int getNewsCount(String table, String[] columns) {
    	Database db = new Database(context);
    	SQLiteCursor cursor;
    	db.read();
    	cursor = db.getCursor(table, columns, null, null);
    	int num = cursor.getCount();
    	db.close();
    	return num;
    }
	
	public void saveContentValues(ContentValues cv, String tableName) {
		Database db = new Database(context);
		db.write();
		if(num == 0) {
			db.clean(tableName);
			num++;
		}
		db.insert(tableName, cv);
		db.close();
	}
	
	public void saveMap(Map<String,String> map, String tableName) {
		ContentValues cv = new ContentValues();
		for(String key: map.keySet()) {
			cv.put(key, map.get(key));
		}
		saveContentValues(cv, tableName);
	}
}
