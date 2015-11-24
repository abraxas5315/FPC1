package com.example.fpc1.HTML;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.lang.Object;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.text.DateFormat;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

import android.util.Log;

public class HttpPostOperator {
	
	public void Post(){
	
		/** ログ出力に使うタグ定数 */
		String TAG = "POST";	
		String url = null;			//送信先
		String areaID = null;		//区画
		String workerID = null;		//利用者
		String fieldID = null;		//圃場
		int vegeCode = 0;			//野菜コード
		File photo = null;			//image file(bin)
		Date time = null;			//ISO8601 日付け
		
		// ISO 8601の日付け生成
	    DateTime dt = new DateTime();			
	    try {
	    	String t = dt.toString(ISODateTimeFormat.dateHourMinuteSecond());						//String型 "yyyy-MM-dd'T'HH:mm:ss"
	    	DateTime date = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").parseDateTime(t); 	//String型 → DateTime型
	    	time = date.toDate(); 																	//DateTime型 → Date型 
	    	System.out.println(date);
	    	} finally {
	    }
			
			/** 送信データの結合  */
		HttpPost httpPost = new HttpPost(url);
		DefaultHttpClient client = new DefaultHttpClient();
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("fieldID", fieldID);
			jsonObject.put("areaID", areaID);
			jsonObject.put("workerID", workerID);
			jsonObject.put("vegeCode", vegeCode);
			jsonObject.put("time", time);
			jsonObject.put("photo", photo);
			
			StringEntity se = new StringEntity(jsonObject.toString());
			httpPost.setEntity(se);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");
			HttpResponse response = client.execute(httpPost);
		    System.out.println(response);
		
		}catch (Exception e){
		    Log.d(TAG, "Exception in doInBackground");
		    Log.d(TAG, e.getMessage());
		}
	}	
}