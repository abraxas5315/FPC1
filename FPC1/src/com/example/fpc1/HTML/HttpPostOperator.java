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
import org.joda.time.format.ISODateTimeFormat;
import android.util.Log;

public class HttpPostOperator {
	
	/** ログ出力に使うタグ定数 */
	private static final String TAG = "POST";	
	public String url ;				//送信先
	public String areaID ;			//区画
	public String workerID ;		//利用者
	public String fieldID;			//圃場
	public int vegeCode ;			//野菜コード
	public File photo ;				//image file(bin)
	public Date time;				//ISO8601 日時
	
	
	
	public void Post(){
		
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