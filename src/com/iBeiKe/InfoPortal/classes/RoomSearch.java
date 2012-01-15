package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.R.id;
import com.iBeiKe.InfoPortal.R.layout;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class RoomSearch extends Activity {
	private RadioButton rbbuilding1;
	private RadioButton rbbuilding2;
	private EditText editRoom;
	private Database database;
	private Cursor cursor;
	private Button btnOK;
	private String TABLE_NAME;
	private int WEEK_NUM;
	private int week_in_term;
	private int room;
	private int building;
	private String[] FROM;
	private String WHERE;
	private String OrderBy;
	private int[] classes = new int[6];
	public String start = "20110911";//read from the database table info
	Bundle bl;
	Intent intent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_search);
        database = new Database(this);

		intent=this.getIntent();
		bl=intent.getExtras();
		//week_in_term = bl.getInt("week_in_term");
		//TABLE_NAME = bl.getString("table_name");
		WEEK_NUM = 1 << week_in_term;
		
		rbbuilding1 = (RadioButton) findViewById(R.id.radio_building1);
		rbbuilding2 = (RadioButton) findViewById(R.id.radio_building2);
		editRoom = (EditText) findViewById(R.id.text_edit);
		btnOK = (Button) findViewById(R.id.btn_OK);
		btnOK.setOnClickListener(new myOnRoomButtonClick());
	}
	public class myOnRoomButtonClick implements OnClickListener {
		public void onClick(View v) {
			try {
				room = Integer.parseInt(editRoom.getText().toString());
			} catch (Exception e) {
				room = 0;
			}
			if (rbbuilding1.isChecked()) {
				building = 0;
			} else {
				building = 1;
			}
			OrderBy = " room ASC ";
			FROM = new String[]{ "class1", "class2", "class3", "class4", "class5", "class6",};
			WHERE =  "room = '" + room + "' and build = " + building;
			cursor = getEvents(TABLE_NAME, FROM, WHERE, null, null, null, OrderBy);
			showEvents(cursor);
			database.close();
		}
	}
	
    private Cursor getEvents(String table, String[] columns, String selection, 
    		String[] selectionArgs, String groupBy, String having, String orderBy) {
    	SQLiteDatabase db = database.getReadableDatabase();
    	Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	startManagingCursor(cursor);
    	return cursor;
    }
    
    private void showEvents(Cursor cursor) {
    	// Stuff them all into a big string
    	StringBuilder builder = new StringBuilder();
    	if(cursor.getCount() == 0)
    		builder.append("\n娌℃湁绗﹀悎鏌ヨ瑕佹眰鐨勭粨鏋溿�\n");
    	else
    		builder.append("\n鏁欏 绗竴鑺�绗簩鑺�绗笁鑺�绗洓鑺�绗簲鑺�绗叚鑺俓n");
	    while (cursor.moveToNext()) {
	    	builder.append(room).append("   ");
	    	for(int i=0; i<6; i++) {
	    		classes[i] = cursor.getInt(i);
	    		String class_static;
	    		if((WEEK_NUM & classes[i]) == 0)
	    			class_static = "  鏈夎   ";
	    		else
	    			class_static = "  鏃犺   ";
	    		builder.append(class_static);
	    	}
	    }
	    builder.append("\n");
    	// Display on the screen
    	TextView text = (TextView) findViewById(R.id.room_result);
    	text.setText(builder);
    	//System.out.println(i);
    	}
}
