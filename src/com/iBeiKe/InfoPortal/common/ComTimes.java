package com.iBeiKe.InfoPortal.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ComTimes {
	private long timeMillis;
	
	public ComTimes() {
		timeMillis = System.currentTimeMillis();
	}
	public void setTime(long timeMillis) {
		this.timeMillis = timeMillis;
	}
	public long stringToMillis(String type, String time) {
		long millis = 0;
		SimpleDateFormat formater = new SimpleDateFormat(type);
		try {
			millis = formater.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return millis;
	}
	public long getTermStartMillis() {
		String termStart = "20110912";// TODO This should get from the database.
		long termStartMillis = stringToMillis("yyyyMMdd",termStart);
		return termStartMillis;
	}
	public int getWeekInTerm() {
		long termStartMillis = getTermStartMillis();
		long dit = (timeMillis - termStartMillis)/86400000;
		int weekInTerm = (int) (dit/7) + 1;
		return weekInTerm;
	}
	
	public int getTimes(String type) {
		SimpleDateFormat timeType = new SimpleDateFormat(type);
		String sTime = timeType.format(timeMillis);
		int time = Integer.parseInt(sTime);
		return time;
	}
	public int getYear() {
		String type = "yyyy";
		return getTimes(type);
	}
	public int getMonth() {
		String type = "MM";
		return getTimes(type);
	}
	public int getDay() {
		String type = "dd";
		return getTimes(type);
	}
	public int getHourAndMinute() {
		String type = "kkmm";
		return getTimes(type);
	}
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
