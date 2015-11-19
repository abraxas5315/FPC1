package com.example.fpc1.MongoDB;

import java.io.IOException;

import com.example.fpc1.HTML.HttpOperator;
import com.example.fpc1.HTML.WebRequest;

/**
*
*  MongoDBへのクエリー要求を行うクラスです
*  テレスコーピングコンストラクタパターンで実装されていて、コンストラクタの引数を省いた場合デフォルト引数が使用されます
*
*  */
public class MongoDBQueryOperator {

	/** データベースにクエリーを送るためのデフォルトリクエストURL */
	private static final String DEFAULT_QUERY_URL = "http://api.farmoni.jsdc.net/v1/json/collection/item/";

	/** データベースにクエリーを送るためのデフォルトコレクション名 */
	private static final String DEFAULT_COLLECTION_NAME = "cropSituation_sample";

	/** クエリーリクエストを送る際にヘッダーに付ける情報 */
	private static final String HEADER_INFO = "Authorization";

	private String queryURL;

	private String collectionName;


	public MongoDBQueryOperator(){
		this(DEFAULT_QUERY_URL, DEFAULT_COLLECTION_NAME);
	}

	public MongoDBQueryOperator(String collectionName){
		this(DEFAULT_QUERY_URL, collectionName);
	}

	public MongoDBQueryOperator(String queryURL, String collectionName){

		this.queryURL = queryURL;
		this.collectionName = collectionName;

	}

	public String getQueryURL() {
		return queryURL;
	}

	public void setQueryURL(String queryURL) {
		this.queryURL = queryURL;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}


	public String sendQuery(String token, String keys, String query){

  		HttpOperator httpOperator =
  				new HttpOperator(this.queryURL + "?Name=" + this.collectionName + "&Keys=" + keys + "&Query=" + query, HEADER_INFO, token);

  		WebRequest webRequest = null;

  		try {

			webRequest = httpOperator.doGet();

		} catch (IOException e) {

			e.printStackTrace();
		}

  		return webRequest.getBody();
	}
}
