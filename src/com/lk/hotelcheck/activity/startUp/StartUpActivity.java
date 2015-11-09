package com.lk.hotelcheck.activity.startUp;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.login.LoginActivity;
import com.lk.hotelcheck.activity.main.MainActivity;
import com.lk.hotelcheck.bean.User;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.network.HttpCallback;
import com.lk.hotelcheck.network.HttpRequest;
import com.lk.hotelcheck.util.CommonUtil;
import com.lk.hotelcheck.util.SharedPreferencesUtil;

import common.NetConstance;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class StartUpActivity extends Activity{

	private MyHandle mHandler;
	private static final int MSG_OK = 0x100;
	private static final int MSG_DISABLE = 0x101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_up);
		mHandler = new MyHandle(StartUpActivity.this);
		validToken();
		
	}
	
	private void validToken() {
		final String lastToken = DataManager.getInstance().getToken(getApplicationContext());
		if (TextUtils.isEmpty(lastToken)) {
			mHandler.sendEmptyMessageDelayed(MSG_DISABLE, 2000);
		}  else {
			HttpRequest.getInstance().validToken(StartUpActivity.this, lastToken, new HttpCallback() {
				
				@Override
				public void onSuccess(JSONObject response) {
					User user = new User();
					String userId = SharedPreferencesUtil.getString(getApplicationContext(), NetConstance.PARAM_NAME);
					user.setUserName(userId);
					user.setToken(lastToken);
					DataManager.getInstance().setUser(user);
					mHandler.sendEmptyMessageDelayed(MSG_OK, 2000);
				}
				
				@Override
				public void onError(int errorCode, String info) {
					if (errorCode == NetConstance.ERROR_CODE_SESSSION_TIME_OUT) {
						Toast.makeText(StartUpActivity.this, "token失效，请重新登录", Toast.LENGTH_SHORT).show();
						
						mHandler.sendEmptyMessageDelayed(MSG_DISABLE, 2000);
					}
				}
			});
		}
	}
	
	private static class MyHandle extends Handler {
		
		private WeakReference<StartUpActivity> mWeak;
		
		

		public MyHandle(StartUpActivity activity) {
			super();
			this.mWeak = new WeakReference<StartUpActivity>(activity);
		}



		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_OK:
				Intent intent = new Intent();
				intent.setClass(mWeak.get(), MainActivity.class);
				mWeak.get().startActivity(intent);
				break;
			case MSG_DISABLE:
				CommonUtil.goToLogin(mWeak.get());
				break;
			default:
				break;
			}
			if (mWeak != null && mWeak.get() != null) {
				mWeak.get().finish();
			}
			
		}
		
	}
	
	
	
}
