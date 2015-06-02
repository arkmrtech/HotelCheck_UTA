package com.lk.hotelcheck.activity.checkIssue;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class HotelIssueAdapter extends RecyclerView.Adapter<ViewHolder>{
	
	private List<IssueItem> dataList;
	private CallBackListener listener;
	private AlertDialog mAlertDialog;
	private boolean mIsChecked;
	
	
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
	public void onBindViewHolder(ViewHolder arg0, int arg1) {
		IssueItem item = dataList.get(arg1);
		((ItemViewHolder)arg0).setData(item, arg1);
	}

	@Override
	public ViewHolder onCreateViewHolder(
			ViewGroup arg0, int arg1) {
		View convertView = LayoutInflater.from(arg0.getContext())
				.inflate(R.layout.listview_hotel_check_issue_item, arg0, false);
		ItemViewHolder viewHolder = new ItemViewHolder(convertView);
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
	
	private void showDialog(final int position, Context context){
		if (mAlertDialog == null) {
			LayoutInflater factory = LayoutInflater.from(context);// 提示框
			View view = factory.inflate(R.layout.alert_dialog_edit, null);// 这里必须是final的
			final EditText alertEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
			alertEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			IssueItem item = dataList.get(position);
			if (!TextUtils.isEmpty(item.getContent())) {
				int index = alertEditText.getSelectionStart();
				alertEditText.getText().insert(index, item.getContent());
			}
			mAlertDialog = new AlertDialog.Builder(context)
					 .setTitle("问题描述")//提示框标题
					.setView(view)
					.setPositiveButton("确定",// 提示框的两个按钮
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String content = alertEditText.getText().toString();
									listener.setContent(position, content);
									
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
		mAlertDialog.show();
	}
	
	class ItemViewHolder  extends RecyclerView.ViewHolder{
		
		private TextView nameTextView;
		private ImageView canmerImageView;
		private TextView mNumberTextView;
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
			canmerImageView.setOnClickListener(photoOnClickListener);
			mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
			mCheckBox.setChecked(item.isCheck());
			if (item.getImageCount() <= 0) {
				mNumberTextView.setVisibility(View.GONE);
			} else {
				mNumberTextView.setVisibility(View.VISIBLE);
				mNumberTextView.setText(""+item.getImageCount());
				if (mCheckBox.isEnabled()) {
					mCheckBox.setEnabled(false);
				}
			}
			if (!TextUtils.isEmpty(item.getContent())) {
				if (mCheckBox.isEnabled()) {
					mCheckBox.setEnabled(false);
				}
			}
			if (mIsChecked) {
				mCheckBox.setEnabled(false);
			} else {
				mCheckBox.setEnabled(true);
			}
		}
		
		public void setChecked(int position) {
			mCheckBox.setChecked(true);
			mCheckBox.setEnabled(true);
		}
		
	}
	
	public interface CallBackListener {
		void onPhotoClick(int position);
		void onCheckedChangeListener(int position, boolean isChecked);
		void setContent(int position, String content);
	}

	
}
