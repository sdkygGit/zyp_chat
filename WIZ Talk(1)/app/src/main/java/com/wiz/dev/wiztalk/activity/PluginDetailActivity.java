package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8093;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.utils.XXBitmapUtils;
import com.wiz.dev.wiztalk.view.HeaderLayout;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;


@EActivity(R.layout.activity_app_detail)
public class PluginDetailActivity extends Activity {
    private static final String TAG = "AppDetailActivity";
    @Extra
    String name;

    @Extra
    String desc;

    @ViewById
    ImageView ivAvatar;

    @ViewById
    TextView tvDisplayName;

    @ViewById
    TextView tvNickName;

    @ViewById
    TextView tvFunctionDetail;

    @ViewById(R.id.cb_app_receive_app_msg)
    CheckBox cbIsReceiveAppMsg;

    @ViewById
    CheckBox cbFollow;

    @ViewById(R.id.btnEnterApp)
    Button btnEnterApp;

    @Extra
    String fromWhere;

    public static final String FROM_WHERE_IS_CHAT = "FROM_WHERE_IS_CHAT";

    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int size;

    private Appmsgsrv8093 mAppmsgsrv8093;
    private Appmsgsrv8094 mAppmsgsrv8094;

//	private boolean mIsChecked;

    private BitmapUtils bitmapUtils;
    HeaderLayout mHeaderLayout;

    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.blueback,
                new HeaderLayout.onLeftImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
    }

    @AfterViews
    void afterViews() {
        bitmapUtils = new XXBitmapUtils(this);
        bitmapUtils.configDefaultLoadingImage(R.drawable.ic_default_app_mini);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_default_app_mini);
        initTopBarForLeft("应用详情");
        tvDisplayName.setText(name);
        tvFunctionDetail.setText(desc);
    }

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

            } else {

            }
        }
    };

    @AfterInject
    void afterInject() {
    }

    @Click(R.id.btnEnterApp)
    void onClickEnterApp() {
// TODO: 2016/5/6 启动插件 
    }
}
