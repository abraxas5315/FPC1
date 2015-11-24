package com.example.fpc1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
	String name="分類なし";
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		String beziString="0";
		switch(resultCode){
		case 0:
			beziString="hakusai";
			break;
		case 1:
			beziString="nasu";
			break;
		case 2:
			beziString="negi";
		case 3:
			beziString="tomato";
			break;
		case -1:
			beziString="error";
			break;

		}
		name=beziString;
	}


	String path;		//SDのパス
	String filepath; 	//ファイルパス

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modeselect);

		
		String gs = android.provider.Settings.Secure.getString(getContentResolver(),
		          android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (gs.indexOf("gps", 0) < 0) {
			//if (!termGpsService) {
			// GPSサービスがOFFになっているので、メッセージ表示
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("GPSが”OFF”になっています");
			alertDialog.setMessage("このアプリケーションでは、GPSサービスによる位置情報の取得が必要になります。[設定]よりGPSサービスを”ON”にしてください。");
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 端末のGPS設定画面へ誘導する
					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					startActivity(intent);
				}
			}).create().show();
		}


		//外部ストレージの確認
		storagecheck();
		//SDのパスを取得
		//path = getMount_sd() + "/FPCpicture/";
		//Log.d("path", path);

        Button cameraButton = (Button)this.findViewById(R.id.camera);
        Button previewButton = (Button)this.findViewById(R.id.preview);
  //      Button naviButton = (Button)this.findViewById(R.id.button3);
        Button homebutton = (Button)this.findViewById(R.id.homebutton);
       // text.setText(name);
        
        // ホーム画面へ
        homebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
            	Log.d("操作","ホームへ");
                // Main画面への遷移
            	Intent i = new Intent(getApplicationContext(),ModeSelect.class);
            	startActivityForResult(i,0);
            }
        });


        // カメラ起動
        cameraButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
            	//Toast.makeText(Main.this, "カメラ起動", Toast.LENGTH_SHORT).show();
            	Log.d("操作","カメラ起動");
                // CameraView画面への遷移
                Intent intent = new Intent(Main.this,CameraView.class);
                intent.putExtra("DIRECTORYPATH",path+name);	// 	パス
                //intent.putExtra("CHECK_FLAG","true");	// ストレージのチェックをするか
                //intent.putExtra("FLAG","true");	// オーバーレイするか

                startActivity(intent);
            }
        });

        // プレビュー起動
        previewButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
            	Toast.makeText(Main.this, "プレビュー起動", Toast.LENGTH_SHORT).show();
            	Log.d("操作","プレビュー起動");
                // Overlap画面への遷移
            	Intent intent = new Intent(Main.this, Overlap.class);
            	intent.putExtra("DIRECTORYPATH", path);	// 	ディレクトリパス
                startActivity(intent);
            }
        });
        
        
/*
        // ナビゲート開始
     naviButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
            	Toast.makeText(Main.this, "ナビゲート開始", Toast.LENGTH_SHORT).show();
            	Log.d("操作","ナビゲート開始");
                // Naivgation画面への遷移
                Intent intent = new Intent(Main.this, Navigation.class);
                intent.putExtra("DIRECTORYPATH",path);	// 	ディレクトリパス
                //intent.putExtra("CHECK_FLAG","true");	// ストレージのチェックをするか
                //intent.putExtra("FLAG","true");	// オーバーレイするか
                startActivity(intent);
            }
        });
*/
    }






	// OnCreate後に呼ばれる
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	 super.onWindowFocusChanged(hasFocus);

	}




	//外部ストレージの確認
	public void storagecheck(){

		// SDカードがマウントされているかの判断(マウントされていた場合Environment.MEDIA_MOUNTEDを返す)
		/*
		String status   = Environment.getExternalStorageState();
		 if (status.equals(Environment.MEDIA_REMOVED)) {		// SDカードが接続されていない場合 //Stringクラスequalsメソッド:文字列の比較
			 Log.d("ストレージチェック","内部ストレージ");
			 Toast.makeText(Main.this, "外部ストレージを認識できません。", Toast.LENGTH_SHORT).show();
			 path = Environment.getExternalStorageDirectory().getPath();// 内部ストレージのパスを取得

		 }else if(status.equals(Environment.MEDIA_MOUNTED)) {	// SDカードが接続されている場合
			 Toast.makeText(Main.this, "外部ストレージを認識しました。", Toast.LENGTH_SHORT).show();
			 Log.d("ストレージチェック","外部ストレージ");
			 path = getMount_sd();

		 }else {
			 Log.d("ストレージチェック","エラー");
			 Toast.makeText(Main.this, "エラー1が発生しました。", Toast.LENGTH_SHORT).show();
			 return;
		 }
		 */
		 //path=Environment.getDataDirectory().getPath();
		 path = Environment.getExternalStorageDirectory().getPath();// 内部ストレージのパスを取得


		 // 指定されたフォルダがあるか確認
		 File directory = new File(path+"/FPCpicture/");
		 if (directory.exists()){
			 Log.d("ストレージチェック", path+"/FPCpicture/" + "を確認しました。");
			 path = path+"/FPCpicture/";
		 }else{ // path+"/FPCpicture/"が存在しない場合は作成
			 Log.d("ストレージチェック", path+"/FPCpicture/" + "を確認できません。");
			 Log.d("ストレージチェック", path+"/FPCpicture/" + "を作成します。");
			 Toast.makeText(Main.this, path+"/FPCpicture/ を作成します。", Toast.LENGTH_SHORT).show();
			 directory.mkdir();
			 path = path+"/FPCpicture/";
		 }

		 // 画像データがあるか確認
		 //directorycheck();
	}

    // SDカードのマウント先をゲットするメソッド
	@SuppressLint("NewApi")
	public String getMount_sd() {
       List<String> mountList = new ArrayList<String>();
       String mount_sdcard = null;

       Scanner scanner = null;
       try {
          // システム設定ファイルにアクセス
          File vold_fstab = new File("/system/etc/vold.fstab");
          scanner = new Scanner(new FileInputStream(vold_fstab));
          // 一行ずつ読み込む
          while (scanner.hasNextLine()) {
             String line = scanner.nextLine();
             // dev_mountまたはfuse_mountで始まる行の
             if (line.startsWith("dev_mount") || line.startsWith("fuse_mount")) {
                // 半角スペースではなくタブで区切られている機種もあるらしいので修正して
                // 半角スペース区切り３つめ（path）を取得
                String path = line.replaceAll("\t", " ").split(" ")[2];
                // 取得したpathを重複しないようにリストに登録
                if (!mountList.contains(path)){
                   mountList.add(path);
                }
             }
          }
       } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
       } finally {
          if (scanner != null) {
             scanner.close();
          }
       }
       // Environment.isExternalStorageRemovable()はGINGERBREAD以降しか使えない
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
          // getExternalStorageDirectory()が罠であれば、そのpathをリストから除外
          if (!Environment.isExternalStorageRemovable()) {   // 注1
             mountList.remove(Environment.getExternalStorageDirectory().getPath());
          }
       }
       // マウントされていないpathは除外
       for (int i = 0; i < mountList.size(); i++) {
          if (!isMounted(mountList.get(i))){
             mountList.remove(i--);
          }
       }
       // 除外されずに残ったものがSDカードのマウント先
       if(mountList.size() > 0){
          mount_sdcard = mountList.get(0);
       }
       // マウント先をreturn（全て除外された場合はnullをreturn）
       return mount_sdcard;
    }

    // 引数に渡したpathがマウントされているかどうかチェックするメソッド
    public boolean isMounted(String path) {
       boolean isMounted = false;
       Scanner scanner = null;
       try {
          // マウントポイントを取得する
          File mounts = new File("/proc/mounts");   // 注2
          scanner = new Scanner(new FileInputStream(mounts));
          // マウントポイントに該当するパスがあるかチェックする
          while (scanner.hasNextLine()) {
             if (scanner.nextLine().contains(path)) {
                // 該当するパスがあればマウントされているってこと
                isMounted = true;
                break;
             }
          }
       } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
       } finally {
          if (scanner != null) {
          scanner.close();
          }
       }
       // マウント状態をreturn
       return isMounted;
    }



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
