package com.example.fpc1.MongoDB.Response;

/**
 *
 * トークン取得リクエストを送ったときのレスポンスをJsonからjavaオブジェクトにコンバートするためのクラス
 *  Jsonレスポンスとの整合性をとるため命名規則を無視している
 *
 *   */
public class ResponseToken {

	public String Token;
	public String Expire;
	public String Response;

	public ResponseToken(String token, String expire, String response){
		Token = token;
		Expire = expire;
		Response = response;
	}
}