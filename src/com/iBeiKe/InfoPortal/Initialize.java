package com.iBeiKe.InfoPortal;

import java.io.File;
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
import com.iBeiKe.InfoPortal.common.MessageHandler;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.update.LoadingView;
import com.iBeiKe.InfoPortal.update.XMLHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

/**
 * 软件数据库初始化类，每次启动检测数据库完整性以及在第一次启动时创建数据库，
 * 创建子线程以提供初始化动画显示，创建子线程以提供xml的数据获取解析，
 * 数据库的创建，以及通过阻塞队列来异步传递数据、控制信息。
 * 
 * TODO:在解析更新数据时用到，不再在初始化时使用。
 * 
 */
public class Initialize extends Activity implements Runnable {
	private String message = "初始化...";
    private LoadingView imageview;
    private TextView textview;
	private InputStream is;
	private Database database = new Database(this);
	private ExecutorService exec;
    private Thread thread;
	private MessageHandler mcr;
	private Intent intent;
	private BlockingQueue<Map<String,Map<String,String>>> structQueue =
			new ArrayBlockingQueue<Map<String,Map<String,String>>>(1);
	private BlockingQueue<Map<String,String>> contentQueue =
			new LinkedBlockingQueue<Map<String,String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File file = new File("/data/data/com.iBeiKe.InfoPortal/databases/infoportal.db");
		intent = new Intent(this, InfoPortal.class);
		if(file.exists()) {
			startActivity(intent);
		} else {
			setContentView(R.layout.initialize);
			
			textview = (TextView)findViewById(R.id.initial_text);
	        imageview = (LoadingView)findViewById(R.id.main_imageview);
	        initLoadingImages();
	        
	        thread = new Thread(this);
	        thread.start();
	
			try {
				is = getAssets().open("initialize.xml");
			} catch (IOException e) {
				e.printStackTrace();
			}
			exec = Executors.newCachedThreadPool();
			exec.execute(new XMLParser(contentQueue,structQueue,is));
			exec.execute(new DatabaseRebuild(contentQueue,structQueue,database));
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		finish();
	}
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("1")) {
				exec.shutdown();
				
				//thread.stop();
				startActivity(intent);
			} else {
				textview.setText(mcr.getString("0", msg));
			}
    	}
	};
	
	public void run() {
        imageview.startAnim();
	}
	
    private void initLoadingImages() {
        int[] imageIds = new int[6];
        imageIds[0] = R.drawable.loader_frame_1;
        imageIds[1] = R.drawable.loader_frame_2;
        imageIds[2] = R.drawable.loader_frame_3;
        imageIds[3] = R.drawable.loader_frame_4;
        imageIds[4] = R.drawable.loader_frame_5;
        imageIds[5] = R.drawable.loader_frame_6;
        
        imageview.setImageIds(imageIds);
    }
    
    private void messageSender(String key, String str) {
    	mcr = new MessageHandler();
    	mcr.bundle(key, str);
		mHandler.sendMessage(mcr.get());
    }
    
    class XMLParser implements Runnable {
    	private XMLHandler myXMLHandler;
    	private InputStream is;
    	public XMLParser(BlockingQueue<Map<String,String>> contentQueue,
    			BlockingQueue<Map<String,Map<String,String>>> structQueue,
    			InputStream is) {
    		messageSender("0","开始解析文件...");
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
    			Log.e("XMLParser Exception", e.toString());
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
    				messageSender("0", "创建数据库...");
    				database.write();
    				database.onRebuild(dbStruct);
    				messageSender("0","初始化数据...");
    				while(!Thread.interrupted()) {
    					content = contentQueue.take();
    					if(content.containsKey("table")) {
    						table = content.get("table");
    					} else if(content.containsKey(";")) {
    						break;
    					} else {
    						database.insert(table, content);
    					}
    				}
    				database.close();
    				messageSender("0","完成！");
    				messageSender("1","completed");
    			}
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    }
}