package com.lk.hotelcheck.activity.hotel;

import com.lk.hotelcheck.bean.Hotel;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

public class BaseHotelFragment extends Fragment{

	protected Hotel mHotel;
	protected Context mContext;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		if (activity instanceof HotelInfoDetailActivity) {
			mHotel = ((HotelInfoDetailActivity)activity).getHotel();
		}
	}
	
	
}
