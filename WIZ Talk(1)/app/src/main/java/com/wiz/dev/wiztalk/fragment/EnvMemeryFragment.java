package com.wiz.dev.wiztalk.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.LocalFileSelectActivity;
import com.wiz.dev.wiztalk.adapter.EventHandler;
import com.wiz.dev.wiztalk.dto.model.MediaModel;
import com.wiz.dev.wiztalk.utils.FileManager;

public class EnvMemeryFragment extends Fragment {

    ListView listView;
    private EventHandler mHandler;
    private EventHandler.TableRow mTable;
    private FileManager mFileMag;
    private TextView mPathLabel;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_env_sdcard, null);
        listView = (ListView) view.findViewById(R.id.listView);
        mFileMag = new FileManager();
        mFileMag.setShowHiddenFiles(false);
        mFileMag.setSortType(2);

        mHandler = new EventHandler(getArguments().getString("path"), getActivity(), mFileMag);
        mTable = mHandler.new TableRow();

        mHandler.setListAdapter(mTable);
        listView.setAdapter(mTable);

        mPathLabel = (TextView) view.findViewById(R.id.path_label);

        mHandler.setUpdateLabels(mPathLabel);

        initEvent();
        return view;
    }

    void initEvent() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final String item = mHandler.getData(position);
                File file = new File(mFileMag.getCurrentDir() + "/" + item);
                if (file.isDirectory()) {
                    if (file.canRead()) {
                        mHandler.updateDirectory(mFileMag.getNextDir(item, false));
                    }
                } else {
                    mTable.addMultiPosition(position, file);
                }
            }
        });
    }

    public boolean goBack() {
        return mHandler.goBack();
    }

    public ArrayList<MediaModel> getData() {
        if (mTable.getSelectFile() != null && mTable.getSelectFile().size() > 0) {
            ArrayList<MediaModel> files = new ArrayList<MediaModel>();
            for (Map.Entry<Integer, File> fileEntry : mTable.getSelectFile()) {
                files.add(new MediaModel(false,fileEntry.getValue().getAbsolutePath(),fileEntry.getValue().getName(), (int) fileEntry.getValue().length(),-1L));
            }
            return files;
        }
        return null;
    }
}
