package com.iBeiKe.InfoPortal.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Library extends Activity {
    private WebView show;
    private EditText txt;
    private ImageButton btn;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        final Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Library.this, com.google.zxing.client.android.CaptureActivity.class);
				startActivityForResult(intent,0);
			}
		});
        show = (WebView)findViewById(R.id.lib_content);
        txt = (EditText)findViewById(R.id.search_edit);
        btn = (ImageButton)findViewById(R.id.search);
        
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(txt.getText().toString() != null) {
        			msg = new LinkedBlockingQueue<Integer>();
        			queue = new LinkedBlockingQueue<Map<String,String>>();
        			
        	    	String newFeed = "http://lib.ustb.edu.cn:8080/opac/search_rss.php?title="
        	    			+ txt.getText().toString();
        	    	Log.v("Library", "newFeed: " + newFeed);
        	    	URL url;
					try {
						url = new URL(newFeed);
	        			ExecutorService exec = Executors.newCachedThreadPool();
	        			exec.execute(new RSSParser(url));
					} catch (MalformedURLException e) {
						Log.e("Library: ", "rss parse1: " + e.getMessage());
					}
        		}
        		txt.setText(txt.getText().toString());
        	}
        }); 
    }
    
    class RSSParser implements Runnable {
    	private URL url;
    	public RSSParser(URL url) {
    		this.url = url;
    	}
    	public void run() {
    		if(!Thread.interrupted()) {
    			try {
    				msg.add(12);
					httpRequest(url, new RSSHandler(queue, msg));
	    			while(true) {
	    				Log.v("queue", "queue: " + queue.take());
	    			}
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