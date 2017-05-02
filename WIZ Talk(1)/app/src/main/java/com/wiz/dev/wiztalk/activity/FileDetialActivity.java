package com.wiz.dev.wiztalk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.download.entities.FileInfo;
import com.download.services.DownloadService;
import com.download.services.FileDownloadCallBack;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.SaveConfig;

import java.io.File;


/**
 * @author Administrator
 */
public class FileDetialActivity extends Activity {


    ImageView img_file;
    TextView file_name;
    TextView file_size;
    TextView progress_tv;
    Button fileOpen;
    ProgressBar progressBar;

    File file;

    Context mContext;

    SaveConfig saveConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filedetial_activity);

        initView();
        mContext = this;
        saveConfig = new SaveConfig(this);
    }

    private void initView() {
        img_file = (ImageView) findViewById(R.id.img_file);
        file_name = (TextView) findViewById(R.id.file_name);
        file_size = (TextView) findViewById(R.id.file_size);
        progress_tv = (TextView) findViewById(R.id.progress_tv);
        fileOpen = (Button) findViewById(R.id.btnOpen);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        String fileName = getIntent().getStringExtra("file_name");
        file_name.setText(fileName);
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "wizTalk/RecVideo/" + fileName);

        if(file.exists()){
            fileOpen.setText("打 开");
        }
        String sub_ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (sub_ext.equalsIgnoreCase("pdf")) {
            img_file.setImageResource(R.drawable.pdf);

        } else if (sub_ext.equalsIgnoreCase("mp3") || sub_ext.equalsIgnoreCase("wma")
                || sub_ext.equalsIgnoreCase("m4a") || sub_ext.equalsIgnoreCase("m4p")
                ||sub_ext.equalsIgnoreCase("amr")) {
            img_file.setImageResource(R.drawable.ic_select_file_music);
        } else if (sub_ext.equalsIgnoreCase("png") || sub_ext.equalsIgnoreCase("jpg")
                || sub_ext.equalsIgnoreCase("jpeg") || sub_ext.equalsIgnoreCase("gif")
                || sub_ext.equalsIgnoreCase("tiff")) {
            img_file.setImageResource(R.drawable.image);
        } else if (sub_ext.equalsIgnoreCase("zip") || sub_ext.equalsIgnoreCase("gzip")
                || sub_ext.equalsIgnoreCase("gz")) {
            img_file.setImageResource(R.drawable.zip);
        } else if (sub_ext.equalsIgnoreCase("m4v") || sub_ext.equalsIgnoreCase("wmv")
                || sub_ext.equalsIgnoreCase("3gp") || sub_ext.equalsIgnoreCase("mp4")) {
            img_file.setImageResource(R.drawable.movies);
        } else if (sub_ext.equalsIgnoreCase("doc") || sub_ext.equalsIgnoreCase("docx")) {
            img_file.setImageResource(R.drawable.word);
        } else if (sub_ext.equalsIgnoreCase("xls") || sub_ext.equalsIgnoreCase("xlsx")) {
            img_file.setImageResource(R.drawable.excel);
        } else if (sub_ext.equalsIgnoreCase("ppt") || sub_ext.equalsIgnoreCase("pptx")) {
            img_file.setImageResource(R.drawable.ppt);
        } else if (sub_ext.equalsIgnoreCase("html")) {
            img_file.setImageResource(R.drawable.html32);
        } else if (sub_ext.equalsIgnoreCase("xml")) {
            img_file.setImageResource(R.drawable.xml32);
        } else if (sub_ext.equalsIgnoreCase("conf")) {
            img_file.setImageResource(R.drawable.config32);
        } else if (sub_ext.equalsIgnoreCase("apk")) {
            img_file.setImageResource(R.drawable.appicon);
        } else if (sub_ext.equalsIgnoreCase("jar")) {
            img_file.setImageResource(R.drawable.jar32);
        } else {
            img_file.setImageResource(R.drawable.text);
        }
        file_size.setText("文件大小："+ getIntent().getStringExtra("file_size"));
    }


    public void back(View v) {
        finish();
    }

    boolean isDowning;

    public void btnOpen(View view) {

        if (isDowning)
            return;

        isDowning = true;
        if (!file.exists()) {
            String url = getIntent().getStringExtra("path");

            if(saveConfig.getStringConfig("httpConfig").equals("true")){
                url = url.split("&")[0];
            }else{
                url = url.split("&")[1];
            }

            FileInfo fileInfo = new FileInfo(getIntent().getIntExtra("fileId", 0), url, file.getName(), 0, 0, new FileDownloadCallBack() {
                @Override
                public void onPregress(int progress) {
                    mHandler.obtainMessage(1,progress).sendToTarget();
                }

                @Override
                public void onFinish(FileInfo fileInfo) {
                    mHandler.obtainMessage(10,fileInfo).sendToTarget();
                }

                @Override
                public void onStart() {
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onNetError() {
                    mHandler.sendEmptyMessage(2);
                }

            });
            MyApplication.getInstance().getDownloadManager().map.put(fileInfo.getId(), fileInfo);
            Intent intent = new Intent(FileDetialActivity.this, DownloadService.class);
            intent.setAction(DownloadService.ACTION_START);
            intent.putExtra("fileId", fileInfo.getId());
            startService(intent);
        }else {
            openFile();
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 1:
                    progressBar.setProgress((Integer) msg.obj);
                    break;
                case 0:
                    progressBar.setVisibility(View.VISIBLE);
                    isDowning = true;
                    break;
                case 2:
                    Toast.makeText(FileDetialActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    isDowning =false;
                    break;

                case 10:
                    isDowning =false;
                    Toast.makeText(FileDetialActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    fileOpen.setText("打 开");
                    break;
            }
        }
    };


    void openFile(){
        String item_ext = file.getName();
        item_ext = item_ext.substring(item_ext.lastIndexOf(".") + 1);
        if (item_ext.equalsIgnoreCase("mp3") ||
                item_ext.equalsIgnoreCase("m4a") ||
                item_ext.equalsIgnoreCase("mp4")||
                item_ext.equalsIgnoreCase("amr")) {

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(i);
        }

	    	/*photo file selected*/
        else if (item_ext.equalsIgnoreCase("jpeg") ||
                item_ext.equalsIgnoreCase("jpg") ||
                item_ext.equalsIgnoreCase("png") ||
                item_ext.equalsIgnoreCase("gif") ||
                item_ext.equalsIgnoreCase("tiff")) {

            if (file.exists()) {
                Intent picIntent = new Intent();
                picIntent.setAction(Intent.ACTION_VIEW);
                picIntent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(picIntent);
            }
        }

	    	/*video file selected--add more video formats*/
        else if (item_ext.equalsIgnoreCase("m4v") ||
                item_ext.equalsIgnoreCase("3gp") ||
                item_ext.equalsIgnoreCase("wmv") ||
                item_ext.equalsIgnoreCase("mp4") ||
                item_ext.equalsIgnoreCase("ogg") ||
                item_ext.equalsIgnoreCase("wav") ) {

            if (file.exists()) {
                Intent movieIntent = new Intent();
                movieIntent.setAction(Intent.ACTION_VIEW);
                movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivity(movieIntent);
            }
        }

	    	/*zip file */
        else if (item_ext.equalsIgnoreCase("zip")) {

        }

	    	/* gzip files, this will be implemented later */
        else if (item_ext.equalsIgnoreCase("gzip") ||
                item_ext.equalsIgnoreCase("gz")) {


        }
            /*pdf file selected*/
        else if (item_ext.equalsIgnoreCase("pdf")) {

            if (file.exists()) {

                Intent pdfIntent = new Intent();
                pdfIntent.setAction(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(Uri.fromFile(file),
                        "application/pdf");
                try {
                    startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "不能打开该文件",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

	    	/*Android application file*/
        else if (item_ext.equalsIgnoreCase("apk")) {

            if (file.exists()) {
                Intent apkIntent = new Intent();
                apkIntent.setAction(Intent.ACTION_VIEW);
                apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(apkIntent);
            }
        }

	    	/* HTML file */
        else if (item_ext.equalsIgnoreCase("html")) {

            if (file.exists()) {

                Intent htmlIntent = new Intent();
                htmlIntent.setAction(Intent.ACTION_VIEW);
                htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");

                try {
                    startActivity(htmlIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "不能打开该文件",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

	    	/* text file*/
        else if (item_ext.equalsIgnoreCase("txt")) {

            if (file.exists()) {

                Intent txtIntent = new Intent();
                txtIntent.setAction(Intent.ACTION_VIEW);
                txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

                try {
                    mContext.startActivity(txtIntent);
                } catch (ActivityNotFoundException e) {
                    txtIntent.setType("text/*");
                    mContext.startActivity(txtIntent);
                }

            }
        }

	    	/* generic intent */
        else {
            if (file.exists()) {

                Intent generic = new Intent();
                generic.setAction(Intent.ACTION_VIEW);
                generic.setDataAndType(Uri.fromFile(file), "text/plain");

                try {
                    mContext.startActivity(generic);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "不能打开该文件" + file.getName(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyApplication.getInstance().getDownloadManager().map.size() != 0) {
            Intent intent = new Intent(FileDetialActivity.this, DownloadService.class);
            intent.setAction(DownloadService.ACTION_STOP);
            startService(intent);
        }
    }
}
