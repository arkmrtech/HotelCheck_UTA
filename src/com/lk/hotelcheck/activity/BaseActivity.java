package com.lk.hotelcheck.activity;

import android.support.v7.app.ActionBarActivity;

import com.lk.hotelcheck.manager.DataManager;

public class BaseActivity extends ActionBarActivity{

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		DataManager.getInstance().saveDataCache();
	}
}
