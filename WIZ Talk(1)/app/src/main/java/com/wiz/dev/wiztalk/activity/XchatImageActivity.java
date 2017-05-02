package com.wiz.dev.wiztalk.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

@EActivity(R.layout.list_item_chat_item_imgs)
public class XchatImageActivity extends ActionBarActivity {

    private static final String TAG = "XchatImageActivity";

    @ViewById(R.id.iv_photoview)
    PhotoView mPhotoView;


    @ViewById(R.id.layout_loading)
    LinearLayout mLayoutLoading;

    @ViewById(R.id.pbloading)
    ProgressBar mProgressBar;

    @ViewById(R.id.tv_percent)
    TextView mTvPercennt;

    @Extra
    String pathUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MyApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @AfterViews
    void afterViews() {
        mPhotoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                Log.i(TAG, "ChatImagesActivity finish");

                finish();
            }
        });
//file:///storage/emulated/0/yixin/image/out/1460447128504.jpg




        try {

//            mPhotoView.setImageBitmap(BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache().get(pathUrl).getPath()));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(pathUrl));


            mPhotoView.setImageDrawable(new BitmapDrawable(bitmap));
//            float scale = getSc(bitmap);
//
//            mPhotoView.setScale(scale);
            mLayoutLoading.setVisibility(View.GONE);
        } catch (Exception e) {
            mPhotoView.setImageBitmap(ImagerLoaderOptHelper.getBitmapFromCache(pathUrl, new ImageSize(108, 108)));
            RequestQueue mQueue = Volley.newRequestQueue(XchatImageActivity.this);
            ImageRequest imageRequest = new ImageRequest(
                    pathUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            mPhotoView.setImageDrawable(new BitmapDrawable(response));
//                            float scale = getSc(response);
//                            mPhotoView.setScale(scale);
                            mLayoutLoading.setVisibility(View.GONE);
                        }
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mLayoutLoading.setVisibility(View.GONE);
                }
            });
            mQueue.add(imageRequest);
        }



//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(
//						new ColorDrawable(Color.parseColor("#33000000")))
//				.showImageForEmptyUri(R.drawable.card_photofail)
//				.showImageOnFail(R.drawable.card_photofail).cacheInMemory(false)
//				.cacheOnDisk(true)
//				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//						.bitmapConfig(Bitmap.Config.ARGB_8888)
//				.considerExifParams(true)
//				.displayer(new SimpleBitmapDisplayer())
//				.build();
//

//		ImageLoader.getInstance().displayImage(pathUrl, mPhotoView, options,
//				callBack2, new ImageLoadingProgressListener() {
//					@Override
//					public void onProgressUpdate(String imageUri, View view,
//							int current, int total) {
//						int percent = (int) (100 * current / total);
//						mTvPercennt.setText(String.valueOf(percent) + "%");
//					}
//				});

    }

    float getSc(Bitmap bitmap) {

        float scale =1.0f;
        WindowManager wm = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screemWidth = dm.widthPixels;
        int screemHeight = dm.heightPixels;

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        if(bitmapWidth<= screemWidth && bitmapHeight <= screemHeight){
            scale = Math.min(screemWidth/bitmapWidth,screemHeight/bitmapHeight);
        }else if(bitmapWidth > screemWidth && bitmapHeight > screemHeight){
            scale = Math.max(screemWidth / bitmapWidth, screemHeight / bitmapHeight);
        }else if(bitmapWidth < screemWidth && bitmapHeight > screemHeight){
            scale =  screemHeight / bitmapHeight;
        }else if(bitmapWidth > screemWidth && bitmapHeight < screemHeight){
            scale = screemWidth / bitmapWidth;
        }

       ;

        if(scale >  mPhotoView.getMaxScale())
            scale =mPhotoView.getMaxScale();
        else if(scale <  mPhotoView.getMinScale())
            scale = mPhotoView.getMinScale();
        return scale;
    }

    ImageLoadingListener callBack2 = new SimpleImageLoadingListener() {

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            mLayoutLoading.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            mLayoutLoading.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            mLayoutLoading.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }

    };

}
