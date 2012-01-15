package com.iBeiKe.InfoPortal.database;

import static com.iBeiKe.InfoPortal.Constants.DATABASE_NAME;
import static com.iBeiKe.InfoPortal.Constants.ROOM;
import static com.iBeiKe.InfoPortal.Constants.BUILDING;

import java.io.File;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <b>数据表：</b>
 * 
 * <p>Mon, Tue, Wed, Thu, Fri, Sat, Sun
 * <li>用来存储每天的无课信息，包括：
 * <li>id, room, building, class 1~6.
 * 
 * <p>class
 * <li>用来存放每节课上下课时间，包括
 * <li>id, begin, end
 * <li>第0行存放学期起讫，其他行每个小节一个记录，使用time stamp。
 * 
 * <p>update
 * <li>用来存放每次更新版本信息，0为更新地址，包括：
 * <li>id, value
 * 
 */
public class Database extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 3;
	private String[] table;
	
	public Database(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		table = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
		for(int i=0;i<5;i++)
			db.execSQL("CREATE TABLE " + table[i] + " (" + _ID +
					" INTEGER PRIMARY KEY AUTOINCREMENT, " + ROOM + " INTEGER, " +
					BUILDING + " INTEGER, " + "class1 INTEGER, class2 INTEGER," +
					"class3 INTEGER, class4 INTEGER, class5 INTEGER, class6 INTEGER);");
		db.execSQL("CREATE TABLE class (" + _ID +
				" INTEGER PRIMARY KEY AUTOINCREMENT, begin INTEGER, end INTEGER);");
		db.execSQL("CREATE TABLE update (" + _ID +
				" INTEGER PRIMARY KEY AUTOINCREMENT, revision varchar(10)," +
				"begin INTEGER, end INTEGER, lastmodified INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String databaseName = "/data/data/com.iBeiKe.InfoPortal/databases/infoportal.db";
		File file = new File(databaseName);   
		if(file.isFile() && file.exists()){
			file.delete();
		}
	}
}