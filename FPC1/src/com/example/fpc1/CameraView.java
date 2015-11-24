package com.example.fpc1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;



public class CameraView extends Activity implements OnClickListener, LocationListener, SensorEventListener, SurfaceHolder.Callback {
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String beziString = null;
		
		switch(resultCode){
		case 0:
			beziString="hakusai";
			break;
		case 1:
			beziString="nasu";
			break;
		case 2:
			beziString="negi";
			break;
		case 3:
			beziString="tomato";
			break;
		case -1:
			beziString="error";
			break;
			
		}
		 path = Environment.getExternalStorageDirectory().getPath();// 内部ストレージのパスを取得
		 path=path+"/"+beziString;
	}

    public static final int IMAGE_CAPTURE = 1;
	// カメラ
    public Camera myCamera;
    public SurfaceView mySurfaceView;
    public SurfaceView SensorSurfaceView;
    public SurfaceHolder overLayHolder;
    public ImageView OverlayImage;
    public View SensorView;
    // Viewサイズ
    int vwidth;
    int vheight;
    // 画像のファイルネーム
	String path;
	String name;
	String defaultPath;
    long currentTimeMillis;
    String title;
    String fileName;
    String overlaybitmap;

    //LocationManagerオブジェクトの宣言
    LocationManager locmanager;
    double latitude = 0.0;		String strlat;	//緯度
    double longitude = 0.0;
    double altitude = 0.0;   		String stralt;	//高度
    //SensorManagerオブジェクトの宣言
	SensorManager sensorManager;
	//現在のアングル
    int roll = 0;			String strroll;	//緯度
    int pitch = 0;			String strpit;	//緯度
    int azimuth = 0;	String strazi;	//緯度
	//前回撮影時のアングル
	private int firstRoll = 0;		// 25;
	private int firstPitch = 0; 	//-82;
	private int firstAzi = 0;		//46;
	//点のアングル
	private double pointRoll = 0;
	private double pointPitch = 0;
	private double pointAzi = 0;

    // 画像ファイルのURI
    Uri PictureUri;
    // ExifInterfaceオブジェクトの宣言
    public ExifInterface exifInterface;
    public ExifInterface exifInterface2;

	Resources res;
	Bitmap point;
	public Canvas canvas;
	private Paint paint;
	Canvas baseCanvas;
	Paint basepaint;
	double X = 0;
	double Y = 0;
	double Z = 0;
	double surfaceViewX = 0;
	double surfaceViewY = 0;
	int centerX = 0;
	int centerY = 0;
	int drawX = 0;
	int drawY = 0;

    // フラグ
    boolean flag = false;				//初期値false(オーバーレイが必要か)
    static boolean check_flag = false;	//初期値false(SDのチェックをしたか)
    boolean overlay_flag = true;		//オーバーレイしているか
    boolean sensor_flag = true;			//センサを表示しているか

    // コールバックメソッド ---------------------------------------------------------------------------------------------------
	// シャッターが押されたときに呼ばれるコールバック
    
	public Camera.ShutterCallback mShutterListener =
	    new Camera.ShutterCallback() {
	        public void onShutter() {
	            // TODO Auto-generated method stub
	        	//GPS・センサー情報取得

		    	Log.d("方位角", String.valueOf(azimuth));
		    	Log.d("傾斜角", String.valueOf(pitch));
		    	Log.d("回転角", String.valueOf(roll));
		    	Log.d("緯度", String.valueOf(latitude));
		    	Log.d("経度", String.valueOf(longitude));
		    	Log.d("高度", String.valueOf(altitude));

    			// Viewサイズの取得
    	    	vwidth = mySurfaceView.getWidth();
    	        vheight =  mySurfaceView.getHeight();
 
    	        sensor_flag = false;
	        }
	    };
    // イメージ生成後に呼ばれるコールバック
    public PictureCallback mPictureListener =
        new PictureCallback() {
			@Override
            public void onPictureTaken(byte[] data, Camera camera) {
            	if (data != null) {
            		try {
                		FileOutputStream myFOS;
                		
                		// 画像ファイルのパス設定
                		title = getDate();		//日時をタイトルに設定
            	        String fileName = title + ".bmp";
            	        
            	        String filePath = path + fileName;			// 画像ファイルのパス
            	        
            	        Log.d("保存Path",path);
            	        
            	        // 画像の保存
            	        Bitmap bitmap = rotate(data, camera);			 // 画像の回転（縦向きに変換）
            	        
            	        Log.d("vwidth",String.valueOf(vwidth));
            	        Log.d("vwidth",String.valueOf(vheight));
            	        
            	        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap,vwidth,vheight, false); // リサイズ（Viewサイズに変換）
            			myFOS = new FileOutputStream(filePath);		// 保存先の指定
            			
            			Log.d("保存先",String.valueOf(filePath));
            			 
            			bitmap2.compress (CompressFormat.JPEG, 100, myFOS);	//qualityは0から100
            			myFOS.close ();
            			
            			// Exif情報書き込み
            			ExifWrite(filePath);
            			
            	        Log.d("撮影","成功");
            	        
            			// 画像ファイルのUriを取得
            			// 明示的インテントによる画面遷移
            	        Log.d("deback","01");
            	        
            	        Intent intent = new Intent(CameraView.this, Preview.class);
            	        intent.putExtra("DIRECTORYPATH",path);	// 	パス
            	        intent.putExtra("FILEPATH",filePath);		// 画像のファイルパス
            	        
            	        Log.d("deback","02");  
            	        
            	    	startActivity(intent);
            	    	
            	    	Log.d("deback","03");
            		} catch (Exception e) {
            			
            			e.printStackTrace();
            			Log.d("撮影","失敗");
                    	Toast.makeText(CameraView.this, "撮影できませんでした。", Toast.LENGTH_SHORT).show();
            		}
            	}
            	// カメラを再開
                myCamera.startPreview();
            }
    };

    // Exif操作 ---------------------------------------------------------------------------------------------------
    //Exif読み込み
    private void getExif() {
		// TODO 自動生成されたメソッド・スタブ

  	  	try {
  	  		exifInterface = new ExifInterface(path + overlaybitmap);
  	  	} catch (IOException e) {
  	  		e.printStackTrace();
  	  		Log.d("エラー", e.getMessage());
  	  		return;
  	  	}

    	if(exifInterface != null) {
    		//緯度・経度の取得
    		/*メーカー情報や端末情報を角度情報に上書き*/
    		strroll =exifInterface.getAttribute (ExifInterface.TAG_MAKE);
    		if(exifInterface.getAttribute (ExifInterface.TAG_MAKE)==null){
    			strroll="0";
    		}
    		strpit = exifInterface.getAttribute (ExifInterface.TAG_MODEL);
    		if(exifInterface.getAttribute (ExifInterface.TAG_MODEL)==null)
    		{
    			strpit="0";
    		}
    		strazi = exifInterface.getAttribute (ExifInterface.TAG_GPS_DATESTAMP);
    		/*GPSの習得日を潰す*/
    		if(exifInterface.getAttribute (ExifInterface.TAG_GPS_DATESTAMP)==null)
    		{
    			strazi="0";
    		}
    		/*out1*/
    		firstRoll = Integer.parseInt(strroll);
    		firstPitch = Integer.parseInt(strpit);
    		firstAzi = Integer.parseInt(strazi);
    		Log.d("Exif", "読み込みました");
    		}
	}

    //Exif情報書き込み
    public void ExifWrite(String filePath){

    	int latnum1 = 0;
  	  	int latnum2 = 0;
  	  	double latnum3 = 0.0;
  	  	int latnum32 = 0;
  	  	int lonnum1 = 0;
  	  	int lonnum2 = 0;
  	  	double lonnum3 = 0.0;
  	  	int lonnum32 = 0;
  	  	String strlat;
  	  	String strlon;
  	  	try {
  	  		exifInterface2 = new ExifInterface(filePath);
  	  	} catch (IOException e) {
  	  		e.printStackTrace();
  	  		Log.d("エラー", e.getMessage());
  	  		return;
  	  	}

    	if(exifInterface2 != null) {
	    	try {
	    		Log.d("ExifWrite","Exif情報書き込み");
	    		//10→60
	    		Log.d("latitude",String.valueOf(latitude));
	    		latnum1 = (int) latitude;
	    		Log.d("latnum1",String.valueOf(latnum1));		//度
	    		double temp1 = (latitude-latnum1)*60;
	    		latnum2 = (int) temp1;
	    		Log.d("latnum2",String.valueOf(latnum2));		//分
	    		double temp2 = temp1-latnum2;
	    		latnum3 = (temp2*60*1000)/1000;
	    		Log.d("latnum3",String.valueOf(latnum3));		//秒
	    		//latnum3変換
	    		latnum32 = RoundOff(latnum3);

	    		strlat = String.valueOf(latnum1)+"/1,"+String.valueOf(latnum2)+"/1,"+String.valueOf(latnum32)+"/1";
	    		//Log.d("latitude",String.valueOf(latitude));		//60進
	    		Log.d("latitude", strlat);		//60進
	    		Log.d("longitude",String.valueOf(longitude));
	    		lonnum1 = (int) longitude;
	    		Log.d("lonnum1",String.valueOf(lonnum1));
	    		temp1 = (longitude-lonnum1)*60;
	    		lonnum2 = (int) temp1;
	    		Log.d("lonnum2",String.valueOf(lonnum2));
	    		temp2 = temp1-lonnum2;
	    		lonnum3 = (temp2*60*1000)/1000;
	    		Log.d("lonnum3",String.valueOf(lonnum3));
	    		//lonnum3変換
	    		lonnum32 = RoundOff(lonnum3);
	    		strlon = String.valueOf(lonnum1)+"/1,"+String.valueOf(lonnum2)+"/1,"+String.valueOf(lonnum32)+"/1";
	    		//Log.d("longitude",String.valueOf(longitude));
	    		Log.d("longitude", strlon);
	    		exifInterface2.setAttribute (ExifInterface.TAG_GPS_LATITUDE, strlat); 			//緯度
	    		exifInterface2.setAttribute (ExifInterface.TAG_GPS_LONGITUDE, strlon); 	//経度
	    		exifInterface2.setAttribute (ExifInterface.TAG_GPS_ALTITUDE, String.valueOf(altitude)); 			//高度
	    	 	exifInterface2.setAttribute (ExifInterface.TAG_MAKE, String.valueOf(roll)); 							//回転角
			 	exifInterface2.setAttribute (ExifInterface.TAG_MODEL , String.valueOf(pitch));						//傾斜角
			 	exifInterface2.setAttribute (ExifInterface.TAG_GPS_DATESTAMP, String.valueOf(azimuth));		//方位角
	    		exifInterface2.saveAttributes();
	    		//Toast.makeText(CameraView.this, "Exifに書き込みました。", Toast.LENGTH_SHORT).show();
	    	} catch (IOException e) {
	    		// TODO 自動生成された catch ブロック
	    		e.printStackTrace();
	    		Toast.makeText(CameraView.this, "Exifに書き込めませんでした。", Toast.LENGTH_SHORT).show();
	    	}
	    }
    }

	  //精度を上げるために小数点第7位で四捨五入して10の6乗してnum3として保存
	  public int RoundOff(double value){
		  double value2 = 0.0;
		  //元データをBigDecimal型にする
		  BigDecimal bd = new BigDecimal(value);
		  //小数第7位で四捨五入
		  BigDecimal bd2 = bd.setScale(8, BigDecimal.ROUND_HALF_UP);
		  //BigDecimal型をdouble型にする
		  value2 = bd2.doubleValue();
		  //10の6乗する
		  value2 = value2*1000000;
		  Log.d("value2",String.valueOf(value2));
		  return (int)value2;
	  }

	//  ---------------------------------------------------------------------------------------------------
    //カメラ用サーフェイスのイベント処理
    //コールバック関数の実装
    public SurfaceHolder.Callback mSurfaceListener =
        new SurfaceHolder.Callback() {

            @Override
            //surfaceCreated：SurfaceViewが生成されたらカメラをオープンする
            public void surfaceCreated(SurfaceHolder holder) {
                myCamera = Camera.open();
                try {
                    myCamera.setPreviewDisplay(holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

			@TargetApi(Build.VERSION_CODES.FROYO)
			@Override
            //surfaceChanged：SurfaceViewに変更があった場合に呼び出される
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                myCamera.stopPreview();
                // カメラのパラメータ取得
                Camera.Parameters parameters = myCamera.getParameters();
                // 画面の向きを設定
                boolean portrait = isPortrait();
                if (portrait) {
                	// 画面を縦向きにする
                    myCamera.setDisplayOrientation(90);
                } else {
                    myCamera.setDisplayOrientation(0);
                }
                // パラメータを設定してカメラを再開
                myCamera.setParameters(parameters);
                myCamera.startPreview();
            }
			@Override
            //surfaceDestroyed：SurfaceViewが破棄されたらカメラを解放する
            public void surfaceDestroyed(SurfaceHolder holder) {
                myCamera.stopPreview();
                myCamera.release();
                myCamera = null;
            }
        };

        //SurfaceViewを操作するには、SurfaceHolderオブジェクトを使う。
        //getHolderメソッドでSurfaceHolderオブジェクトを取得して、SurfaceHolderオブジェクトのaddCallbackメソッドを使って、
        //SurfaceViewの状態が変化した時に呼び出される、 SurfaceHolder.Callbackインターフェースを実装したクラスを、指定している。



     //更新日時が最も新しい画像ファイルを決定（オーバーレイに用いる画像）
     public String compare() {
    	 Log.d("compare","start");
    		// TODO 自動生成されたメソッド・スタブ
        	int i=0; //ループ用
    		int n=0;	//最新の画像
    		int diff =0;	//更新日時の差
    		Date da1 = null,da2 = null; //更新日時
    	
    		//画像ファイルパスを指定
            Log.d("path in compare",path);
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
    				if(diff<0){
    					n=i;
    				}
    		 	}
    		}
	String overlaybitmap = filelist[n].getName();
    		Log.d("filelist",filelist[n].getName());
    		return overlaybitmap;
    	}

    // 画面の向きの取得（縦ならtrue）
    public boolean isPortrait() {
        return (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }
    							/*ここが大事*/
    // 画像の回転（縦向きに変換）
    public Bitmap rotate(byte[] data, Camera camera){
    	Log.d("rotate","start");
        Bitmap tmp_bitmap = BitmapFactory.decodeByteArray (data, 0, data.length);
       int width = tmp_bitmap.getWidth ();
        int height = tmp_bitmap.getHeight ();
    	
    	// 回転マトリックス作成（90度回転）  
    	Matrix mat = new Matrix();  
    	mat.postRotate(90);  
    	  
    	// 回転したビットマップを作成  
    	Bitmap bmp = Bitmap.createBitmap(tmp_bitmap, 0, 0, width, height, mat, true);
    	
    	
		return bmp;
    }

    // アクティビティのサイクル ---------------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  
        // Intent型インスタンスを取得
        Intent intent = getIntent();
        path = intent.getStringExtra("DIRECTORYPATH");
        path=path+"/";
        Log.d("path",path);
		 // 指定されたフォルダがあるか確認
		 File directory = new File(path);
		 if (directory.exists()){
			 Log.d("ストレージチェック", path + "を確認しました。");
		 }else{ // path+"/FPCpicture/"が存在しない場合は作成
			 Log.d("ストレージチェック", path+ "を確認できません。");
			 Log.d("ストレージチェック", path + "を作成します。");
			 Toast.makeText(CameraView.this, path+"を作成します。", Toast.LENGTH_SHORT).show();
			 directory.mkdir();
		 }
        setContentView(R.layout.cameraview);
        Log.d("アクティビティ","CameraView");
        // オーバーレイ用画面の設定
    	OverlayImage = (ImageView) findViewById(R.id.OverlayImage1);
        //フラグの更新
        //get_flag();
		//ディレクトリのチェック
		directorycheck();
		Log.d("check", String.valueOf(flag));
        if (flag){
        	Log.d("オーバーレイ","あり");
        	Log.d("センサ","あり");
        	overlaybitmap = compare();
     		Bitmap bitmap = BitmapFactory.decodeFile(path + overlaybitmap);
     		OverlayImage.setAlpha(100);
     		OverlayImage.setImageBitmap(bitmap);
            //Sensor表示
            SensorSurfaceView = (SurfaceView)findViewById(R.id.SurfaceView2);
        	overLayHolder = SensorSurfaceView.getHolder();
        	// ここで半透明にする
        	overLayHolder.setFormat(PixelFormat.TRANSLUCENT);
        	// コールバック関数の登録
        	overLayHolder.addCallback(this);
        }else {
        	Log.d("オーバーレイ","なし");
        	Log.d("センサ","なし");
        }
        
        // カメラプレビュー画面の設定
        mySurfaceView = (SurfaceView)findViewById(R.id.SurfaceView1);

        // SurfaceHolderのインスタンスを取得
        SurfaceHolder holder = mySurfaceView.getHolder();
   
        holder.setFormat(PixelFormat.TRANSLUCENT);
        // コールバック関数の登録
        holder.addCallback(mSurfaceListener);
        // ボタンを押した時の動作設定
        Button button1 = (Button)this.findViewById(R.id.time);
        button1.setOnClickListener(this);
        //センサーマネージャーの取得
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    
        //ロケーションマネージャの取得
		locmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Log.d("locationmanager",String.valueOf(locmanager));
		
		// 最適なプロバイダーを取り出す
        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        
		//画像読み込み
		res = getBaseContext().getResources();
		paint = new Paint();
		point = BitmapFactory.decodeResource(res, R.drawable.point1);		
	      Button homebutton = (Button)this.findViewById(R.id.homebutton);
	      
	        // ホーム画面へ
	        homebutton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	// メッセージの表示
	            	//Toast.makeText(Main.this, "カメラ起動", Toast.LENGTH_SHORT).show();
	            	Log.d("操作","ホームへ");
	            	Intent i = new Intent(getApplicationContext(),ModeSelect.class);
	            	startActivityForResult(i,0);
	            }
	        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
	    //点を表示するために中心座標を計算
    	if(SensorSurfaceView != null){
		surfaceViewX= SensorSurfaceView.getWidth();
		surfaceViewY= SensorSurfaceView.getHeight();
	    centerX = (int) (surfaceViewX / 2);
	    centerY = (int) (surfaceViewY / 2);
	    drawX = centerX - point.getWidth() / 2;
	    drawY = centerY - point.getHeight() / 2;
	    //Exif情報読み込み（センサ情報）
	    getExif();
	
    	}
    }
    
    public void onClick(View v) {
    	Log.d("Onclick","Start");
    	
    	
    	Toast.makeText(CameraView.this, String.valueOf(latitude), Toast.LENGTH_SHORT).show();
    	
    	Log.d("latitude",String.valueOf(latitude));
		Log.d("longitude",String.valueOf(longitude));
		
			myCamera.takePicture(mShutterListener, null, mPictureListener);
    }

    @Override
    public void onStart() {
    	 super.onStart();
    	}

    @Override
    public void onResume() {
    	
    		if (this.locmanager != null) {
    		
    		// 利用可能なプロバイダを全部使う(GPSがだめならネットワークみたいな考え方)
    		// GPSは空が見えるところでないととれにくい。その場合はネットワークしか座標が取り出せない
    		List<String> providerList = this.locmanager.getProviders(true);
    		
    		// GPSが使えない	
    		if (this.locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
    			
    			new AlertDialog.Builder(this)
    				.setTitle("GPSが無効")
    				.setMessage("GPS機能が有効ではありません。\n\n有効にすることで現在位置をさらに正確に検出できるようになります")
    				.setPositiveButton("設定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								Log.d("deback","06");
								startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
								Log.d("deback","07");
							} catch (final ActivityNotFoundException e) {};
						}
					})
					.setNegativeButton("しない", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					})
					.create()
					.show();
				
    		}
    		
    		// 全部の機能を使って座標を取り出す
    		for (int i = 0; i < providerList.size(); i++) {
    			this.locmanager.requestLocationUpdates(
        				providerList.get(i), 10000, 1, this);
    		}
    		
    	}    
    	
        super.onResume();
		// 位置情報の更新を受け取るように設定(プロバイダ、通知のための最小時間間隔、通知のための最小距離間隔、位置情報リスナー)
        sensorManager.registerListener(
  				this,
  				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 	//方位センサ
  				SensorManager.SENSOR_DELAY_GAME);
 
    }

    @Override
    public void onPause() {
        super.onPause();
        sensor_flag = false;
        //Listenerの登録解除
        if(this.sensorManager!=null)
        	this.sensorManager.unregisterListener(this);
      
        if (this.locmanager != null) {
    		this.locmanager.removeUpdates(this);
    	}
    }

    @Override
    public void onStop() {
    	super.onStop();
        sensor_flag = false;
        //Listenerの登録解除
        sensorManager.unregisterListener(this);

        //解除
        locmanager.removeUpdates(this);
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d("tag","onDestory is called");
        sensor_flag = false;
        //Listenerの登録解除
        sensorManager.unregisterListener(this);
        //解除
        locmanager.removeUpdates(this);
        //点画像の解放
        point.recycle();
        
    }

    // 日時の取得 -------------------------------------------------------------------------------------------------
    public String getDate(){
        currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = dateFormat.format(today);
    	return date;
    }


 // 描画処理部 ---------------------------------------------------------------------------------------------------
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	}
	
	private void position() {
		pointAzi = azimuth-180;	//-180から180に設定
		pointAzi -= (firstAzi-180);	//方位角の基準をずらす
		pointAzi *= (surfaceViewX/360);		//Viewサイズに比を合わせる
		X = pointAzi + drawX;	//中心が原点になるよう調整
		if(X>surfaceViewX) {
			  X = X-surfaceViewX;
		}else if(X<0) {
			  X = X+surfaceViewX;
		}
		
		pointRoll = 0;
		pointRoll = roll-firstRoll;
		Z=pointRoll;
		
		//Y軸の調整
		pointPitch = pitch-firstPitch;		//傾斜角の基準をずらす（下向きが0になっていることに注意）
		pointPitch *= (surfaceViewY/360);		//Viewサイズに比を合わせる
		Y = pointPitch + drawY;	//中心が原点になるよう調整
		if(Y>surfaceViewY) {
			  Y = Y-surfaceViewY;
		}else if(Y<0) {
			  Y = Y+surfaceViewY;
		}	
		
	}

	public void doDraw() {
   
		if(overLayHolder != null )
		{
	
			canvas = overLayHolder.lockCanvas();	//キャンバスのロック
			canvas.drawColor(0,Mode.CLEAR); //前回の描画を消去
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(2);
			//円を描画
		
	        paint.setStyle(Paint.Style.STROKE);	//塗りつぶしなしの設定
			canvas.drawCircle(centerX, centerY, centerX, paint);
		//	canvas.drawBitmap(point, (float) ((int)centerX+centerX*Math.cos(Z)), (float) (centerX*Math.sin(Z)), paint);
			//線を描画
			canvas.drawLine(centerX, 0, centerX, centerY*2, paint);
			canvas.drawLine(0, centerY, centerX*2, centerY, paint);
			//点を描画
			canvas.drawBitmap(point, (int)X, (int)Y, paint);//X+drawX
			overLayHolder.unlockCanvasAndPost(canvas);		//キャンバスのロック解除
		}
	}


	public void onLocationChanged(Location location) {
		Log.d("OnLocationChanged","Start");
		//緯度・経度・高度を取得
		latitude =location.getLatitude();
		longitude = location.getLongitude();
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		//傾きセンサを使用
	    if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
	    	azimuth = (int) event.values[0];		//方位角
	    	pitch = (int) event.values[2];			//傾斜角
	    	roll = (int) event.values[1];			//回転角
	    }

		  //surfaceveiwが生成されてから描画
		  if(sensor_flag == true) {
			  position();
			  doDraw();
		  }
		  
	}

	//画像データがあるか確認
	public void directorycheck(){
		//SDカードのパスを指定
		File dir = new File(path);
		//指定したディレクトリのファイル名を取得
		final File[] filelist = dir.listFiles();
		if(filelist.length == 0){
			Log.d("ディレクトリチェック","画像なし");	//オーバーレイする画像がないということ
			flag = false;
		}else {
			Log.d("ディレクトリチェック","画像あり");	//オーバーレイする画像があるということ
			flag = true;	//オーバーレイを実装
		}
		Log.d("画像ファイル数", String.valueOf(filelist.length));
	}

	// menu --------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(Menu.NONE, 1, Menu.NONE, "Overlay");
		menu.add(Menu.NONE, 2, Menu.NONE, "Sensor");
		return true;
	}

	 public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case 1:
                 Log.d("menu1","Overlay");
                 if(flag == true){
                	 if(overlay_flag == true){
                		 findViewById(R.id.OverlayImage1).setVisibility(View.GONE);
                		 Toast.makeText(getApplicationContext(), "オーバーレイ非表示", Toast.LENGTH_SHORT).show();
                		 overlay_flag = false;
                	 }else{
                		 findViewById(R.id.OverlayImage1).setVisibility(View.VISIBLE);
                		 Toast.makeText(getApplicationContext(), "オーバーレイ表示", Toast.LENGTH_SHORT).show();
                		 overlay_flag = true;
                	 }
                 }
                 return true;
             case 2:
                 Log.d("menu2","Sensor");
                 if(sensor_flag == true){
                	 findViewById(R.id.SurfaceView2).setVisibility(View.GONE);
                	 Toast.makeText(getApplicationContext(), "センサ非表示", Toast.LENGTH_SHORT).show();
                	 sensor_flag = false;
                }else{
                	 findViewById(R.id.SurfaceView2).setVisibility(View.VISIBLE);
                	 Toast.makeText(getApplicationContext(), "センサ表示", Toast.LENGTH_SHORT).show();
                	 sensor_flag = true;
                 }
                 return true;
         }
         return false;
     }

     //メニューボタンを押される度に呼ばれる
    @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
         Log.d("menu","押された");
         return true;
     }

     //メニューが消える度に呼ばれる
     @Override
     public void onOptionsMenuClosed (Menu menu) {
         super.onPrepareOptionsMenu(menu);
         Log.d("menu","閉じられた");
     }
    

}


