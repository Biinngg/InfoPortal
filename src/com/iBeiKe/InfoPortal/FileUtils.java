package com.iBeiKe.InfoPortal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

/**
 * 文件的通用操作。
 * TODO:早期代码，将废弃
 */
public class FileUtils {	
	private String SDPATH;
	
	public String getSDPATH() {
		return SDPATH;
	}
	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	public File write2SDFromInput(String path, String fileName, InputStream input){
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[20 * 1024];
			while((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
}
