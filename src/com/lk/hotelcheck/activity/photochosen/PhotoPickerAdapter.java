package com.lk.hotelcheck.activity.photochosen;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lk.hotelcheck.R;

import common.Constance;

public class PhotoPickerAdapter extends RecyclerView.Adapter<ViewHolder>{

	private List<String> mDataList;
	private CallBackListener mListener;
	
	
	
	public PhotoPickerAdapter(List<String> mDataList, CallBackListener listener) {
		super();
		this.mDataList = mDataList;
		this.mListener = listener;
	}

	@Override
	public int getItemCount() {
		int count = mDataList == null ? 0 : mDataList.size();
		return count + 1;
	}

	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		if (arg1 == 0) {
			((PhotoPickerViewHolder)arg0).setData(arg1, R.drawable.camera);
		} else {
			((PhotoPickerViewHolder)arg0).setData(arg1, mDataList.get(arg1-1));
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
//		DrawUtil.resetDensity(arg0.getContext());
//		LayoutParams params = arg0.getLayoutParams();
//		ImageView item = new ImageView(arg0.getContext());
//		int width = DrawUtil.getScreenWidth(arg0.getContext()) / 2;
//		int padding = DrawUtil.dip2px(10);
//		params.width = DrawUtil.dip2px(width);
//		params.height = DrawUtil.dip2px(width);
//		item.setLayoutParams(params);
//		item.setScaleType(ScaleType.CENTER_INSIDE);
//		item.setPadding(padding, padding, padding, padding);
		PhotoPickerViewHolder viewHolder = new PhotoPickerViewHolder(new PhotoChosenItem(arg0.getContext()));
		return viewHolder;
	}
	
	
	class PhotoPickerViewHolder extends ViewHolder {
		
//		private ImageView mView;
		private PhotoChosenItem mItem;
		
		public PhotoPickerViewHolder(PhotoChosenItem itemView) {
			super(itemView);
//			mView = itemView;
			mItem = itemView;
		}
		
		public void setData(final int position, final String imagePath) {
			if (position > 0) {
				mItem.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (mListener != null) {
							mListener.onPhotoPick(position, imagePath);
						}
					}
				});
			}
			mItem.setOriginImageData("file://"+imagePath);
//			ImageLoader.getInstance().displayImage(imagePath, mView);
		}
		
		public void setData(final int position, int resId) {
			String imagePath = "";
			if (position == 0) {
				imagePath = "drawable://"+ resId;
				mItem.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (mListener != null) {
							mListener.onPhotoClick(position);
						}
					}
				});
			} 
			mItem.setOriginImageData(imagePath);
//			ImageLoader.getInstance().displayImage(imagePath, mView);
		}
	
	}

	public interface CallBackListener {
		void onPhotoClick(int position);
		void onPhotoPick(int position, String imagePath);
	}

	public void updateData(List<String> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

}
