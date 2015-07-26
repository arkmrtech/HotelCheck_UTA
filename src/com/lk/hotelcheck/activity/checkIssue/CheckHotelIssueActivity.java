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
import com.lk.hotelcheck.bean.dao.HotelCheck;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.BitmapUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.ImageUtil;
import com.lk.hotelcheck.util.PictureUtil;

import common.Constance;
import common.Constance.IntentKey;

public class CheckHotelIssueActivity extends BaseActivity implements CallBackListener{
	
	private static final String BUNDLE_POSITION_ID = "position";
	private static final String BUNDLE_CHECK_DATA_POSITION = "dataPosition";
	private static final String BUNDLE_TYPE = "type";
//	private static final String BUNDLE_CHECK_DATA_SUB_POSITION = "subDataPosition";
//	private static final String BUNDLE_IS_SUB_CHECKED_DATA = "isSubCheckData";
	private int mHotelPosition;
	private int mCheckDataPosition;
//	private int mSubCheckDataPosition;
	private Hotel mHotel;
	private CheckData mCheckData;
	public static final int CAMMER_REQUEST_CODE = 1000;
	private int mCurrentIssuePosition;
//	private boolean mIsSubCheckedData;
	private HotelIssueAdapter mAdapter;
	private AlertDialog mAlertDialog;
	private EditText mEditText;
	private int mType; 
	
	
	public static void gotoCheckHotelIssue(Context context,int hotelPosition, int checkDataPosition, int type) {
		Intent intent = new Intent();
		intent.setClass(context, CheckHotelIssueActivity.class);
		intent.putExtra(BUNDLE_POSITION_ID, hotelPosition);
		intent.putExtra(BUNDLE_CHECK_DATA_POSITION, checkDataPosition);
		intent.putExtra(BUNDLE_TYPE, type);
		context.startActivity(intent);
	}
	
//	public static void gotoCheckHotelIssue(Context context,int position, int checkDataPosition, int subCheckedDataPosition) {
//		Intent intent = new Intent();
//		intent.setClass(context, CheckHotelIssueActivity.class);
//		intent.putExtra(BUNDLE_POSITION_ID, position);
//		intent.putExtra(BUNDLE_CHECK_DATA_POSITION, checkDataPosition);
//		intent.putExtra(BUNDLE_IS_SUB_CHECKED_DATA, true);
//		intent.putExtra(BUNDLE_CHECK_DATA_SUB_POSITION, subCheckedDataPosition);
//		context.startActivity(intent);
//	}
	
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
		mType = getIntent().getIntExtra(BUNDLE_TYPE, Constance.CheckDataType.TYPE_NORMAL);
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
				mAdapter = new HotelIssueAdapter(mCheckData.getIssuelist(), this, mHotel.isStatus());
				listView.setAdapter(mAdapter);
				initDataFile();
			}
			
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		DataManager.getInstance().setHotel(mHotelPosition, mHotel);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuItem mChoseAllItem = menu.add(0, 0X10086, 0, getString(R.string.chose_all));
//		mChoseAllItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
		if (resultCode == Activity.RESULT_OK) {
			IssueItem issueItem = mCheckData.getIssue(mCurrentIssuePosition);
			ImageItem imageItem = new ImageItem();
			if (requestCode == CAMMER_REQUEST_CODE) {
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Log.i("lxk", "SD card is not avaiable/writeable right now.");
					return;
				}
				String name = "";
				String imagePath = "";
				if (mCheckData.getId() == Constance.CHECK_DATA_ID_ROOM) {
					name = mCheckData.getName()+"_"+new DateFormat().format("yyyyMMddhhmmss",
							Calendar.getInstance(Locale.CHINA))
							+ ".jpg";
					imagePath = "/"+mHotel.getName()+"/"+"客房"+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
				} else if (mCheckData.getId() == Constance.CHECK_DATA_ID_PASSWAY) {
					name = mCheckData.getName()+"_"+new DateFormat().format("yyyyMMddhhmmss",
							Calendar.getInstance(Locale.CHINA))
							+ ".jpg";
					imagePath = "/"+mHotel.getName()+"/"+"楼层"+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
				} else {
					name = new DateFormat().format("yyyyMMddhhmmss",
							Calendar.getInstance(Locale.CHINA))
							+ ".jpg";
					imagePath = "/"+mHotel.getName()+"/"+mCheckData.getName()+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
				}
				String localSavePath = Constance.Path.HOTEL_SRC+imagePath;
				String serviceSavePath = Constance.Path.SERVER_IMAGE_PATH+name;
				
				Bitmap bitmap = PictureUtil.getSmallBitmap(Constance.Path.TEMP_IMAGE);
				FileUtil.saveBitmapToSDFile(bitmap,localSavePath , name, CompressFormat.JPEG);
				
				imageItem.setLocalImagePath(localSavePath+name);
				imageItem.setServiceSavePath(serviceSavePath);
				issueItem.addImage(imageItem);
				
			} else if (requestCode == Constance.REQUEST_CODE_WIFI) {
				mCurrentIssuePosition = data.getIntExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, -99);
				String imagePath = data.getStringExtra(IntentKey.INTENT_KEY_FILE_PATH);
				File imageFile = new File(imagePath);
				if (imageFile.exists()) {
					String serviceSavePath = Constance.Path.SERVER_IMAGE_PATH+imageFile.getName();
					imageItem.setLocalImagePath(imagePath);
					imageItem.setServiceSavePath(serviceSavePath);
					issueItem.addImage(imageItem);
					
				}
			}
			boolean isWidth = ImageUtil.isWidthPic(imageItem.getLocalImagePath());
			imageItem.setType(mCheckData.getType());
			imageItem.setWidth(isWidth);
			if (!issueItem.isCheck()) {
				issueItem.setCheck(true);
			}
			HotelCheck hotelCheck = new HotelCheck(mHotel.getCheckId(), mCheckData.getId().intValue(), issueItem.getId(), imageItem);
			hotelCheck.save();
//			mCheckData.updateIssueCheck(issueItem);
//			mCheckData.setIssueItem(mCurrentIssuePosition, issueItem);
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
		issueItem.setCheck(isChecked);
		if (issueItem.getId() == Constance.ISSUE_ITEM_WIFI) {
			issueItem.setContent(((HotelCheckApplication)getApplication()).getWifiSpeed());
		}
//		mCheckData.setIssueItem(mCurrentIssuePosition, issueItem);
		mCheckData.updateIssueCheck(issueItem);
		DataManager.getInstance().saveIssueCheck(mHotel.getCheckId(), mCheckData.getId().intValue(), issueItem.getId(), isChecked);
//		saveData();
//		mHotel.setCheckDatas(mCheckData, mCheckDataPosition);
//		DataManager.getInstance().setHotel(mHotelPosition, mHotel);
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
		DataManager.getInstance().saveIssueContent(mHotel.getCheckId(), mCheckData.getId().intValue(), issueItem.getId(), content);
//		mCheckData.setIssueItem(mCurrentIssuePosition, issueItem);
//		mCheckData.updateIssueCheck(issueItem);
		mAdapter.notifyItem(position, issueItem);
		
//		saveData();
		
	}
	
	@Override
	public void onWifiClick(int position) {
		mCurrentIssuePosition = position;
		String imagePath = "";
		String name = mCheckData.getName()+"_";
		if (mCheckData.getId() == Constance.CHECK_DATA_ID_ROOM) {
			
			imagePath = "/"+mHotel.getName()+"/"+"客房"+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
		} else if (mCheckData.getId() == Constance.CHECK_DATA_ID_PASSWAY) {
			imagePath = "/"+mHotel.getName()+"/"+"楼层"+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
		} else {
			imagePath = "/"+mHotel.getName()+"/"+mCheckData.getName()+"/" + mCheckData.getIssue(mCurrentIssuePosition).getName()+"/";
		}
		String localSavePath = Constance.Path.HOTEL_SRC+imagePath;
		Intent intent = new Intent();
		intent.setClass(this, PhotoPickerActivity.class);
		intent.putExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, position);
		intent.putExtra(IntentKey.INTENT_KEY_FILE_PATH, localSavePath);
		intent.putExtra(IntentKey.INTENT_KEY_NAME, name);
		startActivityForResult(intent, Constance.REQUEST_CODE_WIFI);
	}
	
//	private void saveData() {
////		if (mIsSubCheckedData) {
////			CheckData checkData = mHotel.getCheckData(mCheckDataPosition);
////			checkData.setSubCheckData(mSubCheckDataPosition, mCheckData);
////			mHotel.setCheckDatas(checkData, mCheckDataPosition);
////			DataManager.getInstance().setHotel(mHotelPosition, mHotel);
////		} else {
//			mHotel.setCheckDatas(mCheckData, mCheckDataPosition);
////			DataManager.getInstance().setHotel(mHotelPosition, mHotel);
////		}
//		
//	}
	
	private void showAddIssue() {
		if (mAlertDialog == null) {
			LayoutInflater factory = LayoutInflater.from(this);// 提示框
			View view = factory.inflate(R.layout.alert_input, null);// 这里必须是final的
			mEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
			mAlertDialog = new AlertDialog.Builder(this)
					.setView(view)
					.setPositiveButton(
							"确定",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String content = mEditText.getText()
											.toString();
									if (TextUtils.isEmpty(content)) {
										Toast.makeText(CheckHotelIssueActivity.this, "请输入问题名称", Toast.LENGTH_SHORT).show();
										return;
									}
									addIssue(content);
									mEditText.setText("");
								}
							})
					.setNegativeButton(
							"取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mEditText.setText("");
								}
							}).create();
			mAlertDialog.setTitle("请输入问题名称");
		}
		if (!mAlertDialog.isShowing()) {
			mAlertDialog.show();
		}
	}
	
	private void addIssue(String name) {
		IssueItem issueItem = new IssueItem();
		issueItem.setName(name);
		issueItem.setContent("");
		int id = (int) System.currentTimeMillis();
		issueItem.setId(id);
//		mCheckData.addIssue(issueItem);
		mAdapter.addIssue(issueItem);
	}

	
	
	
}
