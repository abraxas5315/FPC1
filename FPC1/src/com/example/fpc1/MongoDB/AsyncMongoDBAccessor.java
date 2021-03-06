package com.example.fpc1.MongoDB;

import java.util.List;
import android.os.AsyncTask;
import android.util.Log;
import com.example.fpc1.MongoDB.Response.ResponsecropSituationList;
import com.example.fpc1.MongoDB.Response.ResponsefieldInfoList;
import com.example.fpc1.MongoDB.Response.ResponseediCodeQuery;
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

	/** doInBackgroundのkey引数が無いときに使われるデフォルトキー */		//欲しいもの
	private static final String DEFAULT_KEYS = "[\"vegeCode\"]";
	/*private static final String field_KEYS = "[\"fieldID\",\"areaID\"]";
	private static final String crop_KEYS = "[\"vegeCode\"]"; 
	private static final String ediCode_KEYS = "[\"name\"]";
	*/
	/** doInBackgroundのquery引数が無いときに使われるデフォルトクエリー */	//一致条件等
	//TODO 変数を渡す処理
	private static final String DEFAULT_QUERY = "{\"workerID\":\"test\"}";
	/*private static final String field_QUERY = "{\"workerID\":\"test\"}";
	private static final String crop_QUERY = "{\"workerID\":\"test\",\"fieldID\":\"変数 \",\"areaID\":\"変数\"}";
	private static final String ediCode_QUERY = "{\"vegeCode\":\"変数\"}"; 
	*/

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
	    	 /*
	    	  switch(params[0]){
	    	  	case "fieldInfo":
	    	  		final String field_KEYS = "[\"fieldID\",\"areaID\"]";
	    	  		final String field_QUERY = "{\"workerID\":\""+params[1]+"\"}";
	    	  		query = fieldQuery.sendQuery(token, field_KEYS, field_QUERY);
	    	  		break;
	    	  	case "cropSituation":
	    	  		final String crop_KEYS = "[\"vegeCode\"]"; 
	    	  		final String crop_QUERY = "{\"fieldID\":\""+params[1]+"\",\"areaID\":\""+params[2]+"\",\"workerID\":\""+params[3]+"\"}";
	    	  		query = cropQuery.sendQuery(token, crop_KEYS, crop_QUERY);
	    	  		break;
	    	  	case "ediCode":
	    	  		final String ediCode_KEYS = "[\"name\"]";
	    	  		final String ediCode_QUERY = "{\"vegeCode\":\""+params[0]+"\"}";
	    	  		query = ediQuery.sendQuery(token, ediCode_KEYS, ediCode_QUERY);	    	  		
	    	  		break;
	    	  }
	    	  	*/

	    	 if(params.length < 2)
	    		 query = mongoDBQueryOperator.sendQuery(token, DEFAULT_KEYS, DEFAULT_QUERY);
	    	 else
	    		 query = mongoDBQueryOperator.sendQuery(token, params[0], params[1]);

	    	 Log.d(TAG, "Query is " + query);

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
	    
	    //TODO switch文で処理分け
	    
	    Gson gson = new Gson();
	    ResponsecropSituationList r = gson.fromJson(responseQuery, ResponsecropSituationList.class);
	    //ResponsefieldInfoQuery f = gson.fromJson(responseQuery, ResponsefieldInfoQuery.class);
	    //ResponseedicodeQuery edi = gson.fromJson(responseQuery, ResponseedicodeQuery.class);
	 
	    System.out.println (responseQuery);
	    
	    Log.d("size", new Integer(r.List.size()).toString());
	    System.out.println ( r.List.get(0).vegeCode );    	
	    
	    for(int i=0 ; i< r.List.size();i++){
	    	this.responseList.add (String.valueOf(r.List.get(i).vegeCode));
    	}
	    System.out.println ( responseList.get(0) );    	 
	   }
}
