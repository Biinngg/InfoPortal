package com.iBeiKe.InfoPortal.library;

import java.util.Map;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Library extends Activity implements Runnable{
	private ComTimes ct;
	private String timeTodayText;
	private String timeTomorrowText;
	private Map<String,String> contentValues;
	private LoginHelper loginHelper;
    private EditText txt, userName, password;
    private ImageButton btn;
    private ListView borrowList;
    private BooksListAdapter borrowAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        getInitData();
        TextView timeToday = (TextView)findViewById(R.id.library_time_today);
        TextView timeTomorrow = (TextView)findViewById(R.id.library_time_tomorrow);
        timeToday.setText(timeTodayText);
        timeTomorrow.setText(timeTomorrowText);
        borrowList = (ListView)findViewById(R.id.library_borrow_list);
        borrowAdapter = new BooksListAdapter(this);
        borrowList.setAdapter(borrowAdapter);
        txt = (EditText)findViewById(R.id.search_edit);
        txt.setFocusable(false);
        txt.setOnClickListener(new SearchClickListener());
        btn = (ImageButton)findViewById(R.id.search);
        btn.setOnClickListener(new SearchClickListener());
        Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new ZxingSearchClickListener());
        
        RelativeLayout loginLayout = (RelativeLayout)findViewById(R.id.lib_login);
        if(contentValues == null) {
        	loginLayout.setVisibility(View.VISIBLE);
            userName = (EditText)findViewById(R.id.lib_username);
            password = (EditText)findViewById(R.id.lib_password);
            Button login = (Button)findViewById(R.id.lib_login_button);
            login.setOnClickListener(new LoginClickListener());
        } else {
        	Log.d("content value", contentValues.toString());

	        Thread thread = new Thread(this);
	        thread.start();
        }

        //ContentValues cv = loginHelper.getLoginData();
        //cv.toString();
    }
    
    private void getInitData() {
    	ct = new ComTimes(this);
    	loginHelper = new LoginHelper(this);
    	int yearMonthDay = ct.getYear() * 10000 + ct.getMonth() * 100 + ct.getDay();
    	timeTodayText = getOpenTime(yearMonthDay);
    	Log.d("getInitData", timeTodayText);
    	ct.moveToNextDays(1);
    	yearMonthDay = ct.getYear() * 10000 + ct.getMonth() * 100 + ct.getDay();
    	timeTomorrowText = getOpenTime(yearMonthDay);
    	contentValues = loginHelper.getLoginData();
    }
    
    private String getOpenTime(int yearMonthDay) {
    	Database db = new Database(this);
    	db.read();
    	String where = "begin<=" + yearMonthDay + " AND end>=" + yearMonthDay;
    	String[] timeTexts = db.getString("lib_time", "value", where, "_id DESC", 0);
    	if(timeTexts == null) {
    		int dayInWeek = ct.getDayInWeek();
    		Log.d("Library getOpenTime", "day in week is " + dayInWeek);
    		where = "begin<=" + dayInWeek + " AND end>=" + dayInWeek;
    		timeTexts = db.getString("lib_time", "value", where, "_id DESC", 0);
    	}
    	String timeText = timeTexts[0];
    	if(timeText.contains(",")) {
    		timeText = timeText.replace(",", "\n");
    	}
    	db.close();
    	return timeText;
    }
    
    class LoginClickListener implements OnClickListener {
    	public void onClick(View v) {
	        String userString = userName.getText().toString();
	        String passString = password.getText().toString();
	        if(userString != null && passString != null)
	        	loginHelper.saveLoginData(userString, passString);
    	}
    }
    
    class SearchClickListener implements OnClickListener {
    	public void onClick(View v) {
    		Intent intent = new Intent();
    		intent.setClass(Library.this, BookSearch.class);
    		startActivityForResult(intent,0);
    	}
    }
    
    class ZxingSearchClickListener implements OnClickListener {
    	public void onClick(View v) {
    		Intent intent = new Intent();
    		intent.setClass(Library.this, com.google.zxing.client.android.CaptureActivity.class);
    		startActivityForResult(intent,0);
    	}
    }
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
    	}
	};
	
	public void run() {
		if(!Thread.interrupted()) {
			MyLibraryFetcher mlf = new MyLibraryFetcher(
					"http://lib.ustb.edu.cn:8080/reader/book_lst.php", contentValues);
			try {
				mlf.fetchData();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}
}