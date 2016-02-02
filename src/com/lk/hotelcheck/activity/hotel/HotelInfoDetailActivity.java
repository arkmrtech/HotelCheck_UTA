package com.lk.hotelcheck.activity.hotel;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.upload.UploadProcessActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.network.HttpCallback;
import com.lk.hotelcheck.network.HttpRequest;
import com.lk.hotelcheck.upload.UploadProxy;
import com.lk.hotelcheck.util.CommonUtil;
import com.lk.hotelcheck.util.Machine;
import common.Constance;
import common.NetConstance;
import common.view.SlidingTabLayout;


public class HotelInfoDetailActivity extends BaseActivity{
	
	private int mPosition;
	private Hotel mHotel;
	private ViewPager mViewPager;
	private SlidingTabLayout mSlidingTabLayout;
	private DetailAdapter mAdapter;
	private View mLoadingGroup;
	private boolean mIsLoading = false;
	
	
	public static void goToHotel(Context context , int id) {
		Intent intent = new Intent();
		intent.setClass(context, HotelInfoDetailActivity.class);
		int positiion = DataManager.getInstance().getHotelPosition(id);
		intent.putExtra(Constance.IntentKey.INTENT_KEY_POSITION, positiion);
		context.startActivity(intent);
	}
	
	public Hotel getHotel() {
		return mHotel;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		mPosition = -1;
		if (getIntent().hasExtra(Constance.IntentKey.INTENT_KEY_POSITION)) {
			mPosition = getIntent().getIntExtra(Constance.IntentKey.INTENT_KEY_POSITION, -1);
			mHotel = DataManager.getInstance().getHotel(mPosition);
			if (mHotel != null) {
				String name = mHotel.getName();
				toolbar.setTitle(name);
				init();
			}
		}
		toolbar.setNavigationIcon(R.drawable.back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setSupportActionBar(toolbar);
		mLoadingGroup = findViewById(R.id.vg_loadig);
	}
	
	@Override 
	public boolean onMenuOpened(int featureId, Menu menu)
	{ 
	    if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
	        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
	            try{ 
	                Method m = menu.getClass().getDeclaredMethod(
	                    "setOptionalIconsVisible", Boolean.TYPE);
	                m.setAccessible(true);
	                m.invoke(menu, true);
	            } 
	            catch(NoSuchMethodException e){
	                Log.e("aa", "onMenuOpened", e);
	            } 
	            catch(Exception e){
	                throw new RuntimeException(e);
	            } 
	        } 
	    } 
	    return super.onMenuOpened(featureId, menu);
	} 
//	public void onEvent(MessageEvent event) {
//		Log.d("lxk", "get event bus message");
//	};
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mHotel != null) {
			mHotel.save();
		}
	}

	private void init() {
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mViewPager = (ViewPager) findViewById(R.id.vp_detail);
		mAdapter = new DetailAdapter(getSupportFragmentManager(), mPosition);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator,
				android.R.id.text1);
		mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(
				R.color.white));
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setViewPager(mViewPager);
		if (mHotel.isStatus()) {
			mViewPager.setCurrentItem(2);
		}
	}
	
	 //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效  
    private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            m.setAccessible(true);  
              
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)  
            m.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
//		 	setIconEnable(menu, true);
	        getMenuInflater().inflate(R.menu.menu_detail, menu);
	        return super.onCreateOptionsMenu(menu);
	    }
	 
	 @Override  
	    public boolean onPrepareOptionsMenu(Menu menu)   
	    {  
	        // TODO Auto-generated method stub  
	        return super.onPrepareOptionsMenu(menu);  
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();
	        switch (id) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_upload_data :
				uploadData();
				break;
			case R.id.menu_chose_image_upload :
				uploadImage();
				break;
			case R.id.menu_check_done:
				if (mHotel != null) {
					if (mHotel.isStatus()) {
						Toast.makeText(this, "已检查", Toast.LENGTH_SHORT).show();
					} else {
						if (mHotel.getRoomCount() == 0
								|| mHotel.getRoomInUseCount() == 0
								|| TextUtils.isEmpty(mHotel.getFloor())
								|| TextUtils.isEmpty(mHotel.getCheckDate())) {
							Toast.makeText(HotelInfoDetailActivity.this, "酒店基本信息未填写完整", Toast.LENGTH_SHORT).show();
							mViewPager.setCurrentItem(0);
						} else if (!mHotel.checkReformState()) {
							Toast.makeText(HotelInfoDetailActivity.this, "复检问题状态未填写完整", Toast.LENGTH_SHORT).show();
							mViewPager.setCurrentItem(2);
						}else {
							showCheckedDoneAlert();
						}
					}
				}
				break;
			case R.id.menu_check_image_transfer_speed :
				UploadProcessActivity.goToUpload(this, mHotel.getCheckId());
				break;
			default:
				break;
			}
	        return super.onOptionsItemSelected(item);
	    }
	    
	    private void showCheckedDoneAlert() {
			LayoutInflater factory = LayoutInflater.from(this);// 提示框
			View view = factory.inflate(R.layout.alert_check_done, null);// 这里必须是final的
			TextView nameTextView = (TextView) view.findViewById(R.id.tv_name);// 获得输入框对象
			TextView issueCountTextView = (TextView) view.findViewById(R.id.tv_checked_issue_count);
//			final EditText numberEditText = (EditText) view.findViewById(R.id.et_number);
			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED); 
			ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK); 
			SpannableStringBuilder hotelName = new SpannableStringBuilder(mHotel.getName());
			SpannableStringBuilder issueCount = new SpannableStringBuilder(""+mHotel.getIssueCount());
			SpannableStringBuilder imageCount = new SpannableStringBuilder(""+mHotel.getImageCount());
			
			hotelName.setSpan(blackSpan, 0, hotelName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			issueCount.setSpan(redSpan, 0, issueCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			imageCount.setSpan(redSpan, 0, imageCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			nameTextView.setText("本次检查中，"+hotelName);
			issueCountTextView.setText("共登记问题"+issueCount+"个问题"+",拍摄"+imageCount+"张照片");
//			if (!TextUtils.isEmpty(mHotel.getGuardianNumber())) {
//				numberEditText.setText(mHotel.getGuardianNumber());
//			}
			AlertDialog alertDialog = new AlertDialog.Builder(this)
//					 .setTitle("完成检查信息确认")//提示框标题
					.setView(view)
					.setPositiveButton("检查完成确认",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
//									String number = numberEditText.getText().toString();
//									if (TextUtils.isEmpty(number)) {
//										Toast.makeText(HotelInfoDetailActivity.this, "请输入陪同人工号", Toast.LENGTH_SHORT).show();
//									} else {
//										mHotel.setGuardianNumber(number);
										updateHotelCheckState();
//									}
									
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
	    
	    
	    
	    
	private void uploadImage() {
		if (mHotel == null) {
			return;
		}
		if (!Machine.isNetworkOK(this)) {
			Toast.makeText(this, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!mHotel.isStatus()) {
			Toast.makeText(this, "请先完成酒店检查", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mHotel.isImageStatus()) {
			Toast.makeText(this, "该酒店图片已经上传过", Toast.LENGTH_SHORT).show();
			return;
		}
//		mHotel.setImageStatus(true);
		UploadProxy.addUploadTask(mHotel);
		UploadProcessActivity.goToUpload(this, mHotel.getCheckId());
	} 
	    
	 private void uploadData() {
		 if (!Machine.isNetworkOK(this)) {
				Toast.makeText(this, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
				return;
			}
		if (!mHotel.isStatus()) {
			Toast.makeText(this, "请先完成酒店检查", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mHotel.isDataStatus()) {
			Toast.makeText(this, "酒店数据已经上传", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mIsLoading) {
			Toast.makeText(this, "酒店数据上传中", Toast.LENGTH_SHORT).show();
			return;
		}
		mIsLoading = true;
		mLoadingGroup.setVisibility(View.VISIBLE);
		HttpRequest.getInstance().uploadHotelData(this, mHotel, DataManager.getInstance().getSession(), new HttpCallback() {
			
			@Override
			public void onSuccess(JSONObject response) {
				Toast.makeText(HotelInfoDetailActivity.this, "酒店数据上传成功", Toast.LENGTH_SHORT).show();
				mHotel.setDataStatus(true);
				DataManager.getInstance().setHotel(mPosition, mHotel);
				mLoadingGroup.setVisibility(View.GONE);
				mIsLoading = false;
			}
			
			@Override
			public void onError(int errorCode, String info) {
				Toast.makeText(HotelInfoDetailActivity.this, "酒店数据上传失败，网络异常，请稍后再试", Toast.LENGTH_SHORT).show();
				mLoadingGroup.setVisibility(View.GONE);
				mIsLoading = false;
				if (errorCode == NetConstance.ERROR_CODE_SESSSION_TIME_OUT) {
					Toast.makeText(HotelInfoDetailActivity.this, "权限过期，请重新登录", Toast.LENGTH_SHORT).show();
					CommonUtil.goToLogin(HotelInfoDetailActivity.this);
					finish();
				} 
			}
		});
	 }
	 
	 private void updateHotelCheckState() {
		 if (!Machine.isNetworkOK(this)) {
				Toast.makeText(this, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
				return;
			}
		 if (mIsLoading) {
				Toast.makeText(this, "酒店数据上传中", Toast.LENGTH_SHORT).show();
				return;
			}
		 mIsLoading = true;
		 mLoadingGroup.setVisibility(View.VISIBLE);
		 HttpRequest.getInstance().updateHotelCheckStatus(this, mHotel.getCheckId(), DataManager.getInstance().getSession(), new HttpCallback() {
			
			@Override
			public void onSuccess(JSONObject response) {
//					((HotelBaseInfoFragment)mAdapter.getItem(0)).setGuardianNumber(number);
//					DataManager.getInstance().setHotelChecked(position, hotel);
				Toast.makeText(HotelInfoDetailActivity.this, "酒店检查状态上传成功", Toast.LENGTH_SHORT).show();
				mHotel.setStatus(true);
				DataManager.getInstance().setHotel(mPosition, mHotel);
				DataManager.getInstance().updateCheckedHotelList(mHotel);
				if (mViewPager.getCurrentItem() != 2) {
					mViewPager.setCurrentItem(2);
				}
				Fragment fragment = (HotelReportFragment) mAdapter.getItem(2);
				if (fragment instanceof HotelReportFragment) {
					((HotelReportFragment)fragment).refreshInfo();
				}
				mLoadingGroup.setVisibility(View.GONE);
				mIsLoading = false;
//				EventBus.getDefault().post(new MessageEvent(MessageEvent.MESSAGE_UPDATE_HOTEL_DATA));
			}
			
			@Override
			public void onError(int errorCode, String info) {
				Toast.makeText(HotelInfoDetailActivity.this, "酒店检查失败，网络异常，请稍后再试", Toast.LENGTH_SHORT).show();
				mLoadingGroup.setVisibility(View.GONE);
				mIsLoading = false;
				if (errorCode == NetConstance.ERROR_CODE_SESSSION_TIME_OUT) {
					Toast.makeText(HotelInfoDetailActivity.this, "权限过期，请重新登录", Toast.LENGTH_SHORT).show();
					CommonUtil.goToLogin(HotelInfoDetailActivity.this);
					finish();
				} 
			}
		});
	 }
	
	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK && mIsLoading) {
				return true;
			}
		return super.onKeyDown(keyCode, event);
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
