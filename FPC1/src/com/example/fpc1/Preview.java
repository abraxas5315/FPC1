package com.example.fpc1;


import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Preview extends Activity {
    /** Called when the activity is first created. */

	String path;
	
    // ExifInterfaceオブジェクトの宣言
    public ExifInterface exifInterface;

    String latitude, altitude, longitude;
    String roll, pitch, azimuch;
    String filePath;
    
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);
        Log.d("アクティビティ","sub");

        // Intent型インスタンスを取得
        Intent intent = getIntent();
        path = intent.getStringExtra("DIRECTORYPATH");
        filePath = intent.getStringExtra("FILEPATH");

        //撮影した画像のプレビュー
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
   	 	ImageView CameraImage = (ImageView) findViewById(R.id.imageView1);
        CameraImage.setImageBitmap(bitmap);
        
        Button homebutton = (Button)this.findViewById(R.id.homebutton);
        
        
        // ホーム画面へ
        homebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Log.d("操作","ホームへ");
                // Main画面への遷移
                Intent intent = new Intent(Preview.this,ModeSelect.class);
                intent.putExtra("DIRECTORYPATH",path);	// 	パス              
                startActivity(intent);

            	}
        	});
    	

        Button button2 = (Button)this.findViewById(R.id.button2);
        Button button3 = (Button)this.findViewById(R.id.button3);

        // 撮り直し（保存データを削除）
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// 画像を削除
                File picture = new File(filePath);
                picture.delete();
                // メッセージの表示
            	Toast.makeText(Preview.this, "保存しませんでした。", Toast.LENGTH_SHORT).show();
            	Log.d("操作","撮り直し");
            	// MainActivity画面への遷移
                Intent intent = new Intent(Preview.this, CameraView.class);
                intent.putExtra("DIRECTORYPATH",path);	// 	パス
                startActivity(intent);
            }
        });

        // 保存
        button3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// Exif書き込み
            	// メッセージの表示
            	Toast.makeText(Preview.this, "保存しました。", Toast.LENGTH_SHORT).show();
            	Log.d("操作","保存");
            	
            	
                // MainActivity画面への遷移
                Intent intent = new Intent(Preview.this,CameraView.class);
                intent.putExtra("DIRECTORYPATH",path);	// 	パス
                Log.d("deback","08");
                startActivity(intent);
                
            }
        });
    }

	// OnCreate後に呼ばれる
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	 super.onWindowFocusChanged(hasFocus);

	}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
