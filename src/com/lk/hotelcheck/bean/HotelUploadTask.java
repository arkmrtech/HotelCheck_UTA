package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.List;

public class HotelUploadTask {
	private int hotelId;
	private List<UploadBean> uploadBeanList;
	
	public int getHotelId() {
		return hotelId;
	}
	public void setHotelId(int hotelId) {
		this.hotelId = hotelId;
	}
	public List<UploadBean> getUploadBeanList() {
		return uploadBeanList;
	}
	public void setUploadBeanList(List<UploadBean> uploadBeanList) {
		this.uploadBeanList = uploadBeanList;
	}
	public void addUploadBean(UploadBean bean) {
		if (uploadBeanList == null) {
			uploadBeanList = new ArrayList<UploadBean>();
		}
		uploadBeanList.add(bean);
	}
	
	
	
}
