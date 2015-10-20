package com.lk.hotelcheck.activity.photochosen;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.photochosen.ListImageDirPopupWindow.OnImageDirSelected;
import com.lk.hotelcheck.activity.photochosen.PhotoPickerAdapter.CallBackListener;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageFloder;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.dao.CheckIssue;
import com.lk.hotelcheck.bean.dao.HotelCheck;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.BitmapUtil;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.ImageUtil;
import com.lk.hotelcheck.util.PictureUtil;

import common.Constance;
import common.Constance.CheckDataType;
import common.Constance.IntentKey;
import common.Constance.Path;

public class PhotoPickerActivity extends BaseActivity implements CallBackListener, OnImageDirSelected{

	private static final int MSG_OK = 0x00001;
	private MyHandler myHandler;
	private List<String> mDataList;
	private RecyclerView mRecycleView;
	private PhotoPickerAdapter mAdapter;
	public static final int CAMMER_REQUEST_CODE = 1000;
	private HashMap<String, String> mSelectedMap;
	private String mImagePath;
	private String mImageName;
	private int mIssuePositon;
	private int mHotelPosition;
	private int mCheckDataPosition;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;
	private TextView mChooseDir;
	private TextView mImageCount;
	private File mImgDir;
	int totalCount = 0;
	private int mScreenHeight;
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	private View mLoadingView;
	private CheckData mCheckData;
	private Hotel mHotel;
	private IssueItem mIssueItem;
	private int mType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_picker);
		mIssuePositon = getIntent().getIntExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, -99);
		mCheckDataPosition = getIntent().getIntExtra(IntentKey.INTENT_KEY_CHECK_DATA_POSITION, -99);
		mHotelPosition = getIntent().getIntExtra(IntentKey.INTENT_KEY_POSITION, -99);
		mType = getIntent().getIntExtra(IntentKey.INTENT_KEY_TYPE, Constance.CheckDataType.TYPE_NORMAL);
//		mImagePath = getIntent().getStringExtra(IntentKey.INTENT_KEY_FILE_PATH);
//		mImageName = getIntent().getStringExtra(IntentKey.INTENT_KEY_NAME);
		mLoadingView = findViewById(R.id.vg_loadig);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("选择图片");
		mToolbar.setNavigationIcon(R.drawable.back);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setSupportActionBar(mToolbar);
		
		myHandler = new MyHandler(this);
		mDataList = new ArrayList<String>();
		mRecycleView = (RecyclerView) findViewById(R.id.rv_photo_picker);
		mBottomLy = (RelativeLayout) findViewById(R.id.rl_bottom);
		mChooseDir = (TextView) findViewById(R.id.tv_choose_dir);
		mImageCount = (TextView) findViewById(R.id.tv_total_count);
		mAdapter = new PhotoPickerAdapter(null, this);
		mRecycleView.setAdapter(mAdapter);
		mScreenHeight = DrawUtil.getScreenHeight(this);
		LayoutManager layoutManager = new GridLayoutManager(this, 2);
		mRecycleView.setLayoutManager(layoutManager);
		getImages();
		initData();
		initEvent();
	}
	
	private void initData() {
		mSelectedMap = new HashMap<String, String>();
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
		}
		mIssueItem = mCheckData.getIssue(mIssuePositon);
		if (mIssueItem.getImageCount() > 0) {
			for (ImageItem imageItem : mIssueItem.getImagelist()) {
				mSelectedMap.put(FileUtil.getFileName(imageItem.getLocalImagePath()), imageItem.getLocalImagePath());
			}
		}
	}
	
	public void refresh() {
		mAdapter.updateData(mDataList, mSelectedMap);
	}
	
	private void initDataFile() {
		File imageFile = new File(mImagePath);
		if (!imageFile.exists()) {
			imageFile.mkdirs();
		}
	} 
	
	private String getSavePath() {
		String imagePath = "";
		if (mCheckData.getId() == Constance.CHECK_DATA_ID_ROOM) {
			imagePath = "/"+mHotel.getName()+"/"+"客房"+"/" + mCheckData.getIssue(mIssuePositon).getName()+"/";
		} else if (mCheckData.getId() == Constance.CHECK_DATA_ID_PASSWAY) {
			imagePath = "/"+mHotel.getName()+"/"+"楼层"+"/" + mCheckData.getIssue(mIssuePositon).getName()+"/";
		} else {
			imagePath = "/"+mHotel.getName()+"/"+mCheckData.getName()+"/" + mCheckData.getIssue(mIssuePositon).getName()+"/";
		}
		String localSavePath = Constance.Path.HOTEL_SRC+imagePath;
		return localSavePath;
	}
	
	private void saveImage(String srcFile, String targetPath) {
		if (!FileUtil.isFileExist(srcFile)) {
			return;
		}
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Log.i("lxk", "SD card is not avaiable/writeable right now.");
			Toast.makeText(this, "sdcard不可读写", Toast.LENGTH_SHORT).show();
			return;	
		}
		String fileName = FileUtil.getFileName(srcFile);
		
		Bitmap bitmap = PictureUtil.getSmallBitmap(srcFile);
		if (bitmap == null) {
			return;
		}
		if (mCheckData.getType() == CheckDataType.TYPE_ROOM) {
			bitmap = BitmapUtil.drawTextToBitmap(bitmap, mCheckData.getName());
		} else if (mCheckData.getType() == CheckDataType.TYPE_PASSWAY) {
			bitmap = BitmapUtil.drawTextToBitmap(bitmap, mCheckData.getName());
		}
		String localSavePath = targetPath + fileName;
		boolean	result = FileUtil.saveBitmapToSDFile(bitmap,targetPath , fileName, CompressFormat.JPEG);
		if (result) {
			bitmap.recycle();
//			IssueItem issueItem = mCheckData.getIssue(mIssuePositon);
			ImageItem imageItem = new ImageItem();
			String serviceSavePath = Constance.Path.SERVER_IMAGE_PATH+mHotel.getCheckId()+"/"+DataManager.getInstance().getUserName()+"/"+fileName;
			imageItem.setLocalImagePath(localSavePath);
			imageItem.setServiceSavePath(serviceSavePath);
			mIssueItem.addImage(imageItem);
			boolean isWidth = ImageUtil.isWidthPic(imageItem.getLocalImagePath());
			imageItem.setType(mCheckData.getType());
			imageItem.setWidth(isWidth);
			if (!mIssueItem.isCheck()) {
				mIssueItem.setCheck(true);
			}
			HotelCheck hotelCheck = new HotelCheck(mHotel.getCheckId(), mCheckData.getId().intValue(), mIssueItem.getId(), imageItem);
			hotelCheck.save();
			initCheckedIssue();
		}
		
	}
	
	private void deleteImage(String imagePath) {
		String fileName = FileUtil.getFileName(imagePath);
		String localSavePath = getSavePath() + fileName;
		mIssueItem.removeImageItem(localSavePath);
		HotelCheck hotelCheck = HotelCheck.deleteItemByImageLocalPath(localSavePath);
		if (TextUtils.isEmpty(mIssueItem.getContent())
				&& mIssueItem.getImageCount() == 0) {
			mIssueItem.setCheck(false);
			if (hotelCheck != null) {
//				long id = Long.valueOf(hotelCheck.getCheckId()+""+hotelCheck.getAreaId()+""+hotelCheck.getIssueId());
//				CheckIssue checkIssue = CheckIssue.findById(CheckIssue.class, id);
				CheckIssue checkIssue = DataManager.getInstance().getCheckIssue(hotelCheck.getCheckId(), hotelCheck.getAreaId(), hotelCheck.getIssueId());
				if (checkIssue != null) {
					checkIssue.delete();
				} 
			}
			initCheckedIssue();
		}
	}
	
	private void initCheckedIssue() {
		DataManager.getInstance().saveIssueCheck(mHotel.getCheckId(),
				mCheckData.getId().intValue(), mIssueItem.getId(),
				mIssueItem.isCheck(), mIssueItem.getReformState());
		//保存动态区域的问题修复状态
		if (mCheckData.getType() == CheckDataType.TYPE_ROOM) {
			DataManager.getInstance().saveIssueCheck(mHotel.getCheckId(),
					Constance.CHECK_DATA_ID_ROOM, mIssueItem.getId(),
					mIssueItem.isCheck(), mIssueItem.getReformState());
		} else if (mCheckData.getType() == CheckDataType.TYPE_PASSWAY) {
			DataManager.getInstance().saveIssueCheck(mHotel.getCheckId(),
					Constance.CHECK_DATA_ID_PASSWAY, mIssueItem.getId(),
					mIssueItem.isCheck(), mIssueItem.getReformState());
		}
		mCheckData.updateIssueCheck(mIssueItem);
		if (mCheckData.getType() == CheckDataType.TYPE_ROOM) {
			mHotel.initDymicRoomCheckedData();
		} else if (mCheckData.getType() == CheckDataType.TYPE_PASSWAY) {
			mHotel.initDymicPasswayCheckedData();
		}
	}
	
	static class MyHandler extends Handler {
		
		private WeakReference<PhotoPickerActivity> mWeak;
		
		
		
		public MyHandler(PhotoPickerActivity activity) {
			super();
			mWeak = new WeakReference<PhotoPickerActivity>(activity);
		}



		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_OK:
				mWeak.get().data2View();
				mWeak.get().initListDirPopupWindw();
				mWeak.get().mLoadingView.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}
		
	}
	
	/** 
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 
     */  
    private void getImages() {  
        //显示进度条  
    	mLoadingView.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
                ContentResolver mContentResolver = PhotoPickerActivity.this.getContentResolver();  
  
                //只查询jpeg和png的图片  
                Cursor mCursor = mContentResolver.query(mImageUri, null,  
                        MediaStore.Images.Media.MIME_TYPE + "=? or "  
                                + MediaStore.Images.Media.MIME_TYPE + "=?",  
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);  
                
//                Log.d("lxk", "image count = "+mCursor.getCount());
                  
                  
//                while (mCursor.moveToNext()) {  
//                    //获取图片的路径  
//                    String path = mCursor.getString(mCursor  
//                            .getColumnIndex(MediaStore.Images.Media.DATA));  
//                      
////                    //获取该图片的父路径名  
////                    String parentName = new File(path).getParentFile().getName();  
//  
//                      
//                    //根据父路径名将图片放入到mGruopMap中  
////                    if (!mGruopMap.containsKey(parentName)) {  
////                        List<String> chileList = new ArrayList<String>();  
////                        chileList.add(path);  
////                        mGruopMap.put(parentName, chileList);  
////                    } else {  
////                        mGruopMap.get(parentName).add(path);  
////                    } 
//                    mDataList.add(path);
//                } 
                String firstImage = null;
                while (mCursor.moveToNext())
				{
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath))
					{
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

				}

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;
                //通知Handler扫描图片完成  
                myHandler.sendEmptyMessage(MSG_OK);  
                mCursor.close();  
            }  
        }).start();  
          
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK &&
				requestCode == CAMMER_REQUEST_CODE) {
			saveImage(mImagePath, getSavePath());
			sendResult();
		}
	}

	@Override
	public void onCameraClick(int position) {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mImagePath = Path.TEMP_IMAGE_FLOER_PATH+mImageName+new DateFormat().format("yyyyMMddhhmmss",
				Calendar.getInstance(Locale.CHINA))+".jpg";
		Uri imageUri = Uri.fromFile(new File(mImagePath));  
		//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换  
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  
		startActivityForResult(openCameraIntent, CAMMER_REQUEST_CODE);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			sendResult();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPhotoPick(int position, String imagePath, boolean isCheck) {
		imagePath = mImgDir.getAbsolutePath()+"/"+imagePath;
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
//			mImagePath = mImagePath+mImageName+imageFile.getName();
//			FileUtil.Copy(imageFile, mImagePath);
//			sendResult();
			if (isCheck) {
				saveImage(imagePath, getSavePath());
			} else {
				deleteImage(imagePath);
			}
			
		}
		
	} 
	
	
	public void sendResult() {
		Intent intent = new Intent();
		intent.putExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, mIssuePositon); 
		this.setResult(RESULT_OK, intent);
		finish();
	}
	
	
	
	/**
	 * 为View绑定数据
	 */
	private void data2View()
	{
		if (mImageFloders == null || mImageFloders.size() == 0)
		{
			Toast.makeText(getApplicationContext(), "擦，一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}
		mImgDir = new File(mImageFloders.get(0).getDir());
		mAdapter.setDirPath(mImgDir.getAbsolutePath());
		mDataList = new ArrayList<String>(Arrays.asList(mImgDir.list()));;
		/* 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；*/
		mChooseDir.setText(mImageFloders.get(0).getName());
		mImageCount.setText(mImageFloders.get(0).getCount() + "张");
		refresh();
	};
	
	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw()
	{
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		super.onBackPressed();
//		sendResult();
//	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			sendResult();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private void initEvent()
	{
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}
	
	@Override
	public void selected(ImageFloder floder) {
		mImgDir = new File(floder.getDir());
		mDataList.clear();
		mDataList = new ArrayList<String>(Arrays.asList(mImgDir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		})));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter.setDirPath(mImgDir.getAbsolutePath());
		mAdapter.updateData(mDataList, mSelectedMap);
		mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}

}
