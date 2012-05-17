package com.iBeiKe.InfoPortal.library;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.iBeiKe.InfoPortal.database.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

/**
 * 图书搜索的数据库类。
 * 提供对数据库的访问，为listView中的cursor提供更新，
 * 获取数据库中的图书搜索结果，
 * 获取存储的图书搜索结果数据以及提供对搜索结果的保存功能。
 *
 */
public class BookHelper {
	private Context context;
	private SQLiteCursor cursor;
	int num;
	
	public BookHelper(Context context) {
		this.context = context;
		Database db = new Database(context);
		db.write();
		db.clean(Book.tableList);
		db.close();
		num = 0;
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
    
    /**
     * 在获取数量时需要和getNewsData使用同一个cursor,否则会在listView中出现Count变大而cursor并没有增大的情况。
     * @param table
     * @param columns
     * @return
     */
    public int getNewsCount(String table, String[] columns) {
    	int num = cursor.getCount();
    	return num;
    }
    
    public URL getUrl(String table, String type) {
    	Database db = new Database(context);
    	String selection = "name=" + type;
    	db.read();
    	String result = db.getString(table, "value", selection, null, 0)[0];
    	db.close();
    	URL url = null;
		try {
			url = new URL(result);
		} catch (MalformedURLException e) {
			Log.e("BookSearchHelper.getUrl", e.toString());
		}
    	return url;
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
