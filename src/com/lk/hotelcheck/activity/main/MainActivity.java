package com.lk.hotelcheck.activity.main;

import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.login.LoginActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.dao.HotelCheck;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.network.DataCallback;
import com.lk.hotelcheck.upload.UploadProxy;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.Machine;



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
		mNameTextView.setText("欢迎回来："+DataManager.getInstance().getUser().getUserName());
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
		initCheckData();
		loadHotelData();
//		checkData();
//		checkNetworkSpeed();
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
//    	DataManager.getInstance().saveDataCache();
    	DataManager.getInstance().clear();
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
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
    	AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();
    	alertDialog.show();
    }
    
    private void exit() {
    	finish();
    	System.exit(1);
    }
    
    private void logout() {
    }
    
	
	private void uploadAllImage() {
		if (!Machine.isNetworkOK(this)) {
			Toast.makeText(this, "网络未链接，请检查网络链接", Toast.LENGTH_SHORT).show();
			return;
		}
		List<Hotel> hotelList = DataManager.getInstance().getCheckedHotelList();
		if (hotelList != null) {
			int i = 0;
			for (Hotel hotel : hotelList) {
				if (hotel.isStatus() && !hotel.isImageStatus()) {
					UploadProxy.addUploadTask(hotel);
					hotel.setImageStatus(true);
					DataManager.getInstance().setHotel(i, hotel);
				}
				i++;
			}
		}
	}
	
	private void initCheckData() {
		mLoadingGroup.setVisibility(View.VISIBLE);
		DataManager.getInstance().loadCheckData(this, new DataCallback() {
			
			@Override
			public void onSuccess() {
				mLoadingGroup.setVisibility(View.GONE);
			}
			
			@Override
			public void onFail() {
				mLoadingGroup.setVisibility(View.GONE);
			}
		});
		
	}

	
	private void loadHotelData() {
		mLoadingGroup.setVisibility(View.VISIBLE);
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
    
    
}
