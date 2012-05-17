package com.iBeiKe.InfoPortal;

import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.news.News;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * 提供桌面小插件显示，更新。
 *
 */
public class widget extends AppWidgetProvider {
	private static String result;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds){
			updateAppWidget(context, appWidgetManager, appWidgetIds);
	}
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager
			,int[] appWidgetIds) {
		String strUrl = "http://unixoss.com/news.html";
		try {
			URL url = new URL(strUrl);
        	SAXParserFactory factory = SAXParserFactory.newInstance();
        	SAXParser parser = factory.newSAXParser();
        	XMLReader xmlreader = parser.getXMLReader();
			//NewsHandler myExampleHandler = new NewsHandler();
			//xmlreader.setContentHandler(myExampleHandler);

        	InputStreamReader isr =new InputStreamReader(url.openStream());
        	InputSource is=new InputSource(isr);
        	System.out.println(url.getFile().toString());
        	xmlreader.parse(is);
			//ParsedXmlDataSet parsedNewsDataSet = myExampleHandler
			//.getParsedData();
			result = "";
			for(int i=0;i<5;i++) {
				//result += parsedNewsDataSet.getExtractedTitle(i) + "\n";
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		
        views.setTextViewText(R.id.widget_text, result);
        
        Intent intent = new Intent(context, News.class);
        PendingIntent Pintent= PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, Pintent);
        
        appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
}
