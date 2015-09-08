package com.lk.hotelcheck.bean.dao;

import com.orm.SugarRecord;

public class CheckIssue extends SugarRecord<CheckIssue> {
	private boolean isCheck;
	private String content;
	private int reformState;

	public CheckIssue() {
		super();
	}

	public CheckIssue(boolean isCheck, String content, int reformState) {
		super();
		this.isCheck = isCheck;
		this.content = content;
		this.setReformState(reformState);
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
