package com.iBeiKe.InfoPortal.database;

import java.util.Map;

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
 * <p>time
 * <li>用来存放每节课上下课时间，包括
 * <li>id, begin, end
 * <li>第0行存放学期起讫，其他行每个小节一个记录，使用time stamp。
 * 
 * <p>update
 * <li>用来存放每次更新版本信息，0为更新地址，包括：
 * <li>id, value(varchar(50))
 * 
 */
public class Database extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "infoportal.db";
	private Map<String,Map<String,String>> struct;
	
	public Database(Context ctx, String version, Map<String,Map<String,String>> struct) {
		super(ctx, DATABASE_NAME, null, (Integer.parseInt(version.split("\\.")[0])));
		this.struct = struct;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		onRebuild(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onUpgrade");
	}
	
	public void onUpdate(SQLiteDatabase db, int oldRevision, int newRevision) {
		//TODO 用于数据更新
	}
	
	public void onRebuild(SQLiteDatabase db) {
		String SQLstring;
		for(String tableName : struct.keySet()) {
			SQLstring = "CREATE TABLE " + tableName + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT";
			Map<String,String> col = struct.get(tableName);
			for(String colName : col.keySet()) {
				String attri = col.get(colName);
				SQLstring += ", " + colName + " " + attri;
			}
			SQLstring += ");";
			db.execSQL(SQLstring);
		}
	}
}