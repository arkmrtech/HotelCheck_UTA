package com.lk.hotelcheck.bean.dao;

import com.orm.SugarRecord;

public class CheckIssue extends SugarRecord<CheckIssue> {
	private boolean isCheck;
	private String content;

	public CheckIssue() {
		super();
	}

	public CheckIssue(boolean isCheck, String content) {
		super();
		this.isCheck = isCheck;
		this.content = content;
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

}
