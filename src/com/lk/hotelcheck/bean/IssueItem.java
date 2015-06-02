package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.provider.MediaStore.Images;

public class IssueItem {
	private int id;
	private String name;
	private String content;
	private List<ImageItem> imagelist;
	private boolean isCheck;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<ImageItem> getImagelist() {
		return imagelist;
	}
	public void setImagelist(List<ImageItem> imagelist) {
		this.imagelist = imagelist;
	}
	
	public void addImage(ImageItem imageItem) {
		if (imagelist == null) {
			imagelist = new ArrayList<ImageItem>();
		}
		imagelist.add(imageItem);
	}
	public int getImageCount() {
		return imagelist == null ? 0 : imagelist.size();
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
}
