/**
 * 
 */
package com.iBeiKe.InfoPortal.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class MyLibraryFetcher {
	private static String url, myLibraryUrl;
	private static List<NameValuePair> nvps;
	
	public MyLibraryFetcher(String myLibraryUrl, Map<String,String> loginData) {
		this.myLibraryUrl = myLibraryUrl;
		loginDataHandler(loginData);
	}
	
	private void loginDataHandler(Map<String,String> loginData) {
		url = loginData.get("url");
		loginData.remove("url");
		nvps = new ArrayList<NameValuePair>();
		for(String key:loginData.keySet()) {
			nvps.add(new BasicNameValuePair(key,loginData.get(key)));
		}
	}
	
    public static void fetchData() throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            entity.consumeContent();
        }
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            Log.e("Cookies", "is empty");
        }
        
        HttpPost httpost = new HttpPost(url);
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        response = httpclient.execute(httpost);
        entity = response.getEntity();
        if (entity != null) {
            entity.consumeContent();
        }
        
        httpget = new HttpGet(myLibraryUrl);
        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpget, responseHandler);
        writeFile("/sdcard/result.html", responseBody);
        Log.d("MyLibraryFetcher", "end");
    }
    public static void writeFile(String path, String content) {
        String s = new String();
        String s1 = new String();
        try {
            File f = new File(path);
            if (f.exists()) {
                System.out.println("文件存在");
                f.delete();
            } else {

            }
            System.out.println("文件不存在，正在创建...");
            if (f.createNewFile()) {
                System.out.println("文件创建成功！");
            } else {
                System.out.println("文件创建失败！");
            }
            BufferedReader input = new BufferedReader(new FileReader(f));

            while ((s = input.readLine()) != null) {
                s1 += s + "\n";
            }
            // System.out.println("文件内容：" + s1);
            input.close();
            s1 += content;

            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            output.write(s1);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}