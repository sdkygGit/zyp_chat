package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

@EActivity(R.layout.activity_user_edit_pwd)
public class UserEditPwdActivity extends Activity implements RestErrorHandler {
    private static final String TAG = "UserEditPwdActivity";
    String matchPwd = "[a-zA-Z0-9@#$%.~+_=-]{6,18}";

    private Appmsgsrv8094 mAppmsgsrv8094;

    @ViewById
    EditText old_pwd_et;

    @ViewById
    EditText new_pwd_et1;

    @ViewById
    EditText new_pwd_et2;


    @ViewById
    ImageView old_pwd_right;

    @ViewById
    ImageView new_pwd1_right;

    @ViewById
    ImageView new_pwd2_right;

    @ViewById
    TextView new_pwd2_tv;


    @ViewById
    Button commit_btn;

    private String commitPwd;


    @Click(R.id.back)
    void onClickBack(View view) {
        finish();
    }


    @Click(R.id.commit_btn)
    void onClickCommit(View view) {
        if (!TextUtils.isEmpty(old_pwd_et.getText()) &&
                !TextUtils.isEmpty(new_pwd_et1.getText()) &&
                !TextUtils.isEmpty(new_pwd_et2.getText())) {
            String pwd = PublicTools.loadLocalString(UserEditPwdActivity.this, "password", true);
            if (old_pwd_et.getText().toString().equals(pwd)) {
                old_pwd_right.setVisibility(View.VISIBLE);
            } else {
                old_pwd_right.setVisibility(View.INVISIBLE);
                Toast.makeText(UserEditPwdActivity.this, "原始密码错误！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (new_pwd_et1.getText().toString().matches(matchPwd)) {
                new_pwd1_right.setVisibility(View.VISIBLE);
            } else {
                new_pwd1_right.setVisibility(View.INVISIBLE);
                Toast.makeText(UserEditPwdActivity.this, "输入密码格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!new_pwd_et1.getText().toString().equals(new_pwd_et2.getText().toString())) {
                new_pwd2_tv.setVisibility(View.VISIBLE);
                new_pwd2_tv.setText("两次输入不一致");
                return;
            }
            commitPwd = new_pwd_et1.getText().toString();

            doInBackgroundChangeUserInfo();

        } else {
            Toast.makeText(UserEditPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    @AfterViews
    void afterViews() {

        old_pwd_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = PublicTools.loadLocalString(UserEditPwdActivity.this, "password", true);
                if (old_pwd_et.getText().toString().equals(pwd)) {
                    old_pwd_right.setVisibility(View.VISIBLE);
                } else {
                    old_pwd_right.setVisibility(View.INVISIBLE);
                }

            }
        });

        new_pwd_et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (new_pwd_et1.getText().toString().matches(matchPwd)) {
                    new_pwd1_right.setVisibility(View.VISIBLE);
                } else {
                    new_pwd1_right.setVisibility(View.INVISIBLE);
                }
            }
        });

        new_pwd_et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(new_pwd_et1.getText()) && new_pwd_et1.getText().toString().equals(new_pwd_et2.getText().toString())) {
                    new_pwd2_right.setVisibility(View.VISIBLE);
                    new_pwd2_tv.setText("");
                }
            }
        });

    }

    @AfterInject
    void afterInject() {
        mAppmsgsrv8094 = Appmsgsrv8094Proxy.create(5 * 1000);
        mAppmsgsrv8094.setRestErrorHandler(this);
    }

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        Toast.makeText(UserEditPwdActivity.this, "访问失败", Toast.LENGTH_SHORT).show();
    }



    private ProgressDialog mProgressDialog;

    @UiThread
    void onPreExecute() {
        commit_btn.setEnabled(false);
        mProgressDialog = ProgressDialog.show(UserEditPwdActivity.this, "提示", "提交中...");
    }

    @Background
    void doInBackgroundChangeUserInfo() {
        onPreExecute();
        try {
            Member member = MyApplication.getInstance().getLocalMember();
            BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
            ChangeUserInfoRequest request = new ChangeUserInfoRequest();
            request.BaseRequest = baseRequest;
            request.uid = member.Uid;

            request.mobile = member.Mobile;
            request.email = member.Email;
            request.tel = member.Tel;
            request.password = commitPwd;

            mAppmsgsrv8094.setRestErrorHandler(this);
            ChangeUserInfoResponse response = mAppmsgsrv8094.changeUserInfo(request);
            onPostExecuteChangeUserInfo(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @UiThread
    void onPostExecuteChangeUserInfo(ChangeUserInfoResponse response) {

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
        PublicTools.saveLocalString(this, "password", commitPwd, true);
        Toast.makeText(UserEditPwdActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
    }
}
