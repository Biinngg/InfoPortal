package com.iBeiKe.InfoPortal.campus;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.common.MessageHandler;
import com.iBeiKe.InfoPortal.common.iBeiKeApi;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.library.BookSearch;
import com.iBeiKe.InfoPortal.library.Library;
import com.iBeiKe.InfoPortal.library.LibraryListAdapter;
import com.iBeiKe.InfoPortal.common.LoginHelper;
import com.iBeiKe.InfoPortal.library.MyLibraryFetcher;

public class Campus extends Activity implements Runnable{
	private Database db;
	private ComTimes ct;
	private String timeTodayText;
	private String timeTomorrowText;
	private Map<String,String> contentValues;
	private LoginHelper loginHelper;
    private EditText txt, userName, password;
    private ImageButton btn;
    private ListView borrowList;
    private RelativeLayout loginLayout;
	private LibraryListAdapter adapter;
	private ProgressBar bar;
	private TextView status;
	private Button login;
	private CampusHandler handler;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus);

        handler = new CampusHandler(this);
        
        
        loginLayout = (RelativeLayout)findViewById(R.id.lib_login);
        getLoginData();
        if(contentValues == null) {
        	relogin();
        } else {
        	Log.d("visibility", "gone");
	        Thread thread = new Thread(this);
	        thread.start();
        }
    }
    
    private void getInitData() {
    	LoginHelper login = new LoginHelper(this);
    	ContentValues cv = login.getLoginData("card_info");
    	if(cv == null) {
    		return;
    	} else {
    		
    	}
    	String userName = cv.getAsString("user");
    	String passWord = cv.getAsString("passwd");
    	String flag = cv.getAsString("type");
    	iBeiKeApi api = new iBeiKeApi(this);
    	api.getApiUrl("camp_info", userName, passWord, flag);
    }
    
    private void getCache() {
    	ct = new ComTimes(this);
    	int yearMonthDay = ct.getYear() * 10000 + ct.getMonth() * 100 + ct.getDay();
    	timeTodayText = getOpenTime(yearMonthDay);
    	Log.d("getInitData", timeTodayText);
    	ct.moveToNextDays(1);
    	yearMonthDay = ct.getYear() * 10000 + ct.getMonth() * 100 + ct.getDay();
    	timeTomorrowText = getOpenTime(yearMonthDay);
    }
    
    private void getLoginData() {
    	loginHelper = new LoginHelper(this);
    	contentValues = loginHelper.getLoginData();
    }
    
    private String getOpenTime(int yearMonthDay) {
    	db = new Database(this);
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
    
    public void setStatus(CharSequence charSequence) {
        TextView status = (TextView)Campus.this.findViewById(R.id.lib_login_status);
        status.setText(charSequence);
    }
    
    private void relogin() {
        bar.setVisibility(View.GONE);
        login.setClickable(true);
    	loginLayout.setVisibility(View.VISIBLE);
        userName = (EditText)findViewById(R.id.lib_username);
        password = (EditText)findViewById(R.id.lib_password);
        login.setOnClickListener(new LoginClickListener());
    }
    
    class LoginClickListener implements OnClickListener {
    	public void onClick(View v) {
	        String userString = userName.getText().toString();
	        String passString = password.getText().toString();
	        if(userString != null && passString != null) {
	            login.setClickable(false);
	            bar.setVisibility(View.VISIBLE);
	            status.setVisibility(View.VISIBLE);
	            setStatus(getText(R.string.logining));
	        	loginHelper.saveLoginData(userString, passString);
		        Thread thread = new Thread(Campus.this);
		        thread.start();
	        }
    	}
    }
    
    class SearchClickListener implements OnClickListener {
    	public void onClick(View v) {
    		Intent intent = new Intent();
    		intent.setClass(Campus.this, BookSearch.class);
    		startActivityForResult(intent,0);
    	}
    }
    
    class ZxingSearchClickListener implements OnClickListener {
    	public void onClick(View v) {
    		Intent intent = new Intent();
    		intent.setClass(Campus.this, com.google.zxing.client.android.CaptureActivity.class);
    		startActivityForResult(intent,0);
    	}
    }
	
	public Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
	        try {
				handler.parseAndSave(data.getString("1"));
			} catch (JSONException e) {
				Log.e("Campus.onCreate()", e.toString());
			}
    	}
	};
	
	public void run() {
		if(!Thread.interrupted()) {
	        String htmlBody = handler.fetchData();
	        MessageHandler mh = new MessageHandler();
	        mh.bundle("1", htmlBody);
	        mHandler.sendMessage(mh.get());
		}
	}
}
class MyLibList{
	public String barCode, marcNo, title, author, store;
	public int borrow, returns;
	public MyLibList(String barCode, String marcNo, String title,
			String author, String store, int borrow, int returns) {
		this.barCode = barCode;
		this.marcNo = marcNo;
		this.title = title;
		this.author = author;
		this.store = store;
		this.borrow = borrow;
		this.returns = returns;
	}
}