package com.lk.hotelcheck.activity.photochosen;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.util.DrawUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.bool;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class PhotoChosenItem extends RelativeLayout{

//	private CheckBox mCheckBox;
	private ImageView mImageView;
	private static final int IMAGE_ID = 0X000001;
	
	public PhotoChosenItem(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public PhotoChosenItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public PhotoChosenItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PhotoChosenItem(Context context) {
		super(context);
		init();
	}

	private void init() {
		DrawUtil.resetDensity(getContext());
		int padding = DrawUtil.dip2px(4);
		setPadding(padding, padding, padding, padding);
		
		
		mImageView = new ImageView(getContext());
		setBackgroundColor(Color.WHITE);
		LayoutParams imageParams = new LayoutParams(getImageWidth(), getImageWidth());
		mImageView.setLayoutParams(imageParams);
		mImageView.setId(IMAGE_ID);
		mImageView.setPadding(padding, padding, padding, padding);
		mImageView.setScaleType(ScaleType.CENTER);
		imageParams.addRule(CENTER_IN_PARENT, TRUE);
//		imageParams.setMargins(padding, padding, padding, padding);
		
		
//		mCheckBox = new CheckBox(getContext());
//		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.addRule(ALIGN_RIGHT, IMAGE_ID);
//		params.addRule(ALIGN_TOP, IMAGE_ID);
//		mCheckBox.setPadding(padding * 2, padding * 2, padding * 2, padding * 4);
//		mCheckBox.setButtonDrawable(R.drawable.image_check_box);
//		mCheckBox.setLayoutParams(params);
		
		
		addView(mImageView);
//		addView(mCheckBox);
	}
	
	public void setImageData(String imageURL) {
		if (mImageView != null) {
			Log.d("lxk", "imageURL = "+imageURL);
			ImageLoader.getInstance().displayImage("file://"+imageURL, mImageView);
		}
	}
	
	public void setOriginImageData(String imageURL) {
		if (mImageView != null) {
			mImageView.setScaleType(ScaleType.FIT_CENTER);
			ImageLoader.getInstance().displayImage(imageURL, mImageView);
		}
	}
	
//	public void setChecked(boolean check) {
////		if (mCheckBox != null) {
//			mCheckBox.setChecked(check);
////		}
//	}
//	
//	public boolean isChecked() {
//		if (mCheckBox == null) {
//			return false;
//		} else {
//			return mCheckBox.isChecked();
//		}
//	}
//	
	
	private int getImageWidth () {
		int width = (DrawUtil.sWidthPixels - DrawUtil.dip2px(24)) / 2;
		return width;
	}
	
//	public CheckBox getCheckBox() {
//		return mCheckBox;
//	}
	
}
