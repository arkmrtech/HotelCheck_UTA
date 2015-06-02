package com.lk.hotelcheck.activity.main;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.DrawUtil;

public class HotelListAdapter extends BaseExpandableListAdapter {

	
	@Override
	public int getGroupCount() {
		return 2;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = DataManager.getInstance().getHotelListCount(groupPosition);
		Log.d("lxk", "count = "+count +" groupPosition = "+groupPosition);
		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			TextView textView = new TextView(parent.getContext());
			textView.setPadding(DrawUtil.dip2px(10), DrawUtil.dip2px(10), 0,
					DrawUtil.dip2px(10));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			convertView = textView;
		}
		String title = null;
		Drawable likeDrawable = null;
		if (groupPosition == 0) {
			title = "未检查";
			((TextView) convertView).setTextColor(Color.RED);
			likeDrawable = parent.getContext().getResources()
					.getDrawable(R.drawable.warning);
		} else {
			title = "已检查";
			((TextView) convertView).setTextColor(parent.getContext()
					.getResources().getColor(R.color.title_blue));
			likeDrawable = parent.getContext().getResources()
					.getDrawable(R.drawable.right);
		}
		likeDrawable.setBounds(0, 0, likeDrawable.getMinimumWidth(),
				likeDrawable.getMinimumHeight());
		((TextView) convertView).setCompoundDrawables(likeDrawable, null, null,
				null);
		((TextView) convertView)
				.setCompoundDrawablePadding(DrawUtil.dip2px(10));
		((TextView) convertView).setText(title);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.listview_hotel_item, parent, false);
			viewHolder = new ChildViewHolder();
			viewHolder.mNameTextView = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.dataTextView = (TextView) convertView.findViewById(R.id.tv_data);
			viewHolder.dataImageView = (ImageView) convertView.findViewById(R.id.iv_data);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}
		Hotel hotel = DataManager.getInstance().getHotel(groupPosition,
				childPosition);
		if (hotel != null) {
			viewHolder.mNameTextView.setText(hotel.getName());
			if (hotel.isStatus()) {
				if (hotel.isDataStatus()) {
					viewHolder.dataTextView.setTextColor(parent.getContext().getResources().getColor(R.color.title_blue));
				} else {
					viewHolder.dataTextView.setTextColor(Color.GRAY);
				}
				if (hotel.isImageStatus()) {
					viewHolder.dataImageView.setImageResource(R.drawable.pic);
				} else {
					viewHolder.dataImageView.setImageResource(R.drawable.pic_gray);
				}
			}
		}
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
//	@Override
//	public int getGroupCount() {
//		return 2;
//	}
//
//	@Override
//	public int getChildrenCount(int groupPosition) {
//		return DataManager.getInstance().getHotelListCount(groupPosition);
//	}
//
//	@Override
//	public Object getGroup(int groupPosition) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Object getChild(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public long getGroupId(int groupPosition) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public long getChildId(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public boolean hasStableIds() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public View getGroupView(int groupPosition, boolean isExpanded,
//			View convertView, ViewGroup parent) {
//		if (convertView == null) {
//			TextView textView = new TextView(parent.getContext());
//			textView.setPadding(DrawUtil.dip2px(10), DrawUtil.dip2px(10), 0, DrawUtil.dip2px(10));
//			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//			textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//			convertView = textView;
//		}
//		String title = null;
//		Drawable likeDrawable = null;
//		if (groupPosition == 0) {
//			title = "未检查";
//			((TextView) convertView).setTextColor(Color.RED);
//			 likeDrawable = parent.getContext().getResources().getDrawable(R.drawable.warning);
//		} else {
//			title = "已检查";
//			((TextView) convertView).setTextColor(parent.getContext().getResources().getColor(R.color.title_blue));
//			likeDrawable = parent.getContext().getResources().getDrawable(R.drawable.right);
//		}
//		likeDrawable.setBounds(0, 0, likeDrawable.getMinimumWidth(),
//				likeDrawable.getMinimumHeight());
//		((TextView) convertView).setCompoundDrawables(likeDrawable, null, null, null);
//		((TextView) convertView).setCompoundDrawablePadding(DrawUtil.dip2px(10));
//		((TextView) convertView).setText(title);
//		return convertView;
//	}
//
//	@Override
//	public View getChildView(int groupPosition, int childPosition,
//			boolean isLastChild, View convertView, ViewGroup parent) {
//		ChildViewHolder viewHolder;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(parent.getContext()).inflate(
//					R.layout.listview_hotel_item, parent, false);
//			viewHolder = new ChildViewHolder();
//			viewHolder.mNameTextView = (TextView) convertView
//					.findViewById(R.id.tv_name);
//			viewHolder.dataTextView = (TextView) convertView.findViewById(R.id.tv_data);
//			viewHolder.dataImageView = (ImageView) convertView.findViewById(R.id.iv_data);
//			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ChildViewHolder) convertView.getTag();
//		}
//		Hotel hotel = DataManager.getInstance().getHotel(groupPosition,
//				childPosition);
//		if (hotel != null) {
//			viewHolder.mNameTextView.setText(hotel.getName());
//			if (hotel.isStatus()) {
//				if (hotel.isDataStatus()) {
//					viewHolder.dataTextView.setTextColor(parent.getContext().getResources().getColor(R.color.title_blue));
//				} else {
//					viewHolder.dataTextView.setTextColor(Color.GRAY);
//				}
//				if (hotel.isImageStatus()) {
//					viewHolder.dataImageView.setImageResource(R.drawable.pic);
//				} else {
//					viewHolder.dataImageView.setImageResource(R.drawable.pic_gray);
//				}
//			}
//		}
//		
//		return convertView;
//	}
//
//	@Override
//	public boolean isChildSelectable(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@Override
//	public boolean areAllItemsEnabled() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isEmpty() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onGroupExpanded(int groupPosition) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onGroupCollapsed(int groupPosition) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public long getCombinedChildId(long groupId, long childId) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public long getCombinedGroupId(long groupId) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	class HeaViewHolder {
		private TextView nameTextView;
	}

	class ChildViewHolder {
		private TextView mNameTextView;
		private TextView dataTextView;
		private ImageView dataImageView;

		public TextView getmNameTextView() {
			return mNameTextView;
		}

		public void setmNameTextView(TextView mNameTextView) {
			this.mNameTextView = mNameTextView;
		}
	}

	
}
