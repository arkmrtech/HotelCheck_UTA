package com.lk.hotelcheck.activity.photochosen;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.photochosen.ListImageDirPopupWindow.OnImageDirSelected;
import com.lk.hotelcheck.activity.photochosen.PhotoPickerAdapter.CallBackListener;
import com.lk.hotelcheck.bean.ImageFloder;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.FileUtil;

import common.Constance.IntentKey;

public class PhotoPickerActivity extends BaseActivity implements CallBackListener, OnImageDirSelected{

	private static final int MSG_OK = 0x00001;
	private MyHandler myHandler;
	private List<String> mDataList;
	private RecyclerView mRecycleView;
	private PhotoPickerAdapter mAdapter;
	public static final int CAMMER_REQUEST_CODE = 1000;
	private String mImagePath;
	private String mImageName;
	private int mIssuePositon;
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
	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	int totalCount = 0;
	private int mScreenHeight;
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_picker);
		mIssuePositon = getIntent().getIntExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, -99);
		mImagePath = getIntent().getStringExtra(IntentKey.INTENT_KEY_FILE_PATH);
		mImageName = getIntent().getStringExtra(IntentKey.INTENT_KEY_NAME);
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
		initDataFile();
		initEvent();
	}
	
	public void refresh() {
		mAdapter.updateData(mDataList);
	}
	
	private void initDataFile() {
		File imageFile = new File(mImagePath);
		if (!imageFile.exists()) {
			imageFile.mkdirs();
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
//        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");  
          
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
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

//					if (picSize > mPicsSize)
//					{
//						mPicsSize = picSize;
//						mImgDir = parentFile;
//					}
				}
//				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;
//                Collections.reverse(mDataList);
                  
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
			sendResult();
		}
	}

	@Override
	public void onCameraClick(int position) {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mImagePath = mImagePath+mImageName+new DateFormat().format("yyyyMMddhhmmss",
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
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPhotoPick(int position, String imagePath) {
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			mImagePath = mImagePath+mImageName+imageFile.getName();
			FileUtil.Copy(imageFile, mImagePath);
			sendResult();
		}
		
	} 
	
	public void sendResult() {
		Intent intent = new Intent();
		intent.putExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, mIssuePositon); 
		intent.putExtra(IntentKey.INTENT_KEY_FILE_PATH, mImagePath);
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
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mImageCount.setText(totalCount + "张");
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
		mAdapter.updateData(mDataList);
		mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}

}
