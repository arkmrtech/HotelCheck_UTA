package com.lk.hotelcheck.activity.hotel;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.photochosen.PhotoChosenActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.manager.DataManager;

import common.Constance;
import common.view.SlidingTabLayout;


public class HotelInfoDetailActivity extends BaseActivity{
	
	private int position;
//	private Hotel mHotel;
	private ViewPager mViewPager;
	private SlidingTabLayout mSlidingTabLayout;
	private DetailAdapter mAdapter;
	
	
	public static void goToHotel(Context context , int id) {
		Intent intent = new Intent();
		intent.setClass(context, HotelInfoDetailActivity.class);
		int positiion = DataManager.getInstance().getHotelPosition(id);
		intent.putExtra(Constance.IntentKey.INTENT_KEY_POSITION, positiion);
		context.startActivity(intent);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		position = -1;
		if (getIntent().hasExtra(Constance.IntentKey.INTENT_KEY_POSITION)) {
			position = getIntent().getIntExtra(Constance.IntentKey.INTENT_KEY_POSITION, -1);
			String name = DataManager.getInstance().getHotelName(position);
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
	}

	private void init() {
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mViewPager = (ViewPager) findViewById(R.id.vp_detail);
		mAdapter = new DetailAdapter(getSupportFragmentManager(), position);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator,
				android.R.id.text1);
		mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(
				R.color.white));
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setViewPager(mViewPager);
	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.menu_detail, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	Hotel hotel = DataManager.getInstance().getHotel(position);
	        int id = item.getItemId();
	        switch (id) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_chose_image_upload :
				PhotoChosenActivity.gotoPhotoChosen(this, position, 0, 0);
				break;
			case R.id.menu_check_done:
				if (hotel != null) {
					if (hotel.isStatus()) {
						Toast.makeText(this, "已检查", Toast.LENGTH_SHORT).show();
					} else {
					if (hotel.getRoomCount() == 0
							|| hotel.getRoomInUseCount() == 0
							|| hotel.getFloorStart() == 0
							|| hotel.getFloorEnd() == 0
							|| TextUtils.isEmpty(hotel.getCheckDate())) {
						Toast.makeText(HotelInfoDetailActivity.this, "酒店基本信息未填写完整", Toast.LENGTH_SHORT).show();
						mViewPager.setCurrentItem(0);
					} else {
						showCheckedDoneAlert(hotel);
					}
					}
				}
				break;
			default:
				break;
			}
	        return super.onOptionsItemSelected(item);
	    }
	    
	    private void showCheckedDoneAlert(final Hotel hotel) {
			LayoutInflater factory = LayoutInflater.from(this);// 提示框
			View view = factory.inflate(R.layout.alert_check_done, null);// 这里必须是final的
			TextView nameTextView = (TextView) view.findViewById(R.id.tv_name);// 获得输入框对象
			TextView issueCountTextView = (TextView) view.findViewById(R.id.tv_checked_issue_count);
			final EditText numberEditText = (EditText) view.findViewById(R.id.et_number);
//			TextView imageCountTextView = (TextView) view.findViewById(R.id.tv_image_count);
			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED); 
			ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK); 
			SpannableStringBuilder hotelName = new SpannableStringBuilder(hotel.getName());
			SpannableStringBuilder issueCount = new SpannableStringBuilder(""+hotel.getIssueCount());
			SpannableStringBuilder imageCount = new SpannableStringBuilder(""+hotel.getImageCount());
			
			hotelName.setSpan(blackSpan, 0, hotelName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			issueCount.setSpan(redSpan, 0, issueCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			imageCount.setSpan(redSpan, 0, imageCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			nameTextView.setText("本次检查中，"+hotelName);
			issueCountTextView.setText("共登记问题"+issueCount+"个问题"+",拍摄"+imageCount+"张照片");
//			imageCountTextView.setText("已拍摄照片数："+hotel.getImageCount());
			AlertDialog alertDialog = new AlertDialog.Builder(this)
//					 .setTitle("完成检查信息确认")//提示框标题
					.setView(view)
					.setPositiveButton("检查完成确认",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String number = numberEditText.getText().toString();
									if (TextUtils.isEmpty(number)) {
										Toast.makeText(HotelInfoDetailActivity.this, "请输入陪同人工号", Toast.LENGTH_SHORT).show();
									} else {
										hotel.setGuardianNumber(number);
										hotel.setStatus(true);
//										((HotelBaseInfoFragment)mAdapter.getItem(0)).setGuardianNumber(number);
										DataManager.getInstance().setHotelChecked(position, hotel);
										Toast.makeText(HotelInfoDetailActivity.this, "酒店检查完成", Toast.LENGTH_SHORT).show();
									}
									
								}
							})
					.setNegativeButton("返回继续修改",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			alertDialog.show();
		}    
	    
	
	class DetailAdapter extends FragmentStatePagerAdapter{

		private String[] mTitle = new String[]{"基本信息","问题","报表"};
		private List<Fragment> mFragmentList;
		
		
		public DetailAdapter(FragmentManager fm, int position) {
			super(fm);
			mFragmentList = new ArrayList<Fragment>();
			mFragmentList.add(HotelBaseInfoFragment.newInstance(position));
			mFragmentList.add(HotelIssueFragment.newInstance(position));
			mFragmentList.add(HotelReportFragment.newInstance(position));
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
	
	
}
