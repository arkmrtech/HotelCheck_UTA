package com.lk.hotelcheck.activity.checkIssue;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.HotelCheckApplication;
import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.photochosen.PhotoPickerActivity;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.util.NetWorkSpeedInfo;
import com.lk.hotelcheck.util.ReadFile;

import common.Constance;
import common.Constance.IntentKey;
import common.Constance.PreQueType;

public class HotelIssueAdapter extends RecyclerView.Adapter<ViewHolder>{
	
	private List<IssueItem> dataList;
	private CallBackListener listener;
	private AlertDialog mAlertDialog;
	private boolean mIsChecked;
	private static final int WIFI_VIEW_TYPE = 0X10086;
	private static final int NORMAL_VIEW_TYPE = 0X10087;
	
	
	public HotelIssueAdapter(List<IssueItem> dataList, CallBackListener listener, boolean isChecked) {
		super();
		this.dataList = dataList;
		this.listener = listener;
		this.mIsChecked = isChecked;
	}

	@Override
	public int getItemCount() {
		return dataList == null ? 0 : dataList.size();
	}
	
	public void setDataList(List<IssueItem> dataList) {
		this.dataList = dataList;
	}
	
	public void notifyItem(int position, IssueItem issueItem) {
		this.dataList.set(position, issueItem);
		notifyItemChanged(position);
	}
	
	
	@Override
	public int getItemViewType(int position) {
		int viewType = 0;
		IssueItem issueItem = dataList.get(position);
		if (issueItem.getId() == Constance.ISSUE_ITEM_WIFI) {
			viewType = WIFI_VIEW_TYPE;
		} else {
			viewType = NORMAL_VIEW_TYPE;
		}
		return viewType;
	}
	
	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		IssueItem item = dataList.get(arg1);
		((ItemViewHolder)arg0).setData(item, arg1);
		
	}

	@Override
	public ViewHolder onCreateViewHolder(
			ViewGroup arg0, int arg1) {
		ViewHolder viewHolder = null;
		View convertView = LayoutInflater.from(arg0.getContext())
					.inflate(R.layout.listview_hotel_check_issue_item, arg0, false);
		viewHolder = new ItemViewHolder(convertView);
		
		return viewHolder;
	}

	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int position = (Integer) buttonView.getTag();
			listener.onCheckedChangeListener(position, isChecked);
		}
	};

	private OnClickListener photoOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mIsChecked) {
				Toast.makeText(v.getContext(), "酒店已检查完成不能再修改", Toast.LENGTH_SHORT).show();
				return;
			} 
			if (listener != null) {
				int position = (Integer) v.getTag();
				listener.onPhotoClick(position);
			}
		}
	};
	
	
	private OnClickListener mContentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mIsChecked) {
				Toast.makeText(v.getContext(), "酒店已检查完成不能再修改", Toast.LENGTH_SHORT).show();
				return;
			} 
			int position = (Integer) v.getTag();
			showDialog(position, v.getContext());
		}
	};
	
	private OnClickListener wifiOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mIsChecked) {
				Toast.makeText(v.getContext(), "酒店已检查完成不能再修改", Toast.LENGTH_SHORT).show();
				return;
			} 
			int position = (Integer) v.getTag();
			listener.onWifiClick(position);
		}
	};
	
	private  EditText alertEditText;
	
	private void showDialog( int issuePositon, Context context){
		final IssueItem issueItem = dataList.get(issuePositon);
		if (mAlertDialog == null) {
			LayoutInflater factory = LayoutInflater.from(context);// 提示框
			View view = factory.inflate(R.layout.alert_dialog_edit, null);// 这里必须是final的
			alertEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
			alertEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			mAlertDialog = new AlertDialog.Builder(context)
					 .setTitle("问题描述")//提示框标题
					.setView(view)
					.setPositiveButton("确定",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String content = alertEditText.getText().toString();
									issueItem.setContent(content);
									int position = (Integer) alertEditText.getTag();
									listener.setContent(position, content);
									alertEditText.setText("");
								}
							})
					.setNegativeButton("取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									alertEditText.setText("");

								}
							}).create();
		} 
//		if (!TextUtils.isEmpty(issueItem.getContent())) {
		alertEditText.setTag(issuePositon);
		int index = alertEditText.getSelectionStart();
		if (issueItem.getContent() != null) {
			alertEditText.getText().insert(index, issueItem.getContent());
		}
//		}
		mAlertDialog.show();
	}
	
	class ItemViewHolder  extends RecyclerView.ViewHolder{
		
		private TextView nameTextView;
		private ImageView canmerImageView;
		private TextView mNumberTextView;
		private TextView mReviewTextView;
		private View mContentRedDot;
		private View mContentView;
		private CheckBox mCheckBox;
		
		public ItemViewHolder(View view) {
			super(view);
			if (view != null) {
				nameTextView = (TextView) view.findViewById(R.id.tv_name);
				mContentView = view.findViewById(R.id.iv_edit);
				canmerImageView = (ImageView) view.findViewById(R.id.iv_photo);
				mCheckBox = (CheckBox) view.findViewById(R.id.cb);
				mNumberTextView = (TextView) view.findViewById(R.id.tv_number);
				mReviewTextView = (TextView) view.findViewById(R.id.tv_review);
				mContentRedDot = view.findViewById(R.id.iv_content_red_dot);
			}
		}
		
		public void setData(IssueItem item, int position) {
			if (item == null) {
				return;
			}
			String name = item.getName();
			nameTextView.setText(name);
			canmerImageView.setTag(position);
			mCheckBox.setTag(position);
			mContentView.setTag(position);
			mContentView.setOnClickListener(mContentClickListener);
			if (item.getId() == Constance.ISSUE_ITEM_WIFI) {
				canmerImageView.setOnClickListener(wifiOnClickListener);
			} else {
				canmerImageView.setOnClickListener(photoOnClickListener);
			}
			mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
//			if (mIsChecked || item.getImageCount() > 0 || !TextUtils.isEmpty(item.getContent())) {
//				mCheckBox.setEnabled(false);
//			} else {
//				mCheckBox.setEnabled(true);
//			}
			if (item.getIsPreQue() == PreQueType.TYPE_REVIEW) {
				mReviewTextView.setVisibility(View.VISIBLE);
				nameTextView.setTextColor(nameTextView.getContext().getResources().getColor(R.color.content_orange));
			} else {
				mReviewTextView.setVisibility(View.GONE);
				nameTextView.setTextColor(Color.BLACK);
			}
			if (mIsChecked) {
				mCheckBox.setEnabled(false);
			} else {
				mCheckBox.setEnabled(true);
			}
			if (!TextUtils.isEmpty(item.getContent())) {
				mContentRedDot.setVisibility(View.VISIBLE);
			} else {
				mContentRedDot.setVisibility(View.GONE);
			}
			mCheckBox.setChecked(item.isCheck());
			if (item.getImageCount() <= 0) {
				mNumberTextView.setVisibility(View.GONE);
			} else {
				mNumberTextView.setVisibility(View.VISIBLE);
				mNumberTextView.setText(""+item.getImageCount());
			}
			
		}
		
		
	}
	
//	class WIFIViewHolder extends ViewHolder {
//
//		private View mView;
//		private ImageView canmerImageView;
//		private TextView mNumberTextView;
//		private View mContentView;
//		private CheckBox mCheckBox;
//		private TextView nameTextView;
//		private TextView mReviewTextView;
//		
//		public WIFIViewHolder(View view) {
//			super(view);
//			if (view != null) {
//				mView = view;
//				nameTextView = (TextView) view.findViewById(R.id.tv_name);
//				mCheckBox = (CheckBox) view.findViewById(R.id.cb);
//				mContentView = view.findViewById(R.id.iv_edit);
//				canmerImageView = (ImageView) view.findViewById(R.id.iv_photo);
//				mNumberTextView = (TextView) view.findViewById(R.id.tv_number);
//				mReviewTextView = (TextView) view.findViewById(R.id.tv_review);
//			}
//		}
//		
//		public void setData(IssueItem item, final int position) {
//			if (item == null) {
//				return;
//			}
//			String name = item.getName();
//			nameTextView.setText(name);
//			mCheckBox.setTag(position);
//			mContentView.setTag(position);
//			mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
//			if (item.getImageCount() > 0 || !TextUtils.isEmpty(item.getContent())) {
//				mCheckBox.setEnabled(false);
//			} else {
//				mCheckBox.setEnabled(true);
//			}
//			mCheckBox.setChecked(item.isCheck());
//			if (item.getImageCount() <= 0) {
//				mNumberTextView.setVisibility(View.GONE);
//			} else {
//				mNumberTextView.setVisibility(View.VISIBLE);
//				mNumberTextView.setText(""+item.getImageCount());
//			}
//			if (item.getIsPreQue() == PreQueType.TYPE_REVIEW) {
//				mReviewTextView.setVisibility(View.VISIBLE);
//				nameTextView.setTextColor(nameTextView.getContext().getResources().getColor(R.color.content_orange));
//			} else {
//				mReviewTextView.setVisibility(View.GONE);
//				nameTextView.setTextColor(Color.BLACK);
//			}
////			mView.setOnClickListener(new OnClickListener() {
////				
////				@Override
////				public void onClick(View v) {
////					listener.onWifiClick(position);
////				}
////			});
//			canmerImageView.setTag(position);
//			canmerImageView.setOnClickListener(wifiOnClickListener);
//			mContentView.setOnClickListener(mContentClickListener);
//		}
//		
//		
//		
//	
//		
//	}
	
	public interface CallBackListener {
		void onPhotoClick(int position);
		void onCheckedChangeListener(int position, boolean isChecked);
		void setContent(int position, String content);
		void onWifiClick(int position);
	}

	public void addIssue(IssueItem issueItem) {
		if (issueItem == null) {
			return;
		}
		dataList.add(issueItem);
		notifyItemInserted(dataList.size()-1);
	}

	
}
