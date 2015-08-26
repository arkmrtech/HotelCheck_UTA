package com.lk.hotelcheck.activity.main;

import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.login.LoginActivity;
import com.lk.hotelcheck.activity.upload.UploadProcessActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.network.DataCallback;
import com.lk.hotelcheck.network.HttpCallback;
import com.lk.hotelcheck.network.HttpRequest;
import com.lk.hotelcheck.upload.UploadProxy;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.Machine;
import com.lk.hotelcheck.util.SharedPrefsUtil;
import common.Constance.HotelAction;
import common.Constance.ImageUploadState;
import common.NetConstance;



public class MainActivity extends BaseActivity {
	
	private HotelListAdapter mAdapter;
	private ViewGroup mLoadingGroup;
	private TextView mNameTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UploadProxy.initInstance(this);
	
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.chose_hotel);
		toolbar.setNavigationIcon(R.drawable.back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showBackDialog();
			}
		});
		setSupportActionBar(toolbar);
		DrawUtil.resetDensity(this);
//		DataManager.getInstance().init(this);
		ExpandableListView listView = (ExpandableListView) findViewById(R.id.elv_hotel);
		mNameTextView = (TextView) findViewById(R.id.tv_welcome);
		mNameTextView.setText("欢迎回来："+DataManager.getInstance().getUserName());
		mAdapter = new HotelListAdapter();
		listView.setAdapter(mAdapter);
		listView.expandGroup(0);
		listView.expandGroup(1);

		listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});
		mLoadingGroup = (ViewGroup) findViewById(R.id.vg_loadig);
		initData();
		registerUoloadBroadcast();
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mAdapter.notifyDataSetChanged();
    } 


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
		case R.id.upload_all_data:
			uploadAllData();
			break;
		case R.id.upload_all_image:
			uploadAllImage();
			break;
		case android.R.id.home :
//			finish();
			showBackDialog();
			break;
		default:
			break;
		}
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	UploadProxy.initInstance(this).doUnbindService();
    	DataManager.getInstance().clear();
    	unRegisterUploadBroadcast();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
			showBackDialog();
			return true;
		}
    	return super.onKeyDown(keyCode, event);
    }
    
    
    private void showBackDialog() {
    	LayoutInflater factory = LayoutInflater.from(this);// 提示框
		View view = factory.inflate(R.layout.alert_back, null);// 这里必须是final的
		Button exitButton = (Button) view.findViewById(R.id.btn_exit);
		Button logoutButton = (Button) view.findViewById(R.id.btn_logout);
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exit();
			}
		});
		logoutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HttpRequest.getInstance().logout(v.getContext(), new HttpCallback() {
					
					@Override
					public void onSuccess(JSONObject response) {
						Toast.makeText(MainActivity.this, "已注销账户", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
					}
					
					@Override
					public void onError(int errorCode, String info) {
						Toast.makeText(MainActivity.this, "网络异常，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
    	AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
    	alertDialog.show();
    }
    
    private void exit() {
//    	UploadProxy.initInstance(this).doUnbindService();
//    	DataManager.getInstance().clear();
    	UploadProxy.initInstance(this).saveData();
    	finish();
    	System.exit(0);
    }
	
	private void uploadAllImage() {
		if (!Machine.isNetworkOK(this)) {
			Toast.makeText(this, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
			return;
		}
		List<Hotel> hotelList = DataManager.getInstance().getCheckedHotelList();
		if (hotelList != null) {
			for (Hotel hotel : hotelList) {
//				if (hotel.isStatus() && !hotel.isAllImageUploaded()) {
//					if (hotel.get) {
//						
//					}
//					UploadProxy.addUploadTask(hotel);
////					hotel.setImageStatus(true);
//				}
				if (hotel.isStatus() && !hotel.isImageStatus()) {
					UploadProxy.addUploadTask(hotel);
					hotel.setImageStatus(true);
				}
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			UploadProcessActivity.goToUpload(this);
		}
	}
	
	private void uploadAllData() {
		if (!Machine.isNetworkOK(this)) {
			Toast.makeText(this, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
			return;
		}
		List<Hotel> hotelList = DataManager.getInstance().getCheckedHotelList();
		if (hotelList != null) {
			for (final Hotel hotel : hotelList) {
				if (hotel.isStatus() && !hotel.isDataStatus()) {
					HttpRequest.getInstance().uploadHotelData(this, hotel, DataManager.getInstance().getSession(), new HttpCallback() {
						
						@Override
						public void onSuccess(JSONObject response) {
							hotel.setDataStatus(true);
							hotel.save();
							Toast.makeText(MainActivity.this, hotel.getName()+"数据上传成功", Toast.LENGTH_SHORT).show();
							if (mAdapter != null) {
								mAdapter.notifyDataSetChanged();
							}
						}
						
						@Override
						public void onError(int errorCode, String info) {
							Toast.makeText(MainActivity.this, hotel.getName()+"数据上传失败", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}
	}
	
	private void initData() {
		mLoadingGroup.setVisibility(View.VISIBLE);
		long lastDataDate = SharedPrefsUtil.getValue(this,NetConstance.PARAM_DATE , 0l);
		if (DataManager.getInstance().getDate() > lastDataDate) {
			SharedPrefsUtil.putValue(this, NetConstance.PARAM_DATE, DataManager.getInstance().getDate());
			DataManager.getInstance().loadCheckData(this, new DataCallback() {
				@Override
				public void onSuccess() {
					loadHotelData();
				}
				
				@Override
				public void onFail() {
					mLoadingGroup.setVisibility(View.GONE);
				}
			});
		} else {
			if (DataManager.getInstance().getHotelCount() == 0) {
				loadHotelData();
			} else {
				mLoadingGroup.setVisibility(View.GONE);
			}
		}
		
		
	}

	
	private void loadHotelData() {
		DataManager.getInstance().loadHotelData(this, new DataCallback() {
			
			@Override
			public void onSuccess() {
				mAdapter.notifyDataSetChanged();
				mLoadingGroup.setVisibility(View.GONE);
			}
			
			@Override
			public void onFail() {
				mLoadingGroup.setVisibility(View.GONE);
			}
		});
	}
    
	
	private void registerUoloadBroadcast() {
		IntentFilter intent = new IntentFilter();
		intent.addAction(HotelAction.ACTION_IMAGE_UPLOAD);
		registerReceiver(uploadBroadcastReceiver, intent);
	}
	
	private void unRegisterUploadBroadcast() {
		unregisterReceiver(uploadBroadcastReceiver);
	}
	
	 private BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(HotelAction.ACTION_IMAGE_UPLOAD)) {
				UploadBean uploadBean = (UploadBean) intent.getSerializableExtra(HotelAction.IMAGE_UPLOAD_EXTRA);
				if (uploadBean.getImageState() == ImageUploadState.STATE_FINISH) {
					Hotel hotel = DataManager.getInstance().getHotelbyId(uploadBean.getCheckId());
					if (hotel != null) {
						if (hotel.isAllImageUploaded()) {
							hotel.setImageStatus(true);
//							int position = DataManager.getInstance().getHotelPosition(hotel.getCheckId());
							mAdapter.notifyDataSetChanged();
						}
					}
				} 
			}
		}
		 
	 };
    
}
