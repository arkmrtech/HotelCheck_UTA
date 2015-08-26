package com.lk.hotelcheck.activity.hotel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.photochosen.PhotoChosenActivity;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.DrawUtil;
import common.Constance;
import common.Constance.CheckDataType;
import common.Constance.CheckType;
import common.Constance.PreQueType;

public class HotelReportFragment extends Fragment{
	
	public static HotelReportFragment newInstance(int position) {
		HotelReportFragment fragment = new HotelReportFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constance.IntentKey.INTENT_KEY_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	private Hotel mHotel;
	private int mPosition;
	private Activity mActivity;
	private View mRootView;
	private IssueListAdapter mAdapter;
	private TextView mUserNameTextView;
	private TextView mCheckDateTextView;
	private TextView mRoomNumberTextView;
	private TextView mRoomInUseNumberTextView;
	private TextView mRoomCheckedNumberTextView;
	private TextView mIssueCountTextView;
	private TextView mGNumberTextView;
	private ViewGroup mReviewGroup;
	private TextView mFixedTextView;
	private TextView mFixingTextView;
	private TextView mNewTextView;
	private ExpandableListView mExpandableListView;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint() && mAdapter != null) {
			mAdapter.notifyDataSetChanged();
			initInfoData();
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(Constance.IntentKey.INTENT_KEY_POSITION);
		mHotel = DataManager.getInstance().getHotel(mPosition);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_report, container,false);
			init(mRootView);
		}
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		return mRootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
		expanAll();
	}
	
	
	public void refreshInfo() {
		initInfoData();
	}
	
	private void initInfoData() {
		if (mHotel != null) {
			mUserNameTextView.setText(DataManager.getInstance().getUserName());
			mCheckDateTextView.setText(mHotel.getCheckDate());
			mRoomNumberTextView.setText(""+mHotel.getRoomCount());
			mRoomInUseNumberTextView.setText(""+mHotel.getRoomInUseCount());
			mRoomCheckedNumberTextView.setText(""+mHotel.getRoomHadCheckedCount());
			mIssueCountTextView.setText(""+mHotel.getIssueCount());
			mGNumberTextView.setText(mHotel.getGuardianNumber());
			if (mHotel.getCheckType() == CheckType.CHECK_TYPE_NEW) {
				mReviewGroup.setVisibility(View.GONE);
			} else {
				mReviewGroup.setVisibility(View.VISIBLE);
				mFixedTextView.setText(""+mHotel.getFixedIssueCount());
				mFixingTextView.setText(""+mHotel.getFixingIssueCount());
				mNewTextView.setText(""+mHotel.getNewIssueCount());
			}
		}
	}
	
	
	private void init(View view) {
		mExpandableListView = (ExpandableListView) view.findViewById(R.id.elv_report);
		View headerView = LayoutInflater.from(view.getContext()).inflate(R.layout.listview_report_header, null);
		mUserNameTextView = (TextView) headerView.findViewById(R.id.tv_check_men);
		mCheckDateTextView = (TextView) headerView.findViewById(R.id.tv_check_date);
		mRoomNumberTextView = (TextView) headerView.findViewById(R.id.tv_room_number);
		mRoomInUseNumberTextView = (TextView) headerView.findViewById(R.id.tv_room_in_use);
		mRoomCheckedNumberTextView = (TextView) headerView.findViewById(R.id.tv_checked_count);
		mIssueCountTextView = (TextView) headerView.findViewById(R.id.tv_issue_number);
		mGNumberTextView = (TextView) headerView.findViewById(R.id.tv_guardian_number);
		mReviewGroup = (ViewGroup) headerView.findViewById(R.id.ll_review);
		mFixingTextView = (TextView) headerView.findViewById(R.id.tv_issue_fixing);
		mFixedTextView = (TextView) headerView.findViewById(R.id.tv_issue_fixed);
		mNewTextView = (TextView) headerView.findViewById(R.id.tv_issue_new);
		initInfoData();
		
		
		mExpandableListView.addHeaderView(headerView);
		mAdapter = new IssueListAdapter();
		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				CheckData checkData = mHotel.getCheckData(groupPosition);
				IssueItem issueItem = null;
				if (checkData.getType() == CheckDataType.TYPE_ROOM) {
					issueItem = mHotel.getDymicRoomCheckedIssue(childPosition);
				} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
					issueItem = mHotel.getDymicPasswayCheckedIssue(childPosition);
				} else {
					issueItem = checkData.getCheckedIssue(childPosition);
				}
				
				if (issueItem != null ) {
					int issueImageCount = 0;
					if (checkData.getType() == CheckDataType.TYPE_ROOM) {
						issueImageCount = mHotel.getDymicRoomCheckedIssueImageCount(issueItem.getId());
					} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
						issueImageCount = mHotel.getDymicPasswayCheckedIssueImageCount(issueItem.getId());
					} else {
						issueImageCount = issueItem.getImageCount();
					}
					if (issueImageCount > 0) {
						PhotoChosenActivity.gotoPhotoChosen(mActivity, mPosition,groupPosition,childPosition);
					}
				}
				return false;
			}
		});
		expanAll();
	}
	
	
	private void expanAll(){
		for (int i = 0; i < mAdapter.getGroupCount(); i++) {
			if (mAdapter.getChildrenCount(i) > 0) {
				mExpandableListView.expandGroup(i);
			}
		}
	}
	
	class IssueListAdapter extends BaseExpandableListAdapter {

		

		public IssueListAdapter() {
			super();
			
		}

		@Override
		public int getGroupCount() {
			return mHotel == null ? 0 : mHotel.getCheckDataCount();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			int count = 0;
			CheckData checkData = mHotel.getCheckData(groupPosition);
			if (checkData.getType() == CheckDataType.TYPE_ROOM) {
				count = mHotel.getDymicRoomCheckedIssueCount();
			} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
				count = mHotel.getDymicPasswayCheckedIssueCount();
			} else {
				count = checkData.getCheckedIssueCount();
			}
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
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_report_group_item, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.issueCountTextView = (TextView) convertView.findViewById(R.id.tv_number);
				viewHolder.colorImageView = (ImageView) convertView.findViewById(R.id.iv_color);
				viewHolder.mNumberView = convertView.findViewById(R.id.rl_number);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			switch (groupPosition) {
			case 0:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_one));
				break;
			case 1:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_two));
				break;
			case 2:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_three));
				break;
			case 3:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_four));
				break;
			case 4:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_five));
				break;
			case 5:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_six));
				break;
			default:
				viewHolder.colorImageView.setBackgroundColor(getResources().getColor(R.color.color_seven));
				break;
			}
			CheckData checkData = mHotel.getCheckData(groupPosition);
			viewHolder.nameTextView.setText(checkData.getName());
			int count = getChildrenCount(groupPosition);
			if (count <= 0) {
				viewHolder.mNumberView.setVisibility(View.GONE);
			} else {
				viewHolder.mNumberView.setVisibility(View.VISIBLE);
				viewHolder.issueCountTextView.setText(""+count);
			}
			ExpandableListView mExpandableListView = (ExpandableListView) parent;
		    mExpandableListView.expandGroup(groupPosition);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_report_check_issue_item, parent,false);
				convertView.setPadding(DrawUtil.dip2px(10), 0, DrawUtil.dip2px(10), 0);
				viewHolder = new ViewHolder();
				viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.flagTextView = (TextView) convertView.findViewById(R.id.tv_flag);
				viewHolder.percentTextView = (TextView) convertView.findViewById(R.id.tv_percent);
				viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.tv_status);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			CheckData checkData = mHotel.getCheckData(groupPosition);
			IssueItem issueItem = null;
			if (checkData.getType() == CheckDataType.TYPE_ROOM) {
				issueItem = mHotel.getDymicRoomCheckedIssue(childPosition);
			} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
				issueItem = mHotel.getDymicPasswayCheckedIssue(childPosition);
			} else {
				issueItem = checkData.getCheckedIssue(childPosition);
			}
			int issueImageCount = 0;
			if (checkData.getType() == CheckDataType.TYPE_ROOM) {
				issueImageCount = mHotel.getDymicRoomCheckedIssueImageCount(issueItem.getId());
			} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
				issueImageCount = mHotel.getDymicPasswayCheckedIssueImageCount(issueItem.getId());
			} else {
				issueImageCount = issueItem.getImageCount();
			}
			
			if (issueImageCount > 0) {
				viewHolder.flagTextView.setText("查看图片");
			} else {
				viewHolder.flagTextView.setText("");
			}
			viewHolder.nameTextView.setText(issueItem.getName());
			if (issueItem.getIsPreQue() == PreQueType.TYPE_REVIEW) {
				if (issueItem.isCheck()) {
					viewHolder.statusTextView.setText("未整改/整改中");
					viewHolder.statusTextView.setTextColor(getResources()
							.getColor(R.color.content_orange));
				} else {
					viewHolder.statusTextView.setText("已整改");
					viewHolder.statusTextView.setTextColor(getResources()
							.getColor(R.color.color_two));
				}
			} else {
				if (mHotel.getCheckType() == CheckType.CHECK_TYPE_REVIEW) {
					viewHolder.statusTextView.setText("新发现");
					viewHolder.statusTextView.setTextColor(getResources()
							.getColor(R.color.color_three));
				} else {
					viewHolder.statusTextView.setText("");
					viewHolder.statusTextView.setTextColor(getResources()
							.getColor(R.color.white));
				}
			}
			if (checkData.getType() == CheckDataType.TYPE_ROOM) {
				viewHolder.percentTextView.setText(mHotel
						.getRoomIssuePercent(issueItem.getId()));
				viewHolder.percentTextView.setVisibility(View.VISIBLE);
			} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
				viewHolder.percentTextView.setText(mHotel
						.getPasswayIssuePercent(issueItem.getId()));
				viewHolder.percentTextView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.percentTextView.setVisibility(View.GONE);
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		
		class ViewHolder {
			private TextView nameTextView;
			private TextView issueCountTextView;
			private TextView percentTextView;
			private TextView flagTextView;
			private ImageView colorImageView;
			private View mNumberView;
			private TextView statusTextView;
		}
	}
}
