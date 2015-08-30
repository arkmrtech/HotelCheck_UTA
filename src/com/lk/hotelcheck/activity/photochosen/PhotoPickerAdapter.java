package com.lk.hotelcheck.activity.photochosen;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lk.hotelcheck.R;

public class PhotoPickerAdapter extends RecyclerView.Adapter<ViewHolder>{

	private List<String> mDataList;
	private CallBackListener mListener;
	private String mDirPath;
	
	
	
	
	
	public String getDirPath() {
		return mDirPath;
	}

	public void setDirPath(String dirPath) {
		this.mDirPath = dirPath;
	}

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
		PhotoPickerViewHolder viewHolder = new PhotoPickerViewHolder(new PhotoChosenItem(arg0.getContext()));
		return viewHolder;
	}
	
	
	class PhotoPickerViewHolder extends ViewHolder {
		
		private PhotoChosenItem mItem;
		
		public PhotoPickerViewHolder(PhotoChosenItem itemView) {
			super(itemView);
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
			mItem.setOriginImageData("file://"+mDirPath+"/"+imagePath);
		}
		
		public void setData(final int position, int resId) {
			String imagePath = "";
			if (position == 0) {
				imagePath = "drawable://"+ resId;
				mItem.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (mListener != null) {
							mListener.onCameraClick(position);
						}
					}
				});
			} 
			mItem.setOriginImageData(imagePath);
		}
	
	}

	public interface CallBackListener {
		void onCameraClick(int position);
		void onPhotoPick(int position, String imagePath);
	}

	public void updateData(List<String> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

}
