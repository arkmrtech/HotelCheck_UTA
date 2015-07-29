package com.lk.hotelcheck.bean;

import java.io.Serializable;

import android.R.string;

public class ImageItem implements Serializable{
	private String localImagePath;
	private String serviceSavePath;
	private transient boolean isSelected;
	private transient boolean isUpload;
	private transient int imageState;
	private boolean isWidth;
	private int type;
	
	
//	public ImageItem(String imageUrl) {
//		super();
//		this.imageUrl = imageUrl;
//	}
	
	
	public boolean isWidth() {
		return isWidth;
	}


	public void setWidth(boolean isWidth) {
		this.isWidth = isWidth;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public ImageItem() {
		super();
	}


//	public String getImageUrl() {
//		return imageUrl;
//	}
//	public void setImageUrl(String imageUrl) {
//		this.imageUrl = imageUrl;
//	}
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


	public String getLocalImagePath() {
		return localImagePath;
	}


	public void setLocalImagePath(String localImagePath) {
		this.localImagePath = localImagePath;
	}


	public String getServiceSavePath() {
		return serviceSavePath;
	}


	public void setServiceSavePath(String serviceSavePath) {
		this.serviceSavePath = serviceSavePath;
	}


	public int getImageState() {
		return imageState;
	}


	public void setImageState(int imageState) {
		this.imageState = imageState;
	}
}
