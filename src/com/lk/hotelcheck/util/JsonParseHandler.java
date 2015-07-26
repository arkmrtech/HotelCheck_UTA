package com.lk.hotelcheck.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.dao.AreaIssue;
import com.lk.hotelcheck.manager.DataManager;

import common.Constance;
import common.NetConstance;

public class JsonParseHandler {

	public static AreaIssue parseAreaIssue(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		AreaIssue areaIssue = new AreaIssue();
		areaIssue.setAreaId(jsonObject.optInt(NetConstance.PARAM_AREA_ID));
		areaIssue.setAreaName(jsonObject.optString(NetConstance.PARAM_AREA_NAME));
		areaIssue.setIssueId(jsonObject.optInt(NetConstance.PARAM_DIM_TWO_ID));
		areaIssue.setIssueName(jsonObject.optString(NetConstance.PARAM_DIM_TWO_NAME));
		areaIssue.setDimOneId(jsonObject.optInt(NetConstance.PARAM_DIM_ONE_ID));
		areaIssue.setDimOneName(jsonObject.optString(NetConstance.PARAM_DIM_ONE_NAME));
		return areaIssue;
	}
	
	public static Hotel parseHotel(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		Hotel hotel = new Hotel();
		JSONObject branchObject = jsonObject.optJSONObject(NetConstance.PARAM_BRANCH);
		JSONArray questionList = jsonObject.optJSONArray(NetConstance.PARAM_QUESTION_LIST);
		JSONArray dymicList = jsonObject.optJSONArray(NetConstance.PARAM_ROOM_ADN_PASSWAY_LIST);
		if (branchObject != null) {
			hotel.setCheckId(branchObject.optInt(NetConstance.PARAM_BRANCH_CHECK_ID));
			hotel.setName(branchObject.optString(NetConstance.PARAM_BRANCH_NAME));
			hotel.setAddress(branchObject.optString(NetConstance.PARAM_ADDRESS));
			hotel.setPhone(branchObject.optString(NetConstance.PARAM_TELT));
			hotel.setMemo(branchObject.optString(NetConstance.PARAM_REGION));
			long openDate = branchObject.optLong(NetConstance.PARAM_OPEN_DATE);
			long lastCheckDate = branchObject.optLong(NetConstance.PARAM_LAST_CHECK_DATE);
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (openDate > 0) {
				hotel.setOpenDate(sFormat.format(new Date(openDate)));
			}
			if (lastCheckDate > 0) {
				hotel.setLastCheckedDate(sFormat.format(new Date(lastCheckDate)));
			}
			
		}
		if (questionList != null) {
			for (int i = 0; i < questionList.length(); i++) {
				AreaIssue issueItem;
				try {
					issueItem = parseIssueItem(questionList.getJSONObject(i));
					hotel.addQuestion(issueItem);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} 
		if (dymicList != null) {
			for (int i = 0; i < dymicList.length(); i++) {
				try {
					CheckData checkData = parseDymicData(dymicList.getJSONObject(i));
					if (checkData.getType() == Constance.CheckDataType.TYPE_ROOM) {
						hotel.addRoom(checkData);
					} else if (checkData.getType() == Constance.CheckDataType.TYPE_PASSWAY) {
						hotel.addPassway(checkData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return hotel;
	}
	
	public static AreaIssue parseIssueItem(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		AreaIssue areaIssue = new AreaIssue();
		areaIssue.setAreaId(jsonObject.optInt(NetConstance.PARAM_AREA_ID));
		areaIssue.setAreaName(jsonObject.optString(NetConstance.PARAM_AREA_NAME));
		areaIssue.setIssueId(jsonObject.optInt(NetConstance.PARAM_DIM_TWO_ID));
		areaIssue.setIssueName(jsonObject.optString(NetConstance.PARAM_DIM_TWO_NAME));
		areaIssue.setDimOneId(jsonObject.optInt(NetConstance.PARAM_DIM_ONE_ID));
		areaIssue.setDimOneName(jsonObject.optString(NetConstance.PARAM_DIM_ONE_NAME));
		return areaIssue;
	}
	
	public static CheckData parseDymicData(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		int type = jsonObject.optInt(NetConstance.PARAM_AREA_TYPE);
		CheckData checkData = null;
		if (type == Constance.CHECK_DATA_ID_ROOM) {
			checkData = DataManager.getInstance().createRoomCheckData();
			checkData.setType(Constance.CheckDataType.TYPE_ROOM);
		} else if (type == Constance.CHECK_DATA_ID_PASSWAY) {
			checkData = DataManager.getInstance().createPasswayCheckData();
			checkData.setType(Constance.CheckDataType.TYPE_PASSWAY);
		} else {
			checkData = new CheckData();
			checkData.setType(Constance.CheckDataType.TYPE_NORMAL);
		}
		checkData.setCheckId(jsonObject.optLong(NetConstance.PARAM_AREA_ID));
		String name = jsonObject.optString(NetConstance.PARAM_QUE_AREA_NAME)+ jsonObject.optString(NetConstance.PARAM_NAME);
		if (name != null) {
			checkData.setName(name);
			checkData.setId((long) Math.abs(name.hashCode()));
		}
		
		
		return checkData;
	}
	

	public static JsonObject parseHotelToJson(Hotel hotel) {
		if (hotel == null) {
			return null;
		}
		JsonObject hotelJsonObject = new JsonObject();
		return null;
	}
	
	
}
