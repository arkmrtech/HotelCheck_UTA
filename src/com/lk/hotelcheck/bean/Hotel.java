package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

import com.lk.hotelcheck.bean.dao.AreaIssue;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import common.Constance;

public class Hotel extends SugarRecord<Hotel>{

//	private int id;/
	private String name;
	private String address;
	private String phone;
	private String memo;
	private String openDate;
	private String lastCheckedDate;
	private String checkDate;
	private int roomCount;
	private int roomCheckedCount;
	private String floorStart;
	private String floorEnd;
	private boolean imageStatus;
	private boolean dataStatus;
	private boolean status;
	private String guardianNumber;
	private int checkType;
	@Ignore
	private List<CheckData> checkDatas;
//	@Ignore
//	private transient SparseArray<CheckData> roomArray;
//	@Ignore
//	private transient SparseArray<CheckData> passwayArray;
	@Ignore
	private transient List<CheckData> roomArray;
	@Ignore
	private transient List<CheckData> passwayArray;
	@Ignore
	private transient List<AreaIssue> questionList;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCheckId() {
		return getId().intValue();
	}
	public void setCheckId(int id) {
		setId((long) id);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getLastCheckedDate() {
		return lastCheckedDate;
	}
	public void setLastCheckedDate(String lastCheckedDate) {
		this.lastCheckedDate = lastCheckedDate;
	}
	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	public int getRoomCount() {
		return roomCount;
	}
	public void setRoomCount(int roomCount) {
		this.roomCount = roomCount;
	}
	public int getRoomCheckedCount() {
		return roomCheckedCount;
	}
	public void setRoomCheckedCount(int roomCheckedCount) {
		this.roomCheckedCount = roomCheckedCount;
	}
//	public int getFloorStart() {
//		return floorStart;
//	}
//	public void setFloorStart(int floorStart) {
//		this.floorStart = floorStart;
//	}
//	public int getFloorEnd() {
//		return floorEnd;
//	}
//	public void setFloorEnd(int floorEnd) {
//		this.floorEnd = floorEnd;
//	}
	public boolean isImageStatus() {
		return imageStatus;
	}
	public void setImageStatus(boolean imageStatus) {
		this.imageStatus = imageStatus;
	}
	public boolean isDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(boolean dataStatus) {
		this.dataStatus = dataStatus;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public List<CheckData> getCheckDatas() {
		return checkDatas;
	}
	public void setCheckDatas(List<CheckData> checkDatas) {
		this.checkDatas = checkDatas;
	}
	
	
	public int getCheckDataCount() {
		int count = checkDatas == null ? 0 : checkDatas.size();
		return count;
	}
	
	public CheckData getCheckData(int position) {
		if (checkDatas == null || position < 0 || checkDatas.size() <= position) {
			return null;
		}
		return checkDatas.get(position);
	}
	
	public void setCheckDatas(CheckData mCheckData, int mCheckDataPosition) {
		if (checkDatas == null) {
			checkDatas = new ArrayList<CheckData>();
		}
		checkDatas.set(mCheckDataPosition, mCheckData);
		
	}
	
	public int getRoomInUseCount() {
		return roomCheckedCount;
	}
	
	public int getRoomHadCheckedCount() {
		return roomArray == null ? 0 : roomArray.size();
	}
	
//	public int getIssueCount() {
//		int count = 0;
//		for (CheckData checkData : checkDatas) {
//				count += checkData.getCheckedIssueCount();
//		}
//		if (roomArray != null) {
//			for (int i = 0; i < roomArray.size() ; i++) {
//				CheckData checkData = roomArray.valueAt(i);
//				count += checkData.getCheckedIssueCount();
//			}
//		}
//		if (passwayArray != null) {
//			for (int i = 0; i < passwayArray.size() ; i++) {
//				CheckData checkData = passwayArray.valueAt(i);
//				count += checkData.getCheckedIssueCount();
//			}
//		}
//		return count;
//	}
//	
//	public int getImageCount() {
//		int count = 0;
//		for (CheckData checkData : checkDatas) {
//				for (IssueItem issueItem : checkData.getIssuelist()) {
//					if (issueItem.isCheck()) {
//						count += issueItem.getImageCount();
//					}
//				}
//		}
//		if (roomArray != null) {
//			for (int i = 0; i < roomArray.size() ; i++) {
//				CheckData checkData = roomArray.valueAt(i);
//				for (IssueItem issueItem : checkData.getIssuelist()) {
//					if (issueItem.isCheck()) {
//						count += issueItem.getImageCount();
//					}
//				}
//			}
//		}
//		if (passwayArray != null) {
//			for (int i = 0; i < passwayArray.size() ; i++) {
//				CheckData checkData = passwayArray.valueAt(i);
//				for (IssueItem issueItem : checkData.getIssuelist()) {
//					if (issueItem.isCheck()) {
//						count += issueItem.getImageCount();
//					}
//				}
//			}
//		}
//		return count;
//	}
	
	public int getIssueCount() {
		int count = 0;
		for (CheckData checkData : checkDatas) {
				count += checkData.getCheckedIssueCount();
		}
		if (roomArray != null) {
			for (CheckData checkData : roomArray) {
				count += checkData.getCheckedIssueCount();
			}
		}
		if (passwayArray != null) {
			for (CheckData checkData : passwayArray) {
				count += checkData.getCheckedIssueCount();
			}
		}
		return count;
	}
	
	public int getImageCount() {
		int count = 0;
		for (CheckData checkData : checkDatas) {
				for (IssueItem issueItem : checkData.getIssuelist()) {
					if (issueItem.isCheck()) {
						count += issueItem.getImageCount();
					}
				}
		}
		if (roomArray != null) {
			for (CheckData checkData : roomArray) {
				for (IssueItem issueItem : checkData.getIssuelist()) {
					if (issueItem.isCheck()) {
						count += issueItem.getImageCount();
					}
				}
			}
		}
		if (passwayArray != null) {
			for (CheckData checkData : passwayArray) {
				for (IssueItem issueItem : checkData.getIssuelist()) {
					if (issueItem.isCheck()) {
						count += issueItem.getImageCount();
					}
				}
			}
		}
		return count;
	}
	
	public String getGuardianNumber() {
		return guardianNumber;
	}
	
	public void setGuardianNumber(String guardianNumber) {
		this.guardianNumber = guardianNumber;
	}
	
	public List<ImageItem> getAllImage() {
		List<ImageItem> allImageItems = new ArrayList<ImageItem>();
		for (CheckData checkData : checkDatas) {
				for (IssueItem issueItem : checkData.getIssuelist()) {
					if (issueItem.isCheck()) {
						if (issueItem.getImageCount() >0) {
							allImageItems.addAll(issueItem.getImagelist());
						}
					}
				}
		}
		return allImageItems;
	}
	public String getFloorStart() {
		return floorStart;
	}
	public void setFloorStart(String floorStart) {
		this.floorStart = floorStart;
	}
	public String getFloorEnd() {
		return floorEnd;
	}
	public void setFloorEnd(String floorEnd) {
		this.floorEnd = floorEnd;
	}
	
	
	
	public void addCheckData(CheckData checkData) {
		if (checkDatas == null) {
			checkDatas = new ArrayList<CheckData>();
		}
		checkDatas.add(checkData);
	}
	
	
//	public void addRoom(CheckData checkData) {
//		if (checkData == null) {
//			return;
//		}
//		if (roomArray == null) {
//			roomArray = new SparseArray<CheckData>();
//		}
//		roomArray.
//		roomArray.put(checkData.getId().intValue(), checkData);
//	}
//	
//	public CheckData getRoom(String areaName) {
//		if (roomArray != null && roomArray.indexOfKey(areaName.hashCode()) > -1) {
//			return roomArray.get(areaName.hashCode());
//		}
//		return null;
//	}
//	
//	public boolean hasRoom(int areaId) {
//		if (roomArray != null && roomArray.indexOfKey(areaId) > -1) {
//			return true;
//		}
//		return false;
//	}
//	
//	public void addPassway(CheckData checkData) {
//		if (checkData == null) {
//			return;
//		}
//		if (passwayArray == null) {
//			passwayArray = new SparseArray<CheckData>();
//		}
//		passwayArray.put(checkData.getId().intValue(), checkData);
//	}
//	
//	public boolean hasPassway(int areaId) {
//		if (passwayArray != null && passwayArray.indexOfKey(areaId) > -1) {
//			return true;
//		}
//		return false;
//	}
//	
//	public CheckData getPassway(String areaName) {
//		if (passwayArray != null && passwayArray.indexOfKey(areaName.hashCode()) > -1) {
//			return passwayArray.get(areaName.hashCode());
//		}
//		return null;
//	}
	
	public void addRoom(CheckData checkData) {
		if (checkData == null) {
			return;
		}
		if (roomArray == null) {
			roomArray = new ArrayList<CheckData>();
		}
		roomArray.add(checkData);
	}
	
//	public CheckData getRoom(String areaName) {
//		if (roomArray != null && roomArray.indexOfKey(areaName.hashCode()) > -1) {
//			return roomArray.get(areaName.hashCode());
//		}
//		return null;
//	}
	
	public boolean hasRoom(long areaId) {
		if (roomArray != null) {
			for (CheckData checkData : roomArray) {
				if (checkData.getId() == areaId) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addPassway(CheckData checkData) {
		if (checkData == null) {
			return;
		}
		if (passwayArray == null) {
			passwayArray = new ArrayList<CheckData>();
		}
		passwayArray.add(checkData);
	}
	
	public boolean hasPassway(long areaId) {
		if (passwayArray != null) {
			for (CheckData checkData : passwayArray) {
				if (checkData.getId() == areaId) {
					return true;
				}
			}
		}
		return false;
	}
	
//	public CheckData getPassway(String areaName) {
//		if (passwayArray != null && passwayArray.indexOfKey(areaName.hashCode()) > -1) {
//			return passwayArray.get(areaName.hashCode());
//		}
//		return null;
//	}
	
	public int getDymicRoomCount() {
		return roomArray == null ? 0 : roomArray.size();
	}
	
	public int getDymicPasswayCount() {
		return passwayArray == null ? 0 : passwayArray.size();
	}
	public CheckData getDymicRoomData(int position) {
		if (roomArray == null) {
			return null;
		}
		return roomArray.get(position);
	}
	public CheckData getDymicPasswayData(int position) {
		if (passwayArray == null) {
			return null;
		}
		return passwayArray.get(position);
	}
	public void removeDymicRoom(int position) {
		if (roomArray != null) {
			CheckData checkData = roomArray.get(position);
			checkData.delete();
			roomArray.remove(position);
		}
	}
	public void removeDymicPassway(int position) {
		if (passwayArray != null) {
			CheckData checkData = passwayArray.get(position);
			checkData.delete();
			passwayArray.remove(position);
		}
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	
	public CheckData getRoomData(int position) {
		if (roomArray != null) {
			return roomArray.get(position);
		}
		return null;
	}
	
	public CheckData getPasswayData(int position) {
		if (passwayArray != null) {
			return passwayArray.get(position);
		}
		return null;
	}
	
	
	public CheckData getCheckData(int type, int position) {
		switch (type) {
		case Constance.CheckDataType.TYPE_NORMAL:
			return getCheckData(position);
		case Constance.CheckDataType.TYPE_ROOM:
			return getRoomData(position);
		case Constance.CheckDataType.TYPE_PASSWAY:
			return getPasswayData(position);
		default:
			return null;
		}
	}
	
	@Override
	public long save() {
		if (roomArray != null) {
			for (int i = 0; i < roomArray.size(); i++) {
				roomArray.get(i).save();
			}
		}

		if (passwayArray != null) {
			for (int i = 0; i < passwayArray.size(); i++) {
				passwayArray.get(i).save();
			}
		}
		return super.save();
	}
	public void addQuestion(AreaIssue issueItem) {
		if (issueItem == null) {
			return;
		}
		if (questionList == null) {
			questionList = new ArrayList<AreaIssue>();
		}
		questionList.add(issueItem);
	}
	public int getCheckType() {
		return checkType;
	}
	public void setCheckType(int checkType) {
		this.checkType = checkType;
	}
	
	public List<AreaIssue> getQuestionList() {
		return questionList;
	}
	public List<CheckData> getRoomList() {
		if (roomArray == null) {
			return Collections.emptyList();
		}
		return roomArray;
	}
	public List<CheckData> getPasswayList() {
		if (passwayArray == null) {
			return Collections.emptyList();
		}
		return passwayArray;
	}
	public void setBaseInfo(Hotel hotel) {
		if (hotel == null) {
			return;
		}
		this.roomCount = hotel.getRoomCount();
		this.roomCheckedCount = hotel.getRoomCount();
		this.floorStart = hotel.getFloorStart();
		this.floorEnd = hotel.getFloorEnd();
		this.imageStatus = hotel.isImageStatus();
		this.guardianNumber = hotel.getGuardianNumber();
	}
	public void setRoom(long areaId, CheckData checkData) {
		if (roomArray == null) {
			roomArray = new ArrayList<CheckData>();
		}
		for (int i = 0; i < roomArray.size(); i++) {
			CheckData roomData = roomArray.get(i);
			if (roomData.getId() == areaId) {
				roomArray.set(i, checkData);
			}
		}
		
	}
	public void setPassway(long areaId, CheckData checkData) {
		if (passwayArray == null) {
			passwayArray = new ArrayList<CheckData>();
		}
		for (int i = 0; i < passwayArray.size(); i++) {
			CheckData passwayData = passwayArray.get(i);
			if (passwayData.getId() == areaId) {
				passwayArray.set(i, checkData);
			}
		}
	}
	
	public int getFixedIssueCount() {
		int count = 0;
		for (CheckData checkData : checkDatas) {
			count += checkData.getFixedIssueCount();
		}
		if (roomArray != null) {
			for (CheckData checkData : roomArray) {
				count += checkData.getFixedIssueCount();
			}
		}
		if (passwayArray != null) {
			for (CheckData checkData : passwayArray) {
				count += checkData.getFixedIssueCount();
			}
		}
		return count;
	}
	public int getFixingIssueCount() {
		int count = 0;
		return count;
	}
	public int getNewIssueCount() {
		int count = 0;
		return count;
	}
	
	public String getRoomIssuePercent(int issueId) {
		if (roomArray == null ) {
			return "";
		}
		String percent = "";
		int count = 0;
		for (CheckData checkData : roomArray) {
			for (IssueItem issueItem : checkData.getCheckedIssue()) {
				if (issueItem.getId() == issueId) {
					count++;
					break;
				}
			}
		}
		int total = roomArray.size();
		if (count >0 && total >0) {
			
			percent = String.format(Locale.CHINA, "%.2f%s", (count*1.00) /total * 100, "%");
		}
		return percent ;
	}
	
	public String getPasswayIssuePercent(int issueId) {
		if (passwayArray == null ) {
			return "";
		}
		String percent = "";
		int count = 0;
		for (CheckData checkData : passwayArray) {
			for (IssueItem issueItem : checkData.getCheckedIssue()) {
				if (issueItem.getId() == issueId) {
					count++;
					break;
				}
			}
		}
		int total = passwayArray.size();
		if (count >0 && total >0) {
			
			percent = String.format(Locale.CHINA, "%.2f%s", (count*1.00) /total * 100, "%");
		}
		return percent ;
	}
	
	
	
}
