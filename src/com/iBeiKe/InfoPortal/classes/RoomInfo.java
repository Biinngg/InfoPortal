package com.iBeiKe.InfoPortal.classes;

import java.util.Locale;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class RoomInfo extends Activity {
	private Database db;
	private ComTimes ct;
	private String title;
	private String roomInfoTitle1;
	private long searchMillis;
	private int weekInTerm;
	private int buildNum;
	private int roomNum;
	private RoomInfoWeeklyAdapter stateAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_info);
		ListView listView = (ListView)findViewById(R.id.room_info_list);
		TextView titleView = (TextView)findViewById(R.id.header_title);
		TextView roomTitle1 = (TextView)findViewById(R.id.room_info_title1);
        Button button = (Button)findViewById(R.id.header_back);
        button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		finish();
        	}
        });
		stateAdapter = new RoomInfoWeeklyAdapter(this);
		
		db = new Database(this);
		db.read();
		getInitData();
		classState();
		db.close();
		listView.setAdapter(stateAdapter);
		titleView.setText(title);
		roomTitle1.setText(roomInfoTitle1);
		setShareButton();
	}
	
	private void setShareButton() {
		ImageButton shareButton = (ImageButton)findViewById(R.id.header_share);
		shareButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
		        Intent intent=new Intent(Intent.ACTION_SEND);
		        String text = "我在";
		        text += title;
		        intent.setType("text/plain");
		        intent.putExtra(Intent.EXTRA_SUBJECT, "iBeiKe InfoPortal");
		        intent.putExtra(Intent.EXTRA_TEXT, text);
		        startActivity(Intent.createChooser(intent, getTitle()));
			}
		});
	}
	
	private void getInitData() {
		Intent intent=this.getIntent();
		Bundle bl=intent.getExtras();
		searchMillis = bl.getLong("timeMillis");
		int id = bl.getInt("id");
		
		ct = new ComTimes(this);
		ct.setTime(searchMillis);
		weekInTerm = ct.getWeekInTerm(db);
		int month = ct.getMonth();
		int day = ct.getDay();
		String tableName = ct.getDayInWeek(Locale.US);
		ct.moveToNextDays(7);
		int monthNext = ct.getMonth();
		int dayNext = ct.getDay();
		roomInfoTitle1 = getString(R.string.room_info_title1);
		roomInfoTitle1 += "(" + month + "." + day
				+ " - " + monthNext + "." + dayNext + ")";
		
		String where = "_id=" + id;
		String[] columns = new String[] {"build", "room"};
		Cursor roomCursor = db.getCursor(tableName, columns, where, null);
		roomCursor.moveToNext();
		buildNum = roomCursor.getInt(0);
		roomNum = roomCursor.getInt(1);
		where = "_id=" + buildNum;
		String[] buildName = db.getString("cla_build",
				"name", where, null, 0);
		title = buildName[0] + roomNum;
	}
	
	private void classState() {
		Cursor classNum = db.getCursor("cla_time",
				new String[]{"_id"}, "period=1", null);
		String[] columns = new String[] {"_id"};
		for(int i=0; i<classNum.getCount()+1; i++) {
			int binWeekInTerm = 1 << weekInTerm-1;
			String[] row = new String[8];
			ct.setTime(searchMillis);
			for(int n=0; n<8; n++) {
				if(i==0) {
					if(n == 0) {
						row[n] = null;
					} else {
						row[n] = ct.getDayInWeek(Locale.US);
						ct.moveToNextDays(1);
					}
				} else if(n == 0) {
					row[n] = i + "";
				} else {
					String tableName = ct.getDayInWeek(Locale.US);
					ct.moveToNextDays(1);
					String selection = "build=" + buildNum
							+ " AND room=" + roomNum + " AND class" + i
							+ " & " + binWeekInTerm + "=" + binWeekInTerm;
					Log.d("RoomInfo SQL", "SELECT _id FROM " + tableName + " WHERE " + selection);
					Cursor state = db.getCursor(tableName,
							columns, selection, null);
					if(tableName.equals("Sun")) {
						binWeekInTerm <<= 1;
					}
					if(state.getCount() == 0) {
						row[n] = getString(R.string.have);
					} else {
						row[n] = getString(R.string.none);
					}
				}
				if(n == 7) {
					stateAdapter.setData(row);
				}
			}
		}
	}
	
}
