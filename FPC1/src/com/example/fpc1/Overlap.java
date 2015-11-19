package com.example.fpc1;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Overlap extends Activity implements AnimationListener {

	ImageView CameraImage1;
	ImageView CameraImage2;
	String path;
	File[] filelist;
	String[] filename;
	int view_width = 0;
	int view_height = 0;
	int touchcount =0;		//タッチ回数カウント（touchcount>filelist.length）
	int viewnumber = 1;	//CameraImageの番号（1or2）


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overlap);

		Intent intent = getIntent();
		path = intent.getStringExtra("DIRECTORYPATH");
		CameraImage1 = (ImageView) findViewById(R.id.imageView1);
		CameraImage2 = (ImageView) findViewById(R.id.imageView2);

		 Toast.makeText(Overlap.this, "画面右側をタップすると進みます。\n"
				 	+"画面左側をタップすると戻ります。", Toast.LENGTH_SHORT).show();
	}
	
	

	// OnCreate後に呼ばれる
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	 super.onWindowFocusChanged(hasFocus);

	 
	   Button homebutton = (Button)this.findViewById(R.id.homebutton);
       
       // ホーム画面へ
       homebutton.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
           	// メッセージの表示
           	Log.d("操作","ホームへ");
               // Main画面への遷移
               Intent intent = new Intent(Overlap.this,ModeSelect.class);
               intent.putExtra("DIRECTORYPATH",path);	// 	パス
               
               startActivity(intent);

           	}
       	});
   	
   		//SDカードのパスを指定
   		File dir = new File(path);
   		//指定したディレクトリのファイル一覧を取得
   		filelist = dir.listFiles();
   		//ファイルの名前を取得
   		filename  = new String[filelist.length];
   		for(int i=0; i<filelist.length; i++) {
   			filename[i] = filelist[i].getName();
   			Log.d("画像ファイル"+i, filename[i]);
   		}

		// Viewサイズの取得
    	view_width = CameraImage1.getWidth();
    	// 一枚目の画像を表示
   		Bitmap bitmap = BitmapFactory.decodeFile(path + filename[touchcount]);
   		CameraImage1.setImageBitmap(bitmap);

	}

	//画面をタッチした時のイベント
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int count = touchcount;
		//Bitmap bitmap;
		int view_x = (int)event.getX();		//x軸：画面タッチ位置の座標を取得
		int view_y = (int)event.getY();		//y軸

		//画面を押した時の処理
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			//画面右側を押した場合：進む
			if(view_x>view_width/2){
				if(touchcount==filelist.length-1) {	//最後の画像だった場合
					count = filelist.length-1;
					touchcount = 0;
					Log.d("最初に戻る", String.valueOf(touchcount));
					overlap(count);	//最初に戻る
				} else { //通常処理
					count = touchcount;
					touchcount++ ;
					Log.d("進む", String.valueOf(touchcount));
					overlap(count);	//進む
				}
			//画面左側を押した場合：戻る
			} else {
				if(touchcount==0) {	//最初の画像だった場合
					count = 0;
					touchcount = filelist.length-1;
					Log.d("最後に進む", String.valueOf(touchcount));
					overlap(count);	//最後に進む
				} else { //通常処理
					count = touchcount;
					touchcount-- ;
					Log.d("戻る", String.valueOf(touchcount));
					overlap(count);	//戻る
				}
			}
		}
		return true;
	}
	
	

	//画像のオーバーラップ
	public void overlap(int count) {

   		AlphaAnimation alpha = new AlphaAnimation((float)0.0, (float)1.0); 	//フェードイン
   		alpha.setDuration(1200); // 1200ms(1.2秒)かけてアニメーションする

		// CameraImage2表示している画像をCameraImage1に描画
   		Bitmap bitmap1 = BitmapFactory.decodeFile(path + filename[count]);
   		CameraImage1.setImageBitmap(bitmap1);
   		// オーバーラップさせる画像をCameraImage2にフェードインさせる
   		Bitmap bitmap2 = BitmapFactory.decodeFile(path + filename[touchcount]);
   		CameraImage2.startAnimation(alpha); // アニメーション適用
   		CameraImage2.setImageBitmap(bitmap2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO 自動生成されたメソッド・スタブ
	}

}

