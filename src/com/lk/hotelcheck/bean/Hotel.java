package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.List;

import android.media.Image;

public class Hotel {

	private int id;
	private String name;
	private String address;
	private String phone;
	private String memo;
	private String lastCheckedDate;
	private String checkDate;
	private int roomCount;
	private int roomCheckedCount;
	private int floorStart;
	private int floorEnd;
	private boolean imageStatus;
	private boolean dataStatus;
	private boolean status;
	private String guardianNumber;
	private List<CheckData> checkDatas;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getFloorStart() {
		return floorStart;
	}
	public void setFloorStart(int floorStart) {
		this.floorStart = floorStart;
	}
	public int getFloorEnd() {
		return floorEnd;
	}
	public void setFloorEnd(int floorEnd) {
		this.floorEnd = floorEnd;
	}
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
		int count = 0;
		for (CheckData checkData : checkDatas) {
			if (checkData.getId() == 8006) {
				count = checkData.getSubCheckDataCount();
			}
		}
		return count;
	}
	
	public int getIssueCount() {
		int count = 0;
		for (CheckData checkData : checkDatas) {
			if (checkData.isGetSublist()) {
				for (CheckData subCheckData : checkData.getSublist()) {
					for (IssueItem issueItem : subCheckData.getIssuelist()) {
						if (issueItem.isCheck()) {
							count++;
						}
					}
				}
			} else {
				for (IssueItem issueItem : checkData.getIssuelist()) {
					if (issueItem.isCheck()) {
						count++;
					}
				}
			}
		}
		return count;
	}
	public int getImageCount() {
		int count = 0;
		for (CheckData checkData : checkDatas) {
			if (checkData.isGetSublist()) {
				for (CheckData subCheckData : checkData.getSublist()) {
					for (IssueItem issueItem : subCheckData.getIssuelist()) {
						if (issueItem.isCheck()) {
							count += issueItem.getImageCount();
						}
					}
				}
			} else {
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
			if (checkData.isGetSublist()) {
				for (CheckData subCheckData : checkData.getSublist()) {
					for (IssueItem issueItem : subCheckData.getIssuelist()) {
						if (issueItem.isCheck()) {
							if (issueItem.getImageCount() >0) {
								allImageItems.addAll(issueItem.getImagelist());
							}
						}
					}
				}
			} else {
				for (IssueItem issueItem : checkData.getIssuelist()) {
					if (issueItem.isCheck()) {
						if (issueItem.getImageCount() >0) {
							allImageItems.addAll(issueItem.getImagelist());
						}
					}
				}
			}
		}
		return allImageItems;
	}
	
	
	
	
	
}
