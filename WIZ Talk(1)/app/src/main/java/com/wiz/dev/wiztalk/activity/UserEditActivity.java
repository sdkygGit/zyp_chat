package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.ChangeUserInfoRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.ChangeUserInfoResponse;
import com.wiz.dev.wiztalk.public_store.PublicTools;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

@EActivity(R.layout.activity_user_edit)
public class UserEditActivity extends Activity implements RestErrorHandler {
    private static final String TAG = "UserEditActivity";

    String matchEmail = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

//    String matchPhone = "^(13[0,1,2,3,4,5,6,7,8,9]|15[0,1,2,3,4,5,6,8,7,9]|188|187|178|186|185|189|182)\\d{8}$";
    String matchPhone = "^(1|[+][8][6][1])\\d{10}$";

    String matchLandline = "\\d{6,15}";
    private Appmsgsrv8094 mAppmsgsrv8094;

    @Extra
    Member member;

    @ViewById
    EditText landline_et;

    @ViewById
    EditText phone_et;

    @ViewById
    EditText email_et;

    @ViewById
    ImageView landline_clear;

    @ViewById
    ImageView phone_clear;

    @ViewById
    ImageView email_clear;

    @ViewById
    Button commit_btn;

    @Click(R.id.back)
    void onClickBack(View view) {
        finish();
    }

    @Click(R.id.commit_btn)
    void onClickCommit(View view) {
        if (!TextUtils.isEmpty(landline_et.getText()) &&
                !TextUtils.isEmpty(phone_et.getText()) &&
                !TextUtils.isEmpty(email_et.getText())) {

            if (!landline_et.getText().toString().matches(matchLandline)) {
                Toast.makeText(UserEditActivity.this, "用户座机格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone_et.getText().toString().matches(matchPhone)) {
                Toast.makeText(UserEditActivity.this, "用户手机格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email_et.getText().toString().matches(matchEmail)) {
                Toast.makeText(UserEditActivity.this, "用户邮箱格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            member.Tel = landline_et.getText().toString();
            member.Mobile = phone_et.getText().toString();
            member.Email = email_et.getText().toString();

            doInBackgroundChangeUserInfo();

        } else {
            Toast.makeText(UserEditActivity.this, "用户座机，手机或邮箱不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterViews
    void afterViews() {
        landline_et.setText(member.Tel);
        phone_et.setText(member.Mobile);
        email_et.setText(member.Email);
        landline_et.setOnFocusChangeListener(new MyOnFocusChangeListener(landline_et));
        phone_et.setOnFocusChangeListener(new MyOnFocusChangeListener(phone_et));
        email_et.setOnFocusChangeListener(new MyOnFocusChangeListener(email_et));
    }

    @AfterInject
    void afterInject() {
        mAppmsgsrv8094 = Appmsgsrv8094Proxy.create(5 * 1000);
        mAppmsgsrv8094.setRestErrorHandler(this);
    }

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        Toast.makeText(UserEditActivity.this, "访问失败", Toast.LENGTH_SHORT).show();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Click(R.id.landline_clear)
    void onClickLandlineClear(View view) {
        landline_et.setText("");
    }

    @Click(R.id.phone_clear)
    void onClickPhoneClear(View view) {
        phone_et.setText("");
    }

    @Click(R.id.email_clear)
    void onClickEmailClear(View view) {
        email_et.setText("");
    }

    class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        private EditText editText;

        public MyOnFocusChangeListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (editText.getId()) {
                case R.id.landline_et:
                    if (hasFocus) {
                        if (TextUtils.isEmpty(landline_et.getText())) {
                            landline_et.setHint("");
                        }
                        landline_clear.setVisibility(View.VISIBLE);
                    } else {
                        landline_clear.setVisibility(View.INVISIBLE);
                        if (TextUtils.isEmpty(landline_et.getText())) {
                            landline_et.setHint("请输入座机");
                        }
                    }

                    break;

                case R.id.phone_et:
                    if (hasFocus) {
                        phone_clear.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(phone_et.getText())) {
                            phone_et.setHint("");
                        }
                    } else {
                        if (TextUtils.isEmpty(phone_et.getText())) {
                            phone_et.setHint("请输入手机");
                        }
                        phone_clear.setVisibility(View.INVISIBLE);
                    }
                    break;

                case R.id.email_et:

                    if (hasFocus) {
                        email_clear.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(email_et.getText())) {
                            email_et.setHint("");
                        }
                    } else {
                        if (TextUtils.isEmpty(email_et.getText())) {
                            email_et.setHint("请输入手机");
                        }
                        email_clear.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }


    private ProgressDialog mProgressDialog;

    @UiThread
    void onPreExecute(){
        commit_btn.setEnabled(false);
        mProgressDialog = ProgressDialog.show(UserEditActivity.this, "提示", "提交中...");
    }

    @Background
    void doInBackgroundChangeUserInfo() {
        onPreExecute();
        try{
            BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
            ChangeUserInfoRequest request = new ChangeUserInfoRequest();
            request.BaseRequest = baseRequest;
            request.uid = member.Uid;
            request.mobile = member.Mobile;
            request.email = member.Email;
            request.tel = member.Tel;
            request.password = PublicTools.loadLocalString(UserEditActivity.this, "password", true);

            mAppmsgsrv8094.setRestErrorHandler(this);
            ChangeUserInfoResponse response = mAppmsgsrv8094.changeUserInfo(request);
            onPostExecuteChangeUserInfo(response);
        }catch (Exception e){
            e.printStackTrace();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @UiThread
    void onPostExecuteChangeUserInfo(ChangeUserInfoResponse response){

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        commit_btn.setEnabled(true);
        if (response == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(this, response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        MyApplication.getInstance().putLocalMember(member);
        Toast.makeText(UserEditActivity.this,"更改成功",Toast.LENGTH_SHORT).show();
    }
}
