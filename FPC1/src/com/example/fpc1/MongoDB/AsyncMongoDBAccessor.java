package com.example.fpc1.MongoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.example.fpc1.MongoDB.Response.ResponsecropSituationList;
import com.example.fpc1.MongoDB.Response.ResponsecropSituationQuery;
import com.example.fpc1.MongoDB.Response.ResponsefieldInfoQuery;
import com.example.fpc1.MongoDB.Response.ResponseedicodeQuery;
import com.google.gson.Gson;

/**
 *
 * MongoDBOperatorのメソッドを使って非同期アクセスを行うためのラッパークラス
 *
 * */
public class AsyncMongoDBAccessor extends AsyncTask<String, String, Void> {

	/** MongoDBにトークンリクエストを送るためのオペレートインスタンス*/
	private MongoDBTokenOperator mongoDBTokenOperator = new MongoDBTokenOperator();

	/** MongoDBにクエリーを送るためのオペレートインスタンス*/
	//コンストラクタの引数をしっかり決め打つ　field crop edicode とりあえずコレクション名だけあればよい
	private MongoDBQueryOperator mongoDBQueryOperator = new MongoDBQueryOperator();
	private MongoDBQueryOperator fieldQuery = new MongoDBQueryOperator("fieldInfo_sample");		//receive
	private MongoDBQueryOperator ediQuery = new MongoDBQueryOperator("edicode");				//receive
	private MongoDBQueryOperator cropQuery = new MongoDBQueryOperator("cropSituation_sample");	//send 

	/** 呼び出しもとのコンテキスト */
//	private Context context;

	public List<String> responseList;

	/** クエリーを送った後のレスポンス */
	private String responseQuery;

	/** ログ出力に使うタグ定数 */
	private static final String TAG = "MyAsyncTask";

	/** doInBackgroundのkey引数が無いときに使われるデフォルトキー */
	private static final String DEFAULT_KEYS = "[\"vegeCode\"]";

	/** doInBackgroundのquery引数が無いときに使われるデフォルトクエリー */
	private static final String DEFAULT_QUERY = "{\"workerID\":\"test\"}";


	public AsyncMongoDBAccessor(List<String> responseList){

		this.responseList = responseList;
	}


	public MongoDBTokenOperator getMongoDBTokenOperator() {
		return mongoDBTokenOperator;
	}


	public void setMongoDBTokenOperator(MongoDBTokenOperator mongoDBTokenOperator) {
		this.mongoDBTokenOperator = mongoDBTokenOperator;
	}


	public MongoDBQueryOperator getMongoDBQueryOperator() {
		return mongoDBQueryOperator;
	}

	public void setMongoDBQueryOperator(MongoDBQueryOperator mongoDBQueryOperator) {
		this.mongoDBQueryOperator = mongoDBQueryOperator;
	}


	@Override
	  protected void onPreExecute() {
	    Log.d(TAG, "onPreExecute");
	  }

	  @Override
	  protected Void doInBackground(String... params) {		//可変長引数
	    Log.d(TAG, "doInBackground ");

	    try {

	    	 String token = mongoDBTokenOperator.sendTokenRequest();

	    	 Log.d(TAG, "TokenRequest is " + token);

	    	 String query;

	    	 //キーとクエリーが引数にそろっていなかったらデフォルト値を使ってクエリーを要求
	    	 /**  それぞれのクエリを生成しておく */
	    	 if(params.length < 2)
	    		 query = mongoDBQueryOperator.sendQuery(token, DEFAULT_KEYS, DEFAULT_QUERY);
	    	 else
	    		 query = mongoDBQueryOperator.sendQuery(token, params[0], params[1]);

	    	 Log.d(TAG, "Query is " + query);

	    	 this.responseQuery = query;
	    	 this.responseQuery = query;
	    	 this.responseQuery = query;
	    	 

	    } catch (Exception e) {

	      Log.d(TAG, "Exception in doInBackground");
	      Log.d(TAG, e.getMessage());
	    }
		return null;
	  }

	  @Override
	  protected void onProgressUpdate(String... values) {
	    Log.d(TAG, "onProgressUpdate - " + values[0]);
	  }

	  @Override
	  protected void onPostExecute(Void result) {
	    Log.d(TAG, "onPostExecute - " + result);

	    Gson gson = new Gson();
	    ResponsecropSituationList r = gson.fromJson(responseQuery, ResponsecropSituationList.class);
	    ResponsefieldInfoQuery f = gson.fromJson(responseQuery, ResponsefieldInfoQuery.class);
	    ResponseedicodeQuery edi = gson.fromJson(responseQuery, ResponseedicodeQuery.class);
	 
	    System.out.println (responseQuery);
	    
	    Log.d("size", new Integer(r.List.size()).toString());
	    System.out.println ( r.List.get(0).vegeCode );    	
	    
	    for(int i=0 ; i< r.List.size();i++){
	    	this.responseList.add (String.valueOf(r.List.get(i).vegeCode));
    	}
	    System.out.println ( responseList.get(0) );    
	 
	   }

}
