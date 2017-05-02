package com.wiz.dev.wiztalk.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.LocalFileSelectActivity;
import com.wiz.dev.wiztalk.adapter.ChildAdapter;
import com.wiz.dev.wiztalk.adapter.MediaSelectAdapter;
import com.wiz.dev.wiztalk.dto.model.MediaModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/11.
 */
public class ImageFragment extends Fragment {

    private GridView mGridView;
    public static final int SCAN_OK = 0x118;
    private List<String> list;
    ChildAdapter adapter;

    LocalFileSelectActivity menuActivity;
    private ProgressDialog mProgressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuActivity = (LocalFileSelectActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        menuActivity.currentFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image,null);
        mGridView = (GridView) view.findViewById(R.id.child_grid);
        getImageList();
        return view;
    }

    void getImageList() {
        mProgressDialog = ProgressDialog.show(menuActivity, null, "正在加载...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getActivity().getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

                if (mCursor.moveToFirst()) {
                    list = new ArrayList<String>();
                    do {
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null)
                            continue;
                        if(parentFile.list()==null)continue;

                        if (!path.endsWith(".jpg")
                                && !path.endsWith(".png")
                                && !path.endsWith(".jpeg"))
                            continue;

                        list.add(path);
                    } while (mCursor.moveToNext());
                    mHandler.sendEmptyMessage(SCAN_OK);
                }
                mCursor.close();
                // 通知Handler扫描图片完成

            }
        }).start();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    adapter = new ChildAdapter(getActivity(), list, mGridView);
                    mGridView.setAdapter(adapter);

                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ChildAdapter adapter = (ChildAdapter) parent.getAdapter();
                            String galleryModel =  adapter.getItem(position);
                            adapter.addSelect(position, galleryModel);
                        }
                    });
                    break;

            }
        };
    };


    public List<String> getImagePath(){
        Map<Integer, String> map = adapter.getSelectImagePath();
        if(map != null){
            List<String> list = new ArrayList<String>();
            for(Map.Entry<Integer,String> entry : map.entrySet()){
                list.add(entry.getValue());
            }
            return list;
        }
        return null;
    }
}
