package com.iBeiKe.InfoPortal.classes;

import java.util.Locale;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class RoomSearch extends Activity {
	private ComTimes times;
	private EditText editRoom;
	private long searchMillis;
	private int buildNum;
	private String[] builds;
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 0;
	
	private void getInitData() {
		Database db = new Database(this);
		db.read();
		builds = db.getString("cla_build", "name", null, null, 0);
		db.close();

        times = new ComTimes(this);
        searchMillis = System.currentTimeMillis();
        mYear = times.getYear();
        mMonth = times.getMonth();
        mDay = times.getDay();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_search);
		getInitData();

        ArrayAdapter<String> buildAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, builds);
        buildAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner buildSpinner = (Spinner)findViewById(R.id.build_spinner);
        buildSpinner.setAdapter(buildAdapter);
        buildSpinner.setOnItemSelectedListener(new MyOnBuildSelectedListener());
        buildSpinner.setSelection(0);
        
		editRoom = (EditText) findViewById(R.id.room_editor);
		Button btnOK = (Button) findViewById(R.id.room_ok);
		btnOK.setOnClickListener(new myOnButtonClicked());
		
        final Button dateButton = (Button) findViewById(R.id.room_date);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
	}

	private boolean isOutOfTerm(Database db, long searchMillis) {
		times.setTime(searchMillis);
		int yearMonthDay = times.getYear() * 10000
				+ times.getMonth() * 100 + times.getDay();
		String where = "_id=1";
		int[] termBegin = db.getInt("cla_time", "begin", where, null, 0);
		int[] termEnd = db.getInt("cla_time", "end", where, null, 0);
		if(yearMonthDay > termEnd[0] || yearMonthDay < termBegin[0]) {
			return true;
		} else {
			return false;
		}
	}
	
	public class myOnButtonClicked implements OnClickListener {
		public void onClick(View v) {
			String text = editRoom.getText().toString();
			if(text.length() == 3) {
				int roomNum = 0;
				times.setTime(searchMillis);
				String table = times.getNextDayInWeek(Locale.US);
				try {
					roomNum = Integer.parseInt(text);
				} catch(Exception e) {
					Log.e("MyOnButtonClicked Exception", e.toString());
				}
				String where = "build=" + buildNum + " AND room=" + roomNum;
				Database db = new Database(RoomSearch.this);
				db.read();
				if(isOutOfTerm(db, searchMillis)) {
					String message = getString(R.string.out_of_term);
					showAlert(message);
				} else {
					int[] id = db.getInt(table, "_id", where, null, 0);
					if(id != null) {
						Intent intent = new Intent();
						intent.setClass(RoomSearch.this, RoomInfo.class);
						Bundle bl = new Bundle();
						bl.putInt("id", id[0]);
						bl.putLong("timeMillis", searchMillis);
						intent.putExtras(bl);
						startActivityForResult(intent, 0);
					} else {
						String message = getString(R.string.room_search_none);
						showAlert(message);
					}
				}
				db.close();
			} else {
				String message = getString(R.string.room_search_error);
				showAlert(message);
			}
		}
	}

	private void showAlert(String message) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(message);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		dialog.show();
	}
	
    public class MyOnBuildSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	buildNum = pos + 1;
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
/*********************The date picker dialog****************************/
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear+1;
                mDay = dayOfMonth;
                updateDisplay();
            }
    };
        
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth-1, mDay);
        }
        return null;
    }
    
    private void updateDisplay() {
        StringBuilder searchDate = new StringBuilder();
        searchDate.append(mYear);
    	if(mMonth < 10)
    		searchDate.append("0");
    	searchDate.append(mMonth).append(mDay);
    	searchMillis = times.stringToMillis("yyyyMMdd", searchDate.toString());
    }
}
