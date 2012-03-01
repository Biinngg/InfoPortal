package com.iBeiKe.InfoPortal.library;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.ComTimes;
import com.iBeiKe.InfoPortal.common.MessageHandler;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Library extends Activity {
	ComTimes ct;
	String timeTodayText;
	String timeTomorrowText;
    private EditText txt;
    private ImageButton btn;
    private Map<String,String> item;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
	private MessageHandler mcr;
	private ExecutorService exec;
	private int listNum;
    private ListView borrowList;
    private BooksListAdapter timeAdapter;
    private BooksListAdapter borrowAdapter;
    
    private void getInitData() {
    	ct = new ComTimes(this);
    	int yearMonthDay = ct.getYear() * 10000 + ct.getMonth() * 100 + ct.getDay();
    	timeTodayText = getOpenTime(yearMonthDay);
    	Log.d("getInitData", timeTodayText);
    	ct.moveToNextDays(1);
    	yearMonthDay = ct.getYear() * 10000 + ct.getMonth() * 100 + ct.getDay();
    	timeTomorrowText = getOpenTime(yearMonthDay);
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
        txt = (EditText)findViewById(R.id.search_edit);
        txt.setFocusable(false);
        txt.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(Library.this, BookSearch.class);
        		startActivity(intent);
        	}
        });
        btn = (ImageButton)findViewById(R.id.search);
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(Library.this, BookSearch.class);
        		startActivity(intent);
        	}
        });
        Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Library.this, com.google.zxing.client.android.CaptureActivity.class);
				startActivityForResult(intent,0);
			}
		});
        
        borrowAdapter = new BooksListAdapter(this);
        borrowList.setAdapter(borrowAdapter);
    }
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("0")) {
				Map<String,String> item = mcr.getMap("0", msg);
				if(!item.containsKey(";")) {
					listNum++;
				}
			}
    	}
	};
	
	public void run() {
		while(!Thread.interrupted()) {
			try {
				item = queue.take();
				mcr = new MessageHandler();
				mcr.bundle("0", item);
				mHandler.sendMessage(mcr.get());
			} catch (InterruptedException e) {
				Log.e("Queue Error", e.toString());
			}
		}
	}
    
    class RSSParser implements Runnable {
    	private URL url;
    	public RSSParser(URL url) {
    		this.url = url;
    	}
    	public void run() {
    		if(!Thread.interrupted()) {
    			try {
					httpRequest(url, new RSSHandler(queue, msg));
				} catch (Exception e) {
					Log.e("Library: ", "rss parse: " + e.toString());
				}
    		}
    	}
    	public void httpRequest(URL url, DefaultHandler myHandler)
        		throws MalformedURLException, IOException,
        		SAXException, ParserConfigurationException {
    		HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
    		httpconn.connect();
        	if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(myHandler);
        		InputStream input = httpconn.getInputStream();
        		xr.parse(new InputSource(input));
        		input.close();
        	} else {
            	String httpResponse = httpconn.getResponseMessage();
        		Log.e("Library:", "Connect Error: " + httpResponse);
        	}
    	}
    }
}