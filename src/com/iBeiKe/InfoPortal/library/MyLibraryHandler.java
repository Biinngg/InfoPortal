package com.iBeiKe.InfoPortal.library;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class MyLibraryHandler {
  public static void main(String[] args) {
    try {
        String ur="http://csbbs.soufun.com/2710156784~-2~683/5236858_5236858.htm"; //获取远程网上的信息
        URL MyURL=new URL(ur);
        String str;
        URLConnection con=MyURL.openConnection();
        InputStreamReader ins=new InputStreamReader(con.getInputStream());
        BufferedReader in=new  BufferedReader(ins);
        StringBuffer sb = new StringBuffer();
        while ((str=in.readLine())!=null)
        {  
            sb.append(str);
        }
            in.close();
            
              Pattern p = Pattern.compile(".*<a href=\"(http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)\".*>社区</a>.*");
            Matcher m  = p.matcher(sb.toString());
            m.matches();
            System.out.println("这个社区的网址是"+m.group(1));
     }
    catch (MalformedURLException mfURLe) {
      System.out.println("MalformedURLException: " + mfURLe);
    }
    catch (IOException ioe) {
      System.out.println("IOException: " + ioe);
    }
  }
}