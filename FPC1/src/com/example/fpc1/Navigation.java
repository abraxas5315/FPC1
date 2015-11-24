package com.example.fpc1;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Navigation extends Activity implements LocationListener, SensorEventListener, SurfaceHolder.Callback {
    /** Called when the activity is first created. */

	public static final String TAG = "mainSurfaceView";
	public SurfaceView SurfaceViewArrow;
	public SurfaceView mainSurfaceView;
	public SurfaceHolder holder;
    boolean click_flag = false;	//クリックしたか判定
    boolean flag = false;			//以前撮影したか（フォルダに画像が保存されているか）
    int i = 0;

	public ExifInterface exifInterface;
    //Dateオブジェクトを作成
	public Date date1 = new Date();
	String path;
	String filename;

    //TextViewオブジェクトの宣言
    TextView textView_firstlat;		//初回の緯度
    TextView textView_firstlon;	//初回の経度
    TextView textView_lat;			//緯度
    TextView textView_lon;			//経度
    TextView textView_disx;			//距離
    TextView textView_disy;			//距離
    TextView textView_d;				//目的地までの距離

    //LocationManagerオブジェクトの宣言
    private LocationManager locmanager;
	double firstlat = 0.0;		//38.31254792;
	double firstlon = 0.0;		//140.79617216;
	double azimuth = 0.0;

	//緯度、経度を取得
	double latitude = 0.0;
	double longitude = 0.0;
	double dislat = 0.0; 		//緯度の差
	double dislon = 0.0;		//経度の差
	double r = 6378150;		//地球の半径(m)
	double d = 0.0;				//距離d
	double fai = 0.0;			//方位角Φ
	double Dbar = 15;	//バーの長さ

    //SensorManagerオブジェクトの宣言
	SensorManager sensorManager;
	private static float orientation;	//回転角

	Resources res;
	Bitmap arrow;
	public Canvas canvas;
	public Paint paint;
	int surfaceViewX = 0;
	int surfaceViewY = 0;
	int centerX = 0;
	int centerY = 0;
	int drawX = 0;
	int drawY = 0;

	// サイクル ------------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("tag","onCreate is called");
        super.onCreate(savedInstanceState);

        // Intent型インスタンスを取得
        Intent intent = getIntent();
        path = intent.getStringExtra("DIRECTORYPATH");
 		//ディレクトリのチェック
		directorycheck();

        setContentView(R.layout.navigation);
        // 描画用SurfaceViewの設定
        SurfaceViewArrow = (SurfaceView)findViewById(R.id.surfaceView1);
        holder = SurfaceViewArrow.getHolder();
        holder.addCallback(this);        // コールバック関数の登録

        //TextViewの生成
        textView_firstlat = (TextView)findViewById(R.id.firstlat);
        textView_firstlon = (TextView)findViewById(R.id.firstlon);
        textView_lat = (TextView)findViewById(R.id.latitude);
        textView_lon = (TextView)findViewById(R.id.longitude);
        textView_disx = (TextView)findViewById(R.id.dis_x);
        textView_disy = (TextView)findViewById(R.id.dis_y);
        textView_d = (TextView)findViewById(R.id.d);

        //初回の緯度経度を表示
		textView_firstlat.setText("緯度：" + Double.toString(firstlat));
		textView_firstlon.setText("経度：" + Double.toString(firstlon));

		//画像読み込み
		res = getBaseContext().getResources();
		arrow = BitmapFactory.decodeResource(res, R.drawable.arrow3);

		try {
			exifInterface = new ExifInterface(path+filename);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
			return;
		}

        // ボタンを押した時の動作設定
        Button button1 = (Button)this.findViewById(R.id.camera);
        Button button2 = (Button)this.findViewById(R.id.preview);
        Button homebutton = (Button)this.findViewById(R.id.homebutton);

        // ホーム画面へ
        homebutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
            	Log.d("操作","ホームへ");
                // Main画面への遷移
                Intent intent = new Intent(Navigation.this,ModeSelect.class);
                intent.putExtra("DIRECTORYPATH",path);	// 	パス

                startActivity(intent);

            	}
        	});


        // ナビゲート開始
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
             	Log.d("操作","カメラ起動");
        		//画像が保存されていればナビゲート
        		if(flag == true){
        			//GPSで位置情報を取得
        			gps();
        			//クリックを認識
        			click_flag = true;
        		}
        		else Toast.makeText(Navigation.this, "カメラ機能で撮影してください。", Toast.LENGTH_SHORT).show();
            }
        });

        // カメラ起動
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// メッセージの表示
            	Toast.makeText(Navigation.this, "カメラ起動", Toast.LENGTH_SHORT).show();
            	Log.d("操作","カメラ起動");
        		// CameraView画面への遷移
            	Intent intent = new Intent(Navigation.this, CameraView.class);
            	intent.putExtra("DIRECTORYPATH", path);	// 	ディレクトリパス
                startActivity(intent);
            }
        });

    }

	private void gps() {
		// TODO 自動生成されたメソッド・スタブ
		locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    //矢印を表示するために中心座標を計算
		surfaceViewX = SurfaceViewArrow.getWidth() ;
		surfaceViewY = SurfaceViewArrow.getHeight() ;
	    centerX = surfaceViewX / 2;
	    centerY = surfaceViewY / 2;
	    drawX = centerX - arrow.getWidth() / 2;		//中心x
	    drawY = centerY - arrow.getHeight() / 2;	//中心y
		getExif();
    }

    public void onResume(){
        super.onResume();
        Log.d("tag", "onResume is called");

      //ロケーションマネージャの取得
        locmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //センサーマネージャーの取得
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //リスナー登録
  		sensorManager.registerListener(
  				this,
  				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),	//加速度センサ
  				SensorManager.SENSOR_DELAY_NORMAL);

    }

	@Override
	protected void onPause() {
		super.onPause();
		click_flag = false;
       //解除
        locmanager.removeUpdates(this);
        //リスナー解除
        sensorManager.unregisterListener(this);

	}

	@Override
	public void onStop(){
	    super.onStop();
	    click_flag = false;
	    //解除
        locmanager.removeUpdates(this);
        //リスナー解除
        sensorManager.unregisterListener(this);
	}

    public void onDestroy(){
        super.onDestroy();
        click_flag = false;
        Log.d("tag","onDestory is called");

        //解除
        locmanager.removeUpdates(this);
        //リスナー解除
        sensorManager.unregisterListener(this);
    }

	//画像データがあるか確認
	public void directorycheck(){
		//パスを指定
		File dir = new File(path);

		//指定したディレクトリのファイル名を取得
		final File[] filelist = dir.listFiles();
		if(filelist.length == 0){
			Log.d("ディレクトリチェック","画像なし");	//初回起動であるということ
			flag = false;
		}else {
			Log.d("ディレクトリチェック","画像あり");	//前回撮影したということ
			flag = true;
			filename = compare();
			Log.d("filename", filename);
		}
		Log.d("画像ファイル数", String.valueOf(filelist.length));
	}

    //初回撮影時の画像ファイルを決定
    public String compare() {
   		// TODO 自動生成されたメソッド・スタブ

       	int i=0; //ループ用
   		int n=0;	//最新の画像
   		int diff =0;	//更新日時の差
   		Date da1 = null,da2 = null; //更新日時

   		//画像ファイルパスを指定
   		File dir = new File(path);
   		//指定したディレクトリのファイル名を取得
   		File[] filelist = dir.listFiles();

		if(filelist.length>=1){
			//更新日時の比較
			for (i = 1; i < filelist.length; i++) {
				//更新日時の取得
				da1 =new Date(filelist[n].lastModified());
				da2 =new Date(filelist[i].lastModified());
				//更新日時の比較
				diff = da1.compareTo(da2);
   		 		if(diff>0){
   		 			n=i;
   		 		}
   			}
   		}
   		return filelist[n].getName();
    }

    // Exif情報の取得 -----------------------------------------------------------------------------------------------
    private void getExif() {
		// TODO 自動生成されたメソッド・スタブ

    	if(exifInterface != null) {

    		//緯度・経度の取得
    		String strlat = exifInterface.getAttribute (ExifInterface.TAG_GPS_LATITUDE);
    		String strlon = exifInterface.getAttribute (ExifInterface.TAG_GPS_LONGITUDE);
    		//logcatへ表示
    		Log.d(TAG, "first_latitude : " + strlat);
    		Log.d(TAG, "first_longitude : " + strlon);

    		Log.d("Exif", "読み込みました");

    		//60進10進変換
    		firstlat = getlatlong(strlat);
       		Log.d("first_latitude：",String.valueOf(firstlat));
       		firstlon = getlatlong(strlon);
       		Log.d("first_longitude：",String.valueOf(firstlon));

			//初回の緯度経度を表示
			textView_firstlat.setText("緯度：" + String.valueOf(firstlat));
			textView_firstlon.setText("経度：" + String.valueOf(firstlon));
		 }
	}

	//60進10進変換
	private double getlatlong(String str) {
		// TODO 自動生成されたメソッド・スタブ

		double num=0;
    	double num1=0;
  	  	double num2=0;
  	  	double num3=0;
  	  	//分割
  	  	String[] strAryAll = str.split("/");				// strAryAll[0]/strAryAll[1]/strAryAll[2]/strAryAll[3]
  	  	String[] strAry2 = strAryAll[1].split(",");		// strAryAll[1]→/strAry2[0],strAry2[1]/
  	  	String[] strAry3 = strAryAll[2].split(",");		// strAryAll[2]→/strAry3[0],strAry3[1]/

  	  	for(int i=0;i<strAryAll.length;i++){
  	  		Log.d("temp：",strAryAll[i]);
  	  	}
  	  	Log.d("strAry2[1]：",strAry2[1]);Log.d("strAry3[1]",strAry3[1]);
  	  	//String→int
  	  	num1 = Integer.parseInt(strAryAll[0]);
  	  	num2 = Integer.parseInt(strAry2[1]);
  	  	num3 = Double.parseDouble(strAry3[1]);
  	  	num3 = num3/1000000;//38.…、140.…に戻す

  	  	num = num1 + num2/60 + num3/60/60;

   		//元データをBigDecimal型にする
   		BigDecimal bd = new BigDecimal(num);
   		//小数第7位で四捨五入
   		BigDecimal bd2 = bd.setScale(9, BigDecimal.ROUND_HALF_UP);
   		//BigDecimal型をdouble型にする
   		num = bd2.doubleValue();

		return num;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	    Log.d(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    Log.d(TAG, "surfaceDestroyed");
	    //スレッドを終了
        //thread = null;
        //矢印画像の解放
        arrow.recycle();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    Log.d(TAG, "surfaceCreated");
		canvas = holder.lockCanvas();
		// 背景を白にする
		canvas.drawColor(Color.BLACK);
		// 画面に描画をする
		holder.unlockCanvasAndPost(canvas);
	}



	// 処理部 --------------------------------------------------------------------------------------------------------------

	//GPS制御
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		latitude = location.getLatitude();
		longitude = location.getLongitude();

	    //Dateオブジェクトを作成
		Date date1 = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("日時：yyyy年MM月dd日 HH:mm:ss");
		//取得できた緯度経度を表示
		textView_lat.setText("緯度：" + Double.toString(latitude));
		textView_lon.setText("経度：" + Double.toString(longitude));
		//目的地までの差を求める
		goal();
	}

	//センサ制御
	@Override
 	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ORIENTATION:
			//北向きをゼロ度とする方位の角度
			orientation = event.values[0];

			//ボタンが押されたら描画開始
			if(click_flag == true){
				//矢印の描画
				doDraw();
			}
			break;
		}
	}

	//描画処理
	public void doDraw() {
		canvas = holder.lockCanvas();
		canvas.drawColor(0,Mode.CLEAR); //前回の描画を消去
		//canvas.drawColor(Color.WHITE);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(30);
		canvas.drawLine( 15, surfaceViewY-25, (float) Dbar, surfaceViewY-25, paint);
		//canvas.save();
		canvas.rotate( (float)(-orientation-fai), centerX, centerY);
		canvas.drawBitmap(arrow, drawX, drawY, paint);
		//canvas.restore();

	  	holder.unlockCanvasAndPost(canvas);
	}

	// -------------------------------------------------------------------------------------------------------------

	//目的地までの差を求める
	private void goal() {
		// TODO 自動生成されたメソッド・スタブ
		double disx = 0;
		double disy = 0;
		double dis = 0;

		dislat = firstlat-latitude;
		dislon = firstlon-longitude;	//目的地までの方位角の導出にも用いる

		//目的地までの方位角を計算
		ArrowAzimuth();
    	Log.d("fai", Double.toString(fai));

		// 緯度経度の値を＋にする
		disy = Math.pow(dislat, 2);	//2乗
		disy = Math.sqrt(disy);			//平方根
		disx = Math.pow(dislon, 2);
		disx = Math.sqrt(disx);

		//ｍ単位で距離を計算
		disy = distancey(disy);
		if(Math.signum(dislat)==1){	//北へ（符号＋）
			textView_disy.setText("北へ " + disy + " m" );
		} else {
			textView_disy.setText("南へ " + disy + " m" );
		}
		disx = distancex(disx);
		if(Math.signum(dislon)==1){	//東へ（符号＋）
			textView_disx.setText("東へ " + disx + " m" );
		} else {
			textView_disx.setText("西へ " + disx + " m" );
		}

		//直線距離を計算
		d = Math.sqrt( Math.pow(disy, 2) +  Math.pow(disx, 2) );
		BigDecimal bd = new BigDecimal(d);
		d = bd.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();	//小数点第1位以下四捨五入
		//barの長さを調整
		Dbar = d;
		if(Dbar <= 15) Dbar = 15;
		else if(Dbar >= surfaceViewX-15 ) Dbar = surfaceViewX-15;

		textView_d.setText(Double.toString(d) + " m" );
		Log.d("d", Double.toString(d));
	}

	//具体的にどの程度離れているか計算
	public double distancey(double disy) {
		double value = 0;
		disy = disy/0.0000009; // 0.0000009=1m
		BigDecimal bi = new BigDecimal(String.valueOf(disy));
	    value = bi.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();	//小数点第1位以下四捨五入
		return value;
	}
	public double distancex(double disx) {
		double value = 0;
		double temp1,temp2;
		//double x= 0;				//一秒の距離
		double x =0;//1mの度数

		//求めたい地点の球体の切断面の半径を考える。地球の半径をR、切断面の半径をrとするとr=Rcosθ
		//円周出して1秒の距離を計算する(
		temp1 = (r*Math.cos(latitude/(180*Math.PI))*2*Math.PI)/(360*60*60);//15
		temp2 = 0.00027777778/temp1;	//1/60/60≒0.0002777778
		x = temp2/temp1;							//
		//disx = disx/x;
		disx = disx/temp2;
		Log.d("x", Double.toString(x));
		Log.d("temp1", Double.toString(temp1));
		Log.d("temp2", Double.toString(temp2));
		BigDecimal bi = new BigDecimal(String.valueOf(disx));
	    value = bi.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();	//小数点第1位以下四捨五入
		return value;
	}

	//目的地までの方位角と2点間の直線距離を計算
	private void ArrowAzimuth() {
		// TODO 自動生成されたメソッド・スタブ
		double R = r/1000;		//地球の半径(km)
		fai = 90 - Math.atan2( Math.sin(dislon), Math.cos(latitude)*Math.tan(firstlat) - Math.sin(latitude)*Math.cos(dislon) );
	}




	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}