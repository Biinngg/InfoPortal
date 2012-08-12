package com.iBeiKe.InfoPortal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.Files;
import com.iBeiKe.InfoPortal.update.LoadingView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 软件数据库初始化类，每次启动检测数据库完整性以及在第一次启动时创建数据库。
 * 
 */
public class Initial extends Activity implements Runnable {
    private LoadingView imageview;
	private ExecutorService exec;
    private Thread thread;
	private Intent intent;
	private BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(1);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File file = new File(
				getResources().getString(R.string.path_database));
		intent = new Intent(this, InfoPortal.class);
		exec = Executors.newCachedThreadPool();
		InputStream is = null;
		if(file.exists()) {
			startActivity(intent);
		} else {
			setContentView(R.layout.initialize);
			
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
		try {
			exec.awaitTermination(3000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finish();
	}
	
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
    		Files file = new Files(
    				getResources().getString(R.string.path_database));
    		file.saveStream(is);
    		blockingQueue.add(1);
    	}
    }
}