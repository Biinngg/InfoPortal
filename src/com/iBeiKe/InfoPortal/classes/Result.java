package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;  

public class Result extends Activity {
	private Database db;
	private long searchMillis;
	private int buiSelection;
	private int floorNum1;
	private int floorNum2;
	private int classNum1;
	private int classNum2;
	private int weekInTerm;
	private String titleText;
	private String tableName;
	private ResultListAdapter adapter;
	private boolean isVertical;

	private boolean isVertical() {
        if(this.getResources().getConfiguration().orientation
        		== Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        } else {
        	return true;
        }
	}
	
	private void getInitData(long searchMillis) {
		int start = 0;
		ComTimes ct = new ComTimes();
		ct.setTime(searchMillis);
		String[] columns = new String[]{"name", "period", "begin"};
		String where = "_id=1";
		Cursor cursor = db.getCursor("cla_time", columns, where, null);
		if(cursor.moveToFirst()) {
			titleText = cursor.getString(0);
			titleText += " " + cursor.getString(1);
			start = cursor.getInt(2);
		}
		weekInTerm = ct.getWeekInTerm(start);
		titleText += " 第" + weekInTerm + "周";
    	tableName = ct.getDayInWeek(java.util.Locale.US);
    	isVertical = isVertical();
	}
	
	private void showResult() {
		int filter = 1;
		String where = "";
		String[] col = new String[]{"_id","room"};
		int binWeek = 1 << (weekInTerm - 1);
		for(int i=classNum1;i<=classNum2;i++) {
			where += "class" + i + " & " + binWeek + "=" + binWeek + " AND ";
		}
		if(floorNum1 == floorNum2) {
			int floor = floorNum1 * 100;
			where += "room=" + floor + " AND ";
		} else {
			int floor1 = floorNum1 * 100;
			int floor2 = floorNum2 * 100;
			where += "room>" + floor1 + " AND room<" + floor2 + " AND ";
		}
		for(int i=1; filter<buiSelection; i++) {
			int res = buiSelection & filter;
			if(res == filter) {
				String selection = where + "build=" + i;
				String build = "_id=" + i;
				Log.d("sql", "Select " + col.toString() + " from " + tableName + " where " + selection);
				resultQuery(build, tableName, col, selection, "room ASC");
			}
			filter = filter << 1;
		}
	}
	
	private void resultQuery(String building, String tableName,
			String[] column, String selection, String orderBy) {
		String[] buil = db.getString("cla_build", "name", building, null, 0);
		Cursor cursor = db.getCursor(tableName, column, selection, orderBy);
		int length = cursor.getCount();
		int num, floor = 1;
		if(isVertical) {
			num = 4;
		} else {
			num = 8;
		}
		int[] id = new int[num], room = new int[num];
		for(int i=0; i<length; i++) {
			cursor.moveToNext();
			id[i%num] = cursor.getInt(0);
			room[i%num] = cursor.getInt(1);
			if(i == 0) {
				adapter.setData(buil[0], id, room);
			} else 	if(i%num == 0) {
				adapter.setData(null, id, room);
				id = new int[num];
				room = new int[num];
			}
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        //Use Intent to transfer key-value.
		Intent intent=this.getIntent();
		Bundle bl=intent.getExtras();
		searchMillis = bl.getLong("searchMillis");
		buiSelection = bl.getInt("buiSelection");
		floorNum1 = bl.getInt("floorNum1");
		floorNum2 = bl.getInt("floorNum2");
		classNum1 = bl.getInt("classNum1");
		classNum2 = bl.getInt("classNum2");

		TextView title = (TextView)findViewById(R.id.class_result_title);
        ListView listView = (ListView) findViewById(R.id.class_result_list);
        adapter = new ResultListAdapter(this);
        db = new Database(this);
        db.read();
        getInitData(searchMillis);
        showResult();
        db.close();
        title.setText(titleText);
        listView.setAdapter(adapter);
    }
}