package com.iBeiKe.InfoPortal.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.iBeiKe.InfoPortal.database.Database;

import android.content.Context;
import android.util.Log;

public class ComTimes {
	private long timeMillis;
	private Context context;
	private long msDay = 86400000;
	
	public ComTimes(Context context) {
		timeMillis = System.currentTimeMillis();
		this.context = context;
	}
	/**
	 * Give the time millis.
	 * Remember to call before get times.
	 * @param timeMillis
	 * the time to query.
	 * 
	 */
	public void setTime(long timeMillis) {
		this.timeMillis = timeMillis;
	}
	public void moveToNextDays(int num) {
		timeMillis += num * msDay;
	}
	/**
	 * return the time stamp as the given pattern and the given time.<br/>
	 * Function as the SimpleDateFormat.</p>
	 * @param type
	 * The given time's format.
	 * @param time
	 * The time to format.
	 */
	public long stringToMillis(String type, String time) {
		long millis = 0;
		SimpleDateFormat formater = new SimpleDateFormat(type);
		try {
			millis = formater.parse(time).getTime();
		} catch (ParseException e) {
			Log.e("ParseException", e.toString());
		}
		return millis;
	}
	/**
	 * <p><b>Return</b><br/>
	 * The beginning of a term.</p>
	 * <p><b>Style</b><br/>
	 * An integer.</p>
	 */
	public int getTermStart(Database db) {
		int[] start = db.getInt("cla_time", "begin", "_id=1", null, 0);
		return start[0];
	}
	
	/**
	 * <p><b>Return</b><br/>
	 * The current week in a term.</p>
	 * <p><b>Style</b><br/>
	 * An integer.</p>
	 */
	public int getWeekInTerm(Database db) {
		long termStartMillis = stringToMillis("yyyyMMdd",getTermStart(db) + "");
		long dit = (timeMillis - termStartMillis)/msDay;
		int weekInTerm = (int) (dit/7) + 1;
		return weekInTerm;
	}

	/**
	 * <p><b>Return</b><br/>
	 * Times as the given pattern.<br/>
	 * Function as the SimpleDateFormat.</p>
	 */
	public String getTimes(long timeMillis, String type, Locale locale) {
		SimpleDateFormat timeType;
		if(locale == null) {
			timeType = new SimpleDateFormat(type);
		} else {
			timeType = new SimpleDateFormat(type, locale);
		}
		String time = timeType.format(timeMillis);
		return time;
	}
	/**
	 * <p><b>Return</b><br/>
	 * Year.</p>
	 * <p><b>Style</b><br/>
	 * "yyyy".</p>
	 */
	public int getYear() {
		String type = "yyyy";
		int year = Integer.parseInt(getTimes(timeMillis, type, null));
		return year;
	}
	/**
	 * <p><b>Return</b><br/>
	 * Month.</p>
	 * <p><b>Style</b><br/>
	 * "MM".</p>
	 */
	public int getMonth() {
		String type = "MM";
		int month = Integer.parseInt(getTimes(timeMillis, type, null));
		return month;
	}
	/**
	 * <p><b>Return</b><br/>
	 * day.</p>
	 * <p><b>Style</b><br/>
	 * "dd".</p>
	 */
	public int getDay() {
		String type = "dd";
		int day = Integer.parseInt(getTimes(timeMillis, type, null));
		return day;
	}
	/**
	 * <p><b>Return</b><br/>
	 * The day in a week.</p>
	 * <p><b>Style</b><br/>
	 * Default shows as the system's location setting.<br/>
	 * Loop calling will get the next day in week.</p>
	 * <p>For example:<br/>
	 * American: "Mon", "Tue", "Wed"...<br/>
	 * Chinese: "周一", "周二", "周三" ...</p>
	 */
	public String getDayInWeek(Locale locale) {
		String type = "E";
		String dayInWeek = getTimes(timeMillis, type, locale);
		return dayInWeek;
	}
	public int getDayInWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		return calendar.get(Calendar.DAY_OF_WEEK)-1;
	}
	/**
	 * <p><b>Return</b><br/>
	 * hour and minute.</p>
	 * <p><b>Style</b><br/>
	 * "HHmm".</p>
	 */
	public int getHourAndMinute() {
		String type = "kkmm";
		int hm = Integer.parseInt(getTimes(timeMillis, type, null));
		return hm;
	}
}
