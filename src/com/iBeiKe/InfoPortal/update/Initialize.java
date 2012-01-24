package com.iBeiKe.InfoPortal.update;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.update.XMLHandler;

import android.app.Activity;
import android.os.Bundle;

/**
 * 初始化：用于第一次启动时创建数据库，第一次启动显示介绍画面，并且读取系统时间以及数据库中的课程时间，学期时间。
 * 并且调用检测更新，附有进度条。
 * 
 */
public class Initialize extends Activity {
	private InputStream is;
	private Database database = new Database(this);
	private BlockingQueue<Map<String,Map<String,String>>> structQueue
	= new ArrayBlockingQueue<Map<String,Map<String,String>>>(1);
	private BlockingQueue<Map<String,String>> contentQueue = new LinkedBlockingQueue<Map<String,String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initialize);
		try {
			is = getAssets().open("initialize.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new XMLParser(contentQueue,structQueue,is));
		exec.execute(new DatabaseRebuild(contentQueue,structQueue,database));
		//TODO 在适当时候关闭线程：exec.shutdownNow();
	}
	/*
    private void addTables(String table_name, int room, int building, 
    		int class1, int class2, int class3, int class4, int class5, int class6) {
    	SQLiteDatabase db = database.getWritableDatabase();
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
    }*/
}

class XMLParser implements Runnable {
	private XMLHandler myXMLHandler;
	private InputStream is;
	public XMLParser(BlockingQueue<Map<String,String>> contentQueue,
			BlockingQueue<Map<String,Map<String,String>>> structQueue,InputStream is) {
		this.is = is;
		myXMLHandler = new XMLHandler(contentQueue, structQueue);
	}
	public void run() {
		try {
			if(!Thread.interrupted()) {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(myXMLHandler);
				xr.parse(new InputSource(is));
			}
		} catch (Exception e) {
			System.out.println("XMLParser: " + e.toString());
		}
	}
}

class DatabaseRebuild implements Runnable {
	private Database database;
	private Map<String,Map<String,String>> dbStruct;
	private BlockingQueue<Map<String,String>> contentQueue;
	private BlockingQueue<Map<String,Map<String,String>>> structQueue;
	public DatabaseRebuild(BlockingQueue<Map<String,String>> contentQueue,
			BlockingQueue<Map<String,Map<String,String>>> structQueue,
			Database database) {
		this.contentQueue = contentQueue;
		this.structQueue = structQueue;
		this.database = database;
	}
	public void run() {
		String table = null;
		Map<String,String> content;
		try {
			if(!Thread.interrupted()) {
				dbStruct = structQueue.take();
				System.out.println("ini dbStruct: " + dbStruct);
				database.open();
				database.onRebuild(dbStruct);
				while(!Thread.interrupted()) {
					content = contentQueue.take();
					System.out.println("insert content: " + content.toString());
					if(content.containsKey("table")) {
						table = content.get("table");
					} else {
						database.insert(table, content);
					}
				}
				database.close();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}