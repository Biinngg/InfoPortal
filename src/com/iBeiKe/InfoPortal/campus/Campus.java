package com.iBeiKe.InfoPortal.campus;

import java.util.ArrayList;
import java.util.Map;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.library.BookSearch;
import com.iBeiKe.InfoPortal.library.Library;
import com.iBeiKe.InfoPortal.library.LibraryListAdapter;
import com.iBeiKe.InfoPortal.library.LoginHelper;
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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus);

        JsonHandler jh = new JsonHandler(this);
        TextView timeToday = (TextView)findViewById(R.id.library_time_today);
        TextView timeTomorrow = (TextView)findViewById(R.id.library_time_tomorrow);
        timeToday.setText(timeTodayText);
        timeTomorrow.setText(timeTomorrowText);
        borrowList = (ListView)findViewById(R.id.library_borrow_list);
        txt = (EditText)findViewById(R.id.search_edit);
        txt.setFocusable(false);
        txt.setOnClickListener(new SearchClickListener());
        btn = (ImageButton)findViewById(R.id.search);
        btn.setOnClickListener(new SearchClickListener());
        Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new ZxingSearchClickListener());
        bar = (ProgressBar)findViewById(R.id.lib_login_progress);
        status = (TextView)findViewById(R.id.lib_login_status);
        login = (Button)findViewById(R.id.lib_login_button);
        adapter = new LibraryListAdapter(this);
        borrowList.setAdapter(adapter);
        
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
			if(data.containsKey("0")){
				loginLayout.setVisibility(View.GONE);
				ArrayList<MyLibList> myLibList =
						(ArrayList<MyLibList>)msg.getData().getSerializable("0");
			} else if(data.containsKey("1")) {
				relogin();
				setStatus(getText(R.string.login_faile));
			} else {
				relogin();
				setStatus((String)data.get("2"));
			}
    	}
	};
	
	public void run() {
		if(!Thread.interrupted()) {
			String loginStatus = null;
			Bundle bul = new Bundle();
			Message msg = Message.obtain();
			getLoginData();
			MyLibraryFetcher mlf = new MyLibraryFetcher(Campus.this, contentValues);
			try {
				loginStatus = mlf.fetchData();
			} catch (Exception e2) {
				Log.e("MyLibraryFetcher Exception", e2.toString());
			}
			if(loginStatus.equals("0")) {
			} else if(loginStatus.equals("1")){
				bul.putString("1", loginStatus);
			} else {
				bul.putString("2", loginStatus);
			}
			msg.setData(bul);
			mHandler.sendMessage(msg);
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