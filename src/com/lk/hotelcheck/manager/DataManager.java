package com.lk.hotelcheck.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.HotelList;
import com.lk.hotelcheck.util.CommonUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.HotelUtil;

import common.Constance;
import common.NetConstance;


public class DataManager {
//	private List<Hotel> mHotelList;
	private HotelList mHotelList;
	private List<Hotel> mCheckedHotelList;
	private List<Hotel> mUnCheckedHoteList;
//	private CheckData mTempRoomCheckData;
//	private CheckData mTempCorridorCheckData;
	
	private static class SingleHolder {
		private static DataManager mInstance = new DataManager();
	}
	
	private DataManager() {
		
	}
	
	public static DataManager getInstance() {
		return SingleHolder.mInstance;
	}

	public int getHotelCount() {
		return mHotelList == null ? 0 : mHotelList.size();
	}
	
	public void init(Context context) {
		if (FileUtil.isFileExist(Constance.Path.DATA_PATH)) {
			loadCache();
		} else {
			try {
				InputStream inputStream = context.getAssets().open("hotel.json");
				loadData(CommonUtil.InputStreamTOString(inputStream));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		mTempRoomCheckData = HotelUtil.createRoomSubCheckData(context);
//		mTempCorridorCheckData = HotelUtil.createCorridorSubCheckData(context);
	}
	
	public void loadData(String hotelJson) {
		if (TextUtils.isEmpty(hotelJson)) {
			return;
		}
		Gson gson = new Gson();
		mHotelList = gson.fromJson(hotelJson, HotelList.class);
		if (mHotelList != null && mHotelList.size() > 0) {
			for (Hotel hotel : mHotelList.getHotels()) {
				if (hotel.isStatus()) {
					if (mCheckedHotelList == null) {
						mCheckedHotelList = new ArrayList<Hotel>();
					}
					mCheckedHotelList.add(hotel);
				} else {
					if (mUnCheckedHoteList == null) {
						mUnCheckedHoteList = new ArrayList<Hotel>();
					}
					mUnCheckedHoteList.add(hotel);
				}
			}
		}
	}
	
	
	
	public Hotel getHotel(int position) {
		if (mHotelList == null || position >= mHotelList.size()) {
			return null;
		}
		return mHotelList.getHotelByPosition(position);
	}

	public String getHotelName(int position) {
		if (mHotelList == null || position >= mHotelList.size()) {
			return "";
		}
		return mHotelList.getHotelByPosition(position).getName();
	}

	public int getHotelListCount(int groupPosition) {
		int count = 0;
		if (groupPosition == 0) {
			count = mUnCheckedHoteList == null ? 0 : mUnCheckedHoteList.size();
		} else {
			count = mCheckedHotelList == null ? 0 : mCheckedHotelList.size();
		}
		return count;	
	}

	public Hotel getHotel(int groupPosition, int childPosition) {
		Hotel hotel = null;
		if (groupPosition == 0) {
			if (mUnCheckedHoteList != null && mUnCheckedHoteList.size() >0) {
				hotel = mUnCheckedHoteList.get(childPosition);
			}
		} else {
			if (mCheckedHotelList != null && mCheckedHotelList.size() > 0) {
				hotel = mCheckedHotelList.get(childPosition);
			}
		}
		return hotel;
	}

	public int getHotelPosition(int id) {
		if (mHotelList != null) {
			int position = 0;
			for (Hotel hotel : mHotelList.getHotels()) {
				if(hotel.getId() == id) {
					return position;
				};
				position ++;
			}
		}
		return -1;
	}

	public void loadCache() {
		String hoteJson = FileUtil.readFileToString(Constance.Path.DATA_PATH);
		loadData(hoteJson);
	}
	
	public void saveDataCache() {
		if (mHotelList != null) {
			Gson gson = new Gson();
			for (Hotel hotel : mHotelList.getHotels()) {
			}
			String jsonString = gson.toJson(mHotelList);
			FileUtil.createFile(Constance.Path.DATA_PATH, false);
			FileUtil.writeStringToSdCard(Constance.Path.DATA_PATH, jsonString);
		}
	}
	
//	public void clearData() {
//		mHotelList = null;
//		mCheckedHotelList.clear();
//		mCheckedHotelList = null;
//		mUnCheckedHoteList.clear();
//		mUnCheckedHoteList = null;
//	}

	public void setHotel(int position, Hotel hotel) {
		if (mHotelList == null) {
			mHotelList = new HotelList();
		}
		mHotelList.setHotel(hotel, position);
//		if (hotel.isDataStatus() && !isExistInCheckedList(hotel)) {
//			 mCheckedHotelList.add(hotel);
//			 mUnCheckedHoteList.remove(hotel);
//		} 
	}
	
	public void setHotelChecked(int position, Hotel hotel) {
		if (mHotelList == null) {
			mHotelList = new HotelList();
		}
		mHotelList.setHotel(hotel, position);
		if (hotel.isStatus() && !isExistInCheckedList(hotel)) {
			 mCheckedHotelList.add(hotel);
			 mUnCheckedHoteList.remove(hotel);
//			 removeUnCheckedHote(hotel);
		} 
	}
	
//	private void removeUnCheckedHote(Hotel hotel) {
//		if (mUnCheckedHoteList == null) {
//			return;
//		}
//		int count = 0;
//		for (Hotel tempHotel : mUnCheckedHoteList) {
//			if (tempHotel.getId() == hotel.getId()) {
//				mUnCheckedHoteList.remove(count);
//				return;
//			}
//			count++;
//		}
//	}
	
	private boolean isExistInCheckedList(Hotel hotel) {
		boolean exist = false;
		if (mCheckedHotelList == null) {
			return false;
		}
		for (Hotel tempHotel : mCheckedHotelList) {
			if (tempHotel.getId() == hotel.getId()) {
				return true;
			}
		}
		return exist;
	}

//	public CheckData createRoomCheckData(String number) {
//		 mTempRoomCheckData.setName(number);
//		 return mTempRoomCheckData;
//	}
//
//	public CheckData createCorridorCheckData(String number) {
//		mTempCorridorCheckData.setName(number);
//		return mTempCorridorCheckData;
//	}
	
	
	public void clear() {
		if (mCheckedHotelList != null) {
			mCheckedHotelList.clear();
		}
		if (mUnCheckedHoteList != null) {
			mUnCheckedHoteList.clear();
		}
		mHotelList = null;
	}
}
