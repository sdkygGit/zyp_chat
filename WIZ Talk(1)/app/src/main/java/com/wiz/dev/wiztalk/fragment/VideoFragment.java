/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wiz.dev.wiztalk.fragment;

import java.util.ArrayList;
import java.util.Map;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.LocalFileSelectActivity;
import com.wiz.dev.wiztalk.adapter.MediaSelectAdapter;
import com.wiz.dev.wiztalk.dto.model.MediaModel;


public class VideoFragment extends Fragment {

    private final static Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    private MediaSelectAdapter mVideoAdapter;
    private ListView mVideoGridView;
    private Cursor mCursor;
    private ArrayList<MediaModel> mGalleryModelList;
    private View mView;

    LocalFileSelectActivity menuActvity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuActvity = (LocalFileSelectActivity) getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();
        menuActvity.currentFragment = this;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_video_chooser, null);
        mVideoGridView = (ListView) mView.findViewById(R.id.gridViewFromMediaChooser);
        initVideos();
        return mView;
    }

    ;

    public void initVideos() {
        try {
            final String orderBy = MediaStore.Video.Media.DATE_TAKEN;

            mCursor = getActivity().getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, null, null, null,
                    orderBy + " DESC");
            setAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        int count = mCursor.getCount();
        if (count > 0) {
            mCursor.moveToFirst();
            mGalleryModelList = new ArrayList<MediaModel>();
            for (int i = 0; i < count; i++) {
                mCursor.moveToPosition(i);
                String url = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String size = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                long addTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                mGalleryModelList.add(new MediaModel(url, name, Integer.valueOf(size), addTime, true));
            }
            mVideoAdapter = new MediaSelectAdapter(getActivity(), mGalleryModelList);
            mVideoGridView.setAdapter(mVideoAdapter);
            mVideoGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MediaSelectAdapter adapter = (MediaSelectAdapter) parent.getAdapter();
                    MediaModel galleryModel = (MediaModel) adapter.getItem(position);
                    adapter.addSelect(position, galleryModel);
                }
            });
        }
    }

    public ArrayList<MediaModel> getData(){
        Map<Integer, MediaModel> map = mVideoAdapter.getSelectMediaModel();

        if(map != null){
            ArrayList<MediaModel> models = new ArrayList<MediaModel>();
            for(Map.Entry<Integer,MediaModel> fileEntry :map.entrySet()){
                models.add(fileEntry.getValue());
            }

            return models;
        }

        return null;
    }
}
