package com.lk.hotelcheck.activity.photochosen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.LayoutParams;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

	private Hotel hotel;
	private int hotelPosition;
	private int checkDataPosition;
	private int issuePosition;
	private Button uploadButton;
	private TextView uploadTextView;
	private static final int MENU_SELECT_ALL = 0X10086;
	private boolean isSelectAll = false;
	private HashMap<String, Boolean> selectedMap;
	private PhotoChosenAdapter adapter;
	private GridLayoutManager layoutManager;
	private RecyclerView photoRecyclerView;
	private MenuItem choseAllItem;
	private static final int SPINNER_TYPE_NAME = 1;
	private static final int SPINNER_TYPE_ISSUE = 2;
	// private SpinnerNameAdapter checkDataMenuAdapter;
	// private SpinnerNameAdapter issueDataMenuAdapter;
	private View mDetailLayout;
	private ImageView mDetailImageView;
	private ImageView mDetailBackImageView;
	private ImageView mDetailDeleteImageView;
	private CheckData mCheckData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_chosen);
		selectedMap = new HashMap<String, Boolean>();
		uploadButton = (Button) findViewById(R.id.btn_upload);
		uploadTextView = (TextView) findViewById(R.id.tv_upload);
		mDetailLayout = findViewById(R.id.rl_image_detail);
		mDetailImageView = (ImageView) findViewById(R.id.iv_detail);
		mDetailBackImageView = (ImageView) findViewById(R.id.iv_back);
		mDetailDeleteImageView = (ImageView) findViewById(R.id.iv_delete);
		mDetailBackImageView.setOnClickListener(mImageBackClickListener);
		mDetailDeleteImageView.setOnClickListener(mImageDeleteClickListener);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		hotelPosition = getIntent().getIntExtra(IntentKey.INTENT_KEY_POSITION,
				-1);
		checkDataPosition = getIntent().getIntExtra(
				IntentKey.INTENT_KEY_CHECK_DATA_POSITION, -1);
		issuePosition = getIntent().getIntExtra(
				IntentKey.INTENT_KEY_ISSUE_POSITION, -1);
		hotel = DataManager.getInstance().getHotel(hotelPosition);
		// Spinner nameSpinner = (Spinner) findViewById(R.id.sp_name);
		// Spinner issueSpinner = (Spinner) findViewById(R.id.sp_issue);
		if (hotel != null) {
			toolbar.setTitle(hotel.getName());
			toolbar.setNavigationIcon(R.drawable.back);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
			setSupportActionBar(toolbar);
			// checkDataMenuAdapter = new SpinnerNameAdapter(SPINNER_TYPE_NAME);
			// issueDataMenuAdapter = new
			// SpinnerNameAdapter(SPINNER_TYPE_ISSUE);
			// nameSpinner.setAdapter(checkDataMenuAdapter);
			// issueSpinner.setAdapter(issueDataMenuAdapter);
			photoRecyclerView = (RecyclerView) findViewById(R.id.rv_photo);
			layoutManager = new GridLayoutManager(this, 2);
			photoRecyclerView.setLayoutManager(layoutManager);
			adapter = new PhotoChosenAdapter();
//			adapter.updateList(hotel.getCheckData(checkDataPosition).getCheckedIssue(issuePosition).getImagelist());
			adapter.updateList(hotel.getCheckData(checkDataPosition).getIssueImageList(issuePosition));
			photoRecyclerView.setAdapter(adapter);
			// nameSpinner.setOnItemSelectedListener(new
			// AdapterView.OnItemSelectedListener() {
			//
			// @Override
			// public void onItemSelected(AdapterView<?> parent, View view,
			// int position, long id) {
			// // if (position == 0) {
			// checkDataPosition = position;
			// // } else {
			// // checkDataPosition = position - 1;
			// // }
			// issueDataMenuAdapter.notifyDataSetChanged();
			// adapter.updateList(hotel.getCheckData(checkDataPosition).getIssue(issuePosition).getImagelist());
			// }
			//
			// @Override
			// public void onNothingSelected(AdapterView<?> parent) {
			// // TODO Auto-generated method stub
			//
			// }
			// });
			//
			// issueSpinner.setOnItemSelectedListener(new
			// AdapterView.OnItemSelectedListener() {
			//
			// @Override
			// public void onItemSelected(AdapterView<?> parent, View view,
			// int position, long id) {
			// issuePosition = position;
			// adapter.updateList(hotel.getCheckData(checkDataPosition).getIssue(issuePosition).getImagelist());
			// }
			//
			// @Override
			// public void onNothingSelected(AdapterView<?> parent) {
			// // TODO Auto-generated method stub
			//
			// }
			//
			// });

		}

	}

	private OnClickListener mImageBackClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mDetailLayout.getVisibility() == View.VISIBLE) {
				mDetailLayout.setVisibility(View.GONE);
			}
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
									ImageItem imageItem = adapter
											.getDataItem(position);
									hotel.getCheckData(checkDataPosition)
											.getIssue(issuePosition)
											.getImagelist().remove(position);
									DataManager.getInstance().setHotel(
											hotelPosition, hotel);
									selectedMap.remove(imageItem.getImageUrl());
									adapter.remove(position);
									updateUploadText();
									mDetailLayout.setVisibility(View.GONE);
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

	private void updateUploadText() {
		StringBuffer textBuffer = new StringBuffer();
		textBuffer.append(getString(R.string.upload));
		if (selectedMap.size() > 0) {
			textBuffer.append("(");
			textBuffer.append(selectedMap.size());
			textBuffer.append(")");
		}
		if (selectedMap.size() == 0 && isSelectAll) {
			isSelectAll = false;
			choseAllItem.setTitle(getString(R.string.chose_all));
		} else if (selectedMap.size() == adapter.getItemCount() && !isSelectAll) {
			isSelectAll = true;
			choseAllItem.setTitle(getString(R.string.chose_none));
		}
		uploadTextView.setText(textBuffer.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		choseAllItem = menu.add(0, 0X10086, 0, getString(R.string.chose_all));
		choseAllItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		case MENU_SELECT_ALL:
			isSelectAll = !isSelectAll;
			if (isSelectAll) {
				item.setTitle(getString(R.string.chose_none));
				if (adapter != null && adapter.getDataList() != null) {
					for (ImageItem imageItem : adapter.getDataList()) {
						if (imageItem != null) {
							selectedMap.put(imageItem.getImageUrl(),
									Boolean.TRUE);
						}
					}
				}
			} else {
				item.setTitle(R.string.chose_all);
				selectedMap.clear();
			}
			updateUploadText();
			if (adapter != null && adapter.getDataList() != null) {
				for (int i = 0; i < adapter.getItemCount(); i++) {
					PhotoItemViewHolder viewHolder = (PhotoItemViewHolder) photoRecyclerView
							.getChildViewHolder(layoutManager.getChildAt(i));
					viewHolder.setItemChecked(isSelectAll);
				}
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
//			mDataList = hotel.getAllImage();
		}

		public ImageItem getDataItem(int position) {
			return mDataList.get(position);
		}


		public void remove(int position) {
			ImageItem imageItem = mDataList.get(position);
			mDataList.remove(position);
			notifyItemRemoved(position);
			notifyDataSetChanged();
			FileUtil.deleteFile(imageItem.getImageUrl());
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
			switch (type) {
			case SPINNER_TYPE_NAME:
				count = hotel == null ? 0 : hotel.getCheckDataCount();
				return count;
			case SPINNER_TYPE_ISSUE:
				if (hotel == null) {
					return count;
				}
				CheckData checkData = hotel.getCheckData(checkDataPosition);
				return checkData == null ? 0 : checkData.getIssueCount();
			default:
				break;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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
			// if (position == 0) {
			// name = "全部";
			// } else {
			switch (type) {
			case SPINNER_TYPE_NAME:
				name = hotel.getCheckData(position).getName();
				return name;
			case SPINNER_TYPE_ISSUE:

				// if (checkDataPosition == 0) {
				name = hotel.getCheckData(checkDataPosition).getIssue(position)
						.getName();
				// } else {
				// name =
				// hotel.getCheckData(checkDataPosition).getIssue(position -
				// 1).getName();
				// }
				return name;
			default:
				break;
			}
			// }
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
			// ImageItem tagItem = (ImageItem) mItem.getTag();
			// if (tagItem == null
			// || (tagItem != null && !item.getImageUrl().equals(
			// tagItem.getImageUrl()))) {
			mItem.setImageData(item.getImageUrl());
			// }
			// mItem.setChecked(item.isSelected());
			mItem.setTag(R.id.iv_back, item);
			mItem.setTag(R.id.iv_delete, position);
			mItem.setOnClickListener(mClickListener);
			mItem.getCheckBox().setTag(item);
			mItem.getCheckBox().setOnCheckedChangeListener(changeListener);
			if (selectedMap.containsKey(item.getImageUrl())) {
				mItem.setChecked(true);
			} else {
				mItem.setChecked(false);
			}
		}

		public void setImageData(String imageURL) {
			mItem.setImageData(imageURL);
		}

		public void setItemChecked(boolean check) {
			mItem.setChecked(check);
		}

		public boolean isChecked() {
			if (mItem == null) {
				return false;
			} else {
				return mItem.isChecked();
			}
		}

		private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				ImageItem item = (ImageItem) buttonView.getTag();
				if (item != null) {
					item.setSelected(!item.isSelected());
					setItemChecked(item.isSelected());
				}
				if (isChecked) {
					selectedMap.put(item.getImageUrl(), isChecked);
				} else {
					selectedMap.remove(item.getImageUrl());
				}
				updateUploadText();
			}
		};

		private OnClickListener mClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageItem item = (ImageItem) v.getTag(R.id.iv_back);
				int position = (Integer) v.getTag(R.id.iv_delete);
				mDetailLayout.setVisibility(View.VISIBLE);
				String imageUrl = "file://" + item.getImageUrl();
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showImageForEmptyUri(R.drawable.icon) // resource or
																// drawable
						.showImageOnFail(R.drawable.icon) // resource or
															// drawable
						.cacheInMemory(true).cacheOnDisk(true)

						.build();
				ImageLoader.getInstance().displayImage(imageUrl,
						mDetailImageView, options);
				mDetailDeleteImageView.setTag(position);
			}
		};

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
		DataManager.getInstance().setHotel(hotelPosition, hotel);
	}
	// @Override
	// public void onBackPressed() {
	// super.onBackPressed();
	// if (mDetailLayout.getVisibility() == View.VISIBLE) {
	// mDetailLayout.setVisibility(View.GONE);
	// } else {
	// finish();
	// DataManager.getInstance().setHotel(hotelPosition, hotel);
	// }
	// }

}
