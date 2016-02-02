package com.lk.hotelcheck.activity.hotel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.photochosen.PhotoChosenActivity;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.DrawUtil;

import common.Constance;
import common.Constance.CheckDataType;
import common.Constance.CheckType;
import common.Constance.PreQueType;

public class HotelReportFragment extends BaseHotelFragment{
	
	public static HotelReportFragment newInstance(int position) {
		HotelReportFragment fragment = new HotelReportFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constance.IntentKey.INTENT_KEY_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}
	
//	private Hotel mHotel;
	private int mPosition;
//	private Activity mActivity;
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
	private TextView mUnFixedTextView;
	private TextView mNewTextView;
	private ExpandableListView mExpandableListView;
	private String[] mStateItems = new String[]{"未整改","整改中","已整改"};
	private int[] mStateValues = new int[]{IssueItem.REFORM_STATE_UN_FIX, IssueItem.REFORM_STATE_FIXING, IssueItem.REFORM_STATE_FIXED};
	private int mChoosePosition = -1;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint() && mAdapter != null) {
			mAdapter.notifyDataSetChanged();
			initInfoData();
		}
	}
	
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		this.mActivity = activity;
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(Constance.IntentKey.INTENT_KEY_POSITION);
		mHotel = DataManager.getInstance().getHotel(mPosition);
//		EventBus.getDefault().register(this);
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
	
//	@Subscribe
//	public void onEvent(MessageEvent event) {
//		if (event.getMessageType() == MessageEvent.MESSAGE_UPDATE_HOTEL_DATA) {
////			mHotel = DataManager.getInstance().getHotel(mPosition);
//		}
//	};

	
	
	public void refreshInfo() {
		initInfoData();
	}
	
	private void initInfoData() {
		if (mHotel != null) {
			mUserNameTextView.setText(DataManager.getInstance().getUserName());
			mCheckDateTextView.setText(mHotel.getCheckDate());
			mRoomNumberTextView.setText(""+mHotel.getRoomCount()+"间");
			mRoomInUseNumberTextView.setText(""+mHotel.getRoomInUseCount()+"间");
			mRoomCheckedNumberTextView.setText(""+mHotel.getRoomHadCheckedCount()+"间");
			mIssueCountTextView.setText(""+mHotel.getIssueCount()+"个");
			mGNumberTextView.setText(mHotel.getGuardianNumber());
			if (mHotel.getCheckType() == CheckType.CHECK_TYPE_NEW) {
				mReviewGroup.setVisibility(View.GONE);
			} else {
				mReviewGroup.setVisibility(View.VISIBLE);
				mFixedTextView.setText(""+mHotel.getFixIssueCount(IssueItem.REFORM_STATE_FIXED)+"个");
				mFixingTextView.setText(""+mHotel.getFixIssueCount(IssueItem.REFORM_STATE_FIXING)+"个");
				mNewTextView.setText(""+mHotel.getNewIssueCount()+"个");
				mUnFixedTextView.setText(""+mHotel.getFixIssueCount(IssueItem.REFORM_STATE_UN_FIX)+"个");
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
		mUnFixedTextView = (TextView) headerView.findViewById(R.id.tv_issue_unfix);
		mNewTextView = (TextView) headerView.findViewById(R.id.tv_issue_new);
		initInfoData();
		
		
		mExpandableListView.addHeaderView(headerView);
		mAdapter = new IssueListAdapter();
		mExpandableListView.setAdapter(mAdapter);
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
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_report_group_item, parent,false);
				viewHolder = new ViewHolder();
				viewHolder.mNameTextView = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.mIssueCountTextView = (TextView) convertView.findViewById(R.id.tv_number);
				viewHolder.mColorImageView = (ImageView) convertView.findViewById(R.id.iv_color);
				viewHolder.mNumberView = convertView.findViewById(R.id.rl_number);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			switch (groupPosition) {
			case 0:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_one));
				break;
			case 1:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_two));
				break;
			case 2:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_three));
				break;
			case 3:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_four));
				break;
			case 4:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_five));
				break;
			case 5:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_six));
				break;
			default:
				viewHolder.mColorImageView.setBackgroundColor(getResources().getColor(R.color.color_seven));
				break;
			}
			CheckData checkData = mHotel.getCheckData(groupPosition);
			viewHolder.mNameTextView.setText(checkData.getName());
			int count = getChildrenCount(groupPosition);
			if (count <= 0) {
				viewHolder.mNumberView.setVisibility(View.GONE);
			} else {
				viewHolder.mNumberView.setVisibility(View.VISIBLE);
				viewHolder.mIssueCountTextView.setText(""+count);
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
				convertView.setPadding(DrawUtil.dip2px(10), 0, DrawUtil.dip2px(2), 0);
				viewHolder = new ViewHolder();
				viewHolder.mNameTextView = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.mFlagTextView = (TextView) convertView.findViewById(R.id.tv_flag);
				viewHolder.mPercentTextView = (TextView) convertView.findViewById(R.id.tv_percent);
				viewHolder.mStatusTextView = (TextView) convertView.findViewById(R.id.tv_status);
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
				viewHolder.mFlagTextView.setText("查看图片");
				viewHolder.mFlagTextView.setTag(R.id.tv_name, groupPosition);
				viewHolder.mFlagTextView.setTag(R.id.tv_flag, childPosition);
				viewHolder.mFlagTextView.setOnClickListener(mPhotoClickListener);
				viewHolder.mFlagTextView.setClickable(true);
			} else {
				viewHolder.mFlagTextView.setText("");
				viewHolder.mFlagTextView.setClickable(false);
			}
			viewHolder.mNameTextView.setText(issueItem.getName());
			if (issueItem.getPreQueType() == PreQueType.TYPE_REVIEW) {
				viewHolder.mStatusTextView.setTag(R.id.tv_name, groupPosition);
				viewHolder.mStatusTextView.setTag(R.id.tv_flag, childPosition);
				if (!mHotel.isStatus()) {
					viewHolder.mStatusTextView.setOnClickListener(mReformStateClickListener);
				} else {
					viewHolder.mStatusTextView.setOnClickListener(null);
				}
				viewHolder.mStatusTextView.setVisibility(View.VISIBLE);
				switch (issueItem.getReformState()) {
				case IssueItem.REFORM_STATE_FIXED:
					viewHolder.mStatusTextView.setText("已整改");
					viewHolder.mStatusTextView.setTextColor(Color.parseColor("#009900"));
					break;
				case IssueItem.REFORM_STATE_UN_FIX:
					viewHolder.mStatusTextView.setText("未整改");
					viewHolder.mStatusTextView.setTextColor(Color.parseColor("#cc0000"));
					break;
				case IssueItem.REFORM_STATE_FIXING:
					viewHolder.mStatusTextView.setText("整改中");
					viewHolder.mStatusTextView.setTextColor(Color.parseColor("#ff9c00"));
					break;
				default:
					viewHolder.mStatusTextView.setText("未整改/整改中");
					viewHolder.mStatusTextView.setTextColor(Color.parseColor("#666666"));
					break;
				}
			} else {
				viewHolder.mStatusTextView.setOnClickListener(null);
				if (mHotel.getCheckType() == CheckType.CHECK_TYPE_REVIEW) {
					viewHolder.mStatusTextView.setVisibility(View.VISIBLE);
					viewHolder.mStatusTextView.setText("新发现");
					viewHolder.mStatusTextView.setTextColor(Color.parseColor("#0e99dd"));
				} else {
					viewHolder.mStatusTextView.setVisibility(View.GONE);
					viewHolder.mStatusTextView.setText("");
					viewHolder.mStatusTextView.setTextColor(getResources()
							.getColor(R.color.white));
				}
			}
			if (checkData.getType() == CheckDataType.TYPE_ROOM 
					|| checkData.getType() == CheckDataType.TYPE_PASSWAY) {
				if (issueItem.isCheck() && issueItem.getReformState() != IssueItem.REFORM_STATE_FIXED) {
					viewHolder.mPercentTextView.setVisibility(View.VISIBLE);
					viewHolder.mPercentTextView.setText(mHotel
								.getDymicAreaCheckedIssuePercent(checkData.getType(), issueItem.getId()));
				} else {
					viewHolder.mPercentTextView.setVisibility(View.GONE);
				}
			} else {
				viewHolder.mPercentTextView.setVisibility(View.GONE);
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private OnClickListener mPhotoClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int groupPosition = (Integer) v.getTag(R.id.tv_name);
				int childPosition = (Integer) v.getTag(R.id.tv_flag);
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
						PhotoChosenActivity.gotoPhotoChosen(mContext, mPosition,groupPosition,childPosition);
					}
				}
			}
		};
		
		private OnClickListener mReformStateClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final int groupPosition = (Integer) v.getTag(R.id.tv_name);
				final int childPosition = (Integer) v.getTag(R.id.tv_flag);
				final CheckData checkData = mHotel.getCheckData(groupPosition);
				IssueItem issueItem = null;
				if (checkData.getType() == CheckDataType.TYPE_ROOM) {
					issueItem = mHotel.getDymicRoomCheckedIssue(childPosition);
				} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
					issueItem = mHotel.getDymicPasswayCheckedIssue(childPosition);
				} else {
					issueItem = checkData.getCheckedIssue(childPosition);
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());  
			    builder.setTitle("请选择该问题整改状态"); 
			    final IssueItem tempIssueItem = issueItem;
			    int position = -1;
			    switch (issueItem.getReformState()) {
				case IssueItem.REFORM_STATE_UN_FIX:
					position = 0;
					break;
				case IssueItem.REFORM_STATE_FIXING:
					position = 1;
					break;
				case IssueItem.REFORM_STATE_FIXED:
					position = 2;
					break;
				default:
					position = -1;
					break;
				}
			    builder.setSingleChoiceItems(mStateItems, position, new DialogInterface.OnClickListener() {  
			        @Override  
			        public void onClick(DialogInterface dialog, int which) {  
			            mChoosePosition = which;
			        }  
			    });  
			    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			          
			        @Override  
			        public void onClick(DialogInterface dialog, int which) {  
			        	if (mChoosePosition != -1) {
			        		tempIssueItem.setReformState(mStateValues[mChoosePosition]);
				        	initInfoData();
				        	DataManager.getInstance().saveIssueCheck(mHotel.getCheckId(), checkData.getId().intValue(), tempIssueItem.getId(), tempIssueItem.isCheck(), tempIssueItem.getReformState());
				        	mAdapter.notifyDataSetChanged();
				        	mChoosePosition = -1;
			        	}
			        }  
			    });  
			    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
			          
			        @Override  
			        public void onClick(DialogInterface dialog, int which) {  
			              
			        }  
			    });  
			    builder.create().show();
			}
		};
		
		
		class ViewHolder {
			private TextView mNameTextView;
			private TextView mIssueCountTextView;
			private TextView mPercentTextView;
			private TextView mFlagTextView;
			private ImageView mColorImageView;
			private View mNumberView;
			private TextView mStatusTextView;
		}
	}
}
