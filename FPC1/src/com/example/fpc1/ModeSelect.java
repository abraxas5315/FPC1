
package com.example.fpc1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fpc1.MongoDB.AsyncMongoDBAccessor;

//OnScrollListenerをimplementsする
public class ModeSelect extends Activity implements OnItemLongClickListener{

	 List<String> words = new ArrayList<String>();

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*同期処理呼び出し*/
     //   new DataSend(null).execute();

        
        //for(int i=0; i<100; i++) words.add("hakusai");
        AsyncMongoDBAccessor aa = new AsyncMongoDBAccessor(words);
        aa.execute();

        // リストアダプターを作成
        ListAdapter la = (ListAdapter)
			new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, words);

        //作成したリストアダプターをリストビューにセットする
        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(la);

        // リスナーを登録する
        lv.setOnItemLongClickListener(this);
    }
    // onItemLongClickをオーバーライドする
	@Override
	public boolean onItemLongClick(
		AdapterView<?> parent,
		View view, int position, long id) {
		Log.d("onItemClick",
			"position: " + String.valueOf(position));
		Toast.makeText(this,
			words.get(position), Toast.LENGTH_SHORT).show();


		/*モードセレクトへ、渡す値は野菜の名前*/

		//Stringをintentで渡す方法がわからんので...すまんの^^
		int beziInt= String_Int(words.get(position));
		Intent i = new Intent();
		setResult(beziInt, i);
		finish();
		return false;
	}

	private int String_Int(String word) {
		int result=-1;

		switch(word){
		case "hakusai":
			result=0;
			break;
		case "nasu":
			result=1;
			break;
		case "negi":
			result=2;
			break;
		case "tomato":
			result=3;
			break;
		}
		return result;
	}
/*
	public class DataSend extends AsyncTask <String, Integer, String>implements DialogInterface.OnCancelListener {
		    public DataSend(Context context) {

		    }

		    @Override
		    protected void onPreExecute() {

		    }

		    @Override
		    protected String doInBackground(String... params) {

		   	try {
		   		/*佐野さんと息を合わせて
		   		/*area.phpはDBから野菜を取得するphp
					URL testURL = new URL("http://133.242.130.175/agri/farm/js/area.php");
					URLConnection con = testURL.openConnection();
					con.setDoOutput(true);
					InputStreamReader isr = new InputStreamReader(con.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String line;
					int count1=0;
					while((line=br.readLine()) != null) {
					    String[] SQLResult = line.split(",", 0);
					    for (int i = 0 ; i < SQLResult.length ; i++){
					    String regex = "name";
					    Pattern p = Pattern.compile(regex);
					    Matcher m = p.matcher(SQLResult[i]);
					    if (m.find()){
					      System.out.println("マッチしました");
					      SQLResult[i] = SQLResult[i].substring(8);
					      SQLResult[i] = SQLResult[i].replaceAll(".gif\"","");
					    
					      words.set(count1,SQLResult[i]);
					      System.out.println(words.get(count1));
					      count1++;
					    }
					    }
					}
					br.close();
					isr.close();
					}
				catch(Exception e) {
					e.printStackTrace();
					}
				return null;
		    }
		    
		    
		    @Override
		    protected void onProgressUpdate(Integer... values) {}

		    @Override
		    protected void onPostExecute(String result) {

		    }

		    @Override
		    public void onCancel(DialogInterface dialog) {

		    }
		    @Override
		    protected void onCancelled() {

		    }
	 }
	 */
}
	