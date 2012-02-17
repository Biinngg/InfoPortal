package com.iBeiKe.InfoPortal.lib;

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
import com.iBeiKe.InfoPortal.common.MessageHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class Library extends Activity {
    private EditText txt;
    private ImageButton btn;
    private Map<String,String> item;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
	private MessageHandler mcr;
	private ExecutorService exec;
	private int listNum;
    private ListView timeList;
    private ListView borrowList;
    private BooksListAdapter timeAdapter;
    private BooksListAdapter borrowAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        timeList = (ListView)findViewById(R.id.library_time_list);
        borrowList = (ListView)findViewById(R.id.library_borrow_list);
        txt = (EditText)findViewById(R.id.search_edit);
        btn = (ImageButton)findViewById(R.id.search);
        
        timeAdapter = new BooksListAdapter(this);
        borrowAdapter = new BooksListAdapter(this);
        timeList.setAdapter(timeAdapter);
        borrowList.setAdapter(borrowAdapter);
        
		queue = new LinkedBlockingQueue<Map<String,String>>();
        
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(txt.getText().toString() != null) {
        		}
        		txt.setText(txt.getText().toString());
        	}
        });
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