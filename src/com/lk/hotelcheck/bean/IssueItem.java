package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lk.hotelcheck.bean.dao.DymicIssue;
import com.orm.SugarRecord;
import common.Constance.DefQueType;

import android.provider.MediaStore.Images;

public class IssueItem{
	
	private int id;
	private String name;
	private String content;
	private transient List<ImageItem> imagelist;
	private boolean isCheck;
	private int isDefQue;//是否自定义问题，0=固定问题，1=自定义问题
	private int isPreQue;
	private int dimOneId;
	private String dimOneName;
	private int reformState;//整改状态：0.未整改，1整改中，2完成整改
	
	
	
	public IssueItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public IssueItem(DymicIssue dymicIssue) {
		this.id = dymicIssue.getId().intValue();
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
	public ImageItem getImageItem(int i) {
		if (imagelist == null) {
			return null;
		}
		return imagelist.get(i);
	}
	public void removeImageItem(int i) {
		if (imagelist == null) {
			return ;
		}
		imagelist.remove(i);
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
		if (imagelist == null) {
			imagelist = new ArrayList<ImageItem>();
		}
		imagelist.addAll(dataList);
	}
	
}
