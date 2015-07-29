package com.lk.hotelcheck.activity.hotel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.checkIssue.CheckHotelIssueActivity;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.DrawUtil;

import common.Constance;
import common.Constance.CheckDataType;

public class HotelIssueFragment extends Fragment {

	private Context mContext;
	private View mRootView;
	private Hotel mHotel;
	private int mPosition;
	private static final int VIEW_TYPE_SUBLIST = 10087;
	private static final int  VIEW_TYPE_NORMAL = 10088;
	private int mCurrentCheckDataPosition;
	private CheckDataAdapter mAdapter;
	private RecyclerView mExpandableListView;
	private LinearLayoutManager mLayoutManager;

	public static HotelIssueFragment newInstance(int position) {
		HotelIssueFragment fragment = new HotelIssueFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constance.IntentKey.INTENT_KEY_POSITION, position);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = -1;
		if (getArguments() != null) {
			mPosition = getArguments().getInt(
					Constance.IntentKey.INTENT_KEY_POSITION);
			mHotel = DataManager.getInstance().getHotel(mPosition);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_issue, container,
					false);
			init();
		}
		return mRootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null && mAdapter.getItemCount() > 0) {
			mAdapter.notifyItemChanged(mCurrentCheckDataPosition);
		}
	}

	private void init() {
		mExpandableListView = (RecyclerView) mRootView
				.findViewById(R.id.rv_issue);
		mAdapter = new CheckDataAdapter();
		mLayoutManager = new LinearLayoutManager(mContext);
		mExpandableListView.setHasFixedSize(false);
		mExpandableListView.setLayoutManager(mLayoutManager);
		mExpandableListView.setAdapter(mAdapter);
	}

	

	class CheckDataAdapter extends
			RecyclerView.Adapter<RecyclerView.ViewHolder> {

		@Override
		public int getItemCount() {
			return mHotel.getCheckDataCount();
		}

		@Override
		public void onBindViewHolder(ViewHolder arg0, int arg1) {
			CheckData data = mHotel.getCheckData(arg1);
			if (arg0 instanceof CheckDataItemHolder) {
				((CheckDataItemHolder) arg0).setData(data, arg1);
			} else if (arg0 instanceof MultiCheckDataItemHolder) {
				((MultiCheckDataItemHolder) arg0).setData(data, arg1);
			}

		}

		@Override
		public int getItemViewType(int position) {
			CheckData data = mHotel.getCheckData(position);
			if (data.getId() == Constance.CHECK_DATA_ID_PASSWAY || data.getId() == Constance.CHECK_DATA_ID_ROOM) {
				return VIEW_TYPE_SUBLIST;
			} else {
				return VIEW_TYPE_NORMAL;
			}

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			ViewHolder viewHolder = null;
			if (arg1 == VIEW_TYPE_SUBLIST) {
				View floorItemView = LayoutInflater.from(arg0.getContext()).inflate(
						R.layout.check_data_multi_item, arg0, false);
				viewHolder = new MultiCheckDataItemHolder(floorItemView);
				return viewHolder;
			} else {
				View itemView = LayoutInflater.from(arg0.getContext()).inflate(
						R.layout.listview_hotel_issue_item, arg0, false);
				viewHolder = new CheckDataItemHolder(itemView);
				return viewHolder;
			}
		}

		class CheckDataItemHolder extends ViewHolder {
			private View mView;
			private TextView mNameTextView;
			private TextView mNumberTextView;

			public CheckDataItemHolder(View itemView) {
				super(itemView);
				mView = itemView;
				if (itemView != null) {
					mNameTextView = (TextView) itemView
							.findViewById(R.id.tv_name);
					mNumberTextView = (TextView) itemView
							.findViewById(R.id.tv_number);
				}
			}

			public void setData(CheckData checkData, int checkDataPosition) {
				if (checkData != null) {
					mNameTextView.setText(checkData.getName());
					int checkedIssueCount = checkData.getCheckedIssueCount();
					if (checkedIssueCount <= 0 ) {
						mNumberTextView.setVisibility(View.GONE);
					} else {
						mNumberTextView.setVisibility(View.VISIBLE);
						mNumberTextView.setText("" + checkedIssueCount);
					}

				}
				mView.setTag(checkDataPosition);
				mView.setOnClickListener(mOnClickListener);

			}
			
			public void setCheckNumber(int count) {
				mNumberTextView.setVisibility(View.VISIBLE);
				mNumberTextView.setText("" + count);
			}

			private OnClickListener mOnClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					int checkDataPosition = (Integer) v.getTag();
					CheckHotelIssueActivity.gotoCheckHotelIssue(mContext,
							mPosition, checkDataPosition, Constance.CheckDataType.TYPE_NORMAL);
					mCurrentCheckDataPosition = checkDataPosition;
				}
			};

		}

		
	}
	
	class MultiCheckDataItemHolder extends ViewHolder {
		private View mView;
		private TextView mNameTextView;
		private AddItemAdapter mSubFloorAdapter;
		private RecyclerView mGridView;
		private TextView mNumberTextView;
		private LayoutManager mSubLayoutManager;
		public MultiCheckDataItemHolder(View itemView) {
			super(itemView);
			mView = itemView;
			if (itemView != null) {
				mNameTextView = (TextView) itemView
						.findViewById(R.id.tv_name);
				mNumberTextView = (TextView) itemView
						.findViewById(R.id.tv_number);
				mGridView = (RecyclerView) itemView
						.findViewById(R.id.rv_sublist);
				mSubLayoutManager = new LinearLayoutManager(mContext,
						RecyclerView.HORIZONTAL, false);
					mGridView.setLayoutManager(mSubLayoutManager);
				
			}
		}

		public void setData(CheckData checkData, int checkDataPosition) {
			if (checkData == null) {
				return;
			}
			int checkedIssueCount = 0;
			if (checkData.getType() == CheckDataType.TYPE_ROOM) {
				checkedIssueCount = mHotel.getDymicRoomCount();
			} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
				checkedIssueCount = mHotel.getDymicPasswayCount();
			} else {
				checkedIssueCount = 0;
			}
			if (checkedIssueCount <= 0) {
				mNumberTextView.setVisibility(View.GONE);
			} else {
				mNumberTextView.setVisibility(View.VISIBLE);
				mNumberTextView.setText("" + checkedIssueCount);
			}
			mNameTextView.setText(checkData.getName());
			if (checkData.getId() == Constance.CHECK_DATA_ID_ROOM) {
				mSubFloorAdapter = new AddItemAdapter(Constance.CheckDataType.TYPE_ROOM, checkDataPosition);
			} else {
				mSubFloorAdapter = new AddItemAdapter(Constance.CheckDataType.TYPE_PASSWAY, checkDataPosition);
			}
			
			mGridView.setAdapter(mSubFloorAdapter);
			mView.setTag(checkDataPosition);
		}

		public void setCheckNumber(int count) {
			mNumberTextView.setVisibility(View.VISIBLE);
			mNumberTextView.setText("" + count);
		}
	}

	class AddItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

		private AlertDialog mAlertDialog;
		private EditText mEditText;
		private int mType;
		private int mCheckDataPosition;

		public AddItemAdapter(int type, int checkDataPosition) {
			super();
			this.mType = type;
			mCheckDataPosition = checkDataPosition;
		}

		
		
		@Override
		public long getItemId(int position) {
			return 0;
		}

		
		private OnClickListener mClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mHotel.isStatus()) {
					Toast.makeText(v.getContext(), "酒店已检查完成不能再修改", Toast.LENGTH_SHORT).show();
					return;
				}
				showDialog(mType);
//				switch (mCheckData.getId()) {
//				case Constance.CHECK_DATA_ID_FLOOR:
//					showDialog(TYPE_CORRIDOR);
//					break;
//				case Constance.CHECK_DATA_ID_ROOM:
//					showDialog(TYPE_ROOM);
//					break;
//				default:
//					break;
//				}
				
			}
		};

		private void showDialog(final int type) {
			if (mAlertDialog == null) {
				LayoutInflater factory = LayoutInflater.from(mContext);// 提示框
				View view = factory.inflate(R.layout.alert_input, null);// 这里必须是final的
				mEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
				mAlertDialog = new AlertDialog.Builder(mContext)
						.setView(view)
						.setPositiveButton(
								"确定",// 提示框的两个按钮
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String number = mEditText.getText()
												.toString();
										CheckData checkData = null;
										String name = null;
										int areaId = 0;
											switch (type) {
											case Constance.CheckDataType.TYPE_ROOM:
												if (TextUtils.isEmpty(number)) {
													Toast.makeText(mContext, "请输入房间名", Toast.LENGTH_SHORT).show();
													return;
												}
												name = "客房"+number;
												areaId = Math.abs(name.hashCode()+mHotel.getName().hashCode());
												if (mHotel.hasRoom(areaId)) {
													Toast.makeText(mContext, "房间名重复，请修改", Toast.LENGTH_SHORT).show();
													return;
												} else {
													checkData = DataManager.getInstance().createRoomCheckData();
													checkData.setName(name);
													checkData.setType(Constance.CheckDataType.TYPE_ROOM);
												}
												break;
											case Constance.CheckDataType.TYPE_PASSWAY:
												if (TextUtils.isEmpty(number)) {
													Toast.makeText(mContext, "请输入走廊名", Toast.LENGTH_SHORT).show();
													return;
												}
												name = number+"楼走廊";
												areaId = Math.abs(name.hashCode()+mHotel.getName().hashCode());
												if (mHotel.hasPassway(areaId)) {
													Toast.makeText(mContext, "走廊名重复，请修改", Toast.LENGTH_SHORT).show();
												} else {
													checkData = DataManager.getInstance().createPasswayCheckData();
													checkData.setName(name);
													checkData.setType(Constance.CheckDataType.TYPE_PASSWAY);
												}
												break;
											default:
												break;
											}
											checkData.setId((long) areaId);
											checkData.setCheckId(mHotel.getCheckId());
											update(checkData);
											mEditText.setText("");
									}
								})
						.setNegativeButton(
								"取消",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mEditText.setText("");
									}
								}).create();
			}

			switch (type) {
			case Constance.CheckDataType.TYPE_ROOM:
				mAlertDialog.setTitle("请输入房间号");
				break;
			case Constance.CheckDataType.TYPE_PASSWAY:
				mAlertDialog.setTitle("请输入走廊所在楼层");
				break;
			default:
				break;
			}

			mAlertDialog.show();
		}

		public void update(CheckData checkData) {
			switch (mType) {
			case Constance.CheckDataType.TYPE_ROOM:
				mHotel.addRoom(checkData);
				break;
			case Constance.CheckDataType.TYPE_PASSWAY:
				mHotel.addPassway(checkData);
				break;
			default:
				break;
			}
			
			mAdapter.notifyItemChanged(mCheckDataPosition);
		}
		
		public void removeSubCheckedData(int position) {
			switch (mType) {
			case Constance.CheckDataType.TYPE_ROOM:
				mHotel.removeDymicRoom(position);
				break;
			case Constance.CheckDataType.TYPE_PASSWAY:
				mHotel.removeDymicPassway(position);
				break;
			default:
				break;
			}
			mAdapter.notifyItemChanged(mCheckDataPosition);
		}

		@Override
		public int getItemCount() {
			int count = 0;
			switch (mType) {
			case Constance.CheckDataType.TYPE_ROOM:
				count = mHotel.getDymicRoomCount() + 1;
				break;
			case Constance.CheckDataType.TYPE_PASSWAY:
				count = mHotel.getDymicPasswayCount() + 1;
				break;
			default:
				break;
			}
			return count;
		}
		
		

		@Override
		public void onBindViewHolder(ViewHolder viewHolder, int arg1) {
			if (arg1 == 0) {
				((ItemViewHolder) viewHolder).initAdd();
				viewHolder.itemView.setOnClickListener(mClickListener);
			} else {
				CheckData checkData = null;
				switch (mType) {
				case Constance.CheckDataType.TYPE_ROOM:
					checkData = mHotel.getDymicRoomData(arg1-1);
					break;
				case Constance.CheckDataType.TYPE_PASSWAY:
					checkData = mHotel.getDymicPasswayData(arg1-1);
					break;
				default:
					break;
				}
				((ItemViewHolder) viewHolder).setData(checkData, arg1);
			}

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			Button imageView = new Button(arg0.getContext());
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
					DrawUtil.dip2px(70), DrawUtil.dip2px(70));
			params.setMargins(DrawUtil.dip2px(10), DrawUtil.dip2px(10),
					DrawUtil.dip2px(10), DrawUtil.dip2px(10));
			imageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			imageView.setLayoutParams(params);
			ItemViewHolder holder = new ItemViewHolder(imageView);
			return holder;
		}

		class ItemViewHolder extends RecyclerView.ViewHolder {
			private Button mButton;
			public ItemViewHolder(View itemView) {
				super(itemView);
				if (itemView instanceof Button) {
					mButton = (Button) itemView;

				}
			}


			public void setData(CheckData subCheckData, int positon) {
				mButton.setTag(positon);
				mButton.setText(subCheckData.getName());
				mButton.setBackgroundColor(getResources().getColor(
						R.color.title_blue));
				mButton.setOnClickListener(onSubDataClickListener);
				mButton.setOnLongClickListener(longClickListener);
			}

			private OnClickListener onSubDataClickListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					CheckHotelIssueActivity.gotoCheckHotelIssue(mContext,
							mPosition, position - 1 , mType);
					mCurrentCheckDataPosition = mCheckDataPosition;
				}
			};
			
			private OnLongClickListener longClickListener = new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					int position = (Integer) v.getTag();
//					CheckData checkData = DataManager.getInstance().getHotel(mPosition).getCheckData(mCheckDataPosition).getSubCheckData(position-1);
					CheckData checkData = null;
					switch (mType) {
					case Constance.CheckDataType.TYPE_ROOM:
						checkData = mHotel.getDymicRoomData(position-1);
						break;
					case Constance.CheckDataType.TYPE_PASSWAY:
						checkData = mHotel.getDymicPasswayData(position-1);
						break;
					default:
						break;
					}
					if (checkData != null && checkData.getCheckedIssueCount() >0 ) {
						Toast.makeText(v.getContext(), "已登记问题，不能删除", Toast.LENGTH_SHORT).show();
						return true;
					}
					showDeleteAlert(v.getContext(), position-1, ((Button)v).getText().toString());
					return true;
				}
			};
			
			public void initAdd() {
				mButton.setBackgroundResource(R.drawable.plus);
				mButton.setText("");
			}
			
			private void showDeleteAlert(Context context, final int position, String title) {
				AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setTitle("确认要删除"+title)
				.setPositiveButton(
								"确定",// 提示框的两个按钮
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										removeSubCheckedData(position);
									}
								})
						.setNegativeButton(
								"取消",
								new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
				.create();
				alertDialog.show();
			}
		}
	}

}
