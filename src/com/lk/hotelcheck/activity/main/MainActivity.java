package com.lk.hotelcheck.activity.main;

import java.io.IOException;
import java.io.InputStream;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.R.id;
import com.lk.hotelcheck.R.layout;
import com.lk.hotelcheck.R.menu;
import com.lk.hotelcheck.activity.BaseActivity;
import com.lk.hotelcheck.activity.hotel.HotelInfoDetailActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.CommonUtil;
import com.lk.hotelcheck.util.DrawUtil;
import com.lk.hotelcheck.util.FileUtil;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.os.Build;



public class MainActivity extends BaseActivity {
	
	private HotelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.chose_hotel);
		setSupportActionBar(toolbar);
        DrawUtil.resetDensity(this);
        Log.d("lxk", "wifi speed = "+CommonUtil.getWifiSpeed(this));
        DataManager.getInstance().init(this);
			ExpandableListView listView = (ExpandableListView) findViewById(R.id.elv_hotel);
			mAdapter = new HotelListAdapter();
			listView.setAdapter(mAdapter);
			listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
				
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					Hotel hotel = DataManager.getInstance().getHotel(groupPosition, childPosition);
					HotelInfoDetailActivity.goToHotel(MainActivity.this, hotel.getId());
					return false;
				}
			});
			listView.expandGroup(0);
			listView.expandGroup(1);

			listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView parent, View v,
						int groupPosition, long id) {
					return false;
				}
			});
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	DataManager.getInstance().saveDataCache();
    	DataManager.getInstance().clear();
    }
    
    
    
}
