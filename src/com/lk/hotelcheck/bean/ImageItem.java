package com.lk.hotelcheck.bean;

import android.R.string;

public class ImageItem {
	private String imageUrl;
	private boolean isSelected;
	private boolean isUpload;
	
	
	
	public ImageItem(String imageUrl) {
		super();
		this.imageUrl = imageUrl;
	}
	
	
	public ImageItem() {
		super();
	}


	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public boolean isUpload() {
		return isUpload;
	}
	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}
}
