package com.iBeiKe.InfoPortal.library;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.MessageHandler;
import com.iBeiKe.InfoPortal.database.Database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

public class Book extends Activity implements Runnable {
	private int pageNum = 1;
	private Thread thread;
	private BookHandler handler;
	private BookHelper helper;
	private BookListAdapter adapter;
	private ProgressBar progressBar;
	private TextView progressState;
	private EditText editText;
	private ListView listView;
	private View foot;
	static String tableList = "book_list";
	static String baseUrl = "http://lib.ustb.edu.cn:8080/opac/item.php?marc_no=";
	static String[] columnsList = new String[]{"bookName",
		"marc_no", "ID", "libHave", "canBorrow", "author", "authority"};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books);
        
        handler = new BookHandler(this);
        foot = ((LayoutInflater)this.getSystemService
        		(Context.LAYOUT_INFLATER_SERVICE)).
        		inflate(R.layout.foot, null, false);
        foot.setVisibility(View.GONE);
        progressBar = (ProgressBar)foot.findViewById(R.id.book_more_progress);
        progressState = (TextView)foot.findViewById(R.id.book_more_info);
        foot.setOnClickListener(new myMoreClickListener());
        foot.setClickable(false);
        listView = (ListView) findViewById(R.id.books_list);
        listView.addFooterView(foot);
        adapter = new BookListAdapter(this);
        listView.setAdapter(adapter);
        editText = (EditText)findViewById(R.id.search_edit);
        ImageButton button = (ImageButton)findViewById(R.id.search);
        button.setOnClickListener(new mySearchListener());
        Button btn = (Button)findViewById(R.id.top_back);
        btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(Book.this, com.google.zxing.client.android.CaptureActivity.class);
        		startActivityForResult(intent,0);
        	}
        });
        handleBarcode();
	}

    private void handleBarcode() {
		Intent intent=this.getIntent();
		Bundle bl=intent.getExtras();
		if(bl==null)
			return;
		String isbn = bl.getString("isbn");
		editText.setText(isbn);
		search(Long.parseLong(isbn));
    }
	
	class mySearchListener implements OnClickListener {
		public void onClick(View v) {
			String text = editText.getText().toString();
			if(text != null) {
				search(text); 
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);     
		        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);    
			}
		}
	}

	private void search(String text) {
        adapter = new BookListAdapter(this);
        listView.setAdapter(adapter);
		foot.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		progressState.setText(R.string.searching);
		handler.updateApiUrl(text);
		pageNum = 1;
		helper = new BookHelper(Book.this);
        thread = new Thread(Book.this);
        thread.start();
	}
	private void search(long text) {
        adapter = new BookListAdapter(this);
        listView.setAdapter(adapter);
		foot.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		progressState.setText(R.string.searching);
		handler.updateApiUrl(text);
		pageNum = 1;
		listView.addFooterView(foot);
        thread = new Thread(Book.this);
        thread.start();
	}
	
	class myMoreClickListener implements OnClickListener {
		public void onClick(View v) {
			progressBar.setVisibility(View.VISIBLE);
			progressState.setText(R.string.searching);
			foot.setClickable(false);
			pageNum++;
	        thread = new Thread(Book.this);
			thread.start();
		}
	}
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("-1")) {
				progressBar.setVisibility(View.GONE);
				progressState.setText(R.string.book_list_null);
			} else if(msg.getData().containsKey("0")) {
				adapter.notifyDataSetChanged();
				foot.setVisibility(View.GONE);
			} else {
				adapter.notifyDataSetChanged();
				progressBar.setVisibility(View.INVISIBLE);
				progressState.setText(R.string.more);
				foot.setClickable(true);
			}
    	}
	};
	
	public void run() {
		if(!Thread.interrupted()) {
			try {
				int result;
				String htmlBody = handler.fetchData(pageNum);
				if(htmlBody.length() == 0) {
					result = -1;
				} else {
					result =  handler.parseAndSave(htmlBody);
				}
				MessageHandler mcr = new MessageHandler();
				switch(result) {
				case -1:
					mcr.bundle("-1", "");
					break;
				case 0:
					mcr.bundle("0", "");
					break;
				case 1:
					mcr.bundle("1", "");
					break;
				}
				mHandler.sendMessage(mcr.get());
			} catch (JSONException e) {
				Log.e("Book.run", e.toString());
			}
		}
	}
}
