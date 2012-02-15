package com.iBeiKe.InfoPortal.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

public class ComTimes {
	private long timeMillis;
	
	public ComTimes() {
		timeMillis = System.currentTimeMillis();
	}
	/**
	 * Give the time millis.
	 * @param timeMillis
	 * the time to query.
	 */
	public void setTime(long timeMillis) {
		this.timeMillis = timeMillis;
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
	 * The current week in a term.</p>
	 * <p><b>Style</b><br/>
	 * An integer.</p>
	 */
	public int getWeekInTerm(int termStart) {
		long termStartMillis = stringToMillis("yyyyMMdd",termStart + "");
		long dit = (timeMillis - termStartMillis)/86400000;
		int weekInTerm = (int) (dit/7) + 1;
		return weekInTerm;
	}

	/**
	 * <p><b>Return</b><br/>
	 * Times as the given pattern.<br/>
	 * Function as the SimpleDateFormat.</p>
	 */
	public int getTimes(String type) {
		SimpleDateFormat timeType = new SimpleDateFormat(type);
		String sTime = timeType.format(timeMillis);
		int time = Integer.parseInt(sTime);
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
		return getTimes(type);
	}
	/**
	 * <p><b>Return</b><br/>
	 * Month.</p>
	 * <p><b>Style</b><br/>
	 * "MM".</p>
	 */
	public int getMonth() {
		String type = "MM";
		return getTimes(type);
	}
	/**
	 * <p><b>Return</b><br/>
	 * day.</p>
	 * <p><b>Style</b><br/>
	 * "dd".</p>
	 */
	public int getDay() {
		String type = "dd";
		return getTimes(type);
	}
	/**
	 * <p><b>Return</b><br/>
	 * hour and minute.</p>
	 * <p><b>Style</b><br/>
	 * "HHmm".</p>
	 */
	public int getHourAndMinute() {
		String type = "kkmm";
		return getTimes(type);
	}
	/**
	 * <p><b>Return</b><br/>
	 * The day in a week.</p>
	 * <p><b>Style</b><br/>
	 * Default shows as the system's location setting.</p>
	 * <p>For example:<br/>
	 * American: "Mon", "Tue", "Wed"...<br/>
	 * China: "周一", "周二", "周三" ...</p>
	 */
	public String getDayInWeek(Locale locale) {
		SimpleDateFormat diw;
		if(locale == null) {
		    diw = new SimpleDateFormat("E");
		} else {
			diw = new SimpleDateFormat("E", locale);
		}
		String dayInWeek = diw.format(timeMillis);
		return dayInWeek;
	}
}
