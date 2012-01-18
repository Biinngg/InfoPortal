package com.iBeiKe.InfoPortal.update;

import static com.iBeiKe.InfoPortal.Constants.BUILDING;
import static com.iBeiKe.InfoPortal.Constants.ROOM;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.update.XMLHandler;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 初始化：用于第一次启动时创建数据库，第一次启动显示介绍画面，并且读取系统时间以及数据库中的课程时间，学期时间。
 * 并且调用检测更新，附有进度条。
 * 
 */
public class Initialize extends Activity implements Runnable {
	private Database database;
	private static final int CLASS_TAG = 6;
	private static final int ROOM_MAX = 80;
	private static final int BUILD_TAG = 2;
	private static final int TABLE_TAG = 5;
	private String databaseName = "/data/data/com.iBeiKe.InfoPortal/databases/infoportal.db";
	private Map<String,Map<String,String>> dbStruct;
	private String version;
	private String[] day_of_week = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
	private String className;
	private String[] infoName = new String[3];
	private String[] infoContent = new String[3];
	private int begin;
	private int end;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initialize);
		
		Thread thread = new Thread(this);
		thread.start();
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			initial();
			database.close();
			System.out.println("After handleMessage");
		}
	};
	public void run() {
		mHandler.sendEmptyMessage(0);
	}
	private void initial() {
		try {
			System.out.println("Begin to initial");
			File file = new File(databaseName);   
			if(file.isFile() && file.exists()){
				file.delete();
			}
			InputStream is = getAssets().open("initialize.xml");
			System.out.println("Opened the file");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			System.out.println("Before xmlhandler initial");
			XMLHandler myXMLHandler = null;
			myXMLHandler = new XMLHandler();

			xr.setContentHandler(myXMLHandler);
			xr.parse(new InputSource(is));
			
			//TODO Not only database, but also progress bar.
			
			version = myXMLHandler.fetchStr();
			dbStruct = myXMLHandler.fetchMap();
			database = new Database(this,version,dbStruct);

			System.out.println("Arrived Initialize.java");
			
			
			
			ParsedXmlDataSet parsedExampleDataSet = myXMLHandler
			.getParsedData();

			for(int t=0; t<TABLE_TAG; t++) {
				for(int b=0; b<BUILD_TAG; b++) {
					for(int r=0; r<ROOM_MAX; r++) {
						if(parsedExampleDataSet.getExtractedRoom(t, b, r) != 0) {
							int room;
							room = parsedExampleDataSet.getExtractedRoom(t, b, r);
							int[] week = new int[CLASS_TAG];
							for(int c=0; c<CLASS_TAG; c++) {
								if(parsedExampleDataSet.getExtractedWeek(t, b, r, c) != 0) {
									week[c] = parsedExampleDataSet.getExtractedWeek(t, b, r, c);
								}
							}
							addTables(day_of_week[t], room, b, 
									week[0], week[1], week[2], week[3], week[4], week[5]);
						}
					}
	        	}
	        }
			for(int c=0; c<CLASS_TAG; c++) {
				className = parsedExampleDataSet.getExtractedTime(c);
				begin = Integer.parseInt(className.substring(0, 4));
				end = Integer.parseInt(className.substring(4, 8));
				addClass(begin, end);
			}
			//Initialize the info table.
	    	SQLiteDatabase db = database.getWritableDatabase();
	    	ContentValues values = new ContentValues();
			for(int i=0; i<3; i++) {
				infoName[i] = parsedExampleDataSet.getExtractedInfoName(i);
				infoContent[i] = parsedExampleDataSet.getExtractedInfoContent(i);
	    		if("revision".equals(infoName[i])) {
	    			values.put(infoName[i], infoContent[i]);
	    		} else {
	    			int Content = Integer.parseInt(infoContent[i]);
	    			values.put(infoName[i], Content);
	    		}
			}
	    	values.put("lastmodified", System.currentTimeMillis());
	    	db.insertOrThrow("info", null, values);

		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
    private void addTables(String table_name, int room, int building, 
    		int class1, int class2, int class3, int class4, int class5, int class6) {
    	SQLiteDatabase db = database.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(ROOM, room);
    	values.put(BUILDING, building);
    	values.put("class1", class1);
    	values.put("class2", class2);
    	values.put("class3", class3);
    	values.put("class4", class4);
    	values.put("class5", class5);
    	values.put("class6", class6);
    	db.insertOrThrow(table_name, null, values);
    }
    private void addClass(int begin, int end) {
    	SQLiteDatabase db = database.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put("begin", begin);
    	values.put("end", end);
    	db.insertOrThrow("class", null, values);
    }
}