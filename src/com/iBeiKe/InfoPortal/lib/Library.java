package com.iBeiKe.InfoPortal.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

public class Library extends ListActivity {
    private WebView show;
    private EditText txt;
    private ImageButton btn;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
    private LibraryAdapter libAdapter;
    
    private class BookList{
    	public String title;
    	public String num;
    	public String author;
    	public String publisher;
    	public String link;
    	public BookList(String title, String num,
    			String author, String publisher, String link) {
    		this.title = title;
    		this.num = num;
    		this.author = author;
    		this.publisher = publisher;
    		this.link = link;
    	}
    }
    
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
    
    private class LibraryAdapter extends BaseAdapter {
    	private LayoutInflater mInflater;
    	private ArrayList<BookList> books;
    	public LibraryAdapter(Context context) {
    		mInflater = LayoutInflater.from(context);
    		books = new ArrayList<BookList>();
    		getdata();
    	}
    	
    	public BookList resultFormater(Map<String,String> item) {
    		String title = null, num = null, author = null,
    				publisher = null, link = null, content = null;
			for(String key : item.keySet()) {
				if(key.equals("title")) {
					title = item.get(key);
				} else if(key.equals("link")) {
					link = item.get(key);
				} else if(key.equals("discription")) {
					content = item.get(key);
				} else {
					Log.d("Unknow Tag", "key: " + key
							+ " content: " + item.get(key));
				}
			}
			int[] n = new int[3];
			n[0] = content.indexOf(R.string.book_author);
			n[1] = content.indexOf(R.string.book_num, n[0]);
			n[2] = content.indexOf(R.string.book_publisher, n[1]);
			if(n[0]!=-1 && n[1]!=-1 && n[2]!=-1) {
				author = content.substring(n[0], n[1]-1);
				num = content.substring(n[1], n[2]-1);
				publisher = content.substring(n[2]);
			} else {
				Log.e("Error Content",content);
			}
			BookList result = new BookList(title,num,author,publisher,link);
			books.add(result);
			return result;
    	}
    	
    	public void getdata() {
    		Map<String,String> item;
    		BookList result;
    		while(!Thread.interrupted()) {
    			try {
    				item = queue.take();
    				result = resultFormater(item);
    				books.add(result);
    			} catch (InterruptedException e) {
    				Log.e("Queue Exception", e.toString());
    			}
    		}
    	}
    	
		public int getCount() {
			return books.size();
		}

		public BookList getItem(int arg0) {
			return books.get(arg0);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View v = convertView;
			if((v == null) || (v.getTag() == null)) {
				v = mInflater.inflate(R.layout.books_row, null);
				holder = new ViewHolder();
				holder.title = (TextView)v.findViewById(R.id.book_title);
				holder.num = (TextView)v.findViewById(R.id.book_num);
				holder.author = (TextView)v.findViewById(R.id.book_author);
				holder.publisher = (TextView)v.findViewById(R.id.book_publisher);
				v.setTag(holder);
			} else {
				holder = (ViewHolder)v.getTag();
			}
			holder.bookList = getItem(position);
			holder.title.setText(holder.bookList.title);
			holder.num.setText(holder.bookList.num);
			holder.author.setText(holder.bookList.author);
			holder.publisher.setText(holder.bookList.publisher);
			v.setTag(holder);
			return v;
		}
		
		public class ViewHolder {
			BookList bookList;
			TextView title;
			TextView num;
			TextView author;
			TextView publisher;
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