package com.iBeiKe.InfoPortal.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class RSSHandler extends DefaultHandler {
	private int itemNum;
	private boolean inItem;
	private StringBuilder sb;
	private Map<String,String> content;
	private BlockingQueue<Map<String,String>> queue;
	private BlockingQueue<Integer> msg;
	
	public RSSHandler(BlockingQueue<Map<String,String>> queue,
			BlockingQueue<Integer> msg) {
		sb = new StringBuilder();
		content = new HashMap<String,String>();
		this.queue = queue;
		this.msg = msg;
	}
	
	private void fetchNum() {
		//Use blocking queue to control parse progress.
		try {
			itemNum = msg.take();
		} catch (InterruptedException e) {
			Log.e("RSSHandler", "Exception: " + e.getMessage());
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
		fetchNum();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		sb.setLength(0);//必须每次清空，以排除夹在关闭标签与开启标签之间的字符
		if(localName.equals("item")) {
			if(itemNum <= 0) {
				fetchNum();
			}
			itemNum--;
			inItem = true;
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
		String character = sb.toString();
		if(inItem) {
			if(localName.equals("item")) {
				queue.add(content);
				content = new HashMap<String,String>();
				inItem = false;
			} else	if(sb.length() != 0) {
				content.put(localName, character);
			}
		}
		sb.setLength(0);//使用完毕，不再有意义
	}
}