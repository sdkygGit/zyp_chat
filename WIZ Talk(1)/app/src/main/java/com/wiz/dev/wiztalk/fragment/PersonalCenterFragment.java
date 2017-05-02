package com.wiz.dev.wiztalk.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.MainActivity;
import com.wiz.dev.wiztalk.activity.MeBarCodeActivity_;
import com.wiz.dev.wiztalk.activity.UserEditActivity_;
import com.wiz.dev.wiztalk.activity.UserEditPwdActivity_;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.CheckUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.SetUserAvatarRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.CheckUpdateResponse;
import com.wiz.dev.wiztalk.dto.response.LoginResponse;
import com.wiz.dev.wiztalk.dto.response.ResponseUpload;
import com.wiz.dev.wiztalk.dto.response.SetUserAvatarResponse;
import com.wiz.dev.wiztalk.preference.LoginPrefs_;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.ImageUtils;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.ProgressView;
import com.wiz.dev.wiztalk.view.dialog.MyAlertDialog;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.rest.RestErrorHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.NestedRuntimeException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import cn.finalteam.okhttpfinal.FileDownloadCallback;

/**
 * Created by Dong on 2016/3/16.
 */
@EFragment(R.layout.activity_personal_center)
public class PersonalCenterFragment extends FragmentBase implements RestErrorHandler {
    public static final String TAG = "PersonalCenterActivity";

    @Pref
    LoginPrefs_ loginPrefs;

    @ViewById
    ImageView iv_icon;
    //    @ViewById
//    TextView user_number;
//    @ViewById
//    TextView user_rank;
//    @ViewById
//    TextView user_post;
    @ViewById
    TextView user_telephony;
    @ViewById
    TextView user_call;
    @ViewById
    TextView user_email;
    //最上面的
    @ViewById
    TextView tv_name;
    @ViewById
    TextView tv_info_post;

    @ViewById
    RelativeLayout rv_user;

    @ViewById
    Button btnCheckUpdate;

    @ViewById
    ProgressView progressView;

    @ViewById
    ImageButton btnQr;

    Appmsgsrv8094 myRestClient;

    int iconSize = 108;

    //换头像相关
    public final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/EPIC/Portrait/";
    private String protraitPath;

    private File protraitFile;

    private Uri cropUri;

    private Uri origUri;

    public final static int CROP = 200;

    MainActivity context;

    @DimensionPixelOffsetRes(R.dimen.icon_size_biggest)
    int sizeBiggest;

    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int sizeBigger;

    @DimensionPixelOffsetRes(R.dimen.icon_size_normal)
    int sizeNormal;

    @DimensionPixelOffsetRes(R.dimen.icon_size_small)
    int sizeSmall;

    private String userName;

    LoginResponse response;


    @AfterInject
    void afterInject() {
        L.d(TAG, "afterInject()");
        myRestClient = Appmsgsrv8094Proxy.create(5 * 1000);
        myRestClient.setRestErrorHandler(this);
    }

    @AfterViews
    public void afterviews() {
        L.d(TAG, "afterViews() ");
        String titleName = getResources().getString(R.string.tab_me);
        initTopBarForLeft(titleName);
        userName = MyApplication.getInstance().getLocalUserName();

        response = MyApplication.getInstance().getLoginResponse();
        initValue();
        // 头像

        Member member = MyApplication.getInstance().getLocalMember();

        final String originalUrl = Utils.getAvataUrl(member.UserName, iconSize);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.persion_me)
                .showImageForEmptyUri(R.drawable.persion_me)
                .showImageOnFail(R.drawable.persion_me)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
//                .displayer(new SimpleBitmapDisplayer())
                .displayer(new RoundedBitmapDisplayer(999))
                .build();

        L.d(TAG, "originalUrl:" + originalUrl);
        ImageLoader.getInstance().displayImage(originalUrl, iv_icon, options);
        context = (MainActivity) getActivity();
    }

    private void initValue() {
        response = MyApplication.getInstance().getLoginResponse();
        if (response != null) {

            String nickName = response.Member.NickName;
//            String orgName = response.Member.OrgName;

            String email = response.Member.Email;
            String mobile = response.Member.Mobile;
            String tel = response.Member.Tel;
//            String level = response.Member.level;
//            String role = response.Member.role;

            tv_name.setText(nickName);
//            tv_info_post.setText(role);

//            user_number.setText(loginPrefs.userName().get());
//            user_rank.setText(level);

//            user_post.setText(role);
            user_telephony.setText(mobile);
            user_call.setText(tel);
            user_email.setText(email);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initValue();
    }

    @Click(R.id.btnLogout)
    void onClickBtnLogout(View view) {
        new MyAlertDialog(getActivity(), "注销登录", "确定要注销登录？", "取消", "确定", new MyAlertDialog.OnMyDialogBtnClickListener() {
            @Override
            public void onOkClick() {
                doInbackgroundExiteOnOpenfire();
                doInbackgroundCleanCache();
                MyApplication.getInstance().onReLogin();
            }

            @Override
            public void onCancleClick() {
            }
        }).showDialog();
    }


    @Click(R.id.zuoji_lay)
    void onClickBtnzuoji(View view) {
        new MyAlertDialog(getActivity(), "拨打电话", "确定拨打电话？", "取消", "确定", new MyAlertDialog.OnMyDialogBtnClickListener() {
            @Override
            public void onOkClick() {
                String zuoji = user_call.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + zuoji);
                intent.setData(data);
                startActivity(intent);
            }

            @Override
            public void onCancleClick() {
            }
        }).showDialog();
    }

    @Click(R.id.iphone_lay)
    void onClickBtniphone(View v) {
        final String iphone = user_telephony.getText().toString();
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.two_lay_dialog, null);
        TextView one = (TextView) view.findViewById(R.id.dialog_one);
        one.setText("打电话");
        TextView two = (TextView) view.findViewById(R.id.dialog_two);
        two.setText("发短信");
        Button cancle = (Button) view.findViewById(R.id.cancleBtn);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + iphone);
                intent.setData(data);
                startActivity(intent);
                dialog.cancel();
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + iphone);
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                sendIntent.putExtra("sms_body", "");
                startActivity(sendIntent);
                dialog.cancel();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.getWindow().setContentView(view);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(width * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

    }

    @Click(R.id.email_lay)
    void onClickBtnemail(View v) {
        String email = user_email.getText().toString();
        Intent dataE = new Intent(Intent.ACTION_SENDTO);
        dataE.setData(Uri.parse("mailto:" + email));
        startActivity(dataE);
    }

    @Background
    void doInbackgroundCleanCache() {
        ImagerLoaderOptHelper.cleanCache();
    }

    @Background
    void doInbackgroundExiteOnOpenfire() {
        XmppManager.getInstance().exit();
    }

    @Click(R.id.btnCheckUpdate)
    void onClickCheckUpdate(View v) {
        checkUpdateDoInBackground();
    }

    @Click(R.id.btnQr)
    void onClickBtnQr() {
        MeBarCodeActivity_.intent(getActivity()).start();
    }

    @Click(R.id.edit_user)
    void onClickEditUser() {
        UserEditActivity_.intent(getActivity()).member(response.Member).start();
    }


    @Click(R.id.eidt_user_pwd)
    void onClickEditUserPwd() {
        UserEditPwdActivity_.intent(getActivity()).start();
    }

    @Background
    void checkUpdateDoInBackground() {
        checkUpdateOnPreExecute();//检查更新进度提示框显示
        BaseRequest baseRequest = CacheUtils.getBaseRequest(getActivity());
        CheckUpdateRequest request = new CheckUpdateRequest();
        request.BaseRequest = baseRequest;
        request.VersionCode = Utils.getVersionCode(getActivity());
        request.VersionName = Utils.getVersionName(getActivity());

        L.d(TAG, "checkUpdateDoInBackground() request:" + request);
        CheckUpdateResponse response = myRestClient.checkUpdate(request);
        L.d(TAG, "checkUpdateDoInBackground() response:" + response);
        checkUpdateOnPostExecute(response);//确定是否下载新版本
    }

    private ProgressDialog mProgressDialog;

    @UiThread
    void checkUpdateOnPreExecute() {
        mProgressDialog = ProgressDialog.show(getActivity(), "提示", "检查更新");
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @UiThread
    void checkUpdateOnPostExecute(CheckUpdateResponse response) {
        L.d(TAG, "checkUpdateOnPostExecute() response:" + response);
        dismissProgressDialog();
        if (response == null) {
            return;
        }

        if (TextUtils.isEmpty(response.BaseResponse.ErrMsg) && response.isUpdate) {
            LinkedHashMap<String, String> oj = (LinkedHashMap<String, String>) response.Msg.getObjectContent();
            final String downloadUrl = oj.get("downloadUrl");
            final String fileName = oj.get("fileName");

            new MyAlertDialog(getActivity(), "发现新版本", "是否下载更新？", "取消", "下载", new MyAlertDialog.OnMyDialogBtnClickListener() {
                @Override
                public void onOkClick() {
                    downFile(downloadUrl, fileName);
                }

                @Override
                public void onCancleClick() {
                }
            }).showDialog();
        } else {
            Toast.makeText(getActivity(), "已经是最新版本", Toast.LENGTH_SHORT).show();
        }
    }

    private void downFile(final String downloadUrl, final String fileName) {
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wizTalk/" + fileName;
        final File saveFile = new File(filePath);
        if (saveFile.exists())
            saveFile.delete();
        cn.finalteam.okhttpfinal.HttpRequest.download(downloadUrl, saveFile, new FileDownloadCallback() {
            @Override
            public void onStart() {
                super.onStart();
                btnCheckUpdate.setVisibility(View.INVISIBLE);
                if (progressView != null)
                    progressView.setVisibility(View.VISIBLE);
            }

            //下载进度
            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                if (progressView != null)
                    progressView.setProgress(progress);
            }

            //下载失败
            @Override
            public void onFailure() {
                super.onFailure();
                if (progressView != null)
                    progressView.setText("下载失败");
                progressView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downFile(downloadUrl, fileName);
                    }
                });
            }

            //下载完成（下载成功）
            @Override
            public void onDone() {
                if (progressView != null)
                    progressView.setText("下载完成");

                progressView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent anZintent = new Intent(Intent.ACTION_VIEW);
                        anZintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        anZintent.setDataAndType(Uri.fromFile(saveFile), "application/vnd.android.package-archive");
                        getActivity().startActivity(anZintent);
                    }
                });

                Intent anZintent = new Intent(Intent.ACTION_VIEW);
                anZintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                anZintent.setDataAndType(Uri.fromFile(saveFile), "application/vnd.android.package-archive");
                getActivity().startActivity(anZintent);
            }
        });
    }

    @UiThread
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        Toast.makeText(getActivity(), "访问失败！", Toast.LENGTH_SHORT).show();
    }

    // TODO: 2016/3/31 换头像 相关 
    @Click(R.id.iv_icon)
    public void iv_icon(View view) {
        CharSequence[] items = {"本地图片", "拍照"};
        imageChooseItem(items);
    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        int result_ok = -1;
        if (resultCode == result_ok) {
            switch (requestCode) {
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                    startActionCrop(origUri);// 拍照后裁剪
                    break;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                    startActionCrop(data.getData());// 选图后裁剪
                    break;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                    uploadNewPhoto(data);// 上传新照片
                    break;
            }
        }

    }

    /**
     * 操作选择
     *
     * @param items
     */
    public void imageChooseItem(CharSequence[] items) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.two_lay_dialog, null);
        TextView one = (TextView) view.findViewById(R.id.dialog_one);
        one.setText(items[0]);
        TextView two = (TextView) view.findViewById(R.id.dialog_two);
        two.setText(items[1]);
        Button cancle = (Button) view.findViewById(R.id.cancleBtn);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImagePick();
                dialog.cancel();
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionCamera();
                dialog.cancel();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.getWindow().setContentView(view);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        dialog.getWindow().setLayout(width * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }

    /**
     * 选择图片裁剪
     */
    private void startImagePick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "选择图片"),
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    /**
     * 相机拍照
     *
     * @param
     */
    private void startActionCamera() {
       /* Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image*//*");
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    // 拍照保存的绝对路径
    private Uri getCameraTempFile() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            Toast.makeText(getActivity(), "无法保存上传的头像，请检查SD卡是否挂载",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // 照片命名
        String cropFileName = "EPIC_camera_" + timeStamp + ".jpg";
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);
        cropUri = Uri.fromFile(protraitFile);
        this.origUri = this.cropUri;
        return this.cropUri;
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        L.d(TAG, "getUploadTempFile() uri:" + uri);
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            Toast.makeText(getActivity(), "无法保存上传的头像，请检查SD卡是否挂载",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (thePath == null || "".equals(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(getActivity(),
                    uri);
        }
        int point = thePath.lastIndexOf('.');
        String ext = thePath.substring(point + 1);
        if (ext == null || "".equals(ext)) {
            ext = "jpg";
        }
        // 照片命名
        String cropFileName = "epic_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);

        cropUri = Uri.fromFile(protraitFile);
        L.d(TAG, "getUploadTempFile() cropUri:" + cropUri);
        return this.cropUri;
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     *             裁剪后图片
     */
    private void startActionCrop(Uri data) {
//		mCropOutPut = this.getUploadTempFile(data);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
//		intent.putExtra("output", mCropOutPut);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    @UiThread
    void onPreExecute() {
        L.d(TAG, "onPreExecute()");
        mProgressDialog = ProgressDialog.show(getActivity(), null, "正在上传头像...");
    }

    @Background
    void doInBackground(String fileUrl, ResponseUpload responseUpload) {
        SetUserAvatarRequest request = new SetUserAvatarRequest();
        request.BaseRequest = CacheUtils.getBaseRequest(getActivity());
        request.fileExtention = MimeTypeMap
                .getFileExtensionFromUrl(fileUrl);
        request.responseUpload = responseUpload;
        String username = MyApplication.getInstance().getLocalUserName();
        request.userName = username;
        L.d(TAG, "request:" + request);
        L.d(TAG, "rootUrl:" + myRestClient.getRootUrl());
        SetUserAvatarResponse response = myRestClient.setUserAvatar(request);
        L.d(TAG, "response:" + response);
        onPostExecute(response);
    }

    @UiThread
    void onPostExecute(SetUserAvatarResponse response) {
        mProgressDialog.dismiss();
        if (response == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Utils.showToastShort(context, "上传头像失败！");
            return;
        }

        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(userName, sizeSmall), new
                ImageSize(sizeSmall, sizeSmall));
        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(userName, sizeNormal), new
                ImageSize(sizeNormal, sizeNormal));
        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(userName, sizeBigger), new
                ImageSize(sizeBigger, sizeBigger));

        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(userName, sizeBiggest), new
                ImageSize(sizeBiggest, sizeBiggest));
        ImagerLoaderOptHelper.removeFromCache(Utils.getAvataUrl(userName, sizeSmall), new
                ImageSize(iconSize, iconSize));

        DisplayImageOptions options = ImagerLoaderOptHelper.getConerOpt();
        if (cropUri != null) {
            ImageLoader.getInstance().displayImage(cropUri.toString(), iv_icon, options);
        }

        Utils.showToastShort(getActivity(), "上传头像成功！");
    }


    private void uploadNewPhoto(Intent data) {
        L.d(TAG, "uploadNewPhoto() cropUri:" + cropUri);
        final RequestCallBack<String> callback = new RequestCallBack<String>() {
            @Override
            public void onStart() {
                L.d(TAG, "onStart()");
                onPreExecute();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                L.d(TAG, "onLoading() isUploading:" + isUploading);
                if (isUploading) {
//					tvUploadPercent.setText((int) (current * 100 / total) + "%");
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.d(TAG, "onSuccess()");
                ResponseUpload responseUpload = Utils.readValue(
                        responseInfo.result, ResponseUpload.class);
                L.d(TAG, "onSuccess() responseUpload:" + responseUpload);
                if (responseUpload != null && TextUtils.isEmpty(responseUpload.error)) {
                    doInBackground(cropUri.getPath(), responseUpload);
                } else {
                    mProgressDialog.dismiss();
                    Utils.showToastShort(getActivity(), "上传头像失败！");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                L.d(TAG, "onFailure() error:" + error);
                L.d(TAG, "onFailure() msg:" + msg);
                mProgressDialog.dismiss();
                Utils.showToastShort(getActivity(), "上传头像失败！");
            }
        };


        String url = Utils.getAvataUrl(getActivity(), Utils.URL_FILE_AVATA);
//        if (url == null) {
//            Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
//        }
//        jStr = url;
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", new File(cropUri.getPath()));
//        String httpUrl = "";
//        if (context.saveConfig.getStringConfig("httpConfig").equals("true")) {
//            try {
//                JSONObject jsonObject = new JSONObject(jStr);
//                httpUrl = "http://" + jsonObject.getString("url") + "/" + jsonObject.getString("fid");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                JSONObject jsonObject = new JSONObject(jStr);
//                httpUrl = "http://" + jsonObject.getString("publicUrl") + "/" + jsonObject.getString("fid");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        HttpHandler<String> handler = http.send(HttpRequest.HttpMethod.POST, url, params, callback);
    }

    String jStr;
}
