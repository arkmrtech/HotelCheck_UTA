package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.List;

public class HotelUploadQueneBean {

	private List<HotelUploadTask> hotelUploadTaskList;

	public List<HotelUploadTask> getHotelUploadTaskList() {
		return hotelUploadTaskList;
	}

	public void setHotelUploadTaskList(List<HotelUploadTask> hotelUploadTaskList) {
		this.hotelUploadTaskList = hotelUploadTaskList;
	}
	
	public void addHotelTask(HotelUploadTask task) {
		if (this.hotelUploadTaskList == null) {
			hotelUploadTaskList = new ArrayList<HotelUploadTask>();
		}
		hotelUploadTaskList.add(task);
	}
	
}
