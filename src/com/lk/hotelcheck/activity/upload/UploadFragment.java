package com.lk.hotelcheck.activity.upload;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.upload.UploadProxy;
import com.lk.hotelcheck.util.Machine;
import com.nostra13.universalimageloader.core.ImageLoader;
import common.Constance;
import common.Constance.ImageUploadState;
import common.Constance.IntentKey;

public class UploadFragment extends Fragment{

	
	
	public static UploadFragment getUploadingInstance(int hotelId) {
		UploadFragment fragment = new UploadFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constance.IntentKey.INTENT_KEY_ID, hotelId);
		bundle.putInt(IntentKey.INTENT_KEY_TYPE, TYPE_UPLOADING);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public static UploadFragment getUploadCompleteInstance(int hotelId) {
		UploadFragment fragment = new UploadFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constance.IntentKey.INTENT_KEY_ID, hotelId);
		bundle.putInt(IntentKey.INTENT_KEY_TYPE, TYPE_UPLOAD_COMPLETE);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private int mHotelId;
	private List<UploadBean> mDataList;
	private View mRootView;
	private UploadAdapter mAdapter;
	private RecyclerView mRecycle;
	private LinearLayoutManager manager;
	private int mType;
	private static final int TYPE_UPLOADING = 1;
	private static final int TYPE_UPLOAD_COMPLETE = 2;
	private Context mContext;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHotelId = getArguments().getInt(IntentKey.INTENT_KEY_ID);
		mType = getArguments().getInt(IntentKey.INTENT_KEY_TYPE);
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_upload, container, false);
			mRecycle = (RecyclerView) mRootView.findViewById(R.id.rv_upload);
			if (mType == TYPE_UPLOADING) {
				mDataList = UploadProxy.getUploadingList(mHotelId);
			} else {
				mDataList = UploadProxy.getUploadComplete(mHotelId);
			}
			mAdapter = new UploadAdapter();
			manager = new LinearLayoutManager(container.getContext());
			mRecycle.setLayoutManager(manager);
			mRecycle.setAdapter(mAdapter);
			
//		}
		return mRootView;
	}
	
	
	class UploadAdapter extends RecyclerView.Adapter<ViewHolder> {

		@Override
		public int getItemCount() {
			Log.d("lxk", "count = "+mDataList.size());
			return mDataList == null ? 0 : mDataList.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder arg0, int arg1) {
			UploadBean bean = mDataList.get(arg1);
			((UploadItemViewHolder)arg0).setData(bean);
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			View view = LayoutInflater.from(arg0.getContext()).inflate(R.layout.upload_item, arg0, false);
			UploadItemViewHolder viewHolder = new UploadItemViewHolder(view);
			return viewHolder;
		}
		
	}
	
	class UploadItemViewHolder extends ViewHolder {

		private ImageView mImageView;
		private TextView mNameTextView;
		private ImageButton mRetryButton;
		private TextView mStateTextView;
		private TextView mSpeedTextView;
		
		public UploadItemViewHolder(View itemView) {
			super(itemView);
			mImageView = (ImageView) itemView.findViewById(R.id.iv_issue);
			mNameTextView = (TextView) itemView.findViewById(R.id.tv_name);
			mStateTextView = (TextView) itemView.findViewById(R.id.tv_state);
			mRetryButton = (ImageButton) itemView.findViewById(R.id.ib_retry);
			mSpeedTextView = (TextView) itemView.findViewById(R.id.tv_speed);
		}
		
		public void setData(UploadBean bean) {
			if (bean == null) {
				return;
			}
			String imageUrl = "file://"+bean.getLocalImagePath();
			String name = "";
			if (!TextUtils.isEmpty(bean.getServiceImageSavePath())) {
				name = bean.getServiceImageSavePath().substring(bean.getServiceImageSavePath().lastIndexOf("/")+1);
			}
			ImageLoader.getInstance().displayImage(imageUrl, mImageView);
			mNameTextView.setText(name);
			if (bean.getImageState() == ImageUploadState.STATE_FAIL) {
				mRetryButton.setVisibility(View.VISIBLE);
			} else {
				mRetryButton.setVisibility(View.GONE);
			}
			if (bean.getImageState() == ImageUploadState.STATE_FINISH) {
				mSpeedTextView.setVisibility(View.GONE);
			} else {
				mSpeedTextView.setVisibility(View.VISIBLE);
			}
			long transfer = bean.getTransferedBytes() / 1024;
			long total = bean.getTotalBytes() / 1024;
			mSpeedTextView.setText(transfer+"kb"+"/"+total+"kb");
			switch (bean.getImageState()) {
			case ImageUploadState.STATE_FAIL:
				mStateTextView.setText("上传失败");
				break;
			case ImageUploadState.STATE_WAIT:
				mStateTextView.setText("等待上传");
				break;
			case ImageUploadState.STATE_START:
				mStateTextView.setText("上传中");
				break;
			case ImageUploadState.STATE_FINISH:
				mStateTextView.setText("上传成功");
				break;
			default:
				break;
			}
			mRetryButton.setTag(bean);
			mRetryButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UploadBean bean = (UploadBean) v.getTag();
					if(bean != null) {
						retry(bean);
					}
				}
			});
		}
		
		public void retry(UploadBean uploadBean) {
			if (!Machine.isNetworkOK(mContext)) {
				Toast.makeText(mContext, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
				return;
			}
			UploadProxy.restart(uploadBean);
			mRetryButton.setVisibility(View.GONE);
		}
		
	}
	
	public int findPosition(UploadBean bean) {
		int i = -1;
		for (int j = 0; j < mDataList.size(); j++) {
			UploadBean uploadBean = mDataList.get(j);
//			if (uploadBean.getId() == bean.getId()) {
//				i = j;
//				return i;
//			}
			if (uploadBean.getLocalImagePath().equals(bean.getLocalImagePath())) {
				i = j;
				return i;
			}
		}
		return i;
	}

	public void update(UploadBean uploadBean) {
		int index = findPosition(uploadBean);
		if (index == -1) {
			return;
		}
		ViewHolder viewHolder = mRecycle.getChildViewHolder(mRecycle.getChildAt(index));
		if (viewHolder != null && viewHolder instanceof UploadItemViewHolder) {
			((UploadItemViewHolder)viewHolder).setData(uploadBean);
		}
	}

	public void remove(UploadBean uploadBean) {
		int index = findPosition(uploadBean);
		if (index == -1) {
			return;
		}
		mDataList.remove(index);
		mAdapter.notifyItemRemoved(index);
	}

	public void add(UploadBean uploadBean) {
		mDataList.add(uploadBean);
		mAdapter.notifyItemInserted(mDataList.size() -1);
	}

	public void uploadAll() {
		if (!Machine.isNetworkOK(mContext)) {
			Toast.makeText(mContext, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
			return;
		}
		for (UploadBean bean : mDataList) {
			if (bean.getImageState() == ImageUploadState.STATE_FAIL) {
				UploadProxy.restart(bean);
			}
		}
	}
	
}
