package com.iBeiKe.InfoPortal.classes;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.*;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class Search extends Activity {
	private String[] build;
	private String[] floor;
	private String[] classes;
	private int curSelection = 0;
	private int buiSelection = 0;
    private ComTimes times;
	private long searchMillis;
	private int floorNum1;
	private int floorNum2;
	private int classNum1;
	private int classNum2;
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes);
        getInitData();
        getBuildCheckBox();

        ArrayAdapter<String> floorAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, floor);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, classes);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        Spinner floorSpinner1 = (Spinner) findViewById(R.id.floor_spinner1);
        floorSpinner1.setAdapter(floorAdapter);
        floorSpinner1.setOnItemSelectedListener(new MyOnFloorSelectedListener1());
        floorSpinner1.setSelection(0);
        
        Spinner floorSpinner2 = (Spinner) findViewById(R.id.floor_spinner2);
        floorSpinner2.setAdapter(floorAdapter);
        floorSpinner2.setOnItemSelectedListener(new MyOnFloorSelectedListener2());
        floorSpinner2.setSelection(floor.length - 1);
        
        Spinner classSpinner1 = (Spinner) findViewById(R.id.class_spinner1);
        classSpinner1.setAdapter(classAdapter);
        classSpinner1.setOnItemSelectedListener(new MyOnClassSelectedListener1());
        classSpinner1.setSelection(curSelection);

        Spinner classSpinner2 = (Spinner) findViewById(R.id.class_spinner2);
        classSpinner2.setAdapter(classAdapter);
        classSpinner2.setOnItemSelectedListener(new MyOnClassSelectedListener2());
        classSpinner2.setSelection(curSelection);
        
        final Button search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(isOutOfTerm(searchMillis)) {
            		showAlert();
            	} else {
	            	times.setTime(searchMillis);

	        		if(floorNum1 > floorNum2) {
	        			int swap;
	        			swap = floorNum1;
	        			floorNum1 = floorNum2;
	        			floorNum2 = swap;
	        		}
	        		if(classNum1 > classNum2) {
	        			int swap;
	        			swap = classNum1;
	        			classNum1 = classNum2;
	        			classNum2 = swap;
	        		}
	
					Intent intent = new Intent();
					intent.setClass(Search.this, Result.class);
					Bundle bl = new Bundle();
					bl.putLong("searchMillis", searchMillis);
					bl.putInt("buiSelection", buiSelection);
					bl.putInt("floorNum1", floorNum1);
					bl.putInt("floorNum2", floorNum2);
					bl.putInt("classNum1", classNum1);
					bl.putInt("classNum2", classNum2);
					Log.d("search button", "searchMillis=" + searchMillis + " buiSelection=" + buiSelection
							+ " floorNum1=" + floorNum1 + " floorNum2=" + floorNum2 + " classNum1=" + classNum1
							+ " classNum2=" + classNum2);
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
				intent.setClass(Search.this, RoomSearch.class);
				startActivityForResult(intent,0);
			}
		});
    }
	
	private void showAlert() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		String message = getString(R.string.out_of_term);
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
	
	private boolean isOutOfTerm(long searchMillis) {
		Database db = new Database(this);
		ComTimes ct = new ComTimes(this);
		ct.setTime(searchMillis);
		int yearMonthDay = ct.getYear() * 10000
				+ ct.getMonth() * 100 + ct.getDay();
		String where = "_id=1";
		db.read();
		int[] termBegin = db.getInt("cla_time", "begin", where, null, 0);
		int[] termEnd = db.getInt("cla_time", "end", where, null, 0);
		db.close();
		if(yearMonthDay > termEnd[0] || yearMonthDay < termBegin[0]) {
			return true;
		} else {
			return false;
		}
	}
    
    private void getInitData() {
        Database db = new Database(this);
        times = new ComTimes(this);
        searchMillis = System.currentTimeMillis();
        int hourAndMin = times.getHourAndMinute();
        
        db.read();
    	build = db.getString("cla_build", "name", null, null, 0);
    	int[] buildNums = db.getInt(
    			"cla_build", "_id", null, "floor_num DESC", 1);
    	String where = "build=" + buildNums[0];
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
	
    private void getBuildCheckBox() {
    	LinearLayout layout = new LinearLayout(this);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        for(int i=0;i<build.length;i++) {
        	CheckBox checkBox = new CheckBox(this);
        	checkBox.setId(i);
        	checkBox.setText(build[i]);
            checkBox.setTextColor(R.color.black);
            checkBox.setWidth(180);
            checkBox.setTextSize(18);
            checkBox.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	boolean checked = ((CheckBox) v).isChecked();
                	int id = v.getId();
                	if(checked) {
                		buiSelection += 1 << id;
                	} else {
                		buiSelection -= 1 << id;
                	}
                }
            });
            layout.addView(checkBox, layoutParams);
        }
        HorizontalScrollView listView = (HorizontalScrollView) findViewById(R.id.cla_build);
        listView.addView(layout);
    }

/**********************The spinner selected listener***********************/
    public class MyOnFloorSelectedListener1 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	floorNum1 = pos + 1;
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }

    public class MyOnFloorSelectedListener2 implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	floorNum2 = pos + 1;
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
        StringBuilder searchDate = new StringBuilder();
        searchDate.append(mYear);
    	if(mMonth < 10)
    		searchDate.append("0");
    	searchDate.append(mMonth).append(mDay);
    	searchMillis = times.stringToMillis("yyyyMMdd", searchDate.toString());
    }
} 