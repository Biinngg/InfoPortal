package com.iBeiKe.InfoPortal.database;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Query extends Activity{
	private Database database;
	private int dbCounts;
	
	public Query() {
		database = new Database(this);
	}
	
    protected Cursor getCursor(String table_name,String[] columns, String selection, String orderBy) {
    	SQLiteDatabase db = database.getReadableDatabase();
    	Cursor result = db.query(table_name, columns, selection, null, null, null, orderBy);
    	startManagingCursor(result);
    	return result;
    }
    
    protected int[] getIntResults(Cursor cursor) {
    	dbCounts = cursor.getCount();
    	int[] queryResult = new int[dbCounts];
    	int i=0;
    	if(dbCounts != 0) {
    		while(cursor.moveToNext()) {
    			queryResult[i++] = cursor.getInt(0); 
    		}
    		return queryResult;
    	} else {
    		return null;
    	}
    }

    protected String[] getStringResults(Cursor cursor) {
    	dbCounts = cursor.getCount();
    	String[] queryResult = new String[dbCounts];
    	int i=0;
    	if(dbCounts != 0) {
    		while(cursor.moveToNext()) {
    			queryResult[i++] = cursor.getString(0); 
    		}
    		return queryResult;
    	} else {
    		return null;
    	}
    }
    
    public void dbClose() {
		database.close();
    }
}
