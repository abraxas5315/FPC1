package com.example.fpc1.HTML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTPリクエストを送った結果を格納しておくバッファクラス
 * */
public class WebRequest {

	/** レスポンスステータス */
    private int status = 0;

    /** ヘッダー情報 */
    private LinkedHashMap header = new LinkedHashMap();

    /** レスポンスボディ */
    private String body = "";

    /** クッキー情報 */
    private List cookies = null;

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {

        this.status = status;
    }

    public LinkedHashMap getHeader() {

        return header;
    }

    public void setHeader(LinkedHashMap header) {

        this.header = header;
    }

    public String getBody() {

        return body;
    }

    public void setBody(String body) {

        this.body = body;
    }

    public List getCookies() {
        if (cookies != null) {
            return cookies;
        }
        cookies = new ArrayList();;
        Map headers = getHeader();
        Iterator it = headers.keySet().iterator();
        while (it.hasNext()){
            String key = (String)it.next();
            if ("Set-Cookie".equals(key)) {
                String val = headers.get(key).toString();
                Matcher m = Pattern.compile("^([a-zA-Z_]+)=([^;]+); path=(.+)$").matcher(val);
                if (m.find()){
                    String cookieKey  = m.group(1);
                    String cookieVal  = m.group(2);
                    String cookiePath = m.group(3);
                    Map cookie = new HashMap();
                    cookie.put("key", cookieKey);
                    cookie.put("val", cookieVal);
                    cookie.put("path", cookiePath);
                    cookies.add(cookie);
                }
            }
        }
        return cookies;
    }

    public String getCookie(String key) {
        List cookies = getCookies();
        for (Object cookie : cookies) {
            String cookieKey = (String)((Map) cookie).get("key");
            if (cookieKey.equals(key)) {
                return (String)((Map) cookie).get("val");
            }
        }
        return "";
    }
}