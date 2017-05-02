package com.wiz.dev.wiztalk.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.view.ViewGroup;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.LocalFileSelectActivity;

public class FileMenuFragment extends Fragment implements OnClickListener {

	LocalFileSelectActivity menuActivity;

	View image_lay, video_lay, audio_lay, sdcard_lay, sdcard1_lay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		menuActivity = (LocalFileSelectActivity) getActivity();
	}

	@Override
	public void onStart() {
		super.onStart();
		menuActivity.currentFragment = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_file_menu, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		image_lay = view.findViewById(R.id.image_lay);
		video_lay = view.findViewById(R.id.video_lay);
		audio_lay = view.findViewById(R.id.audio_lay);
		sdcard_lay = view.findViewById(R.id.sdcard_lay);
		sdcard1_lay = view.findViewById(R.id.sdcard1_lay);

		image_lay.setOnClickListener(this);
		video_lay.setOnClickListener(this);
		audio_lay.setOnClickListener(this);
		sdcard_lay.setOnClickListener(this);
		sdcard1_lay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.image_lay:
			menuActivity.goImage();
			break;

		case R.id.video_lay:
			menuActivity.goVideo();
			break;
		case R.id.audio_lay:
			menuActivity.goAudio();
			break;
		case R.id.sdcard_lay:
			menuActivity.goSysMemory();
			break;
		case R.id.sdcard1_lay:
			menuActivity.goEnvMemory();
			break;

		}
	}

}
