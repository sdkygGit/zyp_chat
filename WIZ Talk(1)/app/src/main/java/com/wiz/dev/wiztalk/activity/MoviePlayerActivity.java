package com.wiz.dev.wiztalk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.wiz.dev.wiztalk.R;


/**
 *
 * @author Administrator
 *
 */
public class MoviePlayerActivity extends Activity {

	private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
		@Override
		public void onPlayerItemChanged(MetaData metaData) {

		}
	});

	VideoPlayerView video_player;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movieplayer_activity);
		video_player = (VideoPlayerView) findViewById(R.id.video_player);
		mVideoPlayerManager.playNewVideo(null, video_player, getIntent().getStringExtra("path"));
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVideoPlayerManager.resetMediaPlayer();
	}
}
