/*
 * @Title DownloadService.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-7 下午10:03:42
 * @version 1.0
 */
package com.download.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.download.db.ThreadDAO;
import com.download.db.ThreadDAOImpl;
import com.download.entities.FileInfo;
import com.download.entities.ThreadInfo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.utils.Utils;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebView.FindListener;
import android.widget.Toast;

/**
 * 类注释
 * @author Yann
 * @date 2015-8-7 下午10:03:42
 */
public class DownloadService extends Service
{
	public static final String DOWNLOAD_PATH =
			Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/wizTalk/RecVideo/";
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_RESTART = "ACTION_RESTART";


	public static final String ACTION_STOP = "ACTION_STOP";
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	public static final String ACTION_FINISHED = "ACTION_FINISHED";
	public static final int MSG_INIT = 0;
	private String TAG = "DownloadService";
//	private Map<Long, DownloadTask> mTasks =
//			new LinkedHashMap<Long, DownloadTask>();

	DownloadTask task;
	FileInfo fileInfo;

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
//	public DownloadService() {
//		super("DownloadService");
//	}

	/**
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// 获得Activity传过来的参数
		if (ACTION_START.equals(intent.getAction()))
		{
			long  fileId = intent.getLongExtra("fileId", -1);

			if(fileId !=-1)
			{
				fileInfo = MyApplication.getInstance().getDownloadManager().map.get(fileId);

				Log.i(TAG , "Start:" + fileInfo.toString());
				// 启动初始化线程

				if(fileInfo!=null)
					new InitThread(fileInfo).start();
			}

		}
		else if (ACTION_STOP.equals(intent.getAction()))
		{
			// 从集合中取出下载任务
			if (task != null)
			{
				task.isPause = true;
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}


//	@Override
//	protected void onHandleIntent(Intent intent) {
//		if (ACTION_START.equals(intent.getAction()))
//		{
//			long  fileId = intent.getLongExtra("fileId", -1);
//
//			if(fileId !=-1)
//			{
//				FileInfo fileInfo = MyApplication.getInstance().getDownloadManager().map.get(fileId);
//
//				Log.i(TAG , "Start:" + fileInfo.toString());
//				// 启动初始化线程
//
//				if(fileInfo!=null){
//					HttpURLConnection connection = null;
//					RandomAccessFile raf = null;
//
//					try
//					{
//						// 连接网络文件
//						URL url = new URL(fileInfo.getUrl());
//						connection = (HttpURLConnection) url.openConnection();
//						connection.setConnectTimeout(5000);
//						connection.setRequestMethod("GET");
//						int length = -1;
//
//						if (connection.getResponseCode() == HttpStatus.SC_OK)
//						{
//							// 获得文件的长度
//							length = connection.getContentLength();
//						}
//
//						if (length <= 0)
//						{
//							return;
//						}
//
//						File dir = new File(DOWNLOAD_PATH);
//						if (!dir.exists())
//						{
//							dir.mkdir();
//						}
//
//						// 在本地创建文件
//						File file = new File(dir, fileInfo.getFileName());
//						raf = new RandomAccessFile(file, "rwd");
//						// 设置文件长度
//						raf.setLength(length);
//						fileInfo.setLength(length);
////						mHandler.obtainMessage(MSG_INIT, fileInfo).sendToTarget();
//
////						task = new DownloadTask(DownloadService.this, fileInfo, 3);
////						task.runOnService();
//
//						runOnService(fileInfo,new ThreadDAOImpl(getBaseContext()));
//					}
//					catch (Exception e)
//					{
//						mHandler.sendEmptyMessage(0x404);
//						e.printStackTrace();
//					}
//					finally
//					{
//						if (connection != null)
//						{
//							connection.disconnect();
//						}
//						if (raf != null)
//						{
//							try
//							{
//								raf.close();
//							}
//							catch (IOException e)
//							{
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//			}
//
//		}else if (ACTION_STOP.equals(intent.getAction())){
//
//			isPause = true;
//		}
//	}

	boolean isPause;

	void runOnService(FileInfo mFileInfo,ThreadDAO mDao) {
		isPause = false;
		ThreadInfo mThreadInfo;
		int mFinised = 0;
		List<ThreadInfo> threads = mDao.getThreads(mFileInfo.getUrl());

		if (0 == threads.size()) {

			int len = mFileInfo.getLength();

			mThreadInfo = new ThreadInfo(0, mFileInfo.getUrl(),
					0,  len , 0);
			mDao.insertThread(mThreadInfo);
		}else {
			mThreadInfo = threads.get(0);
		}

		if (mFileInfo.getCallBack() != null) {
			mFileInfo.getCallBack().onStart();
		}
		boolean isError = false;
		HttpURLConnection connection = null;
		RandomAccessFile raf = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(mThreadInfo.getUrl());
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			// 设置下载位置
			int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
			connection.setRequestProperty("Range",
					"bytes=" + start + "-" + mThreadInfo.getEnd());
			// 设置文件写入位置
			File file = new File(DownloadService.DOWNLOAD_PATH,
					mFileInfo.getFileName());
			raf = new RandomAccessFile(file, "rwd");
			raf.seek(start);
//				Intent intent = new Intent();
//				intent.setAction(DownloadService.ACTION_UPDATE);
			mFinised += mThreadInfo.getFinished();
			Log.i("mFinised", mThreadInfo.getId() + "finished = " + mThreadInfo.getFinished());
			// 开始下载
			if (connection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
				// 读取数据
				inputStream = connection.getInputStream();
				byte buf[] = new byte[1024 << 2];
				int len = -1;
				long time = System.currentTimeMillis();
				while ((len = inputStream.read(buf)) != -1) {
					// 写入文件
					raf.write(buf, 0, len);
					// 累加整个文件完成进度
					mFinised += len;
					// 累加每个线程完成的进度
					mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
					if (System.currentTimeMillis() - time > 1000) {
						time = System.currentTimeMillis();
						int f = mFinised * 100 / mFileInfo.getLength();
						if (f > mFileInfo.getFinished()) {
							if (mFileInfo.getCallBack() != null) {
								mFileInfo.getCallBack().onPregress(f);
							}
						}
					}

					if (isPause) {
						mDao.updateThread(mThreadInfo.getUrl(),
								mThreadInfo.getId(),
								mThreadInfo.getFinished());

						Log.i("mThreadInfo", mThreadInfo.getId() + "finished = " + mThreadInfo.getFinished());
						return;
					}
				}
				mDao.deleteThread(mFileInfo.getUrl());
				MyApplication.getInstance().getDownloadManager().map.remove(mFileInfo.getId());
				if (mFileInfo.getCallBack() != null) {
					mFileInfo.getCallBack().onFinish(mFileInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;

		} finally {
			try {
				if (connection != null) {
					connection.disconnect();
				}
				if (raf != null) {
					raf.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		if (isError) {
			mDao.updateThread(mThreadInfo.getUrl(),
					mThreadInfo.getId(),
					mThreadInfo.getFinished());
			if (Utils.isNetworkAvailable(getBaseContext())) {
				MyApplication.getInstance().getDownloadManager().map.put(mFileInfo.getId(), mFileInfo);
				Intent intent = new Intent(getBaseContext(), DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra("fileId", mFileInfo.getId());
				getBaseContext().startService(intent);
			} else {
				if (mFileInfo.getCallBack() != null) {
					mFileInfo.getCallBack().onNetError();
				}
			}
		}

	}


	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what)
			{
				case MSG_INIT:
					FileInfo fileInfo = (FileInfo) msg.obj;
					Log.i(TAG, "Init:" + fileInfo);
					// 启动下载任务
					task = new DownloadTask(DownloadService.this, fileInfo, 1);
					task.downLoad();

					// 把下载任务添加到集合中
//					mTasks.put(fileInfo.getId(), task);
					break;

				case 0x404:
					Toast.makeText(getBaseContext(),"请检查网络",Toast.LENGTH_SHORT).show();
					FileInfo fileInfo2 = (FileInfo) msg.obj;
					MyApplication.getInstance().getDownloadManager().map.remove(fileInfo2.getId());
					break;
			}
		};
	};

	private class InitThread extends Thread
	{
		private FileInfo mFileInfo = null;

		public InitThread(FileInfo mFileInfo)
		{
			this.mFileInfo = mFileInfo;
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			HttpURLConnection connection = null;
			RandomAccessFile raf = null;

			try
			{
				// 连接网络文件
				URL url = new URL(mFileInfo.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				int length = -1;

				if (connection.getResponseCode() == HttpStatus.SC_OK)
				{
					// 获得文件的长度
					length = connection.getContentLength();
				}

				if (length <= 0)
				{
					return;
				}

				File dir = new File(DOWNLOAD_PATH);
				if (!dir.exists())
				{
					dir.mkdir();
				}

				// 在本地创建文件
				File file = new File(dir, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				// 设置文件长度
				raf.setLength(length);
				mFileInfo.setLength(length);
				mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
			}
			catch (Exception e)
			{
				mHandler.obtainMessage(0x404,mFileInfo).sendToTarget();
				e.printStackTrace();
			}
			finally
			{
				if (connection != null)
				{
					connection.disconnect();
				}
				if (raf != null)
				{
					try
					{
						raf.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}



}
