package com.iBeiKe.InfoPortal.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.R.id;
import com.iBeiKe.InfoPortal.R.layout;
import com.iBeiKe.InfoPortal.common.*;
import com.iBeiKe.InfoPortal.database.Database;

import static android.provider.BaseColumns._ID;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class CurrentClasses extends Activity {
	public static String TABLE_NAME;
	private static String WHERE;
	private static String WHERE1;
	private static String WHERE2;
	private static String ROOM_RESULT;
	public static int WEEK_NUM;
	private static String[] FROM;
	private String str_building1;
	private String str_building2;
	private String str_title;
	private int week_in_term;
	private Database database;
	private int even;
	private int odd;
	private StringBuilder builder = new StringBuilder();
	private String start;
	private String year;
	private int term;
	private int hour_minuts;
	private int day_of_week;
	private int class_num;
	private SimpleDateFormat woy;
	private SimpleDateFormat time;
	ComTimes times = new ComTimes();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        database = new Database(this,null,null);
        databaseStatus();
        currentStatus();
		WEEK_NUM = 1 << (week_in_term-1);
		FROM = new String[]{ "room"};
		str_title = year + "���"+ term + "ѧ�ڵ�" + week_in_term + "��\n" 
				+ times.getDayInWeek(null) + "��" + class_num + "��\n";
		builder.append(str_title);
		WHERE = "class" + class_num + " & " + WEEK_NUM + " = " + WEEK_NUM;
		
		//the search and result to build the string.
		WHERE1 = WHERE + " and build = 0 ";
		str_building1 = "\n�ݷ�¥ û�пεĽ���Ϊ:\n";
		WHERE2 = WHERE + " and build = 1 ";
		str_building2 = "\n��ѧ¥ û�пεĽ���Ϊ:\n";
		System.out.println(WHERE1);
		System.out.println(WHERE2);
		System.out.println(TABLE_NAME);
		try{
			Cursor cursor1 = getEvents(TABLE_NAME,FROM, WHERE1, " room ASC");
			builder.append(str_building1).append(showEvents(cursor1)).append("\n");
			
			Cursor cursor2 = getEvents(TABLE_NAME,FROM, WHERE2, " room ASC");
			builder.append(str_building2).append(showEvents(cursor2));
		} finally {
			database.close();
		}
		
    	// Display on the screen
    	TextView text = (TextView) findViewById(R.id.text);
    	text.setText(builder);
    }

    public Cursor getEvents(String table_name, String[] from, String selection, String orderby) {
    	SQLiteDatabase db = database.getReadableDatabase();
    	Cursor cursor = db.query(table_name, from, selection, null, null, null, orderby);
    	startManagingCursor(cursor);
    	return cursor;
    }
    
    public String showEvents(Cursor cursor) {
    	// Stuff them all into a big string
    	ROOM_RESULT = "\n";
    	if(cursor.getCount() == 0) {
    		ROOM_RESULT += "û�з�ϲ�ѯҪ��Ľ��\n\n";
    	}
		int i = 0;
    	while (cursor.moveToNext()) {
    		int room = cursor.getInt(0);
	    	if(i % 2 == 0) {
	    		even = room;
	    		if(i == 0)
	    			odd=room;
	    	}
	    	else
	    		odd = room;
	    	if((even - odd) > 50 || (odd - even) > 50)
	    		ROOM_RESULT += "\n\n";
	    	ROOM_RESULT += room + "    ";
	    	i++;
    	}
		return ROOM_RESULT;
    }
    
    public void databaseStatus() {
    	String table_name = "info";
    	String[] from = new String[]{"revision", "begin"};
    	Cursor cursor = getEvents(table_name,from, null, null);
    	while(cursor.moveToNext()) {
    		String revision = cursor.getString(0);
    		year = revision.substring(0, 4) + "~" + revision.substring(4, 8);
    		term = revision.charAt(9) - 48;
    		start = cursor.getInt(1) + "";
    	}
    }
    
    public void currentStatus() {
    	long time_millis = System.currentTimeMillis();
        final Calendar c = Calendar.getInstance();
        day_of_week = c.get(Calendar.DAY_OF_WEEK);
        woy = new SimpleDateFormat("w");
        time = new SimpleDateFormat("kkmm");
        try {
			long start_millis = new SimpleDateFormat("yyyyMMdd").parse(start).getTime();
			long week_minus = time_millis - start_millis;
			String current_time = time.format(time_millis);
			//week_in_term = (int) (week_minus/604800000) + 1;
			hour_minuts = Integer.parseInt(current_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		week_in_term = times.getWeekInTerm();
		String TABLE_Class = "class";
		String[] FROM = new String[]{ _ID };
		String WHERE = hour_minuts + " >= begin and " + hour_minuts + " <= end";
		Cursor cursor = getEvents(TABLE_Class,FROM, WHERE, null);
		if(cursor.getCount() == 0) {
			if(hour_minuts > 1200 && hour_minuts < 2400)
				day_of_week++;
			class_num = 1;
		}
		while(cursor.moveToNext()) {
			class_num = cursor.getInt(0);
		}
        if(day_of_week > 6 || day_of_week < 2) {
        	week_in_term++;
        	day_of_week = 2;
        	class_num = 1;
        }
        //TABLE_NAME = weeks[--day_of_week];
        TABLE_NAME = times.getDayInWeek(java.util.Locale.US);
    }
}