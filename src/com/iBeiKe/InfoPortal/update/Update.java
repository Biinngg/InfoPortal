package com.iBeiKe.InfoPortal.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.Archive;
import com.iBeiKe.InfoPortal.common.Files;

public class Update implements Runnable {
	Context context;
	public Update(Context context) {
		Thread th = new Thread(this);
		th.start();
		this.context = context;
	}
	public void get() throws IOException, NameNotFoundException {
		String SDpath = Environment.getExternalStorageDirectory().getPath();
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
		int version = pi.versionCode;
		InputStream stream = null;
		URL url = new URL("http://mobil.unixoss.com/?ver=" + version);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		for(int i=0;;i++) {
			String key = connection.getHeaderFieldKey(i);
			String mine = connection.getHeaderField(i); 
			Log.d(i + ". " + key, mine + "");
			if(mine==null) break;
		    if("content-disposition".equals(connection.getHeaderFieldKey(i).toLowerCase())) {
			    Log.d("filename", mine);
		    }
		}
		stream = connection.getInputStream();
		Files file = new Files(SDpath +
				context.getResources().getString(R.string.path_download));
		file.saveStream(stream);
		stream.close();
		Archive arc = new Archive();
		arc.unzip(file.getPath(), 
				context.getResources().getString(R.string.path_database));
	}

	public void run() {
		try {
			get();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
