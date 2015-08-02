package com.lk.hotelcheck.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
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
			hotel.setBranchNumber(branchObject.optInt(NetConstance.PARAM_BRANCH_NUMBER));
			hotel.setAddress(branchObject.optString(NetConstance.PARAM_ADDRESS));
			hotel.setPhone(branchObject.optString(NetConstance.PARAM_TELT));
			hotel.setMemo(branchObject.optString(NetConstance.PARAM_REGION));
			int checkState = branchObject.optInt(NetConstance.PARAM_CHECK_STATE);
			int uploadDataState = branchObject.optInt(NetConstance.PARAM_IS_UPDATE_DATA);
			boolean status = false;
			if (checkState == 2) {
				status = true;
			}
			boolean dataStatus = false;
			if (uploadDataState == 1) {
				dataStatus = true;
			}
			hotel.setStatus(status);
			hotel.setDataStatus(dataStatus);
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
		areaIssue.setIsDefQue(jsonObject.optInt(NetConstance.PARAM_DEF_QUE));
		areaIssue.setIsPreQue(jsonObject.optInt(NetConstance.PARAM_PRE_QUE));
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
			if (checkData != null) {
				checkData.setType(Constance.CheckDataType.TYPE_ROOM);
			}
		} else if (type == Constance.CHECK_DATA_ID_PASSWAY) {
			checkData = DataManager.getInstance().createPasswayCheckData();
			if (checkData != null) {
				checkData.setType(Constance.CheckDataType.TYPE_PASSWAY);
			}
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
	
	
	

	public static JSONObject parseHotelToJson(Hotel hotel) {
		if (hotel == null) {
			return null;
		}
		JSONObject hotelJsonObject = null;
		try {
			 hotelJsonObject = new JSONObject(new Gson().toJson(hotel));
			 if (hotel.getCheckDatas() != null) {
				JSONArray jsonArray = new JSONArray();
				for (CheckData checkData : hotel.getCheckDatas()) {
					JSONObject checkDataObject = parseCheckDataToJson(checkData);
					if (checkDataObject != null) {
						jsonArray.put(checkDataObject);
					}
				}
				hotelJsonObject.put(NetConstance.PARAM_CHECK_DATA_LIST, jsonArray);
			}
			 if (hotel.getRoomList() != null) {
				 JSONArray jsonArray = new JSONArray();
					for (CheckData checkData : hotel.getRoomList()) {
						JSONObject checkDataObject = parseCheckDataToJson(checkData);
						if (checkDataObject != null) {
							jsonArray.put(checkDataObject);
						}
					}
					hotelJsonObject.put(NetConstance.PARAM_ROOM_LIST, jsonArray);
			}
			 if (hotel.getPasswayList() != null) {
				 JSONArray jsonArray = new JSONArray();
					for (CheckData checkData : hotel.getPasswayList()) {
						JSONObject checkDataObject = parseCheckDataToJson(checkData);
						if (checkDataObject != null) {
							jsonArray.put(checkDataObject);
						}
					}
					hotelJsonObject.put(NetConstance.PARAM_PASSWAY_LIST, jsonArray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return hotelJsonObject;
	}
	
	public static JSONObject parseCheckDataToJson(CheckData checkData) {
		if (checkData == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.PARAM_AREA_ID, checkData.getId());
			jsonObject.put(NetConstance.PARAM_AREA_NAME, checkData.getName());
			if (checkData.getCheckedIssue() != null) {
				JSONArray jsonArray = new JSONArray();
				for (IssueItem issueItem : checkData.getCheckedIssue()) {
					JSONObject issueObject = parseIssueItemToJson(issueItem);
					if (issueObject != null) {
						jsonArray.put(issueObject);
					}
				}
			jsonObject.put(NetConstance.PARAM_ISSUE_LIST, jsonArray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return jsonObject;
	}
	
//	public static JSONObject parseIssueItemToJson(IssueItem issueItem) {
//		if (issueItem == null) {
//			return null;
//		}
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = new JSONObject( new GsonBuilder().serializeNulls().create().toJson(issueItem));
//			if (issueItem.getImagelist() != null) {
//				JSONArray jsonArray = new JSONArray();
//				for (ImageItem imageItem : issueItem.getImagelist()) {
//					JSONObject imageObject = new JSONObject(new GsonBuilder().serializeNulls().create().toJson(imageItem));
////					imageObject.put(NetConstance.PARAM_FILE_PATH, imageItem.getServiceSavePath());
////					imageObject.put(NetConstance.PARAM_IS_WIDTH, imageItem.isWidth());
//					jsonArray.put(imageObject);
//				}
//				if (jsonArray.length() == 0) {
//					jsonObject.put(NetConstance.PARAM_IMAGE_LIST, JSONObject.NULL);
//				} else {
//					jsonObject.put(NetConstance.PARAM_IMAGE_LIST, jsonArray);
//				}
//				
//			} else {
//				jsonObject.put(NetConstance.PARAM_IMAGE_LIST, JSONObject.NULL);
//			}
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return jsonObject;
//	}
	
	public static JSONObject parseIssueItemToJson(IssueItem issueItem) {
		if (issueItem == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.PARAM_DIM_ONE_ID, issueItem.getDimOneId());
			if (issueItem.getDimOneName() == null) {
				jsonObject.put(NetConstance.PARAM_DIM_ONE_NAME, JSONObject.NULL);
			} else {
				jsonObject.put(NetConstance.PARAM_DIM_ONE_NAME, issueItem.getDimOneName());
			}
			jsonObject.put(NetConstance.PARAM_DIM_TWO_ID, issueItem.getId());
			if (issueItem.getName() == null) {
				jsonObject.put(NetConstance.PARAM_DIM_TWO_NAME, JSONObject.NULL);
			} else {
				jsonObject.put(NetConstance.PARAM_DIM_TWO_NAME, issueItem.getName());
			}
			jsonObject.put(NetConstance.PARAM_DEF_QUE, issueItem.getIsDefQue());
			jsonObject.put(NetConstance.PARAM_PRE_QUE, issueItem.getIsPreQue());
			jsonObject.put(NetConstance.PARAM_IS_CHECK, issueItem.isCheck());
			if (issueItem.getContent() == null) {
				jsonObject.put(NetConstance.PARAM_CONTENT, JSONObject.NULL);
			} else {
				jsonObject.put(NetConstance.PARAM_CONTENT, issueItem.getContent());
			}
			jsonObject.put(NetConstance.PARAM_REFORM_STATE, issueItem.getReformState());
			JSONArray jsonArray = new JSONArray();
			if (issueItem.getImagelist() != null) {
				for (ImageItem imageItem : issueItem.getImagelist()) {
					JSONObject imageObject = new JSONObject();
					imageObject.put(NetConstance.PARAM_FILE_PATH, imageItem.getServiceSavePath());
					imageObject.put(NetConstance.PARAM_IS_WIDTH, imageItem.isWidth());
					jsonArray.put(imageObject);
				}
			}
			jsonObject.put(NetConstance.PARAM_IMAGE_LIST, jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
}
