package com.example.fpc1;

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
import org.joda.time.format.ISODateTimeFormat;

import android.app.Activity;
import android.util.Log;

public class Time extends Activity {
	
	public Time(){

	// ISO 8601の日付け生成
	DateTime dt = new DateTime();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");					//この形式の文字列が生成される
	try {
		Date time = df.parse(dt.toString(ISODateTimeFormat.dateHourMinuteSecond()));	//文字列を生成してから変換
		System.out.println(time);
	} catch (ParseException e1) {
		// TODO 自動生成された catch ブロック
		e1.printStackTrace();
	}
	
}
}