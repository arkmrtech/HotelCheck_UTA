package com.lk.hotelcheck.activity.login;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.main.MainActivity;
import com.lk.hotelcheck.bean.User;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.network.DataCallback;
import com.lk.hotelcheck.network.HttpCallback;
import com.lk.hotelcheck.network.HttpRequest;

import common.Constance;
import common.NetConstance;

public class LoginActivity extends Activity{
	
	private EditText mUserIdEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private View mLoadingGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mUserIdEditText = (EditText) findViewById(R.id.et_user_id);
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
		mLoginButton = (Button) findViewById(R.id.bt_login);
		mLoadingGroup = findViewById(R.id.vg_loadig);
		mLoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(LoginActivity.this, MainActivity.class);
//				startActivity(intent);
//				finish();
				login();
			}
		});
		
		
	}
	
	private void login() {
		final String userId = mUserIdEditText.getText().toString();
		String password = mPasswordEditText.getText().toString();
		if (TextUtils.isEmpty(userId)) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(this, "请输入登录密码", Toast.LENGTH_SHORT).show();
			return;
		}
		mLoadingGroup.setVisibility(View.VISIBLE);
		
		HttpRequest.getInstance().login(this, userId, password, new HttpCallback() {
			
			@Override
			public void onSuccess(JSONObject response) {
				User user = new User();
				user.setUserName(userId);
				DataManager.getInstance().setUser(user);
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
				mLoadingGroup.setVisibility(View.GONE);
			}
			
			@Override
			public void onError() {
				Toast.makeText(LoginActivity.this, "登录失败，请稍后再试", Toast.LENGTH_SHORT).show();
				mLoadingGroup.setVisibility(View.GONE);
			}
		});
		
	}
	
	
}
