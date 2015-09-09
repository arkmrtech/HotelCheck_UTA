package com.lk.hotelcheck.bean.dao;

import com.orm.SugarRecord;

public class CheckIssue extends SugarRecord<CheckIssue> {
	private boolean isCheck;
	private String content;
	private int reformState;
	private int checkId;
	private long areaId;
	private int issueId;

	public CheckIssue() {
		super();
	}

	public CheckIssue(boolean isCheck, String content, int reformState,
			int checkId, long areaId, int issueId) {
		super();
		this.isCheck = isCheck;
		this.content = content;
		this.reformState = reformState;
		this.checkId = checkId;
		this.areaId = areaId;
		this.issueId = issueId;
	}

	public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}

	public long getAreaId() {
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

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getReformState() {
		return reformState;
	}

	public void setReformState(int reformState) {
		this.reformState = reformState;
	}

}
