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
	private boolean tabMark=false;
	private String tagId;
	private String tagName;
	private String temTagName = "table";
	private String cirTagName;//用于标记最内层嵌套
	private Map<String,String> cirColumn;
	//database structure
	private int tableNum=0;
	private String version;
	private ArrayList<String> tableName;
	private Map<String,String> column;
	private Map<String,Map<String,String>> dbStruct;
	private BlockingQueue<Map<String,String>> contentQueue;
	private BlockingQueue<Map<String,Map<String,String>>> structQueue;
	
	public XMLHandler(BlockingQueue<Map<String,String>> contentQueue,
			BlockingQueue<Map<String,Map<String,String>>> structQueue) {
		this.contentQueue = contentQueue;
		this.structQueue = structQueue;
	}
	
	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		tagName = localName;
		tagId = atts.getValue(0);
		if(localName.equals("database")) {
			if(tagId.equals("init")) {
				//TODO 分为初始化，更新
			}
		} else if (localName.equals("table")) {
			tabMark = true;
			if(tagId.equals("struct")) {
				//TODO 整合到if(..."init")中
				//在标签属性为struct时更新数据库结构，并且都标记strMark为true.
				//可能同一个文件中有两个结构的定义，所以每次都初始化。
				tableName = new ArrayList<String>();
				column = new HashMap<String,String>();
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

	/**
	 *<b>Notice:</b>
	 *<p> Execute only if the tag doesn't contain characters.
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		String character = new String(ch,start,length);
		if(tabMark && !character.equals("\n")) {
			if(strMark) {
				if(tagName.equals("tag")) {
					if(tagId.equals("ver")) {
						version = character;
					}
				} else if(tagName.equals("col")) {
					String[] columns = character.split(",");
					for(String element : columns) {
						column.put(element, tagId);
					}
				}
			} else {
				if(tagId != null) {
					//允许tagName与tagId合成列名
					if(tagName.equals(temTagName)) {
						cirColumn.remove(temTagName);
						temTagName = cirTagName;
					}
					tagName += tagId;
				} else {
					//在标签有id但没有内容时，此标签才为嵌套标签。
					cirTagName = temTagName;
				}
				if(column.containsKey(tagName)) {
					//重复出现的列也算循环，需考虑tagName+=tagId
					addToQueue();
				}
				column.put(tagName, character);
			}
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		tagName = localName;
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
			}
		} else {
			if(localName.equals("table")) {
				tabMark = false;
			}
			if(localName.equals(cirTagName)) {
				addToQueue();
			}
		}
	}
	public void addToQueue() {
		column.putAll(cirColumn);
		contentQueue.add(column);
		cirColumn.remove(tagName);
		System.out.println("columns: " + column.toString());
		temTagName = "table";
		column = new HashMap<String,String>();
	}
}