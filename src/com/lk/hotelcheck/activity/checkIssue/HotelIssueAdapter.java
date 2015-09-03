package com.lk.hotelcheck.activity.checkIssue;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.dao.DymicIssue;
import common.Constance;
import common.Constance.DefQueType;
import common.Constance.PreQueType;

public class HotelIssueAdapter extends RecyclerView.Adapter<ViewHolder>{
	
	private List<IssueItem> mDataList;
	private CallBackListener mListener;
//	private AlertDialog mAlertDialog;
	private boolean mIsChecked;
	private boolean mIsPreview;
	
	private static final int WIFI_VIEW_TYPE = 0X10086;
	private static final int NORMAL_VIEW_TYPE = 0X10087;
	
	public HotelIssueAdapter(List<IssueItem> dataList, CallBackListener listener, boolean isChecked, boolean isPreview) {
		super();
		this.mDataList = dataList;
		this.mListener = listener;
		this.mIsChecked = isChecked;
		this.mIsPreview = isPreview;
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}
	
	public void setDataList(List<IssueItem> dataList) {
		this.mDataList = dataList;
	}
	
	public void notifyItem(int position, IssueItem issueItem) {
		this.mDataList.set(position, issueItem);
		notifyItemChanged(position);
	}
	
	
	@Override
	public int getItemViewType(int position) {
		int viewType = 0;
		IssueItem issueItem = mDataList.get(position);
		if (issueItem.getId() == Constance.ISSUE_ITEM_WIFI) {
			viewType = WIFI_VIEW_TYPE;
		} else {
			viewType = NORMAL_VIEW_TYPE;
		}
		return viewType;
	}
	
	@Override
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		IssueItem item = mDataList.get(arg1);
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
			mListener.onCheckedChangeListener(position, isChecked);
		}
	};

	private OnClickListener mPhotoOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mIsChecked) {
				Toast.makeText(v.getContext(), "酒店已检查完成不能再修改", Toast.LENGTH_SHORT).show();
				return;
			} 
			if (mListener != null) {
				int position = (Integer) v.getTag();
				mListener.onPhotoClick(position);
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
	
	private OnClickListener mWifiOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mIsChecked) {
				Toast.makeText(v.getContext(), "酒店已检查完成不能再修改", Toast.LENGTH_SHORT).show();
				return;
			} 
			int position = (Integer) v.getTag();
			mListener.onWifiClick(position);
		}
	};
	
	private OnLongClickListener mLongClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			int position = (Integer) v.getTag();
			IssueItem issueItem = mDataList.get(position);
			showDeleteDialog(v.getContext(), position, issueItem.getName());
			return true;
		}
	};

	
	private void showDeleteDialog(Context context, final int position, String name) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("删除自定义问题")
									.setMessage("确定要删除问题"+name+"?")
									.setPositiveButton("确定",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									deleteIssue(position);
								}
							})
					.setNegativeButton("取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create();
		alertDialog.show();
	}
	
	
	
	private void showDialog( int issuePositon, Context context){
		final IssueItem issueItem = mDataList.get(issuePositon);
			LayoutInflater factory = LayoutInflater.from(context);// 提示框
			View view = factory.inflate(R.layout.alert_dialog_edit, null);// 这里必须是final的
			final EditText mAlertEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
			mAlertEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			AlertDialog mAlertDialog = new AlertDialog.Builder(context)
					 .setTitle("问题描述")//提示框标题
					.setView(view)
					.setPositiveButton("确定",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String content = mAlertEditText.getText().toString();
									issueItem.setContent(content);
									int position = (Integer) mAlertEditText.getTag();
									mListener.setContent(position, content);
									mAlertEditText.setText("");
								}
							})
					.setNegativeButton("取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mAlertEditText.setText("");

								}
							}).create();
		mAlertEditText.setTag(issuePositon);
		int index = mAlertEditText.getSelectionStart();
		if (issueItem.getContent() != null) {
			mAlertEditText.getText().insert(index, issueItem.getContent());
		}
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
		private View mView;
		
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
				mView = view;
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
				canmerImageView.setOnClickListener(mWifiOnClickListener);
			} else {
				canmerImageView.setOnClickListener(mPhotoOnClickListener);
			}
			mView.setTag(position);
			if (item.getIsDefQue() == DefQueType.TYPE_DYMIC && !item.isCheck()) {
				mView.setOnLongClickListener(mLongClickListener);
			} else {
				mView.setOnLongClickListener(null);
			}
			mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
			if (item.getIsPreQue() == PreQueType.TYPE_REVIEW) {
				mReviewTextView.setVisibility(View.VISIBLE);
				mReviewTextView.setBackgroundResource(R.color.content_orange);
				nameTextView.setTextColor(nameTextView.getContext().getResources().getColor(R.color.content_orange));
				mReviewTextView.setText("复检问题");
			} else {
				if (mIsPreview && item.isCheck()) {
					mReviewTextView.setVisibility(View.VISIBLE);
					mReviewTextView.setBackgroundResource(R.color.color_three);
					mReviewTextView.setText("新发现");
				} else {
					mReviewTextView.setVisibility(View.GONE);
					nameTextView.setTextColor(Color.BLACK);
				}
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
	

	
	public interface CallBackListener {
		void onPhotoClick(int position);
		void onCheckedChangeListener(int position, boolean isChecked);
		void setContent(int position, String content);
		void onWifiClick(int position);
		void onDeleteItem(int position);
	}

//	public void addIssue(IssueItem issueItem) {
//		if (issueItem == null) {
//			return;
//		}
//		mDataList.add(issueItem);
//		notifyItemInserted(mDataList.size()-1);
//	}
	
	public void deleteIssue(int position) {
		IssueItem issueItem = mDataList.get(position);
		DymicIssue dymicIssue = DymicIssue.findById(DymicIssue.class, issueItem.getId());
		if (dymicIssue != null) {
			dymicIssue.delete();
		}
		mDataList.remove(position);
		if (mListener != null) {
			mListener.onDeleteItem(position);
		}
		notifyItemRemoved(position);
	}

	
}
