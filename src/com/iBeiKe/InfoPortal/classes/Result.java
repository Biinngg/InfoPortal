package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.database.ClassQuery;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;  

public class Result extends Activity {
	public static String TABLE_NAME;
	public static String CLASS_NAME;
	private static String WHERE_F;
	private static String WHERE;
	private static String WHERE1;
	private static String WHERE2;
	private static String ROOM_RESULT;
	public static boolean BUILD_NAME1;
	public static boolean BUILD_NAME2;
	public static int CLASS_NUM1;
	public static int CLASS_NUM2;
	public static int FLOOR_NUM1;
	public static int FLOOR_NUM2;
	public static int ROOM_NUM1;
	public static int ROOM_NUM2;
	public static int WEEK_NUM;
	private static String[] FROM;
	private String str_building;
	private String str_floor;
	private String str_classes;
	private long searchMillis;
	private Database database;
	private int result_number = 0;
	Bundle bl;
	Intent intent;
	Button btn;
	private int even;
	private int odd;
	private StringBuilder builder = new StringBuilder();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        database = new Database(this);
		
        //Use Intent to transfer key-value.
		intent=this.getIntent();
		bl=intent.getExtras();
		CLASS_NUM1 = bl.getInt("class_num1");
		CLASS_NUM2 = bl.getInt("class_num2");
		BUILD_NAME1 = bl.getBoolean("build_name1");
		BUILD_NAME2 = bl.getBoolean("build_name2");
		FLOOR_NUM1 = bl.getInt("floor_num1");
		FLOOR_NUM2 = bl.getInt("floor_num2");
		TABLE_NAME = bl.getString("table_name");
		searchMillis = bl.getLong("search_millis");
		WEEK_NUM = 1 << (5-1);
		FROM = new String[]{ "room"};
		
		ClassQuery query = new ClassQuery();
		query.setQuery(FLOOR_NUM1, FLOOR_NUM2, CLASS_NUM1, CLASS_NUM2, BUILD_NAME1, BUILD_NAME2, searchMillis);
		
		//Create SQL query WHERE and the search result title
		if(FLOOR_NUM1 > FLOOR_NUM2) {
			int swap;
			swap = FLOOR_NUM1;
			FLOOR_NUM1 = FLOOR_NUM2;
			FLOOR_NUM2 = swap;
		}
		ROOM_NUM1 = FLOOR_NUM1 * 100;
		ROOM_NUM2 = (FLOOR_NUM2 + 1) * 100;
		if(FLOOR_NUM1 == 0) {
			WHERE_F = "";
			str_floor = "";
		}
		else {
			WHERE_F = "room > " + ROOM_NUM1 + " and room < " + ROOM_NUM2 + " and ";
			if(FLOOR_NUM1 != FLOOR_NUM2)
				str_floor = FLOOR_NUM1 +"�㵽" + FLOOR_NUM2 +"��,";
			else
				str_floor = FLOOR_NUM1 + "��";
		}
		
		if(CLASS_NUM1 > CLASS_NUM2 ) {
			int swap;
			swap = CLASS_NUM1;
			CLASS_NUM1 = CLASS_NUM2;
			CLASS_NUM2 = swap;
		}
		WHERE = WHERE_F;
		str_classes = "��" + CLASS_NUM1 + "��";
		if(CLASS_NUM1 != CLASS_NUM2) {
			for(int i=CLASS_NUM1; i<CLASS_NUM2; i++) {
				CLASS_NAME = "class" + i;
				WHERE += CLASS_NAME + " & " + WEEK_NUM + " = " + WEEK_NUM + " and ";
			}
			str_classes += "����" + CLASS_NUM2 + "��";
		}
		CLASS_NAME = "class" + CLASS_NUM2;
		WHERE += CLASS_NAME + " & " + WEEK_NUM + " = " + WEEK_NUM + " and ";
		
		//the search and result to build the string.
		if(!(BUILD_NAME1 || BUILD_NAME2)) {
			BUILD_NAME1 = true;
			BUILD_NAME2 = true;
		}
		if(BUILD_NAME1) {
			WHERE1 = WHERE + "build = 0 ";
			str_building = "\n�ݷ�¥";
			try{
				System.out.println("the original where1 "+WHERE1);
				System.out.println(TABLE_NAME);
				Cursor cursor = getEvents(TABLE_NAME,WHERE1);
				result_number += cursor.getCount();
				builder.append(str_building).append(str_floor).append(str_classes).append("\n");
				builder.append(showEvents(cursor,true)).append("\n");
				//builder.append(showRelative(WHERE_F + " build = 0 and "));
			} finally {
				database.close();
			}
		}
		if(BUILD_NAME2) {
			WHERE2 = WHERE + "build = 1 ";
			str_building = "\n��ѧ¥";
			try{
				System.out.println("the original where2 "+WHERE2);
				Cursor cursor = getEvents(TABLE_NAME, WHERE2);
				result_number += cursor.getCount();
				builder.append(str_building).append(str_floor).append(str_classes).append("\n");
				builder.append(showEvents(cursor,true)).append("\n");
				//builder.append(showRelative(WHERE_F + " build = 1 and "));
			} finally {
				database.close();
			}
		}
    	// Display on the screen
    	TextView text = (TextView) findViewById(R.id.text);
    	text.setText(builder);
    }

    public Cursor getEvents(String table_name, String selection) {
    	SQLiteDatabase db = database.getReadableDatabase();
    	Cursor cursor = db.query(table_name, FROM, selection, null, null, null, " room ASC ");
    	startManagingCursor(cursor);
    	return cursor;
    }
    
    public String showEvents(Cursor cursor, boolean mode) {
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
	    	if(((even - odd) > 50 || (odd - even) > 50) && mode)
	    		ROOM_RESULT += "\n\n";
	    	ROOM_RESULT += room + "    ";
	    	i++;
    	}
		return ROOM_RESULT;
    }
/*    
    private String showRelative(String where) {
    	String results = "�����ϰ��";
    	String str_class1 = "";
    	String str_class2 = "";
    	int result_number = 0;
    	int first_num;
    	String first_result = "";
    	String second_result = "";
    	String WHERE;
		int class_max = CLASS_NUM2;
		while(result_number <= 10) {
	    	String where_fir = where;
	    	String where_sec = where;
			class_max--;
			str_class1 = "\n��" + CLASS_NUM1 + "��";
			if(CLASS_NUM1 > class_max) {
				break;
			}
			if(CLASS_NUM1 < class_max) {
				for(int i=CLASS_NUM1; i<class_max; i++) {
					CLASS_NAME = "class" + i;
					where_fir += CLASS_NAME + " & " + WEEK_NUM + " = " + WEEK_NUM + " and ";
				}
				str_class1 += "����" + class_max + "��";
			}
			CLASS_NAME = "class" + class_max;
			where_fir += CLASS_NAME + " & " + WEEK_NUM + " = " + WEEK_NUM ;
			
			try{
				Cursor cursor = getEvents(TABLE_NAME, where_fir);
				first_num = cursor.getCount();
				result_number += cursor.getCount();
				first_result = showEvents(cursor,false);
			} finally {
				database.close();
			}
			
			int class_begin = class_max+1;
			str_class2 = "\n��" + class_begin + "��";
			if(class_begin < CLASS_NUM2) {
				for(int i=class_begin; i<CLASS_NUM2; i++) {
					CLASS_NAME = "class" + i;
					where_sec += CLASS_NAME + " & " + WEEK_NUM + " = " + WEEK_NUM + " and ";
				}
				str_class2 += "����" + CLASS_NUM2 + "��";
			}
			CLASS_NAME = "class" + CLASS_NUM2;
			where_sec +=  CLASS_NAME + " & " + WEEK_NUM + " = " + WEEK_NUM;
			
			try{
				Cursor cursor = getEvents(TABLE_NAME, where_sec);
				second_result = showEvents(cursor,false);
			} finally {
				database.close();
			}
			results += str_class1 + "\n" + first_result + "\n" + str_class2 + "\n" + second_result;
		}
		return results;
    }
   */
    public void onPause() {
    	super.onPause();
    	finish();
    }
}