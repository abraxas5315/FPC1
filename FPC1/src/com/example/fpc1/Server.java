package com.example.fpc1;

import java.util.Vector;

import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;



public class Server extends Activity{

	String path;		//SDのパス
	Button bt1,bt2,pickButton,home;
	TextView tv;
	String picturePath;
	int SELECT_GALLERY_PICTURE=1;
	int RESULT_PICK_FILENAME = 1;

	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d("ftp","01");
	        LinearLayout ll=new LinearLayout(this);
	        ll.setOrientation(LinearLayout.VERTICAL);
	        setContentView(ll);


	        home=new Button(this);
	        home.setText("ホーム");
	        home.setWidth(40);
	        home.setHeight(40);

	        tv = new TextView(this);
	        tv.setText("画像のURLが表示されます。");
	        tv.setWidth(180);
	        tv.setHeight(40);

	        bt1=new Button(this);
	        bt1.setText("画像選択");
	        bt1.setWidth(80);
	        bt1.setHeight(40);

	        bt2=new Button(this);
	        bt2.setText("送信");
	        bt2.setWidth(80);
	        bt2.setHeight(40);

	        pickButton=new Button(this);
	        pickButton.setText("選択２");
	        pickButton.setWidth(80);
	        pickButton.setHeight(40);

	        ll.addView(home);
	        ll.addView(bt1);
	        ll.addView(tv);
	        ll.addView(bt2);
	        ll.addView(pickButton);

	        bt1.setOnClickListener(new BtClicklListener1());
	        bt2.setOnClickListener(new BtClicklListener2());

	        home.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	// メッセージの表示
	            	//Toast.makeText(Main.this, "カメラ起動", Toast.LENGTH_SHORT).show();
	            	Log.d("操作","ホームへ");
	                // Main画面への遷移
	                Intent intent = new Intent(Server.this,ModeSelect.class);
	                intent.putExtra("DIRECTORYPATH",path);	// 	パス
	                //intent.putExtra("CHECK_FLAG","true");	// ストレージのチェックをするか
	                //intent.putExtra("FLAG","true");	// オーバーレイするか

	                startActivity(intent);
	            }
	        });

	 }



	 public class BtClicklListener1 implements OnClickListener {

			@Override
			public void onClick(View v) {
				pickFilenameFromGallery();
			}
	 }

	 private void pickFilenameFromGallery() {
		    Intent i = new Intent(
		      Intent.ACTION_PICK,
		      Media.EXTERNAL_CONTENT_URI);
		    startActivityForResult(i, RESULT_PICK_FILENAME);
		  }
	 @Override
	  protected void onActivityResult(
	    int requestCode,
	    int resultCode,
	    Intent data) {

	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == RESULT_PICK_FILENAME
	      && resultCode == RESULT_OK
	      && null != data) {
	      Uri selectedImage = data.getData();
	      String[] filePathColumn = { Media.DATA };

	      Cursor cursor = getContentResolver().query(
	        selectedImage,
	        filePathColumn, null, null, null);
	      cursor.moveToFirst();

	      int columnIndex
	        = cursor.getColumnIndex(filePathColumn[0]);
	      picturePath = cursor.getString(columnIndex);
	      cursor.close();

	      Toast.makeText(
	        this,
	        picturePath,
	        Toast.LENGTH_LONG).show();
	    //送信処理
			Log.d("ftp","02");
	        Log.d("ftp","03");
	    }
	 }


	 public class BtClicklListener2 implements OnClickListener {

			@Override
			public void onClick(View v) {
				//送信処理
					Log.d("ftp","02");
					String URL = tv.getText().toString();
			        Log.d("ftp","03");
				new DataSend(null).execute("133.242.130.175", "22", "/root", "root", "a7epxfbfvv", "true",URL);/*渡してる値は使ってないよ*/
				/*川島くんガンバ＾＾*/
			}
	 }
	 /*Androidは非同期処理じゃないと通信許してくれないよ^^*/
	 public class DataSend extends AsyncTask <String, Integer, String>implements DialogInterface.OnCancelListener {

		    private Context myContext;
		    private FTPClient myFTPClient;

		    public DataSend(Context context) {

		        myContext = context;
		    }

		    // 非同期処理開始
		    @Override
		    protected void onPreExecute() {

		    }

		    // 非同期処理
		    @Override
		    protected String doInBackground(String... params) {
		    	JSch jsch=new JSch();

				// connect session
				Session session = null;
				try {
					Log.d("session","01");
					session = jsch.getSession("root", "133.242.130.175", 22);
				} catch (JSchException e3) {
					// TODO 自動生成された catch ブロック
					e3.printStackTrace();
				}
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.setPassword("a7epxfbfvv");
				try {
					Log.d("connect","01");
					session.connect();
					Log.d("connect","02");
				} catch (JSchException e3) {
					Log.d("connect","03");
					// TODO 自動生成された catch ブロック
					e3.printStackTrace();
				}

				// sftp remotely
				ChannelSftp channel = null;
				try {
					channel = (ChannelSftp)session.openChannel("sftp");
				} catch (JSchException e2) {
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
				}
				try {
					Log.d("channnel","01");
					channel.connect();
					ChannelSftp sftp = (ChannelSftp) channel;
					Log.d("Pict",picturePath);
					sftp.put(picturePath, "/var/www/html/agri/image");
					Log.d("channnel","02");
				} catch (JSchException e2) {
					Log.d("channnel","03");
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
				} catch (SftpException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				// ls
				Vector list = null;

				try {

					list = channel.ls(".");

				} catch (SftpException e1) {

					e1.printStackTrace();
				}

				channel.disconnect();
				session.disconnect();
				return null;

		    }
		    @Override
		    protected void onProgressUpdate(Integer... values) {}

		    // 非同期処理終了
		    @Override
		    protected void onPostExecute(String result) {

		    }

		    // キャンセル処理
		    @Override
		    public void onCancel(DialogInterface dialog) {

		    }
		    @Override
		    protected void onCancelled() {
		        try {myFTPClient.abort();} catch(Exception e) {}
		        Toast.makeText(myContext, "データ送信 キャンセル", Toast.LENGTH_SHORT ).show();
		    }
	 }
}







