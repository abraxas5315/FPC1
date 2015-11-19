package com.example.fpc1.MongoDB;

import java.io.IOException;

import com.example.fpc1.HTML.HttpOperator;
import com.example.fpc1.HTML.WebRequest;
import com.example.fpc1.MongoDB.Response.ResponseToken;
import com.google.gson.Gson;

/**
 *
 *  MongoDBへのトークン要求を行うクラスです
 *  テレスコーピングコンストラクタパターンで実装されていて、コンストラクタの引数を省いた場合デフォルト引数が使用されます
 *
 *  */
public class MongoDBTokenOperator {

	/** トークンを取得するためのデフォルトリクエストURL */
	private static final String DEFAULT_TOKEN_URL = "http://api.farmoni.jsdc.net/v1/json/token/";

	/** トークンを取得するためのデフォルトAPIキー */
	private static final String DEFAULT_API_KEY = "3fd5aa9c63f9307c98b0eab8c716b2e918212400744d665adff6817b140acf1f";

	/** トークンリクエスト用URL */
	private String tokenURL;

	/** トークン取得用APIキー */
	private String apiKey;


	public MongoDBTokenOperator(){
		this(DEFAULT_TOKEN_URL, DEFAULT_API_KEY);
	}

	public MongoDBTokenOperator(String apiKey){
		this(DEFAULT_TOKEN_URL, apiKey);
	}

	public MongoDBTokenOperator(String tokenURL, String apiKey){
		this.tokenURL = tokenURL;
		this.apiKey = apiKey;
	}


    /** getter and setter*/

	public String getTokenRequest() {
		return tokenURL;
	}

	public void setTokenRequest(String tokenURL) {
		this.tokenURL = tokenURL;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}


	/** トークン取得リクエストを送る */
  	public String sendTokenRequest(){

  		HttpOperator httpOperator = new HttpOperator(this.tokenURL + "?Apikey=" + this.apiKey);

  		WebRequest webRequest = null;

  		try {
  			webRequest = httpOperator.doGet();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

  		Gson gson = new Gson();

  		ResponseToken rt = gson.fromJson(webRequest.getBody(), ResponseToken.class);

  		return rt.Token;
  	}



}
