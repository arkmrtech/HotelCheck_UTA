package com.lk.hotelcheck.activity.checkIssue;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.lk.hotelcheck.HotelCheckApplication;
import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.checkIssue.HotelIssueAdapter.CallBackListener;
import com.lk.hotelcheck.activity.photochosen.PhotoPickerActivity;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.dao.DymicIssue;
import com.lk.hotelcheck.bean.dao.HotelCheck;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.BitmapUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.ImageUtil;
import com.lk.hotelcheck.util.PictureUtil;

import common.Constance;
import common.Constance.CheckDataType;
import common.Constance.CheckType;
import common.Constance.DefQueType;
import common.Constance.IntentKey;
import common.Constance.PreQueType;

public class CheckHotelIssueActivity extends BaseActivity implements CallBackListener{
	
	private int mHotelPosition;
	private int mCheckDataPosition;
	private Hotel mHotel;
	private CheckData mCheckData;
	public static final int CAMMER_REQUEST_CODE = 1000;
	private int mCurrentIssuePosition;
	private HotelIssueAdapter mAdapter;
	private AlertDialog mAlertDialog;
	private EditText mEditText;
	private int mType; 
	
	
	public static void gotoCheckHotelIssue(Context context,int hotelPosition, int checkDataPosition, int type) {
		Intent intent = new Intent();
		intent.setClass(context, CheckHotelIssueActivity.class);
		intent.putExtra(IntentKey.INTENT_KEY_POSITION, hotelPosition);
		intent.putExtra(IntentKey.INTENT_KEY_CHECK_DATA_POSITION, checkDataPosition);
		intent.putExtra(IntentKey.INTENT_KEY_TYPE, type);
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
		
		
		mHotelPosition = getIntent().getIntExtra(IntentKey.INTENT_KEY_POSITION, -1);
		mCheckDataPosition = getIntent().getIntExtra(IntentKey.INTENT_KEY_CHECK_DATA_POSITION, -1);
		mType = getIntent().getIntExtra(IntentKey.INTENT_KEY_TYPE, Constance.CheckDataType.TYPE_NORMAL);
		mHotel = DataManager.getInstance().getHotel(mHotelPosition);
		if (mHotel != null) {
			switch (mType) {
			case Constance.CheckDataType.TYPE_NORMAL:
				mCheckData = mHotel.getCheckData(mCheckDataPosition);
				break;
			case Constance.CheckDataType.TYPE_ROOM:
				mCheckData = mHotel.getRoomData(mCheckDataPosition);
				break;
			case Constance.CheckDataType.TYPE_PASSWAY:
				mCheckData = mHotel.getPasswayData(mCheckDataPosition);
				break;
			default:
				break;
			}
			if (mCheckData != null) {
				toolbar.setTitle(mCheckData.getName());
				setSupportActionBar(toolbar);
				RecyclerView listView = (RecyclerView) findViewById(R.id.lv_hotel_issue);
				LinearLayoutManager layoutManager = new LinearLayoutManager(this);
				listView.setLayoutManager(layoutManager);
				boolean isPreview = mHotel.getCheckType() == CheckType.CHECK_TYPE_REVIEW;
				mAdapter = new HotelIssueAdapter(mCheckData.getIssuelist(), this, mHotel.isStatus(), isPreview);
				listView.setAdapter(mAdapter);
				initDataFile();
			}
			
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.menu_check_issue, menu);
		return true;
	}
	
	
	
	private void initDataFile() {
		File tempCacheFile = new File(Constance.Path.TEMP_IMAGE_FLOER_PATH);
		if (!tempCacheFile.exists()) {
			tempCacheFile.mkdirs();
		}
		File imageFile = new File(Constance.Path.IMAGE_PATH);
		if (!imageFile.exists()) {
			imageFile.mkdirs();
		}
	} 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mCheckData != null) {
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == CAMMER_REQUEST_CODE) {
					saveImage();
				} else if (requestCode == Constance.REQUEST_CODE_WIFI) {
					mCurrentIssuePosition = data.getIntExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, -99);
					IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
					mAdapter.notifyItem(mCurrentIssuePosition, issueItem);
				}
			}
		}
	}
	
	/**
	 * 保存拍摄图片的数据
	 */
	private void saveImage() {
		String localSavePath = "";
		String serviceSavePath = "";
		IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
		ImageItem imageItem = new ImageItem();
		String fileName = "";
		boolean result = false;
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Log.i("lxk", "SD card is not avaiable/writeable right now.");
			Toast.makeText(this, "sdcard不可读写", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String imagePath = "";
		Bitmap bitmap = PictureUtil.getSmallBitmap(Constance.Path.TEMP_IMAGE);
		if (mCheckData.getType() == CheckDataType.TYPE_ROOM) {
			fileName = mCheckData.getName()+"_"+new DateFormat().format("yyyyMMddhhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			imagePath = "/"+mHotel.getName()+"/"+"客房"+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
			bitmap = BitmapUtil.drawTextToBitmap(bitmap, mCheckData.getName());
		} else if (mCheckData.getType() == CheckDataType.TYPE_PASSWAY) {
			fileName = mCheckData.getName()+"_"+new DateFormat().format("yyyyMMddhhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			imagePath = "/"+mHotel.getName()+"/"+"楼层"+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
			bitmap = BitmapUtil.drawTextToBitmap(bitmap, mCheckData.getName());
		} else {
			fileName = new DateFormat().format("yyyyMMddhhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			imagePath = "/"+mHotel.getName()+"/"+mCheckData.getName()+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
		}
		String filepath = Constance.Path.HOTEL_SRC+imagePath;
		localSavePath = filepath+fileName;
		result = FileUtil.saveBitmapToSDFile(bitmap,filepath , fileName, CompressFormat.JPEG);
		bitmap.recycle();
		serviceSavePath = Constance.Path.SERVER_IMAGE_PATH+mHotel.getCheckId()+"/"+DataManager.getInstance().getUserName()+"/"+fileName;
		if (result) {
			imageItem.setLocalImagePath(localSavePath);
			imageItem.setServiceSavePath(serviceSavePath);
			issueItem.addImage(imageItem);
			boolean isWidth = ImageUtil.isWidthPic(imageItem.getLocalImagePath());
			imageItem.setType(mCheckData.getType());
			imageItem.setWidth(isWidth);
			if (!issueItem.isCheck()) {
				issueItem.setCheck(true);
			}
			HotelCheck hotelCheck = new HotelCheck(mHotel.getCheckId(), mCheckData.getId().intValue(), issueItem.getId(), imageItem);
			hotelCheck.save();
			updateIssue(mCurrentIssuePosition, issueItem, null);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_add:
			showAddIssue();
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
		IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
		if (isChecked != issueItem.isCheck()) {
			issueItem.setCheck(isChecked);
			updateIssue(position, issueItem, null);
		}
	}

	@Override
	public void setContent(int position, String content) {
		
		mCurrentIssuePosition = position;
		IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
		issueItem.setContent(content);
		if (TextUtils.isEmpty(content) && issueItem.getImageCount() == 0 ) {
			issueItem.setCheck(false);
		} else {
			issueItem.setCheck(true);
		} 
		updateIssue(position, issueItem, content);
	}
	
	@Override
	public void onWifiClick(int position) {
		mCurrentIssuePosition = position;
		Intent intent = new Intent();
		intent.setClass(this, PhotoPickerActivity.class);
		intent.putExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, position);
		intent.putExtra(IntentKey.INTENT_KEY_CHECK_DATA_POSITION, mCheckDataPosition);
		intent.putExtra(IntentKey.INTENT_KEY_POSITION, mHotelPosition);
		intent.putExtra(IntentKey.INTENT_KEY_TYPE, mType);
		startActivityForResult(intent, Constance.REQUEST_CODE_WIFI);
	}
	
	/**
	 * 更新问题的质检状态和数据
	 * @param position
	 * @param issueItem
	 * @param content
	 */
	private void updateIssue(int position, IssueItem issueItem, String content) {
		if (content != null) {
			DataManager.getInstance().saveIssueContent(mHotel.getCheckId(), mCheckData.getId().intValue(), issueItem.getId(), content, issueItem.isCheck(), issueItem.getReformState());
		} else {
			DataManager.getInstance().saveIssueCheck(mHotel.getCheckId(), mCheckData.getId().intValue(), issueItem.getId(), issueItem.isCheck(), issueItem.getReformState());
		}
		mCheckData.updateIssueCheck(issueItem);
		if (mCheckData.getType() == CheckDataType.TYPE_ROOM || 
				mCheckData.getType() == CheckDataType.TYPE_PASSWAY) {
			mHotel.updateDymicCheckedData(position, mCheckData, issueItem);
		} 
		mAdapter.notifyItem(position, issueItem);
	}
	
	
	private void showAddIssue() {
		if (mHotel.isStatus()) {
			Toast.makeText(this, "酒店已经完成检查，不能再新增问题", Toast.LENGTH_SHORT).show();
			return;
		}
		LayoutInflater factory = LayoutInflater.from(this);// 提示框
		View view = factory.inflate(R.layout.alert_input, null);// 这里必须是final的
		mEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
		mAlertDialog = new AlertDialog.Builder(this)
				.setView(view)
				.setPositiveButton("确定",// 提示框的两个按钮
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String content = mEditText.getText().toString();
								if (TextUtils.isEmpty(content)) {
									Toast.makeText(
											CheckHotelIssueActivity.this,
											"请输入问题名称", Toast.LENGTH_SHORT)
											.show();
									return;
								}
								if (hasIssue(content)) {
									Toast.makeText(
											CheckHotelIssueActivity.this,
											"问题已存在，请修改", Toast.LENGTH_SHORT)
											.show();
									return;
								} else {
									addIssue(content);
									mEditText.setText("");
								}

							}
						})
				.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mEditText.setText("");
							}
						}).create();
		mAlertDialog.setTitle("请输入问题名称");
		if (!mAlertDialog.isShowing()) {
			mAlertDialog.show();
		}
	}
	
	
	private void addIssue(String name) {
		IssueItem issueItem = null;
		DymicIssue dymicIssue = null;
		switch (mType) {
		case Constance.CheckDataType.TYPE_NORMAL:
			issueItem = createDymicIssueItem(name);
			dymicIssue = new DymicIssue(mHotel.getId(), mCheckData.getId(), issueItem);
			dymicIssue.save();
			mCheckData.addIssue(issueItem);
			break;
		case Constance.CheckDataType.TYPE_ROOM:
			for (CheckData checkData : mHotel.getRoomList()) {
				issueItem = createDymicIssueItem(name);
				dymicIssue = new DymicIssue(mHotel.getId(), checkData.getId(), issueItem);
				dymicIssue.save();
				checkData.addIssue(issueItem);
			}
			if (issueItem != null) {
				mHotel.addRoomDymicIssue(issueItem);
			}
			break;
		case Constance.CheckDataType.TYPE_PASSWAY:
			for (CheckData checkData : mHotel.getPasswayList()) {
				issueItem = createDymicIssueItem(name);
				dymicIssue = new DymicIssue(mHotel.getId(), checkData.getId(), issueItem);
				dymicIssue.save();
				checkData.addIssue(issueItem);
			}
			if (issueItem != null) {
				mHotel.addPasswayDymicIssue(issueItem);
			}
			break;
		default:
			break;
		}
		mAdapter.notifyItemInserted(mCheckData.getIssueCount()-1);
	}
	
	private IssueItem createDymicIssueItem(String name) {
		IssueItem issueItem = new IssueItem();
		issueItem.setName(name);
		issueItem.setContent("");
		issueItem.setIsDefQue(DefQueType.TYPE_DYMIC);
		issueItem.setIsPreQue(PreQueType.TYPE_NEW);
		issueItem.setDimOneId(1013);
		issueItem.setDimOneName("其他");
		int id = Math.abs(name.hashCode());
		issueItem.setId(id);
		return issueItem;
	}
	
	private boolean hasIssue(String name) {
		for (IssueItem issueItem : mCheckData.getIssuelist()) {
			if (issueItem.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void onDeleteItem(int position) {
		mCheckData.initCheckedIssue();
	}

	
	
	
}
