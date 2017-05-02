package com.wiz.dev.wiztalk.activity;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.model.MediaModel;
import com.wiz.dev.wiztalk.fragment.AudioFragment;
import com.wiz.dev.wiztalk.fragment.EnvMemeryFragment;
import com.wiz.dev.wiztalk.fragment.FileMenuFragment;
import com.wiz.dev.wiztalk.fragment.ImageFragment;
import com.wiz.dev.wiztalk.fragment.VideoFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class LocalFileSelectActivity extends FragmentActivity {
    FragmentManager fragmentManager;

    public Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localfile_select);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, new FileMenuFragment()).commit();
    }

    public void goImage() {
        fragmentManager.beginTransaction().replace(R.id.content, new ImageFragment()).addToBackStack(null).commit();
    }

    public void goVideo() {
        fragmentManager.beginTransaction().replace(R.id.content, new VideoFragment()).addToBackStack(null).commit();
    }

    public void goAudio() {
        fragmentManager.beginTransaction().replace(R.id.content, new AudioFragment()).addToBackStack(null).commit();
    }

    public void goSysMemory() {
        Fragment fragment = new EnvMemeryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", Environment.getExternalStorageDirectory().getPath());
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    public void goEnvMemory() {
        Fragment fragment = new EnvMemeryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", "/storage/sdcard1");
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    public void back(View v) {
        onKeyDown(KeyEvent.KEYCODE_BACK, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentFragment != null) {
                if (currentFragment instanceof EnvMemeryFragment) {
                    if (((EnvMemeryFragment) (currentFragment)).goBack()) {
                        return true;
                    }
                }
            }

            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void sendFile(View view) {
        if (currentFragment != null) {
            if (currentFragment instanceof VideoFragment) {
                ArrayList<MediaModel> models = ((VideoFragment) currentFragment).getData();
                if (models != null) {
                    Intent intent = new Intent();
                    intent.putExtra("file", models.get(0));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "请选择文件", Toast.LENGTH_SHORT).show();
                }
            } else if (currentFragment instanceof AudioFragment) {
                ArrayList<MediaModel> models = ((AudioFragment) currentFragment).getData();
                if (models != null) {
                    Intent intent = new Intent();
                    intent.putExtra("file", models.get(0));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "请选择文件", Toast.LENGTH_SHORT).show();
                }
            } else if (currentFragment instanceof EnvMemeryFragment) {
                ArrayList<MediaModel> models = ((EnvMemeryFragment) currentFragment).getData();
                if (models != null) {
                    Intent intent = new Intent();
                    intent.putExtra("file", models.get(0));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "请选择文件", Toast.LENGTH_SHORT).show();
                }
            } else if (currentFragment instanceof ImageFragment) {
                ArrayList<String> paths = (ArrayList<String>) ((ImageFragment) currentFragment).getImagePath();
                if (paths != null) {
                    Intent intent = new Intent();
                    intent.putExtra("file", new MediaModel(true,paths.get(0),"",0,0L));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "请选择文件", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getBaseContext(), "请选择文件", Toast.LENGTH_SHORT).show();
        }
    }
}
