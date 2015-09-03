package com.lk.hotelcheck.bean.dao;

import com.lk.hotelcheck.bean.IssueItem;
import com.orm.SugarRecord;

public class DymicIssue extends SugarRecord<DymicIssue>{
	private long checkId;
	private long areaId;
	private String name;
	private int dimOneId;
	private String dimOneName;
	private int issueId;
	
	public DymicIssue() {
		super();
	}
	
	
	
	
	public DymicIssue(long checkId, long areaId, String name, int dimOneId,
			String dimOneName) {
		super();
		this.checkId = checkId;
		this.areaId = areaId;
		this.name = name;
		this.dimOneId = dimOneId;
		this.dimOneName = dimOneName;
	}




	public DymicIssue(long checkId,long areaId, IssueItem issueItem) {
		this.checkId = checkId;
		this.areaId = areaId;
		this.setIssueId(issueItem.getId());
		this.name = issueItem.getName();
		this.dimOneId = issueItem.getDimOneId();
		this.dimOneName = issueItem.getDimOneName();
	}




	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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




	public long getCheckId() {
		return checkId;
	}




	public void setCheckId(long checkId) {
		this.checkId = checkId;
	}




	public long getAreaId() {
		return areaId;
	}




	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}




	public int getIssueId() {
		return issueId;
	}




	public void setIssueId(int issueId) {
		this.issueId = issueId;
	}
	
	
	
	
}
