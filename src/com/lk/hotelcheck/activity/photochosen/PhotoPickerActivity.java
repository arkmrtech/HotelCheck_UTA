package com.lk.hotelcheck.activity.photochosen;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.photochosen.PhotoPickerAdapter.CallBackListener;
import com.lk.hotelcheck.util.FileUtil;

import common.Constance.IntentKey;

public class PhotoPickerActivity extends BaseActivity implements CallBackListener{

	private static final int MSG_OK = 0x00001;
	private MyHandler myHandler;
	private List<String> mDataList;
	private RecyclerView mRecycleView;
	private PhotoPickerAdapter mAdapter;
	public static final int CAMMER_REQUEST_CODE = 1000;
	private String mImagePath;
	private String mImageName;
	private int mIssuePositon;
	
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
		mAdapter = new PhotoPickerAdapter(null, this);
		mRecycleView.setAdapter(mAdapter);
		LayoutManager layoutManager = new GridLayoutManager(this, 2);
		mRecycleView.setLayoutManager(layoutManager);
		getImages();
		initDataFile();
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
				mWeak.get().refresh();
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
                  
                if(mCursor == null){  
                    return;  
                }  
                  
                while (mCursor.moveToNext()) {  
                    //获取图片的路径  
                    String path = mCursor.getString(mCursor  
                            .getColumnIndex(MediaStore.Images.Media.DATA));  
                      
//                    //获取该图片的父路径名  
//                    String parentName = new File(path).getParentFile().getName();  
  
                      
                    //根据父路径名将图片放入到mGruopMap中  
//                    if (!mGruopMap.containsKey(parentName)) {  
//                        List<String> chileList = new ArrayList<String>();  
//                        chileList.add(path);  
//                        mGruopMap.put(parentName, chileList);  
//                    } else {  
//                        mGruopMap.get(parentName).add(path);  
//                    } 
                    mDataList.add(path);
                }  
                Collections.reverse(mDataList);
                  
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
}
