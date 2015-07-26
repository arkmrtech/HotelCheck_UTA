package com.lk.hotelcheck.bean.dao;

import com.orm.SugarRecord;

public class Branch extends SugarRecord<Branch>{
	
	private int checkId;
	private int name;
	private int checkType;
	private int branchNumber;
	
	
	
	public Branch() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Branch(int checkId, int name, int checkType, int branchNumber) {
		super();
		this.checkId = checkId;
		this.name = name;
		this.checkType = checkType;
		this.branchNumber = branchNumber;
	}

	public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}

	public int getName() {
		return name;
	}

	public void setName(int name) {
		this.name = name;
	}

	public int getCheckType() {
		return checkType;
	}

	public void setCheckType(int checkType) {
		this.checkType = checkType;
	}

	public int getBranchNumber() {
		return branchNumber;
	}

	public void setBranchNumber(int branchNumber) {
		this.branchNumber = branchNumber;
	}
	
	
	
}
