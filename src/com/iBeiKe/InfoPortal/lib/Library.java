package com.iBeiKe.InfoPortal.lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.MessageHandler;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class Library extends ListActivity implements Runnable {
    private WebView show;
    private EditText txt;
    private ImageButton btn;
    private Map<String,String> item;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
    private BooksListAdapter bookAdapter;
    private ListView listView;
	private MessageHandler mcr;
	private ExecutorService exec;
	private boolean execMark = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

		queue = new LinkedBlockingQueue<Map<String,String>>();
        final Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Library.this, com.google.zxing.client.android.CaptureActivity.class);
				startActivityForResult(intent,0);
			}
		});
        
		listView = getListView();
        txt = (EditText)findViewById(R.id.search_edit);
        btn = (ImageButton)findViewById(R.id.search);
        
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(txt.getText().toString() != null) {
        			Log.d("txt gettext", txt.getText().toString());
        			msg = new LinkedBlockingQueue<Integer>();
        			if(execMark) {
        				exec.shutdown();
        				execMark = false;
        			}
        			bookAdapter = new BooksListAdapter(Library.this);
        			Library.this.setListAdapter(bookAdapter);
        	    	String newFeed = "http://lib.ustb.edu.cn:8080/opac/search_rss.php?title="
        	    			+ toHexString(txt.getText().toString());
        	    	Log.i("Library", "newFeed: " + newFeed);
        	    	URL url;
					try {
						url = new URL(newFeed);
	        			exec = Executors.newCachedThreadPool();
	        			execMark = true;
	        			exec.execute(new RSSParser(url));
	    				msg.add(12);
	        	        Thread thread = new Thread(Library.this);
	        	        thread.start();
					} catch (MalformedURLException e) {
						Log.e("Library: ", "rss parse1: " + e.toString());
					}
        		}
        		txt.setText(txt.getText().toString());
        	}
        }); 
    }
    public static String toHexString(String s) {
    	String str= "";
    	for(byte b: s.getBytes()) {
    		str += "%" + Integer.toHexString(b & 0XFF);
    	}
    	return str;
    }
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("0")) {
				bookAdapter.getdata(mcr.getMap("0", msg));
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
        		//BufferedReader in = new BufferedReader(new InputStreamReader(input));
        		//Log.d("input Stream", in.readLine());
        		xr.parse(new InputSource(input));
        		input.close();
        	} else {
            	String httpResponse = httpconn.getResponseMessage();
        		Log.e("Library:", "Connect Error: " + httpResponse);
        	}
    	}
    }
}