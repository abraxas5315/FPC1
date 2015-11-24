package com.example.fpc1.HTML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * HTTPリクエスト操作クラス
 * コンストラクタでヘッダー情報を付加するとヘッダー付きGETリクエストが可能になります
 *
 * */
public class HttpOperator {

	/** リクエストを送るURL */
	private String url;

	/** エンコード方式 */
    private String webEncode = "utf-8";

    /** ヘッダー情報 */
    private String[] header = {"", ""};

    public HttpOperator(String url){
    	this(url, "utf-8", "", "");
    }

    public HttpOperator(String url, String encode){
        this(url, encode, "", "");
    }

    public HttpOperator(String url, String headerKey, String headerValue){
    	this(url, "utf-8", headerKey, headerValue);
    }

    public HttpOperator(String url, String encode, String headerKey, String headerValue){
    	this.url = url;
        this.webEncode = encode;
        this.header[0] = headerKey;
        this.header[1] = headerValue;
    }

    /**
     * GETリクエストをおくる
     * */
    public WebRequest doGet() throws IOException {

        URL urlObj = new URL(url);
        HttpURLConnection http = (HttpURLConnection) urlObj.openConnection();
        http.setRequestMethod("GET");

        if(!(header[0].equals("") || header[1].equals("")))
        http.setRequestProperty(header[0], header[1]);

        http.connect();

        // 結果を取得
        return getResponse(http, webEncode);
    }

    /**
     * POSTリクエストをおくる
     * @param postData POSTするデータ
     * */
    public WebRequest doPost(Map postData) throws IOException {

        // //////////////////////////////////////
        // リスエスト情報の組み立て
        // //////////////////////////////////////
        Iterator it = postData.keySet().iterator();
        StringBuilder sbParam = new StringBuilder();			//StringBuilderクラスは文字列をくっつける事ができる
        while (it.hasNext()) {
            String key = (String) it.next();
            String val = (String) postData.get(key);
            key = URLEncoder.encode(key, webEncode);
            val = URLEncoder.encode(val, webEncode);
            if (sbParam.length() > 0) {
                sbParam.append("&");
            }
            sbParam.append(key).append("=").append(val);
        }

        URL urlObj = new URL(url);
        HttpURLConnection http = (HttpURLConnection) urlObj.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Accept-Language", "ja");

        // //////////////////////////////////////
        // リスエストの送信
        // //////////////////////////////////////
        OutputStream os = http.getOutputStream();
        PrintStream ps = new PrintStream(os);
        ps.print(sbParam.toString());// データをPOSTする
        ps.close();

        // //////////////////////////////////////
        // レスポンスの取得
        // //////////////////////////////////////
        return getResponse(http, webEncode);
    }

    /**
     * レスポンスデータを取得する
     * @param http http接続オブジェクト
     * @param webEncode エンコーディング
     *  */
    private WebRequest getResponse(HttpURLConnection http, String webEncode) throws IOException {

        WebRequest response = new WebRequest();

        // ステータスコードの取得
        response.setStatus(http.getResponseCode());

        // ヘッダの取得
        LinkedHashMap resHeader = new LinkedHashMap();
        Map header = http.getHeaderFields();
        Iterator headerIt = header.keySet().iterator();
        while (headerIt.hasNext()) {
            String key = (String) headerIt.next();
            List valList = (List) header.get(key);
            if (key != null) {
                StringBuilder sb = new StringBuilder();
                for (Object val : valList) {
                    if (sb.length() > 0)
                        sb.append("\n");
                    sb.append(val);
                }
                resHeader.put(key, sb.toString());
            }
        }
        response.setHeader(resHeader);

        // ボディ(コンテンツ)の取得
        InputStream is = http.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, webEncode));
        StringBuilder sbBody = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null) {
            sbBody.append(s);
            sbBody.append("\n");
        }
        response.setBody(sbBody.toString());

        return response;
    }
}