package com.wiz.dev.wiztalk.fragment;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.activity.H5Activity_;
import com.wiz.dev.wiztalk.adapter.base.BaseAdapterHelper;
import com.wiz.dev.wiztalk.adapter.base.QuickAdapter;
import com.wiz.dev.wiztalk.dto.model.AppFileInfo;
import com.wiz.dev.wiztalk.dto.response.AppFileListRespones;
import com.wiz.dev.wiztalk.R;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@EFragment(R.layout.fragment_app_detail)
public class AppDetailFragment extends Fragment {
 
	private static final String TAG = "AppDetailFragment";

	@ViewById(R.id.etSearch)
	EditText edt_search;

	@ViewById(R.id.id_list)
	PullToRefreshListView mRefreshListView;

	QuickAdapter<AppFileInfo> mAdapter;

	List<AppFileInfo> mDatas = new ArrayList<AppFileInfo>();

	@FragmentArg("app_id")
	String app_id;// 根据ID获取内容

	String appsrvUrl = "/bankappmobile/phone/client/getFiles";// 应用下的文件

	String url;// 服务地址
	
	private int mPagerNum = 1;//分页计数器

	private int mPageSize = 10;

	@AfterInject
	public void afterInject() {
		Log.d(TAG, "afterInject() ");
	}

	
	@AfterViews
	public void afterViews() {
		Log.d(TAG, "afterViews() ");
		mAdapter = new QuickAdapter<AppFileInfo>(getActivity(),
				R.layout.list_item_app_content, mDatas) {
			@Override
			protected void convert(BaseAdapterHelper helper,
					final AppFileInfo item) {
				TextView tvTitle = helper.getView(R.id.tv_title);
					tvTitle.setText(item.getTitle());
				tvTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
				 if(item.isBold()){
					 tvTitle.setTextColor(getResources().getColor(R.color.item_app_context_file));
				 }
				helper.setOnClickListener(R.id.main_layout,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
//								Toast.makeText(getActivity(), "todo:"+item.isBold(), 1).show();
								H5Activity_.intent(getActivity())
										.remoteDisplayName("详情")
										.flags(Intent.FLAG_ACTIVITY_NEW_TASK)
										.url(item.getUrl()).start();
							}
						});
			}
		};
		mRefreshListView.setMode(Mode.BOTH);
		mRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						ILoadingLayout startLabels = mRefreshListView
								.getLoadingLayoutProxy(true, false);
						startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
						startLabels.setRefreshingLabel("正在载入...");// 刷新时
						startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
						mPagerNum = 1;
						AppDetailFragment.this.onPullDownToRefresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 上拉加载
						ILoadingLayout endLabels = mRefreshListView
								.getLoadingLayoutProxy(false, true);
						endLabels.setPullLabel("上拉加载...");// 刚下拉时，显示的提示
						endLabels.setRefreshingLabel("正在载入...");// 刷新时
						endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
						mPagerNum ++;
						AppDetailFragment.this.onPullUpToRefresh();
					}

				});

		mRefreshListView.setAdapter(mAdapter);
		// 设置自动刷新
		mRefreshListView.setRefreshing(true);
		// doInbackground();
		

	}


	// 下拉刷新
	@Background
	protected void onPullDownToRefresh() {
		Log.d(TAG, "onPullDownToRefresh()");
		String uid = MyApplication.getInstance().getUid();
		String token = MyApplication.getInstance().getToken();
		url = MyApplication.getInstance().getPaasBaseUrl() + appsrvUrl
				+ "?uid=" + uid + "&token=" + token + "&appid=" + app_id+"&pageNum="+mPagerNum+"&pageSize="+mPageSize ;
		HttpUtils http = new HttpUtils();
		http.configRequestRetryCount(0);
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Log.d(TAG, "onLoading() ");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String str = responseInfo.result;
				Log.d(TAG, "onSuccess() responseInfo:" + str);
				
				AppFileListRespones respones = null;
				try {
					respones = new Gson().fromJson(str,AppFileListRespones.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}

				if (respones ==null || respones.getData() == null || respones.isSucceed() == false) {
					return;
				}
				Log.d(TAG, "onSuccess() data:" + respones.getData().toString());
				List<AppFileInfo> appFileInfos = respones.getData();
				//
				onPostExcute(appFileInfos, true);
			}

			@Override
			public void onStart() {
				Log.d(TAG, "onStart() ");
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.d(TAG, "onFailure() ："+msg);
				mRefreshListView.onRefreshComplete();
				Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
			}
		});

	}
	//TODO 搜索
	@Click(R.id.btnSearch)
	void btnKeySearch(View view){
		Log.d(TAG, "btnKeySearch()");
		String key = edt_search.getText().toString().trim();
		if(TextUtils.isEmpty(key))
			return ;
		doInbackground(key);
	}
	@Background
	public void doInbackground(String key) {
		Log.d(TAG, "doInbackground()");
		
		String uid = MyApplication.getInstance().getUid();
		String token = MyApplication.getInstance().getToken();
		//TODO key 参数
		
		url = MyApplication.getInstance().getPaasBaseUrl() + appsrvUrl
				+ "?uid=" + uid + "&token=" + token + "&appid=" + app_id+"&pageNum="+mPagerNum+"&pageSize="+mPageSize+"&fileName="+key ;
		
		HttpUtils http = new HttpUtils();
		http.configRequestRetryCount(0);
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Log.d(TAG, "onLoading() ");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String str = responseInfo.result;
				Log.d(TAG, "onSuccess() responseInfo:" + str);
				AppFileListRespones respones = null;
				try {
					respones = new Gson().fromJson(str,AppFileListRespones.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}

				if (respones ==null || respones.getData() == null || respones.isSucceed() == false) {
					return;
				}
				Log.d(TAG, "onSuccess() data:" + respones.getData().toString());
				List<AppFileInfo> appFileInfos = respones.getData();
				//
				onPostExcute(appFileInfos, true);

			}

			@Override
			public void onStart() {
				Log.d(TAG, "onStart() ");
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.d(TAG, "onFailure() :"+msg);
				mRefreshListView.onRefreshComplete();
				Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	//  上拉加载
	@Background
	protected void onPullUpToRefresh() {
		Log.d(TAG, "onPullUpToRefresh()");
		
		String uid = MyApplication.getInstance().getUid();
		String token = MyApplication.getInstance().getToken();
		//TODO pagerNum 参数
		url = MyApplication.getInstance().getPaasBaseUrl() + appsrvUrl
				+ "?uid=" + uid + "&token=" + token + "&appid=" + app_id+"&pageNum="+mPagerNum+"&pageSize="+mPageSize ;
		
		HttpUtils http = new HttpUtils();
		http.configRequestRetryCount(0);
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Log.d(TAG, "onLoading() ");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String str = responseInfo.result;
				Log.d(TAG, "onSuccess() responseInfo:" + str);
				AppFileListRespones respones = null;
				try {
					respones = new Gson().fromJson(str,AppFileListRespones.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}

				if (respones ==null || respones.getData() == null || respones.isSucceed() == false) {
					Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
					
					return;
				}
				Log.d(TAG, "onSuccess() data:" + respones.getData().toString());
				List<AppFileInfo> appFileInfos = respones.getData();
				//
				onPostExcute(appFileInfos, false);

			}

			@Override
			public void onStart() {
				Log.d(TAG, "onStart() ");
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.d(TAG, "onFailure() ");
				mRefreshListView.onRefreshComplete();
				Toast.makeText(getActivity(), "访问失败", Toast.LENGTH_SHORT).show();
			}
		});
		
		/*HttpEngine httpEngine = HttpEngine.getInstance();
		Type type = new TypeToken<ApiResponse<AppFileInfo>>(){}.getType();
		
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("uid", uid);
		paramsMap.put("token", token);
		paramsMap.put("appid", app_id);
		
		url = MyApplication.getInstance().getPaasBaseUrl() + appsrvUrl;
		
		try {
			ApiResponse<AppFileInfo> respones = httpEngine.postHandle(url, paramsMap, type);
			Log.d(TAG, "onPullUpToRefresh respones :"+respones);
			onPostExcute(respones.getData(), false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	/**
	 * 
	 * @param datas
	 * @param flag true代表刷新 ，false：加载
	 */
	@UiThread
	public void onPostExcute(List<AppFileInfo> datas, boolean flag) {
		Log.d(TAG, "onPostExcute() datas sizeBigger:" + datas.size());
		
		mRefreshListView.onRefreshComplete();
		if(datas.size()==0||datas==null){
			Toast.makeText(getActivity(), "没有数据", Toast.LENGTH_SHORT).show();
			return ;
		}
		// true代表刷新 ，false：加载
		if (flag) {
			mDatas.clear();
			mAdapter.clear();
		}
		
		mDatas.addAll(datas);
//		mAdapter.notifyDataSetChanged();
		//TODO 单独的执行上面两句，居然不行。？？？？
		mAdapter.addAll(mDatas);
		

		if (!flag) {
//			Toast.makeText(getActivity(), "pageNum= " + mPagerNum ,
//					Toast.LENGTH_SHORT).show();
			mRefreshListView.getRefreshableView().setSelection(mDatas.size() -1);
			Toast.makeText(getActivity(), "加载完成",
					Toast.LENGTH_SHORT).show();
		} else{
			mRefreshListView.getRefreshableView().setSelection(0);
		}
		
		
	}

}
