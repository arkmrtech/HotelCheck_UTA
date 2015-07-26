package com.lk.hotelcheck.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lk.hotelcheck.bean.CheckData;

//public class HotelUtil {
//	
//	public static CheckData createCorridorSubCheckData(Context context) {
//		CheckData subCheckData = null;
//		try {
//			InputStream inputStream = context.getAssets().open("corridor.json");
//			Gson gson = new Gson();
//			subCheckData = gson.fromJson(CommonUtil.InputStreamTOString(inputStream), CheckData.class);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return subCheckData;
//	}
//	
//	public static CheckData createRoomSubCheckData(Context context) {
//		CheckData subCheckData = null;
//		try {
//			InputStream inputStream = context.getAssets().open("room.json");
//			Gson gson = new Gson();
//			subCheckData = gson.fromJson(CommonUtil.InputStreamTOString(inputStream), CheckData.class);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return subCheckData;
//	}
//}
