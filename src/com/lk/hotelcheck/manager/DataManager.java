package com.lk.hotelcheck.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.HotelUploadQueneBean;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.bean.User;
import com.lk.hotelcheck.bean.dao.AreaIssue;
import com.lk.hotelcheck.bean.dao.CheckIssue;
import com.lk.hotelcheck.bean.dao.DymicIssue;
import com.lk.hotelcheck.bean.dao.IssueCheckedImage;
import com.lk.hotelcheck.network.DataCallback;
import com.lk.hotelcheck.network.HttpCallback;
import com.lk.hotelcheck.network.HttpRequest;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.JsonParseHandler;
import com.lk.hotelcheck.util.SharedPreferencesUtil;
import com.tencent.bugly.proguard.u;

import common.Constance;
import common.Constance.CheckDataType;
import common.Constance.DefQueType;
import common.Constance.ImageUploadState;
import common.Constance.PreQueType;
import common.NetConstance;


public class DataManager {
	private List<Hotel> mCheckedHotelList;
	private List<Hotel> mUnCheckedHoteList;
	private List<Hotel> mHotelDataList;
	private User mUser;
	
	private static class SingleHolder {
		private static DataManager mInstance = new DataManager();
	}
	
	private DataManager() {
		
	}
	
	public static DataManager getInstance() {
		return SingleHolder.mInstance;
	}

	public int getHotelCount() {
		return mHotelDataList == null ? 0 : mHotelDataList.size();
	}
	
	
	
	public Hotel getHotel(int position) {
		if (mHotelDataList == null || position >= mHotelDataList.size()) {
			return null;
		}
		return mHotelDataList.get(position);
	}

	public String getHotelName(int position) {
		if (mHotelDataList == null || position >= mHotelDataList.size()) {
			return "";
		}
		return mHotelDataList.get(position).getName();
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
		if (mHotelDataList != null) {
			int position = 0;
			for (Hotel hotel : mHotelDataList) {
				if(hotel.getCheckId() == id) {
					return position;
				};
				position ++;
			}
		}
		return -1;
	}


	public void setHotel(int position, Hotel hotel) {
		if (mHotelDataList == null) {
			mHotelDataList = new ArrayList<Hotel>();
		}
		mHotelDataList.set(position, hotel);
	}
	
	public void setHotelChecked(int position, Hotel hotel) {
		if (mHotelDataList == null) {
			mHotelDataList = new ArrayList<Hotel>();
		}
		mHotelDataList.set(position, hotel);
		if (hotel.isStatus() && !isExistInCheckedList(hotel)) {
			if (mCheckedHotelList == null) {
				mCheckedHotelList = new ArrayList<Hotel>();
			}
			 mCheckedHotelList.add(hotel);
			 mUnCheckedHoteList.remove(hotel);
		} 
	}
	
	private boolean isExistInCheckedList(Hotel hotel) {
		boolean exist = false;
		if (mCheckedHotelList == null) {
			return false;
		}
		for (Hotel tempHotel : mCheckedHotelList) {
			if (tempHotel.getCheckId() == hotel.getCheckId()) {
				return true;
			}
		}
		return exist;
	}

	
	public void clear() {
		if (mCheckedHotelList != null) {
			mCheckedHotelList.clear();
		}
		if (mUnCheckedHoteList != null) {
			mUnCheckedHoteList.clear();
		}
		if (mHotelDataList != null) {
			mHotelDataList.clear();;
		}
	}

	public List<Hotel> getCheckedHotelList() {
		return mCheckedHotelList;
	}
	
	public void addHotelChecked(Hotel hotel) {
		if (mCheckedHotelList == null) {
			mCheckedHotelList = new ArrayList<Hotel>();
		}
		if (hotel.isStatus()) {
			mCheckedHotelList.add(hotel);
		}
	}

	public void saveUploadData(HotelUploadQueneBean uploadQueneBean) {
		if (uploadQueneBean != null) {
			Gson gson = new Gson();
			String jsonString = gson.toJson(uploadQueneBean);
			FileUtil.createFile(Constance.Path.UPLOAD_TASK_DATA_PATH, false);
			FileUtil.writeStringToSdCard(Constance.Path.UPLOAD_TASK_DATA_PATH, jsonString);
		}
		
	}
	
	public HotelUploadQueneBean loadUploadData() {
		String uploadTaskJson = FileUtil.readFileToString(Constance.Path.UPLOAD_TASK_DATA_PATH);
		if (TextUtils.isEmpty(uploadTaskJson)) {
			return null;
		}
		Gson gson = new Gson();
		HotelUploadQueneBean bean = gson.fromJson(uploadTaskJson, HotelUploadQueneBean.class);
		return bean;
	}
	
//	private SparseArray<CheckData> initCheckData() {
////		List<AreaIssue> areaIssueList = AreaIssue.listAll(AreaIssue.class);
//		Iterator<AreaIssue> dataIterator = AreaIssue.findAsIterator(AreaIssue.class, null, null, null, "sort ASC", null);
//		if (dataIterator != null) {
//			SparseArray<CheckData> checkModel = new SparseArray<CheckData>();
//			while (dataIterator.hasNext()) {
//				AreaIssue areaIssue = (AreaIssue) dataIterator.next();
//				IssueItem issueItem = new IssueItem();
//				issueItem.setId(areaIssue.getIssueId());
//				issueItem.setName(areaIssue.getIssueName());
//				issueItem.setDimOneId(areaIssue.getDimOneId());
//				issueItem.setDimOneName(areaIssue.getDimOneName());
//				CheckData checkData = null;
//				if (checkModel.indexOfKey(areaIssue.getSort()) > -1) {
//					checkData = checkModel.get(areaIssue.getSort());
//				} else {
//					checkData = new CheckData();
//					checkData.setId((long) areaIssue.getAreaId());
//					checkData.setName(areaIssue.getAreaName());
//					if (checkData.getId() == Constance.CHECK_DATA_ID_ROOM) {
//						checkData.setType(Constance.CheckDataType.TYPE_ROOM);
//					} else if (checkData.getId() == Constance.CHECK_DATA_ID_PASSWAY) {
//						checkData.setType(Constance.CheckDataType.TYPE_PASSWAY);
//					} else {
//						checkData.setType(Constance.CheckDataType.TYPE_NORMAL);
//					}
//				}
//				checkData.addIssue(issueItem);
//				checkModel.put(areaIssue.getSort(), checkData);
//			}
//			return checkModel;
//		} else {
//			return null;
//		}
//		
//		
//		
//		
//	}
	
	private SparseArray<CheckData> initCheckData() {
		List<AreaIssue> areaIssueList = AreaIssue.listAll(AreaIssue.class);
		if (areaIssueList != null) {
			SparseArray<CheckData> checkModel = new SparseArray<CheckData>();
			for (AreaIssue areaIssue : areaIssueList) {
				IssueItem issueItem = new IssueItem();
				issueItem.setId(areaIssue.getIssueId());
				issueItem.setName(areaIssue.getIssueName());
				issueItem.setDimOneId(areaIssue.getDimOneId());
				issueItem.setDimOneName(areaIssue.getDimOneName());
				CheckData checkData = null;
				if (checkModel.indexOfKey(areaIssue.getSort()) > -1) {
					checkData = checkModel.get(areaIssue.getSort());
				} else {
					checkData = new CheckData();
					checkData.setId((long) areaIssue.getAreaId());
					checkData.setName(areaIssue.getAreaName());
					if (checkData.getId() == Constance.CHECK_DATA_ID_ROOM) {
						checkData.setType(Constance.CheckDataType.TYPE_ROOM);
					} else if (checkData.getId() == Constance.CHECK_DATA_ID_PASSWAY) {
						checkData.setType(Constance.CheckDataType.TYPE_PASSWAY);
					} else {
						checkData.setType(Constance.CheckDataType.TYPE_NORMAL);
					}
				}
				checkData.addIssue(issueItem);
				checkModel.put(areaIssue.getSort(), checkData);
			}
			return checkModel;
		} else {
			return null;
		}
		
		
		
		
	}
	
	
//	private SparseArray<CheckData> initCheckData() {
////		List<AreaIssue> areaIssueList = AreaIssue.listAll(AreaIssue.class);
//		Iterator<AreaIssue> dataIterator = AreaIssue.findAsIterator(AreaIssue.class, null, null, null, "sort ASC", null);
//		if (dataIterator == null) {
//			while (dataIterator.hasNext()) {
//				type type = (type) dataIterator.nextElement();
//				
//			}
//		} else {
//			return null;
//		}
//		SparseArray<CheckData> mCheckModel = new SparseArray<CheckData>();
//		for (AreaIssue areaIssue : areaIssueList) {
//			IssueItem issueItem = new IssueItem();
//			issueItem.setId(areaIssue.getIssueId());
//			issueItem.setName(areaIssue.getIssueName());
//			issueItem.setDimOneId(areaIssue.getDimOneId());
//			issueItem.setDimOneName(areaIssue.getDimOneName());
//			CheckData checkData;
//			if (mCheckModel.indexOfKey(areaIssue.getAreaId()) > -1) {
//				checkData = mCheckModel.get(areaIssue.getAreaId());
//			} else {
//				checkData = new CheckData();
//				checkData.setId((long) areaIssue.getAreaId());
//				checkData.setName(areaIssue.getAreaName());
//				if (checkData.getId() == Constance.CHECK_DATA_ID_ROOM) {
//					checkData.setType(Constance.CheckDataType.TYPE_ROOM);
//				} else if (checkData.getId() == Constance.CHECK_DATA_ID_PASSWAY) {
//					checkData.setType(Constance.CheckDataType.TYPE_PASSWAY);
//				} else {
//					checkData.setType(Constance.CheckDataType.TYPE_NORMAL);
//				}
//			}
//			checkData.addIssue(issueItem);
//			mCheckModel.put(areaIssue.getAreaId(), checkData);
//		}
//		
//		return mCheckModel;
//	}
	
	private void initHotelData(List<Hotel> hoteList) {
		if (hoteList == null ) {
			return;
		}
		for (Hotel hotel : hoteList) {
			SparseArray<CheckData> checkModel = initCheckData();
			if (checkModel == null) {
				return;
			}
			//init normal checkData
			for (int i = 0; i < checkModel.size(); i++) {
				CheckData checkData = checkModel.valueAt(i);
				if (checkData.getType() != Constance.CheckDataType.TYPE_ROOM
						&& checkData.getType() != Constance.CheckDataType.TYPE_PASSWAY) {
					initCheckDataIssue(hotel, checkData, hotel.getCheckId());
				}
				hotel.addCheckData(checkData);
			}
			//恢复上次保存记录的动态区域(客房，走廊)的数据
			List<CheckData> dymicCheckData = CheckData.find(CheckData.class, "CHECK_ID = ?", String.valueOf(hotel.getCheckId()));
			if (dymicCheckData != null) {
				for (CheckData checkData : dymicCheckData) {
					CheckData tempCheckData = null;
					if (checkData.getType() == Constance.CheckDataType.TYPE_ROOM) {
						tempCheckData = createRoomCheckData();
					} else if (checkData.getType() == Constance.CheckDataType.TYPE_PASSWAY) {
						tempCheckData = createPasswayCheckData();
					}
					if (tempCheckData != null) {
						checkData.setIssuelist(tempCheckData.getIssuelist());
						//初始化之前检查过的问题数据
						initCheckDataIssue(hotel, checkData, hotel.getCheckId());
						checkData.initCheckedIssue();
						if (checkData.getType() == Constance.CheckDataType.TYPE_ROOM) {
							if (hotel.hasRoom(checkData.getId())) {
								hotel.setRoom(checkData.getId(), checkData);
							} else {
								hotel.addRoom(checkData);
							}
						} else if (checkData.getType() == Constance.CheckDataType.TYPE_PASSWAY) {
							if (hotel.hasPassway(checkData.getId())) {
								hotel.setPassway(checkData.getId(), checkData);
							} else {
								hotel.addPassway(checkData);
							}
						}
					}
				}
			}
			//初始化复检问题
			initQuestion(hotel);
			//初始化之前检查过的动态区域的问题数据
			hotel.initCheckedData();
			if (mHotelDataList == null) {
				mHotelDataList = new ArrayList<Hotel>();
			}
			mHotelDataList.add(hotel);
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
	
	
	private void initCheckDataIssue(Hotel hotel, CheckData checkData , int checkId) {
		if (checkData == null) {
			return;
		}
		//动态问题
		List<DymicIssue> dataList = DymicIssue.find(DymicIssue.class, "CHECK_ID = ? and AREA_ID = ?", String.valueOf(checkId), String.valueOf(checkData.getId()));
		if (dataList != null) {
			for (DymicIssue dymicIssue : dataList) {
				IssueItem issueItem = new IssueItem(dymicIssue);
				checkData.addIssue(issueItem);
				if (hotel != null) {
					if (checkData.getType() == CheckDataType.TYPE_ROOM) {
						hotel.addRoomDymicIssue(issueItem);
					} else if (checkData.getType() == CheckDataType.TYPE_PASSWAY) {
						hotel.addPasswayDymicIssue(issueItem);
					}
				}
			}
		}
		//初始化上次保存的检查点拍摄的图片
		for (IssueItem issueItem : checkData.getIssuelist()) {
			List<IssueCheckedImage> hotelCheckList = IssueCheckedImage.find(
					IssueCheckedImage.class,
					"CHECK_ID = ? and AREA_ID = ? and ISSUE_ID = ?",
					String.valueOf(checkId),
					String.valueOf(checkData.getId()),
					String.valueOf(issueItem.getId()));
			for (IssueCheckedImage hotelCheck : hotelCheckList) {
				if (issueItem.getId() == hotelCheck.getIssueId()) {
					ImageItem imageItem = new ImageItem();
					imageItem.setLocalImagePath(hotelCheck
							.getLocalImagePath());
					imageItem.setServiceSavePath(hotelCheck
							.getServiceImagePath());
					issueItem.addImage(imageItem);
				}
			}
			//问题是否已经检查过
			CheckIssue checkIssue = getCheckIssue(checkId,
					checkData.getId(), issueItem.getId());
			if (checkIssue != null) {
				issueItem.setCheck(checkIssue.isCheck());
				issueItem.setContent(checkIssue.getContent());
//				if (checkData.getType() == CheckDataType.TYPE_NORMAL) {
				Log.d("lxk", "hotel checked issue hotel name = "+hotel.getName()+" checkData = "+checkData.getName()+ " issue name = "+issueItem.getName()+" issue reform status = "+checkIssue.getReformState()
						+" isCheck = "+checkIssue.isCheck());
				issueItem.setReformState(checkIssue.getReformState());
				Log.d("lxk", "set reform hotel checked issue hotel name = "+hotel.getName()+" checkData = "+checkData.getName()+ " issue name = "+issueItem.getName()+" issue reform status = "+checkIssue.getReformState()
						+" isCheck = "+checkIssue.isCheck());
//				}
			}
		}
		checkData.initCheckedIssue();
	}
	
	private void initQuestion(Hotel hotel) {
		if (hotel.getQuestionList() != null) {
			for (AreaIssue areaIssue : hotel.getQuestionList()) {
				if (areaIssue.getType() == CheckDataType.TYPE_ROOM) {
					initQuestionCheckData(hotel.getCheckId(), areaIssue, hotel.getRoomList());
				} else if (areaIssue.getType() == CheckDataType.TYPE_PASSWAY) {
					initQuestionCheckData(hotel.getCheckId(), areaIssue, hotel.getPasswayList());
				} else {
					initQuestionCheckData(hotel.getCheckId(), areaIssue, hotel.getCheckDatas());
				}
				
			}
		}
	}
	
//	private void initDymicQuestionCheckData(int checkId, AreaIssue areaIssue, List<CheckData> dataList) {
//		if (dataList == null) {
//			return;
//		}
//		for (CheckData checkData : dataList) {
//				if (areaIssue.getIsDefQue() == DefQueType.TYPE_DYMIC) {
//					
//					IssueItem issueItem = new IssueItem();
//					issueItem.setId(areaIssue.getIssueId());
//					issueItem.setName(areaIssue.getIssueName());
//					issueItem.setIsPreQue(areaIssue.getIsPreQue());
//					issueItem.setIsDefQue(areaIssue.getIsDefQue());
//					//问题是否已经检查过
//					CheckIssue checkIssue = getCheckIssue(checkId,
//							checkData.getId(), areaIssue.getIssueId());
//					if (checkIssue != null) {
//						issueItem.setCheck(checkIssue.isCheck());
//						issueItem.setContent(checkIssue.getContent());
//						issueItem.setReformState(checkIssue.getReformState());
//					} 
//					checkData.addIssue(issueItem);
//				} else {
//					if (areaIssue.getIsPreQue() == PreQueType.TYPE_REVIEW) {
//						for (IssueItem issueItem : checkData.getIssuelist()) {
//							if (issueItem.getId() == areaIssue.getIssueId()) {
//								issueItem.setIsPreQue(areaIssue.getIsPreQue());
//								issueItem.setIsDefQue(areaIssue.getIsDefQue());
////								if (issueItem.getReformState() == IssueItem.REFORM_STATE_DEFAULT) {
////									issueItem.setReformState(IssueItem.REFORM_STATE_FIXED);
////								}
////								issueItem.setCheck(issueItem.isCheck());
//							}
//						}
//					} 
//				}
//				checkData.initCheckedIssue();
//		}
//	}
	
	private void initQuestionCheckData(int checkId, AreaIssue areaIssue, List<CheckData> dataList) {
		if (dataList == null) {
			return;
		}
		for (CheckData checkData : dataList) {
			if (checkData.getId() == areaIssue.getAreaId() 
					|| (areaIssue.getAreaId() == Constance.CHECK_DATA_ID_ROOM && checkData.getName().contains("客房"))
					|| (areaIssue.getAreaId() == Constance.CHECK_DATA_ID_PASSWAY && checkData.getName().contains("走廊"))) {
				if (areaIssue.getIsDefQue() == DefQueType.TYPE_DYMIC) {
					IssueItem issueItem = new IssueItem();
					issueItem.setId(areaIssue.getIssueId());
					issueItem.setName(areaIssue.getIssueName());
					issueItem.setPreQueType(areaIssue.getIsPreQue());
					issueItem.setIsDefQue(areaIssue.getIsDefQue());
					//问题是否已经检查过
					CheckIssue checkIssue = getCheckIssue(checkId,
							checkData.getId(), areaIssue.getIssueId());
					if (checkIssue != null) {
						issueItem.setCheck(checkIssue.isCheck());
						issueItem.setContent(checkIssue.getContent());
						issueItem.setReformState(checkIssue.getReformState());
					} 
					checkData.addIssue(issueItem);
				} else {
					if (areaIssue.getIsPreQue() == PreQueType.TYPE_REVIEW) {
						for (IssueItem issueItem : checkData.getIssuelist()) {
							if (issueItem.getId() == areaIssue.getIssueId()) {
								issueItem.setIsDefQue(areaIssue.getIsDefQue());
								if (checkData.getType() == CheckDataType.TYPE_PASSWAY 
										|| checkData.getType() == CheckDataType.TYPE_ROOM) {
									issueItem.setPreQueType(areaIssue.getIsPreQue());
								} else {
									if (issueItem.getReformState() != IssueItem.REFORM_STATE_DEFAULT) {
										issueItem.setPreQueTypeOnly(areaIssue.getIsPreQue());
									} else {
										issueItem.setPreQueType(areaIssue.getIsPreQue());
									}
								}
//								if (issueItem.getReformState() == IssueItem.REFORM_STATE_DEFAULT) {
//									issueItem.setReformState(IssueItem.REFORM_STATE_FIXED);
//								}
//								issueItem.setCheck(issueItem.isCheck());
							}
						}
					} 
				}
				checkData.initCheckedIssue();
			}
		}
	}
	
	
	public void loadCheckData(Context context, final DataCallback callback) {
		HttpRequest.getInstance().getCheckDataList(context, getSession(), new HttpCallback() {
			
			@Override
			public void onSuccess(JSONObject response) {
				if (response != null) {
					JSONArray jsonArray = response.optJSONArray(NetConstance.PARAM_DIM_LIST);
					if (jsonArray != null) {
						AreaIssue.deleteAll(AreaIssue.class);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.optJSONObject(i);
							AreaIssue areaIssue = JsonParseHandler.parseAreaIssue(jsonObject);
							if (areaIssue != null) {
								areaIssue.save();
							}
						}
						initCheckData();
					}
				}
				callback.onSuccess();
			}
			
			@Override
			public void onError(int errorCode, String info) {
				callback.onFail(errorCode, info);
			}
		});
	}

	public void loadHotelData(Context context, final DataCallback callback) {
		HttpRequest.getInstance().getHotelList(context, getSession(), new HttpCallback() {
			
			@Override
			public void onSuccess(JSONObject response) {
				if (response != null) {
					JSONArray jsonArray = response.optJSONArray(NetConstance.PARAM_BRANCH_LIST);
					if (jsonArray != null) {
						List<Hotel> hotelList = new ArrayList<Hotel>();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.optJSONObject(i);
							 Hotel hotelTemp = JsonParseHandler.parseHotel(jsonObject);
							 if (hotelTemp != null) {
								 //初始化保存的酒店基本数据
								 Hotel hotel = Hotel.findById(Hotel.class, hotelTemp.getId());
								 if (hotel != null) {
									 hotelTemp.setBaseInfo(hotel);
								} 
								 //for test 
//								hotelTemp.setStatus(false);
//								hotelTemp.setDataStatus(false);
//								hotelTemp.setImageStatus(false);
								 
								hotelList.add(hotelTemp);
							}
						}
						initHotelData(hotelList);
					}
				}
				callback.onSuccess();
			}
			
			@Override
			public void onError(int errorCode, String info) {
				callback.onFail(errorCode, info);
				
			}
		});
	}
	
	
	public void saveIssueImage(int checkId, int areaId, String areaName, int issueId, ImageItem imageItem) {
		if (imageItem == null) {
			return;
		}
		IssueCheckedImage hotelCheck = new IssueCheckedImage();
		hotelCheck.setCheckId(checkId);
		hotelCheck.setAreaId(areaId);
		hotelCheck.setAreaName(areaName);
		hotelCheck.setIssueId(issueId);
		hotelCheck.setLocalImagePath(imageItem.getLocalImagePath());
		hotelCheck.setServiceImagePath(imageItem.getServiceSavePath());
		hotelCheck.save();
	}
	
	public void saveIssueContent(int checkId, int areaId, int issueId, String content, boolean isCheck, int reformState) {
		if (content == null) {
			return;
		}
		CheckIssue checkIssue = null;
		List<CheckIssue> checkIssueList = CheckIssue.find(CheckIssue.class, "CHECK_ID = ? and AREA_ID = ? and ISSUE_ID = ?", ""+checkId, ""+areaId, ""+issueId);
		if (checkIssueList == null || checkIssueList.size() == 0) {
			checkIssue = new CheckIssue();
			checkIssue.setCheckId(checkId);
			checkIssue.setAreaId(areaId);
			checkIssue.setIssueId(issueId);
		} else {
			checkIssue = checkIssueList.get(0);
		}
		checkIssue.setCheck(isCheck);
		checkIssue.setContent(content);
		checkIssue.setReformState(reformState);
		checkIssue.save();
	}
	
	
	public void saveIssueCheck(int checkId, int areaId, int issueId, boolean isCheck, int reformState) {
		CheckIssue checkIssue = null;
		List<CheckIssue> checkIssueList = CheckIssue.find(CheckIssue.class, "CHECK_ID = ? and AREA_ID = ? and ISSUE_ID  = ?", ""+checkId, ""+areaId, ""+issueId);
		if (checkIssueList == null || checkIssueList.size() == 0) {
			checkIssue = new CheckIssue();
			checkIssue.setCheckId(checkId);
			checkIssue.setAreaId(areaId);
			checkIssue.setIssueId(issueId);
		} else {
			checkIssue = checkIssueList.get(0);
		}
		checkIssue.setCheck(isCheck);
		checkIssue.setReformState(reformState);
		checkIssue.save();
	}
	
	public boolean isIssueCheck(int checkId, int areaId, int issueId ) {		
		List<CheckIssue> checkIssueList = CheckIssue.find(CheckIssue.class, "CHECK_ID = ? and AREA_ID = ? and ISSUE_ID = ?", ""+checkId, ""+areaId, ""+issueId);
		if (checkIssueList == null || checkIssueList.size() == 0) {
			return false;
		} 
		CheckIssue checkIssue = checkIssueList.get(0);
		return checkIssue.isCheck();
	}
	
	public String getIssueContent(int checkId, long areaId, int issueId ) {
		List<CheckIssue> checkIssueList = CheckIssue.find(CheckIssue.class, "CHECK_ID = ? and AREA_ID = ? and ISSUE_ID = ?", ""+checkId, ""+areaId, ""+issueId);
		if (checkIssueList == null || checkIssueList.size() == 0) {
			return null;
		} 
		String content = null;
		CheckIssue checkIssue = checkIssueList.get(0);
		content = checkIssue.getContent();
		return content;
	}
	
	public CheckIssue getCheckIssue(int checkId, long areaId, int issueId ) {
		List<CheckIssue> checkIssueList = CheckIssue.find(CheckIssue.class, "CHECK_ID = ? and AREA_ID = ? and ISSUE_ID = ?", ""+checkId, ""+areaId, ""+issueId);
		if (checkIssueList == null || checkIssueList.size() == 0) {
			return null;
		} 
		CheckIssue checkIssue = checkIssueList.get(0);
		return checkIssue;
	}

	public CheckData createRoomCheckData() {
		CheckData checkData = createDymicCheckData(Constance.CHECK_DATA_ID_ROOM);
		if (checkData != null) {
			checkData.setType(CheckDataType.TYPE_ROOM);
		}
		return checkData;
	}
	
	public CheckData createPasswayCheckData() {
		CheckData checkData = createDymicCheckData(Constance.CHECK_DATA_ID_PASSWAY);
		if (checkData != null) {
			checkData.setType(CheckDataType.TYPE_PASSWAY);
		}
		return checkData;
	}
	
	public CheckData createDymicCheckData(int checkDataId) {
		CheckData checkData = null;
		List<AreaIssue> areaIssueList = AreaIssue.find(AreaIssue.class, "AREA_ID = ? ", String.valueOf(checkDataId));
		if (areaIssueList != null && areaIssueList.size() > 0) {
			checkData = new CheckData();
			for (AreaIssue areaIssue : areaIssueList) {
				IssueItem issueItem = new IssueItem();
				issueItem.setId(areaIssue.getIssueId());
				issueItem.setName(areaIssue.getIssueName());
				issueItem.setDimOneId(areaIssue.getDimOneId());
				issueItem.setDimOneName(areaIssue.getDimOneName());
				checkData.addIssue(issueItem);
			}

		}
//		checkData.setId(System.currentTimeMillis());
		return checkData;
	}

	public List<UploadBean> getUploadingList(long checkId) {
		List<UploadBean> dataList  = null;
		if (checkId == -1) {
			//get all uploading data 
			dataList = UploadBean.find(UploadBean.class,
					"IMAGE_STATE != ?", 
					String.valueOf(ImageUploadState.STATE_FINISH));
		} else {
			dataList = UploadBean.find(UploadBean.class,
					"CHECK_ID = ? and IMAGE_STATE != ?", String.valueOf(checkId),
					String.valueOf(ImageUploadState.STATE_FINISH));
		}
		return dataList;
	}

	public List<UploadBean> getUploadTaskList(long checkId) {
		return UploadBean.find(UploadBean.class, "CHECK_ID = ?", String.valueOf(checkId));
	}

	public List<UploadBean> getUploadCompleteList(long checkId) {
		List<UploadBean> dataList  = null;
		if (checkId == -1) {
			dataList = UploadBean.find(UploadBean.class,
					"IMAGE_STATE = ?",
					String.valueOf(ImageUploadState.STATE_FINISH));
		} else {
			dataList = UploadBean.find(UploadBean.class,
					"CHECK_ID = ? and IMAGE_STATE = ?", String.valueOf(checkId),
					String.valueOf(ImageUploadState.STATE_FINISH));
		}
		
		return dataList;
	}

	public void updateImageStatus(Context context, UploadBean uploadBean) {
		if (uploadBean != null) {
			HttpRequest.getInstance().updateImageStatus(context, uploadBean, getSession(), new HttpCallback() {
				
				@Override
				public void onSuccess(JSONObject response) {
					
				}
				
				@Override
				public void onError(int errorCode, String info) {
					
				}
			});
		}
		
	}

//	public User getUser() {
//		return mUser;
//	}
//
	public void setUser(User user) {
		this.mUser = user;
	}
	
	public long getDate() {
		return mUser == null ? 0 : mUser.getDate();
	}
	
	public String getUserName() {
		if (mUser == null) {
			return "";
		}
		return mUser.getUserName();
	}

	public void updateCheckedHotelList(Hotel hotel) {
		if (mCheckedHotelList == null) {
			mCheckedHotelList = new ArrayList<Hotel>();
		}
		if (hotel.isStatus()) {
			mCheckedHotelList.add(hotel);
		}
		for (int i = 0 ;i< mUnCheckedHoteList.size(); i++) {
			Hotel temp = mUnCheckedHoteList.get(i);
			if (temp.getId() == hotel.getId()) {
				mUnCheckedHoteList.remove(i);
				return;
			}
		}
	}

	public Hotel getHotelbyId(long checkId) {
		if (mHotelDataList != null) {
			for (Hotel hotel : mHotelDataList) {
				if(hotel.getCheckId() == checkId) {
					return hotel;
				};
			}
		}
		return null;
	}

	public String getSession() {
		return mUser == null ? "" : mUser.getToken();
	}

	public void updateHotelImageStatus() {
		// TODO Auto-generated method stub
		
	}

	public String getToken(Context context) {
		if (context != null) {
			return SharedPreferencesUtil.getString(context.getApplicationContext(), NetConstance.PARAM_SESSION);
		} else {
			return "";
		}
	}
	
	public void saveToken(Context context, String token) {
		SharedPreferencesUtil.putString(context, NetConstance.PARAM_SESSION, token);
	}

	public void resetUser() {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
