package com.lk.hotelcheck.activity.hotel;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.StringUtil;

import common.Constance;

public class HotelBaseInfoFragment extends Fragment {
	private Context mContext;
	private int mPosition;
	private Hotel mHotel;
	private View mRootView;
	private TextView mNameTextView;
	private TextView mAddressTextView;
	private TextView mPhoneTextView;
	private TextView memoTextView;
	private TextView managerTextView;
	private TextView managerTelTextView;
	private TextView mAreaTextView;
	private TextView mOpenDateTextView;
	private TextView mLastCheckedDateTextView;
	private TextView mRoomNumberTextView;
	private TextView mRoomCheckedNumberTextView;
	private TextView mFloorTextView;
	private TextView mCheckedDateTextView;
	private RelativeLayout mRoomNumberLayout;
	private RelativeLayout mRoomCheckedNumberLayout;
	private RelativeLayout mCheckedDateLayout;
	private RelativeLayout mFloorLayout;
	private DatePickerDialog mDatePickerDialog;
	private AlertDialog mAlertDialog;
	private static final int ALERT_DIALOG_ROOM_NUMBER = 1;
	private static final int ALERT_DIALOG_ROOM_CHECKED_NUMBER = 2;
	private static final int ALERT_DIALOG_FLOOR = 3;
	private EditText mAlertEditText;

	public static HotelBaseInfoFragment newInstance(int position) {
		HotelBaseInfoFragment fragment = new HotelBaseInfoFragment();
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
		if (getArguments() != null) {
			mPosition = getArguments().getInt(
					Constance.IntentKey.INTENT_KEY_POSITION, -1);
			mHotel = DataManager.getInstance().getHotel(mPosition);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_baseinfo, container,
					false);
			mNameTextView = (TextView) mRootView.findViewById(R.id.tv_name);
			mAddressTextView = (TextView) mRootView
					.findViewById(R.id.tv_address);
			mPhoneTextView = (TextView) mRootView.findViewById(R.id.tv_phone);
			memoTextView = (TextView) mRootView.findViewById(R.id.tv_memo);
			managerTextView = (TextView) mRootView.findViewById(R.id.tv_manager);
			managerTelTextView = (TextView) mRootView.findViewById(R.id.tv_mamager_tel);
			mAreaTextView = (TextView) mRootView.findViewById(R.id.tv_area_info);
			mOpenDateTextView = (TextView) mRootView
					.findViewById(R.id.tv_open_date);
			mLastCheckedDateTextView = (TextView) mRootView
					.findViewById(R.id.tv_last_checked_date);
			mRoomNumberTextView = (TextView) mRootView
					.findViewById(R.id.tv_room_number);
			mRoomCheckedNumberTextView = (TextView) mRootView
					.findViewById(R.id.tv_room_check_number);
			mFloorTextView = (TextView) mRootView
					.findViewById(R.id.tv_floor_number);
			mCheckedDateTextView = (TextView) mRootView
					.findViewById(R.id.tv_check_date);
			mRoomNumberLayout = (RelativeLayout) mRootView
					.findViewById(R.id.rl_room_number);
			mRoomCheckedNumberLayout = (RelativeLayout) mRootView
					.findViewById(R.id.rl_room_check_number);
			mCheckedDateLayout = (RelativeLayout) mRootView
					.findViewById(R.id.rl_check_date);
			mFloorLayout = (RelativeLayout) mRootView
					.findViewById(R.id.rl_floor_number);
			if (mHotel != null) {
				init();
			}
		}
		return mRootView;
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	

	private void init() {
		mNameTextView.setText(mHotel.getBrand()+mHotel.getName());
		mAddressTextView.setText(mHotel.getAddress());
		mPhoneTextView.setText("电话："+mHotel.getPhone());
		memoTextView.setText(mHotel.getMemo());
		mAreaTextView.setText("区总："+mHotel.getRegionalManager());
		if (!TextUtils.isEmpty(mHotel.getOpenDate())) {
			mOpenDateTextView.setText("开业时间：" + mHotel.getOpenDate());
		}
		if (!TextUtils.isEmpty(mHotel.getLastCheckedDate())) {
			mLastCheckedDateTextView.setText("上次检查：" + mHotel.getLastCheckedDate());
		}
		managerTextView.setText("店长:"+mHotel.getBranchManager());
		managerTelTextView.setText("店长电话:"+mHotel.getBranchManagerTele());
		mRoomNumberTextView.setText("" + mHotel.getRoomCount());
		mRoomCheckedNumberTextView.setText("" + mHotel.getRoomCheckedCount());
		if (!TextUtils.isEmpty(mHotel.getFloor())) {
			mFloorTextView.setText(mHotel.getFloor());
		}
		if (mHotel.getCheckDate() != null) {
			mCheckedDateTextView.setText(mHotel.getCheckDate());
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			String date = calendar.get(Calendar.YEAR) + "-"
					+ (calendar.get(Calendar.MONTH) + 1) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH);
			mHotel.setCheckDate(date);
			mCheckedDateTextView.setText(date);
		}
		
		
		if (!mHotel.isStatus()) {
			mFloorLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog(ALERT_DIALOG_FLOOR);
				}
			});
			mRoomNumberLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showDialog(ALERT_DIALOG_ROOM_NUMBER);
				}
			});
			mRoomCheckedNumberLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mHotel.getRoomCount() == 0) {
						Toast.makeText(mContext, "请先输入酒店房间数量", Toast.LENGTH_SHORT).show();
						return;
					}
					showDialog(ALERT_DIALOG_ROOM_CHECKED_NUMBER);
				}
			});

			mCheckedDateLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mDatePickerDialog == null) {
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(System.currentTimeMillis());
						mDatePickerDialog = new DatePickerDialog(mContext,
								null, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
										.get(Calendar.DAY_OF_MONTH));
					}
					//手动设置按钮  
					mDatePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息  
			                DatePicker datePicker = mDatePickerDialog.getDatePicker();  
			                int year = datePicker.getYear();  
			                int month = datePicker.getMonth();  
			                int day = datePicker.getDayOfMonth();  
			                String date = year + "-" + (month + 1)
									+ "-" + day;
							mCheckedDateTextView.setText(date);
							mHotel.setCheckDate(date);
			            }  
			        });  
			        //取消按钮，如果不需要直接不设置即可  
					mDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			            }  
			        }); 
					
					
					
					mDatePickerDialog.show();
				}
			});
		}
	}

	private void showDialog(final int type) {
		LayoutInflater factory = LayoutInflater.from(mContext);// 提示框
		View view = factory.inflate(R.layout.alert_dialog_edit, null);// 这里必须是final的
		mAlertEditText = (EditText) view.findViewById(R.id.et_content);// 获得输入框对象
		mAlertDialog = new AlertDialog.Builder(mContext)
				// .setTitle(title)//提示框标题
				.setView(view)
				.setPositiveButton("确定",// 提示框的两个按钮
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String number = mAlertEditText.getText()
										.toString();
								
								if (TextUtils.isEmpty(number)) {
									Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
									return;
								}
								switch (type) {
								case ALERT_DIALOG_ROOM_NUMBER:
									if (!StringUtil.isNumeric(number)) {
										Toast.makeText(mContext, "只能输入数字", Toast.LENGTH_SHORT).show();
										return;
									}
									mRoomNumberTextView.setText(number);
									mHotel.setRoomCount(Integer.valueOf(number));
									break;
								case ALERT_DIALOG_ROOM_CHECKED_NUMBER:
									if (!StringUtil.isNumeric(number)) {
										Toast.makeText(mContext, "只能输入数字", Toast.LENGTH_SHORT).show();
										return;
									}
									if (Integer.valueOf(number) > mHotel.getRoomCount()) {
										Toast.makeText(mContext, "在住房数不能大于房间数量", Toast.LENGTH_SHORT).show();
										return;
									}
									mRoomCheckedNumberTextView.setText(number);
									mHotel.setRoomCheckedCount(Integer
											.valueOf(number));
									break;
								case ALERT_DIALOG_FLOOR:
									mFloorTextView.setText(number);
									mHotel.setFloor(number);
									break;
								default:
									break;
								}
								mAlertEditText.setText("");
								// DataManager.getInstance().setHotel(mPosition,
								// mHotel);
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
		switch (type) {
		case ALERT_DIALOG_ROOM_NUMBER:
			mAlertDialog.setTitle("请输入房间数量");
			mAlertEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			break;
		case ALERT_DIALOG_ROOM_CHECKED_NUMBER:
			mAlertDialog.setTitle("请输入在住房数");
			mAlertEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			break;
		case ALERT_DIALOG_FLOOR:
			mAlertDialog.setTitle("请输入楼层范围");
			mAlertEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			break;
		default:
			break;
		}
		mAlertDialog.show();
	}

}
