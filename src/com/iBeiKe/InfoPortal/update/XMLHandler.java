package com.iBeiKe.InfoPortal.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {
	//xml structure
	private boolean strMark=false;
	private String tagId;
	private String tagName;
	private String character;
	private String temTagName = "table";
	private String cirTagName;//用于标记最内层嵌套
	private Map<String,String> cirColumn;
	//database structure
	private int tableNum=0;
	private ArrayList<String> tableName;
	private Map<String,String> column = new HashMap<String,String>();
	private Map<String,Map<String,String>> dbStruct;
	private BlockingQueue<Map<String,String>> contentQueue;
	private BlockingQueue<Map<String,Map<String,String>>> structQueue;
	
	private StringBuilder sb = new StringBuilder();
	
	public XMLHandler(BlockingQueue<Map<String,String>> contentQueue,
			BlockingQueue<Map<String,Map<String,String>>> structQueue) {
		this.contentQueue = contentQueue;
		this.structQueue = structQueue;
	}
	
	@Override
	public void startDocument() throws SAXException {
		sb.setLength(0);
	}

	@Override
	public void endDocument() throws SAXException {
		column.put(";", ";");
		contentQueue.add(column);
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		tagName = localName;
		tagId = atts.getValue(0);
		sb.setLength(0);//必须每次清空，以排除夹在关闭标签与开启标签之间的字符
		if(localName.equals("database")) {
			if(tagId.equals("init")) {
				//TODO 分为初始化，更新
			}
		} else if (localName.equals("table")) {
			if(tagId.equals("struct")) {
				//在标签属性为struct时更新数据库结构，并且都标记strMark为true.
				//可能同一个文件中有两个结构的定义，所以每次都初始化。
				tableName = new ArrayList<String>();
				dbStruct = new HashMap<String,Map<String,String>>();
				strMark = true;
			} else {
				//表名单独放到map中，原因：1.每次可以减少一项.2.HashMap不一定table项总是在最前
				column.clear();//每次放入队列后已经指向一个新map。
				column.put("table", tagId);
				contentQueue.add(column);
				cirColumn = new HashMap<String,String>();
				column = new HashMap<String,String>();
			}
		} else if(strMark) {
			if(tagName.equals("tag") && !tagId.equals("ver")) {
				String[] tableNames = tagId.split(",");
				for(String element : tableNames) {
					tableName.add(element);
				}
			}
		} else if(tagId!=null) {
			//tagName作为关键字，用来保留外层嵌套，更新内层
			cirTagName = temTagName;
			temTagName = localName;
			cirColumn.put(temTagName, tagId);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch,start,length);//解决换行符带来的bug
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		//System.out.println("tagName: " + tagName + " localName: " + localName +
				//" tagId: " + tagId + " chara: " + character + " sb: " + sb.toString());
		character = sb.toString();
		if(strMark) {
			if(localName.equals("table")) {
				structQueue.add(dbStruct);
				strMark = false;
			} else if(localName.equals("tag") && !column.isEmpty()) {
				int length = tableName.size();
				while(tableNum < length) {
					dbStruct.put(tableName.get(tableNum++), column);
				}
				column=new HashMap<String,String>();
			} else 	if(localName.equals("col")) {
				String[] columns = character.split(",");
				for(String element : columns) {
					column.put(element, tagId);
				}
			}
		} else {
			if(localName.equals(cirTagName)) {
				addToQueue();
			}
			if(column.containsKey(tagName)) {
				//重复出现的列也算循环，需考虑tagName+=tagId
				addToQueue();
			}
			if(character != null && tagName != null) {
				if(tagId != null) {
					//允许tagName与tagId合成列名
					if(tagName.equals(temTagName)) {
						cirColumn.remove(temTagName);
						temTagName = cirTagName;
					}
					tagName = localName + tagId;
				} else {
					//在标签有id但没有内容时，此标签才为嵌套标签。
					cirTagName = temTagName;
				}
				column.put(tagName, character);
			}
		}
		tagName = null;
		tagId = null;
		sb.setLength(0);//使用完毕，不再有意义
	}
	private void addToQueue() {
		column.putAll(cirColumn);
		contentQueue.add(column);
		cirColumn.remove(tagName);
		temTagName = "table";
		column = new HashMap<String,String>();
	}
}