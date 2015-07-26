package com.lk.hotelcheck.activity.photochosen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.LayoutParams;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.upload.UploadProxy;
import com.lk.hotelcheck.util.CustomBasePagerAdapter;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import common.Constance.IntentKey;

public class PhotoChosenActivity extends BaseActivity {

	public static void gotoPhotoChosen(Context context, int hotelPosition,
			int checkDataPosition, int issurePosition) {
		Intent intent = new Intent();
		intent.setClass(context, PhotoChosenActivity.class);
		intent.putExtra(IntentKey.INTENT_KEY_POSITION, hotelPosition);
		intent.putExtra(IntentKey.INTENT_KEY_CHECK_DATA_POSITION,
				checkDataPosition);
		intent.putExtra(IntentKey.INTENT_KEY_ISSUE_POSITION, issurePosition);
		context.startActivity(intent);
	}

	private Hotel mHotel;
	private int mHotelPosition;
	private int mCheckDataPosition;
	private int mIssuePosition;
//	private Button mUploadButton;
//	private TextView mUploadTextView;
	private static final int MENU_SELECT_ALL = 0X10086;
	private boolean mIsSelectAll = false;
	private HashMap<String, Boolean> mSelectedMap;
	private PhotoChosenAdapter mAdapter;
	private GridLayoutManager mLayoutManager;
	private RecyclerView mPhotoRecyclerView;
	private MenuItem mChoseAllItem;
	private static final int SPINNER_TYPE_NAME = 1;
	private static final int SPINNER_TYPE_ISSUE = 2;
	private SpinnerNameAdapter mCheckDataMenuAdapter;
	private SpinnerNameAdapter mIssueDataMenuAdapter;
	private View mDetailLayout;
//	private ImageView mDetailImageView;
	private ViewPager mDetailViewPager;
	private ImageView mDetailBackImageView;
	private ImageView mDetailDeleteImageView;
//	private CheckData mCheckData;
	private Toolbar mToolbar;
	private TextView mErrorTextView;
	private Spinner mNameSpinner;
	private Spinner mIssueSpinner;
	private boolean mIsFristTime;
	private ImageDetailAdapter mDetailAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_chosen);
		mSelectedMap = new HashMap<String, Boolean>();
//		mUploadButton = (Button) findViewById(R.id.btn_upload);
//		mUploadTextView = (TextView) findViewById(R.id.tv_upload);
		mDetailLayout = findViewById(R.id.rl_image_detail);
//		mDetailImageView = (ImageView) findViewById(R.id.iv_detail);
		mDetailViewPager = (ViewPager) findViewById(R.id.vp_detail);
		mDetailBackImageView = (ImageView) findViewById(R.id.iv_back);
		mDetailDeleteImageView = (ImageView) findViewById(R.id.iv_delete);
		mDetailBackImageView.setOnClickListener(mImageBackClickListener);
		mDetailDeleteImageView.setOnClickListener(mImageDeleteClickListener);
//		mUploadButton.setOnClickListener(mUploadClickListener);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mErrorTextView = (TextView) findViewById(R.id.tv_error_tips);
		initData();

	}
	
	private void initData() {
		mHotelPosition = getIntent().getIntExtra(IntentKey.INTENT_KEY_POSITION,
				-1);
		mCheckDataPosition = getIntent().getIntExtra(
				IntentKey.INTENT_KEY_CHECK_DATA_POSITION, -1);
		mIssuePosition = getIntent().getIntExtra(
				IntentKey.INTENT_KEY_ISSUE_POSITION, -1);
		mHotel = DataManager.getInstance().getHotel(mHotelPosition);
		mNameSpinner = (Spinner) findViewById(R.id.sp_name);
		mIssueSpinner = (Spinner) findViewById(R.id.sp_issue);
		if (mHotel != null) {
			mToolbar.setTitle(mHotel.getName());
			mToolbar.setNavigationIcon(R.drawable.back);
			mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
			setSupportActionBar(mToolbar);
			mCheckDataMenuAdapter = new SpinnerNameAdapter(SPINNER_TYPE_NAME);
			mIssueDataMenuAdapter = new SpinnerNameAdapter(SPINNER_TYPE_ISSUE);
			mNameSpinner.setAdapter(mCheckDataMenuAdapter);
			mIssueSpinner.setAdapter(mIssueDataMenuAdapter);
			mPhotoRecyclerView = (RecyclerView) findViewById(R.id.rv_photo);
			mLayoutManager = new GridLayoutManager(this, 2);
			mPhotoRecyclerView.setLayoutManager(mLayoutManager);
			mAdapter = new PhotoChosenAdapter();
			mPhotoRecyclerView.setAdapter(mAdapter);
			mDetailAdapter = new ImageDetailAdapter();
			mDetailViewPager.setAdapter(mDetailAdapter);
			if (mHotel.isStatus()) {
				mDetailDeleteImageView.setVisibility(View.GONE);
			}
//			updatePhotoData();
			mNameSpinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							mCheckDataPosition = position;
							mIssueDataMenuAdapter.notifyDataSetChanged();
							if (mIsFristTime) {
								mIssueSpinner.setSelection(mIssuePosition+1);
								mIsFristTime = false;
							} else {
								if (mIssueSpinner.getSelectedItemPosition() == 0) {
									updatePhotoData();
								} else {
									mIssueSpinner.setSelection(0);
								}
							}
//							mIssueSpinner.setSelection(0);
//							updatePhotoData();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

						}
					});

			mIssueSpinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							mIssuePosition = position;
							updatePhotoData();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

						}

					});
		}
		if (mCheckDataPosition != -1 && mIssuePosition != -1) {
			Log.d("lxk", "mCheckDataPosition = "+mCheckDataPosition +" mIssuePosition "+mIssuePosition);
			mIsFristTime = true;
			mNameSpinner.setSelection(mCheckDataPosition);
//			mIssueSpinner.setSelection(mIssuePosition+1);
		}
	}
	
	private void updatePhotoData() {
		List<ImageItem> data = null;
		Log.d("lxk", "mCheckDataPosition = "+mCheckDataPosition +" mIssuePosition "+mIssuePosition);
		if (mIssuePosition == 0) {
			data = mHotel.getCheckData(mCheckDataPosition).getAllCheckedImage();
		} else {
			data = mHotel.getCheckData(mCheckDataPosition)
						.getCheckedPointImageList(mIssuePosition-1);
		}
		
		mAdapter.updateList(data);
		mDetailAdapter.notifyDataSetChanged();
		if (mAdapter.getItemCount() == 0) {
			showError();
		} else {
			hideError();
		}
	}
	
	private void showError() {
		mDetailLayout.setVisibility(View.GONE);
		mPhotoRecyclerView.setVisibility(View.GONE);
		mErrorTextView.setVisibility(View.VISIBLE);
	}
	
	private void hideError() {
		mPhotoRecyclerView.setVisibility(View.VISIBLE);
		mErrorTextView.setVisibility(View.GONE);
	}

	private OnClickListener mImageBackClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mDetailLayout.getVisibility() == View.VISIBLE) {
				mDetailLayout.setVisibility(View.GONE);
			}
		}
	};
			
	private OnClickListener mUploadClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			if (mSelectedMap.size() > 0) {
//				Iterator<String> keys = mSelectedMap.keySet().iterator();
//				List<String> key = new ArrayList<String>();
//				while (keys.hasNext()) {
//					key.add(keys.next());
//				}
//				UploadActivity.gotoUpload(v.getContext(), mAdapter.getDataItem(0).getLocalImagePath());
//			}
			UploadProxy.addUploadTask(mHotel);
		}
	};		

	private OnClickListener mImageDeleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final int position = (Integer) v.getTag();
			AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
					.setTitle("确认删除这张图片？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ImageItem imageItem = mAdapter
											.getDataItem(position);
									if (mIssuePosition == 0) {
										mHotel.getCheckData(mCheckDataPosition).deleteCheckedIssueImage(imageItem);
									} else {
										mHotel.getCheckData(mCheckDataPosition).deleteCheckedIssueImage(mIssuePosition -1, imageItem);
									}
									DataManager.getInstance().setHotel(
											mHotelPosition, mHotel);
//									mSelectedMap.remove(imageItem.getImageUrl());
									mAdapter.remove(position);
									if (mAdapter.getItemCount() == 0) {
										mDetailLayout.setVisibility(View.GONE);
										showError();
									} 
									mDetailAdapter.notifyDataSetChanged();
//									updateUploadText();
									FileUtil.deleteFile(imageItem.getLocalImagePath());
									
									
								}
							})
					.setNegativeButton(
							"取消",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			alertDialog.show();
		}
	};

//	private void updateUploadText() {
//		StringBuffer textBuffer = new StringBuffer();
//		textBuffer.append(getString(R.string.upload));
//		if (mSelectedMap.size() > 0) {
//			textBuffer.append("(");
//			textBuffer.append(mSelectedMap.size());
//			textBuffer.append(")");
//		}
//		if (mSelectedMap.size() == 0 && mIsSelectAll) {
//			mIsSelectAll = false;
//			mChoseAllItem.setTitle(getString(R.string.chose_all));
//		} else if (mSelectedMap.size() == mAdapter.getItemCount() && !mIsSelectAll) {
//			mIsSelectAll = true;
//			mChoseAllItem.setTitle(getString(R.string.chose_none));
//		}
//		mUploadTextView.setText(textBuffer.toString());
//	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		mChoseAllItem = menu.add(0, 0X10086, 0, getString(R.string.chose_all));
//		mChoseAllItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		return true;
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		case MENU_SELECT_ALL:
			mIsSelectAll = !mIsSelectAll;
			if (mIsSelectAll) {
				item.setTitle(getString(R.string.chose_none));
				if (mAdapter != null && mAdapter.getDataList() != null) {
					for (ImageItem imageItem : mAdapter.getDataList()) {
						if (imageItem != null) {
							mSelectedMap.put(imageItem.getLocalImagePath(),
									Boolean.TRUE);
						}
					}
				}
			} else {
				item.setTitle(R.string.chose_all);
				mSelectedMap.clear();
			}
//			updateUploadText();
			if (mAdapter.getItemCount() >0 ) {
				int firstIndex = mLayoutManager.findFirstVisibleItemPosition();
				int lastIndex = mLayoutManager.findLastVisibleItemPosition()+1;
				mAdapter.notifyItemRangeChanged(firstIndex, lastIndex);
			}
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class PhotoChosenAdapter extends
			RecyclerView.Adapter<RecyclerView.ViewHolder> {

		private List<ImageItem> mDataList;

		public PhotoChosenAdapter() {
			super();
		}

		public ImageItem getDataItem(int position) {
			return mDataList.get(position);
		}


		public void remove(int position) {
			ImageItem imageItem = mDataList.get(position);
			mDataList.remove(position);
			notifyItemRemoved(position);
			notifyDataSetChanged();
			FileUtil.deleteFile(imageItem.getLocalImagePath());
		}

		public List<ImageItem> getDataList() {
			return mDataList;
		}

		public void updateList(List<ImageItem> dataList) {
			if (dataList == null) {
				return;
			}
			if (mDataList != null) {
				mDataList.clear();
			} else {
				mDataList = new ArrayList<ImageItem>();
			}
			mDataList.addAll(dataList);
			notifyDataSetChanged();
		}

		@Override
		public int getItemCount() {
			return mDataList == null ? 0 : mDataList.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder arg0, int arg1) {
			ImageItem item = mDataList.get(arg1);
			((PhotoItemViewHolder) arg0).setData(item, arg1);

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			PhotoChosenItem itemView = new PhotoChosenItem(arg0.getContext());
			PhotoItemViewHolder holder = new PhotoItemViewHolder(itemView);
			GridLayoutManager.LayoutParams params = (LayoutParams) holder.itemView
					.getLayoutParams();
			if (params == null) {
				params = new GridLayoutManager.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(DrawUtil.dip2px(4), DrawUtil.dip2px(4),
						DrawUtil.dip2px(4), DrawUtil.dip2px(4));
				holder.itemView.setLayoutParams(params);
			}
			return holder;
		}

	}

	class SpinnerNameAdapter extends BaseAdapter {

		private int type;

		public SpinnerNameAdapter(int type) {
			super();
			this.type = type;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mHotel == null) {
				return count;
			}
			switch (type) {
			case SPINNER_TYPE_NAME:
				count = mHotel == null ? 0 : mHotel.getCheckDataCount();
				return count;
			case SPINNER_TYPE_ISSUE:
				CheckData checkData = mHotel.getCheckData(mCheckDataPosition);
				return checkData == null ? 0 : checkData.getCheckedIssueCount()+1;
			default:
				break;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getDropDownView(int position, View view, ViewGroup parent) {
			if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
				view = getLayoutInflater().inflate(R.layout.spinner_name,
						parent, false);
				view.setTag("DROPDOWN");
				view.setPadding(0, DrawUtil.dip2px(8), 0, DrawUtil.dip2px(8));
			}
			TextView textView = (TextView) view.findViewById(R.id.tv_name);
			textView.setText(getTitle(position));
			return view;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null
					|| !convertView.getTag().toString().equals("NON_DROPDOWN")) {
				convertView = getLayoutInflater().inflate(
						R.layout.spinner_name, parent, false);
				convertView.setTag("NON_DROPDOWN");
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.tv_name);
			textView.setText(getTitle(position));
			return convertView;
		}

		private String getTitle(int position) {
			String name = "";
			switch (type) {
			case SPINNER_TYPE_NAME:
				name = mHotel.getCheckData(position).getName();
				break;
			case SPINNER_TYPE_ISSUE:
				if (position == 0) {
					name = "全部";
				} else {
					CheckData checkData = mHotel.getCheckData(mCheckDataPosition);
//					if (checkData.isGetSublist()) {
//						name = checkData.getName()+checkData.getCheckedIssue(position-1).getName();
//					} else {
						name = checkData.getCheckedIssue(position-1).getName();
//					}
				}
				break;
			default:
				break;
			}
			return name;
		}

	}

	class PhotoItemViewHolder extends RecyclerView.ViewHolder {
		private PhotoChosenItem mItem;

		public PhotoItemViewHolder(PhotoChosenItem itemView) {
			super(itemView);
			if (itemView instanceof PhotoChosenItem) {
				mItem = itemView;
			}
		}

		public void setData(ImageItem item, int position) {
			if (item == null) {
				return;
			}
			mItem.setImageData(item.getLocalImagePath());
			mItem.setTag(R.id.iv_delete, position);
			mItem.setOnClickListener(mClickListener);
//			mItem.getCheckBox().setTag(item);
//			mItem.setChecked(mSelectedMap.containsKey(item.getLocalImagePath()));
//			mItem.getCheckBox().setOnCheckedChangeListener(changeListener);
		}

		public void setImageData(String imageURL) {
			mItem.setImageData(imageURL);
		}

//		public void setItemChecked(boolean check) {
//			mItem.setChecked(check);
//		}
//
//		public boolean isChecked() {
//			if (mItem == null) {
//				return false;
//			} else {
//				return mItem.isChecked();
//			}
//		}

//		private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				ImageItem item = (ImageItem) buttonView.getTag();
//				if (item != null) {
//					item.setSelected(!item.isSelected());
//					setItemChecked(item.isSelected());
//				}
//				if (isChecked) {
//					mSelectedMap.put(item.getLocalImagePath(), isChecked);
//				} else {
//					mSelectedMap.remove(item.getLocalImagePath());
//				}
////				updateUploadText();
//			}
//		};

		private OnClickListener mClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
//				ImageItem item = (ImageItem) v.getTag(R.id.iv_back);
				int position = (Integer) v.getTag(R.id.iv_delete);
				mDetailLayout.setVisibility(View.VISIBLE);
				mDetailViewPager.setCurrentItem(position);
				mDetailDeleteImageView.setTag(position);
//				String imageUrl = "file://" + item.getImageUrl();
//				DisplayImageOptions options = new DisplayImageOptions.Builder()
//						.showImageForEmptyUri(R.drawable.icon) // resource or
//																// drawable
//						.showImageOnFail(R.drawable.icon) // resource or
//															// drawable
//						.cacheInMemory(true).cacheOnDisk(true)
//
//						.build();
//				ImageLoader.getInstance().displayImage(imageUrl,
//						mDetailImageView, options);
				
			}
		};

	}
	
	class ImageDetailAdapter extends CustomBasePagerAdapter {

		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				ImageView imageView = new ImageView(parent.getContext());
				convertView = imageView;
			} 
			ImageItem imageItem = mAdapter.getDataItem(position);
			String filePath = "file://"+imageItem.getLocalImagePath();
			ImageLoader.getInstance().displayImage(filePath, (ImageView)convertView);
			return convertView;
		}

		@Override
		public int getCount() {
			return mAdapter.getItemCount();
		}

		private int mChildCount = 0;
		 
	     @Override
	     public void notifyDataSetChanged() {         
	           mChildCount = getCount();
	           super.notifyDataSetChanged();
	     }
	 
	     @Override
	     public int getItemPosition(Object object)   {          
	           if ( mChildCount > 0) {
	           mChildCount --;
	           return POSITION_NONE;
	           }
	           return super.getItemPosition(object);
	     }
		
		
		
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDetailLayout.getVisibility() == View.VISIBLE) {
				mDetailLayout.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataManager.getInstance().setHotel(mHotelPosition, mHotel);
	}

}
