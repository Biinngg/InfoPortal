package com.iBeiKe.InfoPortal.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

import com.iBeiKe.InfoPortal.common.Archive;
import com.iBeiKe.InfoPortal.common.Files;

public class Update implements Runnable {
	public Update() {
		Thread th = new Thread(this);
		th.start();
	}
	public void get() throws IOException {
		String path = Environment.getExternalStorageDirectory().getPath();
		InputStream stream = null;
		URL url = new URL("http://m.unixoss.com/infoportal.zip");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		stream = connection.getInputStream();
		Files file = new Files(path + "/infoportal/tmp/db.zip");
		file.saveStream(stream);
		stream.close();
		Archive arc = new Archive();
		arc.unzip(file.getPath(), "/data/data/" +
				"com.iBeiKe.InfoPortal/databases/infoportal.db");
	}

	public void run() {
		try {
			get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
