package com.wiz.dev.wiztalk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.wiz.dev.wiztalk.R;

/**
 *
 * @author Administrator
 *
 */
public class MediaPlayerActivity extends Activity implements SurfaceHolder.Callback{

	// private
	MediaPlayer player;
	SurfaceView surface;
	SurfaceHolder surfaceHolder;
	String path = null;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.videoview_activity);
		surface=(SurfaceView)findViewById(R.id.surface);

		surfaceHolder=surface.getHolder();   //SurfaceHolder是SurfaceView的控制接口
		surfaceHolder.addCallback(this);     //因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
		surfaceHolder.setFixedSize(320, 220);//显示的分辨率,不设置为视频默认
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//Surface类型
	}


	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
//必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
		player= new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(surfaceHolder);
		//设置显示视频显示在SurfaceView上
		try {
			// 新建Bundle对象
			path = this.getIntent().getStringExtra("path");
//                player.setDataSource("/sdcard/im/video"+path.substring(path.lastIndexOf("/")));
			player.setDataSource(path);
			player.prepare();
			player.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(player.isPlaying()){
			player.stop();
		}
		player.release();
		//Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}
}
