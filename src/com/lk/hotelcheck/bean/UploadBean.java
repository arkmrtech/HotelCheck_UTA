package com.lk.hotelcheck.bean;

import java.io.Serializable;
import java.util.List;

import com.orm.SugarRecord;

public class UploadBean extends SugarRecord<UploadBean> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8442111964479258123L;
	
	private long checkId;
	private long areaid;
	private int issueId;
	private String areaName;
	private String issueName;
	private int dimOneId;
	private String dimOneName;
	private String localImagePath;
	private String serviceImageSavePath;
	private String fileName;
	private long transferedBytes;
	private long totalBytes;
	private int imageState;
	private int isWidth;
	private int type;
	
	
	
	
	public UploadBean() {
		super();
		// TODO Auto-generated constructor stub
	}





	public UploadBean(long checkId, long areaId, String areaName, int issueId,
			String issueName, int dimOneId, String dimOneName,
			ImageItem imageItem) {
		super();
		this.checkId = checkId;
		this.areaid = areaId;
		this.areaName = areaName;
		this.issueId = issueId;
		this.issueName = issueName;
		this.dimOneId = dimOneId;
		this.dimOneName = dimOneName;
		this.localImagePath = imageItem.getLocalImagePath();
		this.serviceImageSavePath = imageItem.getServiceSavePath();
	}





	public int getIsWidth() {
		return isWidth;
	}





	public void setIsWidth(int isWidth) {
		this.isWidth = isWidth;
	}





	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}
	
	public long getCheckId() {
		return checkId;
	}


	public void setCheckId(long checkId) {
		this.checkId = checkId;
	}


	public long getAreaid() {
		return areaid;
	}


	public void setAreaid(long areaid) {
		this.areaid = areaid;
	}


	public int getIssueId() {
		return issueId;
	}


	public void setIssueId(int issueId) {
		this.issueId = issueId;
	}


	public String getAreaName() {
		return areaName;
	}


	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}


	public String getIssueName() {
		return issueName;
	}


	public void setIssueName(String issueName) {
		this.issueName = issueName;
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


	public String getLocalImagePath() {
		return localImagePath;
	}


	public void setLocalImagePath(String localImagePath) {
		this.localImagePath = localImagePath;
	}


	public String getServiceImageSavePath() {
		return serviceImageSavePath;
	}


	public void setServiceImageSavePath(String serviceImageSavePath) {
		this.serviceImageSavePath = serviceImageSavePath;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public long getTransferedBytes() {
		return transferedBytes;
	}


	public void setTransferedBytes(long transferedBytes) {
		this.transferedBytes = transferedBytes;
	}


	public long getTotalBytes() {
		return totalBytes;
	}


	public void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}

	public int getImageState() {
		return imageState;
	}


	public void setImageState(int imageState) {
		this.imageState = imageState;
	}


	
	
	
	
	
	
	
}
