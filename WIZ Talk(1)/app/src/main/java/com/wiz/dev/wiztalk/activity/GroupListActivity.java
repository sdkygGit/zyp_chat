package com.wiz.dev.wiztalk.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.skysea.group.GroupService;
import com.skysea.group.packet.GroupSearch;
import com.skysea.group.packet.RSMPacket;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.adapter.base.BaseAdapterHelper;
import com.wiz.dev.wiztalk.adapter.base.QuickAdapter;
import com.wiz.dev.wiztalk.dto.model.AppInfo;
import com.wiz.dev.wiztalk.dto.model.GroupEntity;
import com.wiz.dev.wiztalk.dto.util.Utils;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;

import org.afinal.simplecache.ACache;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.fragment_app_chat_list)
public class GroupListActivity extends BaseActivity {

    private static final String TAG = "GroupListFragment";

    public static final String CACHE_KEY = "groupList";

    @ViewById(android.R.id.list)
    ListView mListView;

//	@ViewById(R.id.et_group_search)
//	ClearEditText et_group_search;

    List<GroupEntity> mDatas = new ArrayList<GroupEntity>();

    QuickAdapter<GroupEntity> mAdapter;

    View view;
    PopupWindow pop;

    @ViewById
    TextView title;

    @ViewById
    ImageView back;

    @Click(R.id.back)
    void onBack(View view){
        finish();
    }


    @Click(R.id.right_btn)
    void onRight(View view){
        ComponentName component = new ComponentName(
                GroupListActivity.this, ContactSelectActivity_.class);
        Intent intent = new Intent();
        //设置Intent的Component属性
        intent.setComponent(component);
        //设置参数
        intent.putExtra("localUserName", MyApplication.getInstance().getLocalUserName());
        intent.putExtra("isPickMode", false);

        //启动SecondActivity
        startActivity(intent);
    }

//	private Dialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @AfterViews
    void afterViews() {
        Log.d(TAG, "afterViews() ");
        // registerForContextMenu(mListView);

        String titleName = "我加入的群";

        title.setText(titleName);

        mAdapter = new QuickAdapter<GroupEntity>(this,
                R.layout.list_item_app_list, mDatas) {

            @Override
            protected void convert(BaseAdapterHelper helper, final GroupEntity item) {
                //TODO  设置头像
//				ImageView ivIcon = (ImageView) helper.getView(R.id.iv_icon);
//				setAvatar(item, ivIcon);
                helper.setText(R.id.tv_name, item.getName());
            }

        };
        mListView.setAdapter(mAdapter);
        /*et_group_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence str, int start, int before, int count) {
				BackgroundExecutor.cancelAll("doInbackgroundSearch", true);
				doInbackgroundSearch(str.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable edit) {
				
			}
		});*/

//		doInbackground();
    }


    @Override
    protected void onResume() {
        super.onResume();

        doInbackground();
    }

    @Click(R.id.group_list_search)
    void searchViewClick(View view) {
        GroupSearchActivity_.intent(this).start();
    }


    @UiThread
    void onPreExecute() {
        L.d(TAG, "onPreExecute()");
    }

    @Background(id = "doInbackgroundSearch", delay = 100)
    public void doInbackgroundSearch(String groupName) {
        L.d(TAG, "doInbackgroundSearch() ");
        List<GroupEntity> results = new ArrayList<GroupEntity>();
        try {
            GroupService groupService = XmppManager.getInstance().getGroupService
                    (OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            // 搜索条件
            DataForm searchForm = new DataForm(DataForm.Type.submit);

            FormField field = new FormField("name");
            field.addValue(groupName);
            searchForm.addField(field);

            // 分页信息
            RSMPacket rsm = new RSMPacket();
            rsm.setIndex(0);
            rsm.setMax(10);

            // Act
            GroupSearch result = groupService.search(new GroupSearch(searchForm, rsm));

            for (DataForm.Item item : result.getDataForm().getItems()) {
                results.add(mapFields(item));
            }

            onPostExcute(results);

        } catch (Exception e) {
            e.printStackTrace();
            onPostExcute(null);
        }
    }


    @Background(id = "doInbackground", delay = 100)
    public void doInbackground() {
        Log.d(TAG, "doInbackground() ");

        List<GroupEntity> results = new ArrayList<GroupEntity>();
        try {
            GroupService groupService = XmppManager.getInstance().getGroupService(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
            DataForm resultForm = groupService.getJoinedGroups();
            for (DataForm.Item item : resultForm.getItems()) {
                results.add(mapFields(item));
            }
            //TODO 缓存
            List<GroupEntity> responseCache = null;
            String groupListStr = ACache.get(this).getAsString(CACHE_KEY);
            if (!TextUtils.isEmpty(groupListStr)) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    responseCache = mapper.readValue(groupListStr, new TypeReference<List<GroupEntity>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!isEqueals(results, responseCache)) {
                ACache.get(this).put(CACHE_KEY, Utils.writeValueAsString(results));
            }
            if (results == null) {
                results = responseCache;
            }
            onPostExcute(results);

        } catch (Exception e) {
            e.printStackTrace();
            onPostExcute(null);
        }
    }

    private boolean isEqueals(List<GroupEntity> r1,
                              List<GroupEntity> r2) {

        if (r1 == null || r2 == null) {
            return false;
        }
        String str1 = Utils.writeValueAsString(r1);
        String str2 = Utils.writeValueAsString(r2);
        if (str1 != null && str1.equals(str2)) {
            return true;
        }
        return false;
    }

    private GroupEntity mapFields(DataForm.Item item) {
        GroupEntity entity = new GroupEntity();
        for (FormField field : item.getFields()) {
            if (field.getVariable().equals("jid")) {
                entity.setJid(field.getValues().get(0));
            }
            if (field.getVariable().equals("name")) {
                entity.setName(field.getValues().get(0));
            }/* else if (field.getVariable().equals("description")) {
            	entity.setDescription(field.getValues().get(0));//返回的数据有问题
            }*/
        }
        return entity;
    }

    @UiThread
    public void onPostExcute(List<GroupEntity> respones) {
        L.d(TAG, "onPostExcute() ");
        if (respones == null || respones.size() == 0) {
//			Toast.makeText(this, "查询结果为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatas.clear();
        mDatas.addAll(respones);

        mAdapter.clear();
        mAdapter.addAll(respones);
        mAdapter.notifyDataSetChanged();
        // TODO: 2016/3/15 删除自己已经不在的群的消息 ，防止自己接受不到群不存在的消息
       /* String[] ids = new String[respones.sizeBigger()];
        for (int i = 0; i < respones.sizeBigger(); i++) {
            ids[i] = respones.get(i).getJid();
        }
        XmppDbManager.getInstance(this).deleteOldGroupMsg(ids);*/
    }

    private static final int REQUEST_CODE_CHAT_DETAIL = 1;

    @ItemClick(android.R.id.list)
    void onItemClick(GroupEntity group) {
        L.d(TAG, "onItemClick() ");
        XChatDetailActivity_
                .intent(GroupListActivity.this)
                .localMember(MyApplication.getInstance().getLocalMember())
                .remoteUserName(group.getJid())
                .remoteDisplayName(group.getName())  //remoteDisplayName 
                .fromWhere(XChatDetailActivity.FROM_GROUP_LIST_ACTIVITY)
                .startForResult(REQUEST_CODE_CHAT_DETAIL);
    }

    @OnActivityResult(REQUEST_CODE_CHAT_DETAIL)
    void onActivityResultChatDetail(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            doInbackground();
        }
    }

    /**
     * 设置头像
     *
     * @param item    appinfo
     * @param mAvatar 头像ImageView
     */
    private void setAvatar(AppInfo item, ImageView mAvatar) {
        if (mAvatar.getTag() == null || mAvatar.getTag() != item.getAvatar()) {// 第二个参数是因为控件是复用的
            // 可以通过这种方式设置那些不需要刷新的的值,因为iv通过网络加载，会导致列表项的图片更新频繁
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(new ColorDrawable(Color.WHITE))
                    .showImageForEmptyUri(R.drawable.ic_default_qun_mini)
                    .showImageOnFail(R.drawable.ic_default_qun_mini)
                    .cacheInMemory(false).cacheOnDisk(true)
                    .displayer(new SimpleBitmapDisplayer())
//					.displayer(new RoundedBitmapDisplayer(999))
                    .build();
//			ImageLoader.getInstance().displayImage(null, mAvatar, options);
            mAvatar.setTag(item.getAvatar());
        }
    }
}
