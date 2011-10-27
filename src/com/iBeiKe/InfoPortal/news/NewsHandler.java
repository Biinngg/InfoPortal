package com.iBeiKe.InfoPortal.news;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.iBeiKe.InfoPortal.ParsedXmlDataSet;

public class NewsHandler extends DefaultHandler {
	private String title;
	private String link;
	private String paragraph;
	private String tagName;
	private int i;
	private int n;

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
		n = 0;
		tagName = localName;
		if (localName.equals("a") && i<5) {
			link = atts.getValue(0);
			myParsedXmlDataSet.setExtractedLink(i, link);
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("p")) {
				i++;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if(tagName.equals("a") && i<5 && n==0) {
			title = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedTitle(i, title);
			n++;
		} else if(tagName.equals("p") && i<5 && n==0) {
			paragraph = new String(ch, start, length);
			myParsedXmlDataSet.setExtractedParagraph(i, paragraph);
			n++;
		}
	}
}
