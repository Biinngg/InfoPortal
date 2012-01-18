package com.iBeiKe.InfoPortal.update;

import static com.iBeiKe.InfoPortal.Constants.ROOM;
import static com.iBeiKe.InfoPortal.Constants.BUILDING;
import static com.iBeiKe.InfoPortal.Constants.CLASS;
import static com.iBeiKe.InfoPortal.Constants.TABLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.iBeiKe.InfoPortal.database.Database;


public class XMLHandler extends DefaultHandler {
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
	private String info;
	//xml structure
	private boolean strMark=false;
	private String tagId;
	private String tagName;
	private int i;
	//database structure
	private int tableNum=0;
	private String version;
	private ArrayList<String> tableName = new ArrayList<String>();
	private Map<String,String> column = new HashMap<String,String>();
	private Map<String,Map<String,String>> dbStruct = new HashMap<String,Map<String,String>>();
	private BlockingQueue<Map<String,Map<String,String>>> mapQueue;
	private BlockingQueue<String> strQueue;
	
	public XMLHandler() {
		System.out.println("Test: XMLHandler to begin");
	}
	
	public void add(Map<String,Map<String,String>> struct) throws InterruptedException {
		System.out.println("Begin to add to the blocking queue");
		mapQueue.put(struct);
	}
	public Map<String,Map<String,String>> fetchMap() throws InterruptedException {
		if(mapQueue.isEmpty()) {
			return null;
		} else {
			return mapQueue.take();
		}
	}
	public void add(String sentense) throws InterruptedException {
		strQueue.put(sentense);
	}
	public String fetchStr() throws InterruptedException {
		if(strQueue.isEmpty()) {
			return null;
		} else {
			return strQueue.take();
		}
	}

	private ParsedXmlDataSet myParsedXmlDataSet = new ParsedXmlDataSet();
	
	public ParsedXmlDataSet getParsedData() {
		return this.myParsedXmlDataSet;
	}
	
	
	@Override
	public void startDocument() throws SAXException {
		System.out.println("Begin to handle xml file");
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		i++;
		
		System.out.println("Element num. " + i);
		tagName = localName;
		tagId = atts.getValue(0);
		if(localName.equals("database")) {
			if(tagId.equals("init")) {
				System.out.println("xml初始化");
				//TODO 分为初始化，更新
			}
		} else if (localName.equals("table")) {
			if(tagId.equals("struct")) {
				System.out.println("定义数据库结构");
				strMark = true;
			}
			
			
			String[] table_name = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "time", "update"};
			for(int i=0; i<7; i++) {
				if(table_name[i].equals("table"))
					table_num = i;
			}
			if(atts.getValue(0) == "info") {
				info_column = 0;
			}
		} else if(strMark && localName.equals("tag")) {
		}
		else if (localName.equals(BUILDING)) {
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
			myParsedXmlDataSet.setExtractedRoom(room, room_num, build, table_num);
		} else if (localName.equals(CLASS)) {
			class_num = Integer.parseInt(atts.getValue(0)) - 1;
		}
		else if (localName.equals("time")) {
			classNum = Integer.parseInt(atts.getValue(0))-1;
		} else if (localName.equals("info")) {
			info_name = atts.getValue(0);
			myParsedXmlDataSet.setExtractedInfoName(info_column, info_name);
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("database")) {
		} else if (localName.equals("table")) {
			if(strMark) {
				try {
					this.add(dbStruct);
					System.out.println("The structure: " + dbStruct.toString());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				strMark = false;
			}
		} else if (localName.equals("tag")) {
			if(strMark && !column.isEmpty()) {
				dbStruct.put(tableName.get(tableNum++), column);
				System.out.println("The columns: " + column.toString());
				column.clear();
			}
		}
		
		
		else if (localName.equals(ROOM)) {
			room_num++;
		} else if (localName.equals("time")) {
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if(strMark && tagName.equals("tag")) {
			if(tagId.equals("ver")) {
				version = new String(ch,start,length);
			} else {
				String[] tableNames = tagId.split(",");
				for(String element : tableNames) {
					System.out.println("table name " + element);
					tableName.add(element);
				}
			}
		} else if(strMark && tagName.equals("col")) {
			String character = new String(ch, start, length);
			String[] columns = character.split(",");
			for(String element : columns) {
				System.out.println("column name " + element);
				column.put(element, tagId);
			}
		}
		
		
		if(tagName.equals(CLASS) && i == 0){
			i++;
			String classes = new String(ch, start, length);
			week = Integer.parseInt(classes);
			myParsedXmlDataSet.setExtractedWeek(week, class_num, room_num, build, table_num);
	    	}
		else if(tagName.equals("time") && i==0) {
			i++;
			classTime = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedTime(classNum, classTime);
		}
		else if(tagName.equals("info") && i==0) {
			i++;
			info = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedInfoContent(info_column, info);
			info_column++;
		}
	}
} 
