/*
 * @Title DownloadTask.java
 * @Copyright Copyright 2010-2015 Yann Software Co,.Ltd All Rights Reserved.
 * @Description：
 * @author Yann
 * @date 2015-8-7 下午10:11:05
 * @version 1.0
 */
package com.download.services;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpStatus;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.download.db.ThreadDAO;
import com.download.db.ThreadDAOImpl;
import com.download.entities.FileInfo;
import com.download.entities.ThreadInfo;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.utils.Utils;

/**
 * 下载任务类
 *
 * @author Yann
 * @date 2015-8-7 下午10:11:05
 */
public class DownloadTask {
    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadDAO mDao = null;
    private int mFinised = 0;
    public boolean isPause = false;
    private int mThreadCount = 1;  // 线程数量
    private List<DownloadThread> mDownloadThreadList = null; // 线程集合


    boolean isError;

    /**
     * @param mContext
     * @param mFileInfo
     */
    public DownloadTask(Context mContext, FileInfo mFileInfo, int count) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = count;
        mDao = new ThreadDAOImpl(mContext);
        this.mCallBack = mFileInfo.getCallBack();
    }

    public void downLoad() {
        // 读取数据库的线程信息
        List<ThreadInfo> threads = mDao.getThreads(mFileInfo.getUrl());
        ThreadInfo threadInfo = null;

        if (0 == threads.size()) {
            // 计算每个线程下载长度
            int len = mFileInfo.getLength() / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                // 初始化线程信息对象
                threadInfo = new ThreadInfo(i, mFileInfo.getUrl(),
                        len * i, (i + 1) * len - 1, 0);

                if (mThreadCount - 1 == i)  // 处理最后一个线程下载长度不能整除的问题
                {
                    threadInfo.setEnd(mFileInfo.getLength());
                }

                // 添加到线程集合中
                threads.add(threadInfo);
                mDao.insertThread(threadInfo);
            }
        }

        mDownloadThreadList = new ArrayList<DownloadTask.DownloadThread>();
        // 启动多个线程进行下载
        for (ThreadInfo info : threads) {
            DownloadThread thread = new DownloadThread(info);
            thread.start();
            // 添加到线程集合中
            mDownloadThreadList.add(thread);
        }
    }


    void runOnService() {
        ThreadInfo mThreadInfo;

        List<ThreadInfo> threads = mDao.getThreads(mFileInfo.getUrl());

        if (0 == threads.size()) {
            // 计算每个线程下载长度
            int len = mFileInfo.getLength();
            // 初始化线程信息对象
            mThreadInfo = new ThreadInfo(0, mFileInfo.getUrl(),
                    0,  len , 0);
            mDao.insertThread(mThreadInfo);
        }else {
            mThreadInfo = threads.get(0);
        }

        if (mCallBack != null) {
            mCallBack.onStart();
        }
        isError = false;
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
                            if (mCallBack != null) {
                                mCallBack.onPregress(f);
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
                if (mCallBack != null) {
                    mCallBack.onFinish(mFileInfo);
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
            if (Utils.isNetworkAvailable(mContext)) {
                MyApplication.getInstance().getDownloadManager().map.put(mFileInfo.getId(), mFileInfo);
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileId", mFileInfo.getId());
                mContext.startService(intent);
            } else {
                if (mCallBack != null) {
                    mCallBack.onNetError();
                }
            }
        }

    }


    /**
     * 下载线程
     *
     * @author Yann
     * @date 2015-8-8 上午11:18:55
     */
    private class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo = null;
        public boolean isFinished = false;  // 线程是否执行完毕

        /**
         * @param mInfo
         */
        public DownloadThread(ThreadInfo mInfo) {
            this.mThreadInfo = mInfo;
        }

        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            if (mCallBack != null) {
                mCallBack.onStart();
            }
            isError = false;
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
//								intent.putExtra("finished", f);
//								intent.putExtra("id", mFileInfo.getId());
//								mContext.sendBroadcast(intent);

                                if (mCallBack != null) {
                                    mCallBack.onPregress(f);
                                }
                            }
                        }

                        // 在下载暂停时，保存下载进度
                        if (isPause) {
//                            mDao.updateThread(mThreadInfo.getUrl(),
//                                    mThreadInfo.getId(),
//                                    mThreadInfo.getFinished());


                            Log.i("mThreadInfo", mThreadInfo.getId() + "finished = " + mThreadInfo.getFinished());
                            MyApplication.getInstance().getDownloadManager().map.remove(mFileInfo.getId());

                            new File(DownloadService.DOWNLOAD_PATH + mFileInfo.getFileName()).delete();
                            mDao.deleteThread(mFileInfo.getUrl());

                            return;
                        }
                    }

                    // 标识线程执行完毕
                    isFinished = true;
                    checkAllThreadFinished();
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
                isPause = true;
                if (Utils.isNetworkAvailable(mContext)) {

                    MyApplication.getInstance().getDownloadManager().map.put(mFileInfo.getId(), mFileInfo);
                    Intent intent = new Intent(mContext, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("fileId", mFileInfo.getId());
                    mContext.startService(intent);
                } else {
                    if (mCallBack != null) {
                        mCallBack.onNetError();
                    }
                }
            }

        }
    }

    /**
     * 判断所有的线程是否执行完毕
     *
     * @return void
     * @author Yann
     * @date 2015-8-9 下午1:19:41
     */
    private synchronized void checkAllThreadFinished() {
        boolean allFinished = true;

        // 遍历线程集合，判断线程是否都执行完毕
        for (DownloadThread thread : mDownloadThreadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }

        if (allFinished) {
            // 删除下载记录
            mDao.deleteThread(mFileInfo.getUrl());

            MyApplication.getInstance().getDownloadManager().map.remove(mFileInfo.getId());
            // 发送广播知道UI下载任务结束
//			Intent intent = new Intent(DownloadService.ACTION_FINISHED);
//			intent.putExtra("fileInfo", mFileInfo);
//			mContext.sendBroadcast(intent);

            if (mCallBack != null) {
                mCallBack.onFinish(mFileInfo);
            }

        }
    }

    public FileDownloadCallBack mCallBack;

}
