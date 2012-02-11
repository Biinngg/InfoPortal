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
import com.iBeiKe.InfoPortal.common.MessageHandler;
import com.iBeiKe.InfoPortal.lib.ListViewActivity.ListViewAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BookSearch extends Activity implements Runnable {
    private WebView show;
    private EditText txt;
    private ImageButton btn;
    private Map<String,String> item;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
	private MessageHandler mcr;
	private ExecutorService exec;
	private boolean execMark = false;
	private int listNum;
	
    private LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    private LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.FILL_PARENT);
    ListView listView;
    private int lastItem = 0;
    LinearLayout loadingLayout;
    private BooksListAdapter adapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setPadding(0, 0, 15, 0);
        layout.addView(progressBar, mLayoutParams);
        TextView textView = new TextView(this);
        textView.setText("加载中...");
        textView.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(textView, FFlayoutParams);
        layout.setGravity(Gravity.CENTER);
        loadingLayout = new LinearLayout(this);
        loadingLayout.addView(layout, mLayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);
        listView = (ListView) findViewById(R.id.myList);
        adapter = new BooksListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                Log.i("test" , "Scroll>>>first: " + firstVisibleItem + ", visible: "
                		+ visibleItemCount + ", total: " + totalItemCount);
                lastItem = firstVisibleItem + visibleItemCount - 2;
                Log.i("test" , "Scroll>>>lastItem:" + lastItem);
                if (listNum != -1) {
                    if (lastItem + 2 == listNum) {
            			if(execMark) {
            				msg.add(10);
            			}
            			listView.setSelection(lastItem);
                    }
                }
            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
            }
        });
        
        
		queue = new LinkedBlockingQueue<Map<String,String>>();
        final Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(BookSearch.this, com.google.zxing.client.android.CaptureActivity.class);
				startActivityForResult(intent,0);
			}
		});
        
        txt = (EditText)findViewById(R.id.search_edit);
        btn = (ImageButton)findViewById(R.id.search);
        
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if(txt.getText().toString() != null) {
        			listNum = 0;
        			msg = new LinkedBlockingQueue<Integer>();

        	        adapter = new BooksListAdapter(BookSearch.this);
        	        listView.setAdapter(adapter);
        			
        			if(execMark) {
        				exec.shutdown();
        				execMark = false;
        			} else {
            	        listView.addFooterView(loadingLayout);
        			}
        	    	String newFeed = "http://lib.ustb.edu.cn:8080/opac/search_rss.php?title="
        	    			+ toHexString(txt.getText().toString());
        	    	Log.i("Library", "newFeed: " + newFeed);
        	    	URL url;
					try {
						url = new URL(newFeed);
	        			exec = Executors.newCachedThreadPool();
	        			execMark = true;
	        			exec.execute(new RSSParser(url));
	    				msg.add(15);
	        	        Thread thread = new Thread(BookSearch.this);
	        	        thread.start();
					} catch (MalformedURLException e) {
						Log.e("Library: ", "rss parse1: " + e.toString());
					}
        		}
        		txt.setText(txt.getText().toString());
        	}
        }); 
    }
    
    private static String toHexString(String s) {
    	String str= "";
    	for(byte b: s.getBytes()) {
    		str += "%" + Integer.toHexString(b & 0XFF);
    	}
    	return str;
    }
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("0")) {
				Map<String,String> item = mcr.getMap("0", msg);
				if(!item.containsKey(";")) {
					listNum++;
					adapter.getdata(item);
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