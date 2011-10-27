package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.About;
import com.iBeiKe.InfoPortal.ErrorHandler;
import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.Settings;
import com.iBeiKe.InfoPortal.common.*;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class Search extends Activity {
	private String tableName;
	private int weekInTerm;
	private StringBuilder searchDate;
    private ComTimes times;
	private long searchMillis;
	private boolean build1;
	private boolean build2;
	private int floor_num1;
	private int floor_num2;
	private int class_num1;
	private int class_num2;
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes); 
        
        times = new ComTimes();
        searchMillis = System.currentTimeMillis();
        mYear = times.getYear();
        mMonth = times.getMonth();
        mDay = times.getDay();
        
        final CheckBox checkbox1 = (CheckBox) findViewById(R.id.build_box1);
        checkbox1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	build1 = ((CheckBox) v).isChecked();
            }
        });
        final CheckBox checkbox2 = (CheckBox) findViewById(R.id.build_box2);
        checkbox2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	build2 = ((CheckBox) v).isChecked();
            }
        });

        Spinner floor_spinner1 = (Spinner) findViewById(R.id.floor_spinner1);
        ArrayAdapter<CharSequence> floor_adapter1 = ArrayAdapter.createFromResource(
                this, R.array.floor_array, android.R.layout.simple_spinner_item);
        floor_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floor_spinner1.setAdapter(floor_adapter1);
        floor_spinner1.setOnItemSelectedListener(new MyOnFloorSelectedListener1());
        
        Spinner floor_spinner2 = (Spinner) findViewById(R.id.floor_spinner2);
        ArrayAdapter<CharSequence> floor_adapter2 = ArrayAdapter.createFromResource(
                this, R.array.floor_array, android.R.layout.simple_spinner_item);
        floor_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floor_spinner2.setAdapter(floor_adapter2);
        floor_spinner2.setOnItemSelectedListener(new MyOnFloorSelectedListener2());
        
        Spinner class_spinner1 = (Spinner) findViewById(R.id.class_spinner1);
        ArrayAdapter<CharSequence> class_adapter1 = ArrayAdapter.createFromResource(
                this, R.array.class_array, android.R.layout.simple_spinner_item);
        class_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        class_spinner1.setAdapter(class_adapter1);
        class_spinner1.setOnItemSelectedListener(new MyOnClassSelectedListener1());

        Spinner class_spinner2 = (Spinner) findViewById(R.id.class_spinner2);
        ArrayAdapter<CharSequence> class_adapter2 = ArrayAdapter.createFromResource(
                this, R.array.class_array, android.R.layout.simple_spinner_item);
        class_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        class_spinner2.setAdapter(class_adapter2);
        class_spinner2.setOnItemSelectedListener(new MyOnClassSelectedListener2());
        
        final Button search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(searchMillis != 0) {
            		times.setTime(searchMillis);
            	}
            	weekInTerm = times.getWeekInTerm();
            	tableName = times.getDayInWeek(java.util.Locale.US);

        		if(("Sat".equals(tableName)) || ("Sun".equals(tableName))) {
        			int ErrorNum = 2;
        			Intent intent = new Intent();
        			intent.setClass(Search.this, ErrorHandler.class);
        			Bundle bl = new Bundle();
        			bl.putInt("ErrorNum", ErrorNum);
        			intent.putExtras(bl);
        			startActivity(intent);
        		} else {
					//閫氳繃intent璺宠浆鍒颁笅涓�釜椤甸潰.
					Intent intent = new Intent();
					intent.setClass(Search.this, Result.class);
					//閫氳繃Bundle鏉ヨ幏鍙栨暟鎹�閫氳繃key-Value鐨勬柟寮忔斁鍏ユ暟鎹�
					Bundle bl = new Bundle();
					bl.putInt("class_num1", class_num1);
					bl.putInt("class_num2", class_num2);
					bl.putBoolean("build_name1", build1);
					bl.putBoolean("build_name2", build2);
					bl.putInt("floor_num1", floor_num1);
					bl.putInt("floor_num2", floor_num2);
					bl.putLong("search_millis", searchMillis);
					bl.putString("table_name", tableName);
					//灏咮undle鏀惧叆Intent浼犲叆涓嬩竴涓狝ctivity
					intent.putExtras(bl);
					//璺冲埌涓嬩竴涓狝ctivity,骞朵笖绛夊緟鍏惰繑鍥炵粨鏋�
					startActivityForResult(intent, 0);
					//涓嶈兘澶熷湪杩欎釜Activity璋冪敤浜唖tartActivityForResult涔嬪悗璋冪敤finsh()
					//鍚﹀垯鏃犳硶鎺ユ敹鍒拌繑鍥�
        			}
                }
        });
        
        final Button date_button = (Button) findViewById(R.id.set_date);

        // add a click listener to the button
        date_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        final ImageButton roomSearch = (ImageButton) findViewById(R.id.search);
        roomSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Search.this, CurrentClasses.class);
				startActivityForResult(intent,0);
			}
		});
    }

/**********************The spinner selected listener***********************/
    public class MyOnFloorSelectedListener1 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	floor_num1 = pos;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }

    public class MyOnFloorSelectedListener2 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	floor_num2 = pos;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    public class MyOnClassSelectedListener1 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	class_num1 = pos + 1;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }

    public class MyOnClassSelectedListener2 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	class_num2 = pos + 1;
        }

        public void onNothingSelected(AdapterView parent) {
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
        searchDate = new StringBuilder();
        searchDate.append(mYear);
    	if(mMonth < 10)
    		searchDate.append("0");
    	searchDate.append(mMonth).append(mDay);
    	searchMillis = times.stringToMillis("yyyyMMdd", searchDate.toString());
    }
}