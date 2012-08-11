package com.iBeiKe.InfoPortal.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class Files extends File {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String path = "";
	String file = "";
	public Files(String FileName) {
		super(FileName);
		path = this.getParent();
		file = this.getName();
	}
	
	public void saveStream(InputStream stream) {
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		BufferedInputStream bis = new BufferedInputStream(stream);
		File f = new File(path);
		f.mkdirs();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(this.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e("Files.saveStream() FileNotFoundException", e.toString());
		}
		try {
			while((size = bis.read(buf)) != -1)
				fos.write(buf, 0, size);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Files.saveStream() IOException", e.toString());
		}
		try {
			fos.close();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Files.saveStream() IOException", e.toString());
		}
	}
}
