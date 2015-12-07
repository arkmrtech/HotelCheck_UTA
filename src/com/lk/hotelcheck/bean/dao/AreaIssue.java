package com.lk.hotelcheck.bean.dao;

import com.orm.SugarRecord;

public class AreaIssue extends SugarRecord<AreaIssue>{
	private int areaId;
	private int issueId;
	private int dimOneId;
	private String areaName;
	private String issueName;
	private String dimOneName;
	private int type;
	private int isDefQue;
	private int isPreQue;
	private int sort;
	
	



	public AreaIssue(int areaId, int issueId, int dimOneId, String areaName,
			String issueName, String dimOneName, int type, int isDefQue,
			int isPreQue) {
		super();
		this.areaId = areaId;
		this.issueId = issueId;
		this.dimOneId = dimOneId;
		this.areaName = areaName;
		this.issueName = issueName;
		this.dimOneName = dimOneName;
		this.type = type;
		this.isDefQue = isDefQue;
		this.isPreQue = isPreQue;
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


	public AreaIssue() {
		super();
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

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
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


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public int getSort() {
		return sort;
	}


	public void setSort(int sort) {
		this.sort = sort;
	}
	
	
	
	
}
