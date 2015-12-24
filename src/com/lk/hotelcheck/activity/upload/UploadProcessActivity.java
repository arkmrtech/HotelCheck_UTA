package com.lk.hotelcheck.activity.upload;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.manager.DataManager;

import common.Constance;
import common.Constance.HotelAction;
import common.Constance.ImageUploadState;
import common.view.SlidingTabLayout;

public class UploadProcessActivity extends BaseActivity {

	private ViewPager mViewPager;
	private SlidingTabLayout mSlidingTabLayout;
	private UploadAdapter mAdapter;
	private int mPosition;
	private int mCheckId = -1;
	
	public static void goToUpload(Context context , int id) {
		Intent intent = new Intent();
		intent.setClass(context, UploadProcessActivity.class);
		int positiion = DataManager.getInstance().getHotelPosition(id);
		intent.putExtra(Constance.IntentKey.INTENT_KEY_POSITION, positiion);
		context.startActivity(intent);
	}
	
	public static void goToUpload(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, UploadProcessActivity.class);
		intent.putExtra(Constance.IntentKey.INTENT_KEY_POSITION, -1);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_process);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		mPosition = -1;
		if (getIntent().hasExtra(Constance.IntentKey.INTENT_KEY_POSITION)) {
			mPosition = getIntent().getIntExtra(Constance.IntentKey.INTENT_KEY_POSITION, -1);
			String name = "图片传输进度";
			if (mPosition != -1) {
				name = DataManager.getInstance().getHotelName(mPosition)+name;
				Hotel hotel =  DataManager.getInstance().getHotel(mPosition);
				if (hotel != null ) {
					mCheckId = hotel.getCheckId();
				}
//				mCheckId = DataManager.getInstance().getHotel(mPosition).getCheckId();
			}  
			toolbar.setTitle(name);
			init();
		}
		toolbar.setNavigationIcon(R.drawable.back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setSupportActionBar(toolbar);
		registerUoloadBroadcast();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterUploadBroadcast();
	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.menu_upload, menu);
	        return true;
	    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_start_all :
			UploadFragment fragment = (UploadFragment) mAdapter.getItem(0);
			fragment.uploadAll();
			break;
		default:
			break;
		}
        return super.onOptionsItemSelected(item);
	}
	
	private void init() {
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mViewPager = (ViewPager) findViewById(R.id.vp_upload);
		mAdapter = new UploadAdapter(getSupportFragmentManager(), mPosition);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator,
				android.R.id.text1);
		mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(
				R.color.white));
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setViewPager(mViewPager);
		
	}
	
	class UploadAdapter extends FragmentStatePagerAdapter{

		private String[] mTitle = new String[]{"上传中","上传完成"};
		private List<Fragment> mFragmentList;
		
		
		public UploadAdapter(FragmentManager fm, int position) {
			super(fm);
			mFragmentList = new ArrayList<Fragment>();
			mFragmentList.add(UploadFragment.getUploadingInstance(mCheckId));
			mFragmentList.add(UploadFragment.getUploadCompleteInstance(mCheckId));
		}

		@Override
		public Fragment getItem(int arg0) {
			return mFragmentList.get(arg0);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTitle[position];
			
		}
		
		@Override
		public int getCount() {
			return mFragmentList.size();
		}
		
	}
	
	private void registerUoloadBroadcast() {
		IntentFilter intent = new IntentFilter();
		intent.addAction(HotelAction.ACTION_IMAGE_UPLOAD);
		registerReceiver(uploadBroadcastReceiver, intent);
	}
	
	private void unRegisterUploadBroadcast() {
		unregisterReceiver(uploadBroadcastReceiver);
	}
	
	 private BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(HotelAction.ACTION_IMAGE_UPLOAD)) {
				UploadBean uploadBean = (UploadBean) intent.getSerializableExtra(HotelAction.IMAGE_UPLOAD_EXTRA);
				if (uploadBean.getImageState() == ImageUploadState.STATE_FINISH) {
					UploadFragment uploadingFragment = (UploadFragment) mAdapter.getItem(0);
					uploadingFragment.remove(uploadBean);
					UploadFragment uploadCompleteFragment = (UploadFragment) mAdapter.getItem(1);
					uploadCompleteFragment.add(uploadBean);
				} else {
					UploadFragment fragment = (UploadFragment) mAdapter.getItem(0);
					fragment.update(uploadBean);
				}
			}
		}
		 
	 };
	
}

