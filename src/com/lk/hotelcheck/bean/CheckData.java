package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class CheckData {
	private List<IssueItem> issuelist;
	private int id;
	private String name;
	private boolean isgetsublist;
	private List<CheckData> sublist;
	private List<IssueItem> checkedIssueList;
	private SparseArray<IssueItem> checkedSubMap;
	
	public List<IssueItem> getIssuelist() {
		return issuelist;
	}
	public void setIssuelist(List<IssueItem> issuelist) {
		this.issuelist = issuelist;
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
	public boolean isGetSublist() {
		return isgetsublist;
	}
	public void setIsgetsublist(boolean isgetsublist) {
		this.isgetsublist = isgetsublist;
	}
	public List<CheckData> getSublist() {
		return sublist;
	}
	public void setSublist(List<CheckData> sublist) {
		this.sublist = sublist;
	}
	public IssueItem getIssue(int position) {
		if (isgetsublist) {
			if (checkedSubMap == null) {
				return null;
			}
			return checkedSubMap.get(checkedSubMap.keyAt(position));
		} else {
			if (issuelist == null) {
				return null;
			}
			return issuelist.get(position);
		}
		
	}
	
	public int getIssueCount() {
		return issuelist == null ? 0 :issuelist.size();
	}
	
	public int getCheckedIssueCount() {
		int count = 0;
		if (isgetsublist) {
			if (sublist != null) {
				if (checkedSubMap == null) {
					checkedSubMap = new SparseArray<IssueItem>();
				}
				for (CheckData checkData : sublist) {
					for (IssueItem issueItem : checkData.getIssuelist()) {
						if (issueItem.isCheck()) {
							checkedSubMap.put(issueItem.getId(), issueItem);
						}
					}
				}
				count = checkedSubMap.size();
//				count = sublist.size();
			}
		} else {
			if (issuelist != null) {
				if (checkedIssueList == null) {
					checkedIssueList = new ArrayList<IssueItem>();
				} else {
					checkedIssueList.clear();
				}
				for (IssueItem issueItem : issuelist) {
					if (issueItem.isCheck()) {
						checkedIssueList.add(issueItem);
					}
				}
				count = checkedIssueList.size();
			}
		}
		return count;
	}
	
	public IssueItem getCheckedIssue(int position) {
//		if (getCheckedIssueCount() > 0 && checkedIssueList == null) {
//			for (IssueItem issueItem : issuelist) {
//				if (issueItem.isCheck()) {
//					if (checkedIssueList == null) {
//						checkedIssueList = new ArrayList<IssueItem>();
//					}
//					checkedIssueList.add(issueItem);
//				}
//			}
//		}
//		if (checkedIssueList == null) {
//			return null;
//		}
//		return checkedIssueList.get(position);
		if (isgetsublist) {
				return checkedSubMap.get(checkedSubMap.keyAt(position));
		} else {
				return checkedIssueList.get(position);
		}
	}
	public int getSubCheckDataCount() {
		return sublist == null ? 0 : sublist.size();
	}
	
	public CheckData getSubCheckData(int subCheckDataPosition) {
		if (sublist == null || subCheckDataPosition >= sublist.size()) {
			return null;
		}
		return sublist.get(subCheckDataPosition);
	}
	public void setSubCheckData(int subCheckDataPosition, CheckData checkData) {
		if (sublist == null) {
			sublist = new ArrayList<CheckData>();
		}
		sublist.set(subCheckDataPosition, checkData);
		
	}
	public String getSubIssuePercent(int position) {
		if (checkedSubMap == null || sublist == null) {
			return "";
		}
		String percent = "";
		IssueItem issueItem = checkedSubMap.get(checkedSubMap.keyAt(position));
		int count = 0;
		for (CheckData checkData : sublist) {
			if (checkData.getCheckedIssueCount() >0 ) {
				for (IssueItem temp : checkData.getCheckedIssueList()) {
					if (issueItem.getId() == temp.getId()) {
						count++;
						break;
					}
				}
			}
		}
		int total = getSubCheckDataCount();
		if (count >0 && total >0) {
			
			percent = String.format(Locale.CHINA, "%.2f%s", (count*1.00) /total * 100, "%");
		}
		return percent ;
	}
	
	public List<IssueItem> getCheckedIssueList() {
		if (getCheckedIssueCount() > 0 && checkedIssueList == null) {
			for (IssueItem issueItem : issuelist) {
				if (issueItem.isCheck()) {
					if (checkedIssueList == null) {
						checkedIssueList = new ArrayList<IssueItem>();
					}
					checkedIssueList.add(issueItem);
				}
			}
		}
		return checkedIssueList;
	}
	public void setIssueItem(int position, IssueItem issueItem) {
		issuelist.set(position, issueItem);
		
	}
	public int getSubCheckedCount() {
//		int count = 0;
//		if (isgetsublist && sublist != null) {
//			for (CheckData checkData : sublist) {
//				if (checkData.getCheckedIssueCount() >0) {
//					count++;
//				}
//			}
//		}
		
		return sublist == null ? 0: sublist.size();
	}
	public List<ImageItem> getIssueImageList(int issuePosition) {
		List<ImageItem> imageList = null;
		if (isgetsublist) {
			imageList = new ArrayList<ImageItem>();
			IssueItem issueItem = getCheckedIssue(issuePosition);
			for (CheckData checkData : sublist) {
				if (checkData.getCheckedIssueCount() >0 ) {
					for (IssueItem temp : checkData.getCheckedIssueList()) {
						if (issueItem.getId() == temp.getId()) {
							imageList.addAll(temp.getImagelist());
							break;
						}
					}
				}
			}
		} else {
			imageList = getCheckedIssue(issuePosition).getImagelist();
		}
		return imageList;
	}
}
