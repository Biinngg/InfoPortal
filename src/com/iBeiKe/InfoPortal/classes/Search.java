package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.ErrorHandler;
import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.*;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.lib.BooksListAdapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class Search extends Activity {
	private int orientation;
	private Database db;
	private String where;
	private String[] build;
	private String[] floor;
	private String[] classes;
	private int buildNum = 1;
	private int curSelection = 0;
	private String tableName;
	private int weekInTerm;
	private StringBuilder searchDate;
    private ComTimes times;
	private long searchMillis;
	private boolean build1;
	private boolean build2;
	private int floorNum1;
	private int floorNum2;
	private int classNum1;
	private int classNum2;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int hourAndMin;
	static final int DATE_DIALOG_ID = 0;
	private Spinner floorSpinner1, floorSpinner2,
					classSpinner1, classSpinner2;
	private ArrayAdapter<String> floorAdapter1, floorAdapter2,
					classAdapter1, classAdapter2;
	
	private boolean isVertical() {
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        } else {
        	return true;
        }
	}
	
    private void getBuildCheckBox() {
    	CheckBox checkBox;
    	LinearLayout layoutH = null;
    	LinearLayout layoutV = new LinearLayout(this);
        LayoutParams hLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutParams vLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        boolean vertical = isVertical();
        for(int i=0;i<build.length;i++) {
        	if(vertical && i%2==0) {
        		if(layoutH != null) {
        			layoutV.addView(layoutH);
        		}
	        	layoutH = new LinearLayout(this);
	        	layoutH.setOrientation(LinearLayout.HORIZONTAL);
        	} else if(i%4==0){
        		if(layoutH != null) {
        			layoutV.addView(layoutH);
        		}
	        	layoutH = new LinearLayout(this);
	        	layoutH.setOrientation(LinearLayout.HORIZONTAL);
        	}
        	checkBox = new CheckBox(this);
        	checkBox.setId(i);
        	checkBox.setText(build[i]);
            checkBox.setTextColor(R.color.black);
            checkBox.setWidth(180);
            checkBox.setTextSize(18);
            checkBox.setSelected(true);
            checkBox.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	build1 = ((CheckBox) v).isChecked();
                }
            });
            layoutH.addView(checkBox, hLayoutParams);
        }
		layoutV.addView(layoutH);
        ScrollView listView = (ScrollView) findViewById(R.id.cla_build);
        listView.addView(layoutV);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes);
        getInitData();
        getBuildCheckBox();
        
        /*
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
        */

        floorSpinner1 = (Spinner) findViewById(R.id.floor_spinner1);
        floorAdapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, floor);
        floorAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner1.setAdapter(floorAdapter1);
        floorSpinner1.setOnItemSelectedListener(new MyOnFloorSelectedListener1());
        floorSpinner1.setSelection(0);
        
        floorSpinner2 = (Spinner) findViewById(R.id.floor_spinner2);
        floorAdapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, floor);
        floorAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner2.setAdapter(floorAdapter2);
        floorSpinner2.setOnItemSelectedListener(new MyOnFloorSelectedListener2());
        floorSpinner2.setSelection(floor.length - 1);
        
        classSpinner1 = (Spinner) findViewById(R.id.class_spinner1);
        classAdapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, classes);
        classAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner1.setAdapter(classAdapter1);
        classSpinner1.setOnItemSelectedListener(new MyOnClassSelectedListener1());
        classSpinner1.setSelection(curSelection);

        classSpinner2 = (Spinner) findViewById(R.id.class_spinner2);
        classAdapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, classes);
        classAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner2.setAdapter(classAdapter2);
        classSpinner2.setOnItemSelectedListener(new MyOnClassSelectedListener2());
        classSpinner2.setSelection(curSelection);
        
        final Button search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	times.setTime(searchMillis);
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
					Intent intent = new Intent();
					intent.setClass(Search.this, Result.class);
					Bundle bl = new Bundle();
					bl.putInt("class_num1", classNum1);
					bl.putInt("class_num2", classNum2);
					bl.putBoolean("build_name1", build1);
					bl.putBoolean("build_name2", build2);
					bl.putInt("floor_num1", floorNum1);
					bl.putInt("floor_num2", floorNum2);
					bl.putLong("search_millis", searchMillis);
					bl.putString("table_name", tableName);
					intent.putExtras(bl);
					startActivityForResult(intent, 0);
        			}
                }
        });
        
        final Button date_button = (Button) findViewById(R.id.set_date);
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
    
    private void getInitData() {
        times = new ComTimes();
        searchMillis = System.currentTimeMillis();
        hourAndMin = times.getHourAndMinute();
        
        db = new Database(this);
        db.read();
    	build = db.getString("cla_build", "name", null, null, 0);
    	int[] buildNums = db.getInt(
    			"cla_build", "_id", null, "floor_num DESC", 1);
    	where = "build=" + buildNums[0];
    	floor = db.getString("cla_floor", "name", where, null, 0);
    	where = "period=1";
    	classes = db.getString("cla_time", "name", where, null, 0);
    	
    	if(hourAndMin >= 2125 && hourAndMin < 2400) {
    		curSelection = 0;
    		searchMillis += 86400000;//86400000ms = 1day
    	} else if(hourAndMin < 2125){
    		String[] curClass = null;
    		for(int t=hourAndMin; curClass==null ||
    				curClass.length==0; t+=45) {
	        	where = "begin <= " + t + " AND end >= " + t;
	    		curClass = db.getString("cla_time", "name", where, null, 0);
    		}
    		for(String str : classes) {
    			if(str.equals(curClass[0])) {
    				break;
    			}
    			curSelection++;
    		}
    	} else {
    		curSelection = 0;
    	}
    	db.close();

    	times.setTime(searchMillis);
        mYear = times.getYear();
        mMonth = times.getMonth();
        mDay = times.getDay();
    }

/**********************The spinner selected listener***********************/
    public class MyOnFloorSelectedListener1 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	floorNum1 = pos;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }

    public class MyOnFloorSelectedListener2 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	floorNum2 = pos;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    public class MyOnClassSelectedListener1 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	classNum1 = pos + 1;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }

    public class MyOnClassSelectedListener2 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	classNum2 = pos + 1;
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