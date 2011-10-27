package com.iBeiKe.InfoPortal;

public class ParsedXmlDataSet {
	private static final int CLASS_TAG = 6;
	private static final int ROOM_MAX = 80;
	private static final int BUILD_TAG = 2;
	private static final int TABLE_TAG = 5;
	private int[][][][] week = new int[TABLE_TAG][BUILD_TAG][ROOM_MAX][CLASS_TAG];
	private int[][][] room = new int[TABLE_TAG][BUILD_TAG][ROOM_MAX];
	private String[] time = new String[CLASS_TAG];
	private String[] info_content = new String[3];
	private String[] info_name = new String[3];
	private String[] titles = new String[5];
	private String[] links = new String[5];
	private String[] paragraphs = new String[5];

	//Handle the values posted by ExampleHandler.java
	public void setExtractedWeek(int ext_week, int c, int r, int b, int t) {
		week[t][b][r][c] = ext_week;
	}
	public void setExtractedRoom(int ext_room, int r, int b, int t) {
		room[t][b][r] = ext_room;
	}
	public void setExtractedTime(int n, String classTime) {
		time[n] = classTime;
	}
	public void setExtractedInfoContent(int i, String infoContent) {
		info_content[i] = infoContent;
	}
	public void setExtractedInfoName(int i, String infoName) {
		info_name[i] = infoName;
	}
	
	//Handle the values posted by NewsHandler.java
	public void setExtractedTitle(int i, String title) {
		titles[i] = title;
	}
	public void setExtractedLink(int i, String link) {
		links[i] = link;
	}
	public void setExtractedParagraph(int i, String paragraph) {
		paragraphs[i] = paragraph;
	}
	
	//return the values in arrays
	public int getExtractedWeek(int t, int b, int r, int c) {
		return week[t][b][r][c];
	}
	public int getExtractedRoom(int t, int b, int r) {
		return room[t][b][r];
	}
	public String getExtractedTime(int n) {
		return time[n];
	}
	public String getExtractedInfoContent(int i) {
		return info_content[i];
	}
	public String getExtractedInfoName(int i) {
		return info_name[i];
	}
	
	public String getExtractedTitle(int i) {
		return titles[i];
	}
	public String getExtractedLink(int i) {
		return links[i];
	}
	public String getExtractedParagraph(int i) {
		return paragraphs[i];
	}
}

