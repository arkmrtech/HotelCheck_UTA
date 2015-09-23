package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lk.hotelcheck.bean.dao.DymicIssue;
import com.tencent.bugly.proguard.u;

import common.Constance.DefQueType;
import common.Constance.PreQueType;

public class IssueItem{
	
	private int id;
	private String name;
	private String content;
	private transient List<ImageItem> imageList;
	private boolean isCheck;
	private int isDefQue;//是否自定义问题，0=固定问题，1=自定义问题
	private int isPreQue;
	private int dimOneId;
	private String dimOneName;
	private int reformState = 0;//整改状态：1.未整改，2整改中，3完成整改
	public static final int REFORM_STATE_FIXED = 3;
	public static final int REFORM_STATE_FIXING = 2;
	public static final int REFORM_STATE_UN_FIX = 1;
	
	public IssueItem() {
		super();
	}
	
	public IssueItem(DymicIssue dymicIssue) {
//		this.id = dymicIssue.getId().intValue();
		this.id = dymicIssue.getIssueId();
		this.name = dymicIssue.getName();
		this.dimOneId = dymicIssue.getDimOneId();
		this.dimOneName = dymicIssue.getDimOneName();
		this.isDefQue = DefQueType.TYPE_DYMIC;
	}
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
		if (imageList == null) {
			return Collections.emptyList();
		}
		return imageList;
	}
	public void setImagelist(List<ImageItem> imagelist) {
		this.imageList = imagelist;
	}
	
	public void addImage(ImageItem imageItem) {
		if (imageList == null) {
			imageList = new ArrayList<ImageItem>();
		}
		imageList.add(imageItem);
	}
	public int getImageCount() {
		return imageList == null ? 0 : imageList.size();
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
		if (isPreQue == PreQueType.TYPE_REVIEW) {
			if (isCheck) {
				this.reformState = REFORM_STATE_UN_FIX;
			} else if (!isCheck && reformState == 0) {
				this.reformState = REFORM_STATE_FIXED;
			}
		}
	}
	public ImageItem getImageItem(int i) {
		if (imageList == null) {
			return null;
		}
		return imageList.get(i);
	}
	public void removeImageItem(int i) {
		if (imageList == null) {
			return ;
		}
		imageList.remove(i);
	}
	
	public int getIsDefQue() {
		return isDefQue;
	}
	public void setIsDefQue(int isDefQue) {
		this.isDefQue = isDefQue;
	}
	public int getIsPreQue() {
		return isPreQue;
	}
	public void setIsPreQue(int isPreQue) {
		this.isPreQue = isPreQue;
	}
	public int getDimOneId() {
		return dimOneId;
	}
	public void setDimOneId(int dimOneId) {
		this.dimOneId = dimOneId;
	}
	public String getDimOneName() {
		return dimOneName;
	}
	public void setDimOneName(String dimOneName) {
		this.dimOneName = dimOneName;
	}
	public int getReformState() {
		return reformState;
	}
	public void setReformState(int reformState) {
		this.reformState = reformState;
	}
	public void addImageList(List<ImageItem> dataList) {
		if (imageList == null) {
			imageList = new ArrayList<ImageItem>();
		}
		imageList.addAll(dataList);
	}

	public void removeImageItem(String imagePath) {
		if (imageList != null) {
			for (int i = 0; i < imageList.size(); i++) {
				ImageItem imageItem = imageList.get(i);
				if (imageItem.getLocalImagePath().equals(imagePath)) {
					imageList.remove(i);
					return ;
				}
			}
		}
		
	}
	
	public ImageItem getImageItem(String imagePath) {
		if (imageList != null) {
			for (int i = 0; i < imageList.size(); i++) {
				ImageItem imageItem = imageList.get(i);
				if (imageItem.getLocalImagePath().equals(imagePath)) {
					return imageItem;
				}
			}
		}
		return null;
		
	}
	
}
