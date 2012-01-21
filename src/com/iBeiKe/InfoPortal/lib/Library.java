package com.iBeiKe.InfoPortal.lib;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.iBeiKe.InfoPortal.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Library extends Activity {
    private WebView show;
    private EditText txt;
    private ImageButton btn;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        final Button roomSearch = (Button) findViewById(R.id.top_back);
        roomSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Library.this, com.google.zxing.client.android.CaptureActivity.class);
				startActivityForResult(intent,0);
			}
		});
        show = (WebView)findViewById(R.id.lib_content);
        txt = (EditText)findViewById(R.id.search_edit);
        btn = (ImageButton)findViewById(R.id.search);
        btn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		dopost(txt.getText().toString());
        	}
        }); 
    }
    private void dopost(String val){
    	try {
    		URL url = new URL("http://lib.ustb.edu.cn:8080/opac/openlink.php?" +
    				"historyCount=1&strText="+val+"&doctype=ALL&strSearchType=title" +
    				"&match_flag=forward&displaypg=20&sort=CATA_DATE&orderby=desc&showmode=list&location=ALL");
    		URLConnection urlConnection = url.openConnection();
    		InputStream is = urlConnection.getInputStream();

    		SAXParserFactory spf = SAXParserFactory.newInstance();
    		SAXParser sp = spf.newSAXParser();
    		XMLReader xr = sp.getXMLReader();
    		//LibSearchResult myXMLHandler = new LibSearchResult();
    		//xr.setContentHandler(myXMLHandler);
    		xr.parse(new InputSource(is));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}