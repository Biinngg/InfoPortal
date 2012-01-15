package com.iBeiKe.InfoPortal.lib;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.iBeiKe.InfoPortal.update.ParsedXmlDataSet;

public class LibSearchResult extends DefaultHandler {
	private int table_num;
	private int build;
	private int class_num;
	private int room_num;
	private int week;
	private int classNum;
	private int info_column;
	private String classTime;
	private String tagName;
	private String info;
	private int i;
	private int itemTag = 0;

	private ParsedXmlDataSet myParsedXmlDataSet = new ParsedXmlDataSet();
	
	public ParsedXmlDataSet getParsedData() {
		return this.myParsedXmlDataSet;
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.myParsedXmlDataSet = new ParsedXmlDataSet();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		tagName = localName;//To use localName outside the function.
		i = 0;//Use i==0 to ensure there are no blank characters between tags.
		if(tagName.equals("item")) {
			itemTag = 1;
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if(tagName.equals("item")) {
			itemTag = 0;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if(tagName.equals("title") && i == 0 && itemTag == 1) {
			i++;
			String title = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedWeek(week, class_num, room_num, build, table_num);
	    } else if(tagName.equals("link") && i==0) {
			i++;
			String link = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedTime(classNum, classTime);
		} else if(tagName.equals("description") && i==0) {
			i++;
			String description = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedInfoContent(info_column, info);
		}
	}
}