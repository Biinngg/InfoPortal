package com.iBeiKe.InfoPortal;

import com.iBeiKe.InfoPortal.classes.Search;
import com.iBeiKe.InfoPortal.news.News;
import com.iBeiKe.InfoPortal.lib.Library;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 整个程序主要可见Activity的容器，提供底部导航按钮;
 * 提供程序常量
 *
 */
public class InfoPortal extends ActivityGroup {
	Button classes;
	Button lib;
	Button news;
	Intent intent;
	TextView textView;
	private int click;
	public LinearLayout container;
	private static final int clickClasses = 0;
	private static final int clickLib = 1;
	private static final int clickNews = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        container = (LinearLayout) findViewById(R.id.Container);
        navigationButtonEvent();
    }
    
	private void navigationButtonEvent() {
		classes = (Button) findViewById(R.id.classes);
		lib = (Button) findViewById(R.id.lib);
		news = (Button) findViewById(R.id.news);
		textView = (TextView) findViewById(R.id.title);
		
		/*******************Initial state*****************/
		click = clickClasses;
		classes.setEnabled(false);
		lib.setEnabled(true);
		news.setEnabled(true);
		SwitchActivity(0);

		classes.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (click != clickClasses) {
					click = clickClasses;
					classes.setEnabled(false);
					lib.setEnabled(true);
					news.setEnabled(true);
					SwitchActivity(0);
				}
			}
		});
		lib.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (click != clickLib) {
					click = clickLib;
					classes.setEnabled(true);
					lib.setEnabled(false);
					news.setEnabled(true);
					SwitchActivity(1);
				}
			}
		});
		news.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (click != clickNews) {
					click = clickNews;
					classes.setEnabled(true);
					lib.setEnabled(true);
					news.setEnabled(false);
					SwitchActivity(2);
				}
			}
		});
	}
	
	void SwitchActivity(int id)
	{
		container.removeAllViews();
		Intent intent =null;
		if(id == 0) {
			intent = new Intent(InfoPortal.this, Search.class);
		} else if(id == 1) {
			intent = new Intent(InfoPortal.this, Library.class);
		} else if(id == 2) {
			intent = new Intent(InfoPortal.this, News.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//Activity 转为 View
		Window subActivity = getLocalActivityManager().startActivity(
				"subActivity", intent);
		container.addView(subActivity.getDecorView(),
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

/***********************The menu inflater****************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.settings:
    		try{
				Intent intent = new Intent();
				intent.setClass(InfoPortal.this, Settings.class);
				startActivity(intent);
    		}catch(Exception e){
    			Log.e("Menu Exception", e.toString());
    		}
    		break;
    	case R.id.about:
    		try{
				Intent intent = new Intent();
				intent.setClass(InfoPortal.this, About.class);
				startActivity(intent);
    		}catch(Exception e){
    			Log.e("Menu Exception", e.toString());
    		}
    		break;
    	case R.id.quit:
    		finish();
    		break;
    	}
		return true;
    }
}