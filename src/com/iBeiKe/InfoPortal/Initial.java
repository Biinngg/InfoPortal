package com.iBeiKe.InfoPortal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.MessageHandler;
import com.iBeiKe.InfoPortal.update.LoadingView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

/**
 * 软件数据库初始化类，每次启动检测数据库完整性以及在第一次启动时创建数据库。
 * 
 */
public class Initial extends Activity implements Runnable {
    private LoadingView imageview;
    private TextView textview;
	private ExecutorService exec;
    private Thread thread;
	private MessageHandler mcr;
	private Intent intent;
	private BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(1);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File file = new File("/data/data/com.iBeiKe.InfoPortal/databases/infoportal.db");
		intent = new Intent(this, InfoPortal.class);
		exec = Executors.newCachedThreadPool();
		InputStream is = null;
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
				is = getAssets().open("infoportal.db");
			} catch (IOException e) {
				e.printStackTrace();
			}
			exec.execute(new DatabaseRebuild(is));
			try {
				blockingQueue.take();
				startActivity(intent);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		exec.shutdown();
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

    class DatabaseRebuild implements Runnable {
    	private InputStream is;
    	public DatabaseRebuild(InputStream is) {
    		this.is = is;
    	}
    	public void run() {
    		try {
    			int BUFFER_SIZE = 1024;
    			byte[] buf = new byte[BUFFER_SIZE];
    			int size = 0;
    			BufferedInputStream bis = new BufferedInputStream(is);
    			File f = new File("/data/data/" +
    					"com.iBeiKe.InfoPortal/databases");
    			f.mkdirs();
    			FileOutputStream fos = new FileOutputStream("/data/data/" +
    					"com.iBeiKe.InfoPortal/databases/infoportal.db");
    			while((size = bis.read(buf)) != -1)
    				fos.write(buf, 0, size);
    			fos.close();
    			bis.close();
    			blockingQueue.add(1);
    		} catch(IOException e) {
    			Log.e("DatabaseBuild run file", e.toString());
    		}
    	}
    }
}