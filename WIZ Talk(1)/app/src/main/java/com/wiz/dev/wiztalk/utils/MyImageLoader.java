package com.wiz.dev.wiztalk.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.wiz.dev.wiztalk.R;

public class MyImageLoader {

    private static MyImageLoader mInstance;

    private LruCache<String, Bitmap> mLruCache;

    private ExecutorService mThreadPool;

    private Semaphore mSempaphorePoolThreadHandler = new Semaphore(0);

    private Semaphore mSemaphoreThreadPool;

    private static final int DEFAULT_THREAD_COUNT = 1;

    private static Type mType = Type.FIFO;

    public enum Type {
        FIFO, LIFO;
    }

    private LinkedList<Runnable> mTaskQueue;

    // 后台轮训线程
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    // UI线程中的 Handler
    private Handler mUIHandler;

    private MyImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSempaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mSemaphoreThreadPool = new Semaphore(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;
    }

    public static MyImageLoader getInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (MyImageLoader.class) {
                mInstance = new MyImageLoader(threadCount, type);
            }
        }
        return mInstance;
    }

    public void loadImage(final String path, final ImageView imageView, final boolean isSrc) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                public void handleMessage(Message msg) {
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;
                    Bitmap bitmap = holder.bitmap;
                    if (imageView.getTag().toString().equals(path)) {
//						imageView.setImageBitmap(bitmap);
                        if (isSrc){
                            if(bitmap != null)
                                imageView.setImageBitmap(bitmap);
                            else
                                imageView.setImageResource(R.drawable.ic_loading);
                        }

                        else
                            imageView.setBackground(new BitmapDrawable(bitmap));
                    }
                }
            };
        }
        Bitmap bitmap = getBitmapFromLruCache(path);
        if (bitmap != null) {
            refreashBitmap(path, imageView, bitmap);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    ImageSize imageSize = getImageViewSize(imageView);
//					Bitmap bitmap = decodeSampledBitmapFromPath(path,imageSize.width,imageSize.height);
                    Bitmap bitmap = Utils.createVideoThumbnail(path, imageSize.width, imageSize.height);
                    if(bitmap != null){
                        addBitmapToLruCache(path, bitmap);
                        refreashBitmap(path, imageView, bitmap);
                    }
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    private void refreashBitmap(final String path, final ImageView imageView, Bitmap bitmap) {
        Message message = Message.obtain();
        ImgBeanHolder mHolder = new ImgBeanHolder();
        mHolder.bitmap = bitmap;
        mHolder.path = path;
        mHolder.imageView = imageView;
        message.obj = mHolder;
        mUIHandler.sendMessage(message);
    }

    protected void addBitmapToLruCache(String path, Bitmap bitmap) {
        if (getBitmapFromLruCache(path) == null) {
            if (bitmap != null)
                mLruCache.put(path, bitmap);
        }
    }

    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    private int caculateInSampleSize(Options options, int reqwidth, int reqheight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqwidth || height > reqheight) {
            int widthRadio = Math.round(width * 1.0f / reqwidth);
            int heightRadio = Math.round(height * 1.0f / reqheight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();

        LayoutParams lp = imageView.getLayoutParams();

        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        int width = imageView.getWidth();

        if (width <= 0) {
            width = lp.width;
        }

        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }

        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();

        if (height <= 0) {
            height = lp.height;
        }

        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");
        }

        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;

        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            int fieldValue = field.getInt(object);

            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
                value = fieldValue;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return value;
    }

    private void addTask(Runnable task) {
        mTaskQueue.add(task);

        if (mPoolThreadHandler == null)
            try {
                mSempaphorePoolThreadHandler.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        mPoolThreadHandler.sendEmptyMessage(0x111);
    }

    // 根据path在缓存中获取bitmap
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    private Runnable getTask() {

        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }

        return null;
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        String path;
        ImageView imageView;
    }

    private class ImageSize {
        int width;
        int height;
    }

}
