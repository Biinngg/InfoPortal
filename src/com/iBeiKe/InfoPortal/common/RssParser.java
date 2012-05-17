package com.iBeiKe.InfoPortal.common;

import static com.iBeiKe.InfoPortal.Constants.BUILDING;
import static com.iBeiKe.InfoPortal.Constants.CLASS;
import static com.iBeiKe.InfoPortal.Constants.ROOM;
import static com.iBeiKe.InfoPortal.Constants.TABLE;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 提供对rss文件的解析。
 * TODO:早期代码，将废弃。
 *
 */
public class RssParser extends DefaultHandler {
	private String table;
	private int table_num;
	private int build;
	private int class_num;
	private int room;
	private int room_num;
	private int week;
	private int classNum;
	private int info_column;
	private String info_name;
	private String classTime;
	private String tagName;
	private String info;
	private int i;

	@Override
	public void startDocument() throws SAXException {
		System.out.println("Start to parse rss files.");
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println("Finished parsing rss files.");
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		tagName = localName;
		i = 0;
		if(localName.equals("rss")) {
			
		} else if(localName.equals("channel")) {
			
		} else if(localName.equals(TABLE)) {
			String[] table_name = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "class", "info"};
			table = atts.getValue(0);
			for(int i=0; i<7; i++) {
				if(table_name[i].equals(table))
					table_num = i;
			}
			if(atts.getValue(0) == "info") {
				info_column = 0;
			}
		} else if (localName.equals(BUILDING)) {
			if(atts.getValue(0).equals("y"))
				build = 0;
			else
				build = 1;
		} else if (localName.equals(ROOM)) {
			String rooms = atts.getValue(0);
			int floor = rooms.charAt(0) - 49;
			int room_number = rooms.charAt(2) - 49;
			room_num = floor * 10 + room_number;
			room = Integer.parseInt(atts.getValue(0));
		} else if (localName.equals(CLASS)) {
			class_num = Integer.parseInt(atts.getValue(0)) - 1;
		}
		else if (localName.equals("time")) {
			classNum = Integer.parseInt(atts.getValue(0))-1;
		} else if (localName.equals("info")) {
			info_name = atts.getValue(0);
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("database")) {
		} else if (localName.equals(TABLE)) {
		} else if (localName.equals(BUILDING)) {
		} else if (localName.equals(ROOM)) {
			room_num++;
		} else if (localName.equals("time")) {
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if(tagName.equals(CLASS) && i == 0){
			i++;
			String classes = new String(ch, start, length);
			week = Integer.parseInt(classes);
	    	}
		else if(tagName.equals("time") && i==0) {
			i++;
			classTime = new String(ch, start, length);
		}
		else if(tagName.equals("info") && i==0) {
			i++;
			info = new String(ch, start, length);
			info_column++;
		}
	}
}