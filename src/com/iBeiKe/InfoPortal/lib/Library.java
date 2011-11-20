package com.iBeiKe.InfoPortal.lib;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

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
	   
	   /* 用ByteArrayBuffer做缓存 */
	            ByteArrayBuffer baf = new ByteArrayBuffer(50);
	            int current = 0;
	            
	            while((current = is.read()) != -1){
	                 baf.append((byte)current);
	            }
	            
	            /* 将缓存的内容转化为String， 用UTF-8编码 */
	            String myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
	           //myString = new String(baf.toByteArray());
	            show.loadUrl("http://lib.ustb.edu.cn:8080/opac/search_rss.php?" +
	            		"location=ALL&title="+val+"&doctype=ALL&lang_code=ALL" +
	            		"&match_flag=forward&displaypg=20&showmode=list&orderby=DESC" +
	            		"&sort=CATA_DATE&onlylendable=yes");
	            
	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	    }
}