package com.lk.hotelcheck.activity.checkIssue;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.checkIssue.HotelIssueAdapter.CallBackListener;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.NetWorkSpeedInfo;
import com.lk.hotelcheck.util.PictureUtil;
import com.lk.hotelcheck.util.ReadFile;

import common.Constance;

public class CheckHotelIssueActivity extends BaseActivity implements CallBackListener{
	
	private static final String BUNDLE_POSITION_ID = "position";
	private static final String BUNDLE_CHECK_DATA_POSITION = "dataPosition";
	private static final String BUNDLE_CHECK_DATA_SUB_POSITION = "subDataPosition";
	private static final String BUNDLE_IS_SUB_CHECKED_DATA = "isSubCheckData";
	private int mHotelPosition;
	private int mCheckDataPosition;
	private int mSubCheckDataPosition;
	private Hotel mHotel;
	private CheckData mCheckData;
	public static final int CAMMER_REQUEST_CODE = 1000;
	private int mCurrentIssuePosition;
	private boolean mIsSubCheckedData;
	private HotelIssueAdapter mAdapter;
//	private String imagePath;
	
	//for check network speed
	private String url = "http://soft.images.paojiao.cn/soft/200911/20/99148038/jietu/13349055330161_paojiao.jpg";

	byte[] imageData = null;
	Button b;
	NetWorkSpeedInfo netWorkSpeedInfo = null;
	private final int UPDATE_SPEED = 1;// 进行中
	private final int UPDATE_DNOE = 0;// 完成下载
	private long begin = 0;
	long tem = 0;
	long falg = 0;
	long numberTotal = 0;
	List<Long> list = new ArrayList<Long>();
	
	
	public static void gotoCheckHotelIssue(Context context,int position, int checkDataPosition) {
		Intent intent = new Intent();
		intent.setClass(context, CheckHotelIssueActivity.class);
		intent.putExtra(BUNDLE_POSITION_ID, position);
		intent.putExtra(BUNDLE_CHECK_DATA_POSITION, checkDataPosition);
		intent.putExtra(BUNDLE_IS_SUB_CHECKED_DATA, false);
		context.startActivity(intent);
	}
	
	public static void gotoCheckHotelIssue(Context context,int position, int checkDataPosition, int subCheckedDataPosition) {
		Intent intent = new Intent();
		intent.setClass(context, CheckHotelIssueActivity.class);
		intent.putExtra(BUNDLE_POSITION_ID, position);
		intent.putExtra(BUNDLE_CHECK_DATA_POSITION, checkDataPosition);
		intent.putExtra(BUNDLE_IS_SUB_CHECKED_DATA, true);
		intent.putExtra(BUNDLE_CHECK_DATA_SUB_POSITION, subCheckedDataPosition);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_issue);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		toolbar.setNavigationIcon(R.drawable.back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		mHotelPosition = getIntent().getIntExtra(BUNDLE_POSITION_ID, -1);
		mCheckDataPosition = getIntent().getIntExtra(BUNDLE_CHECK_DATA_POSITION, -1);
		mIsSubCheckedData = getIntent().getBooleanExtra(BUNDLE_IS_SUB_CHECKED_DATA, false);
		if (mIsSubCheckedData) {
			mSubCheckDataPosition = getIntent().getIntExtra(BUNDLE_CHECK_DATA_SUB_POSITION, -1);
		}
		mHotel = DataManager.getInstance().getHotel(mHotelPosition);
		if (mIsSubCheckedData) {
			CheckData checkData = mHotel.getCheckData(mCheckDataPosition);
			mCheckData = checkData.getSubCheckData(mSubCheckDataPosition);
		} else {
			mCheckData = mHotel.getCheckData(mCheckDataPosition);
		}
		toolbar.setTitle(mCheckData.getName());
		setSupportActionBar(toolbar);
		RecyclerView listView = (RecyclerView) findViewById(R.id.lv_hotel_issue);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		listView.setLayoutManager(layoutManager);
		mAdapter = new HotelIssueAdapter(mCheckData.getIssuelist(), this, mHotel.isStatus());
		listView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		DataManager.getInstance().setHotel(mHotelPosition, mHotel);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK &&
				requestCode == CAMMER_REQUEST_CODE) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.i("lxk", "SD card is not avaiable/writeable right now.");
				return;
			}
			String name = new DateFormat().format("yyyyMMddhhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			String imagePath = Constance.Path.HOTEL_SRC+"/"+mHotel.getName()+"/"+mCheckData.getName()+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
			String imageSavePath = imagePath+name;
			Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			Bitmap bitmap = PictureUtil.getSmallBitmap(Constance.Path.TEMP_IMAGE);
			FileUtil.saveBitmapToSDFile(bitmap,imagePath , name, CompressFormat.JPEG);
			IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
			issueItem.addImage(new ImageItem(imageSavePath));
			if (!issueItem.isCheck()) {
				issueItem.setCheck(true);
			}
			mCheckData.setIssueItem(mCurrentIssuePosition, issueItem);
			saveData();
			mAdapter.notifyItem(mCurrentIssuePosition, mCheckData.getIssue(mCurrentIssuePosition));
			
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPhotoClick(int position) {
		mCurrentIssuePosition = position;
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(Constance.Path.TEMP_IMAGE));  
		//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换  
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  
		startActivityForResult(openCameraIntent, CAMMER_REQUEST_CODE);
	}

	@Override
	public void onCheckedChangeListener(int position, boolean isChecked) {
		mCurrentIssuePosition = position;
		mCheckData.getIssue(mCurrentIssuePosition).setCheck(isChecked);
		saveData();
//		mHotel.setCheckDatas(mCheckData, mCheckDataPosition);
//		DataManager.getInstance().setHotel(mHotelPosition, mHotel);
	}

	@Override
	public void setContent(int position, String content) {
		if (TextUtils.isEmpty(content)) {
			return;
		}
		mCurrentIssuePosition = position;
		IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
		issueItem.setContent(content);
		issueItem.setCheck(true);
		mCheckData.setIssueItem(mCurrentIssuePosition, issueItem);
		mAdapter.notifyItem(position, issueItem);
		saveData();
		
	}
	
	private void saveData() {
		if (mIsSubCheckedData) {
			CheckData checkData = mHotel.getCheckData(mCheckDataPosition);
			checkData.setSubCheckData(mSubCheckDataPosition, mCheckData);
			mHotel.setCheckDatas(checkData, mCheckDataPosition);
			DataManager.getInstance().setHotel(mHotelPosition, mHotel);
		} else {
			mHotel.setCheckDatas(mCheckData, mCheckDataPosition);
			DataManager.getInstance().setHotel(mHotelPosition, mHotel);
		}
		
	}
	
	private void checkNetworkSpeed() {
		new Thread() {
			@Override
			public void run() {
				Log.i("开始", "**********开始  ReadFile*******");
				imageData = ReadFile.getFileFromUrl(url, netWorkSpeedInfo);
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				Log.i("开始", "**********开始  netWorkSpeedInfo1*******");
				while (netWorkSpeedInfo.hadFinishedBytes < netWorkSpeedInfo.totalBytes) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(UPDATE_SPEED);
				}
				if (netWorkSpeedInfo.hadFinishedBytes == netWorkSpeedInfo.totalBytes) {
					handler.sendEmptyMessage(UPDATE_SPEED);
					netWorkSpeedInfo.hadFinishedBytes = 0;
				}

			}
		}.start();
	}
	
	
	public int getDuShu(double number) {
		double a = 0;
		if (number >= 0 && number <= 512) {
			a = number / 128 * 15;
		} else if (number > 521 && number <= 1024) {
			a = number / 256 * 15 + 30;
		} else if (number > 1024 && number <= 10 * 1024) {
			a = number / 512 * 5 + 80;
		} else {
			a = 180;
		}
		return (int) a;
	}

	private Handler handler = new Handler() {
		long tem = 0;
		long falg = 0;
		long numberTotal = 0;
		List<Long> list = new ArrayList<Long>();

		@Override
		public void handleMessage(Message msg) {
			int value = msg.what;
			switch (value) {
			case UPDATE_SPEED:
				tem = netWorkSpeedInfo.speed / 1024;
				list.add(tem);
				Log.i("a", "tem****" + tem);
				for (Long numberLong : list) {
					numberTotal += numberLong;
				}
				falg = numberTotal / list.size();
				numberTotal = 0;
//				nowSpeed.setText(tem + "kb/s");
//				avageSpeed.setText(falg + "kb/s");
//				startAnimation(Double.parseDouble(tem+""));
				break;
			default:
				break;
			}
		}
	};
}
