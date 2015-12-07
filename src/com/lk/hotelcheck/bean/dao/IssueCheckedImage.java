package com.lk.hotelcheck.bean.dao;

import java.util.List;

import com.lk.hotelcheck.bean.ImageItem;
import com.orm.SugarRecord;

/**
 * 每一个检查项的检查内容
 *
 * @author lk
 *
 */
public class IssueCheckedImage extends SugarRecord<IssueCheckedImage>{

//	public static final int IMAGE_TYPE = 1;
//	public static final int TEXT_TYPE = 2;
	
	private int checkId;
	private String name;
	private int areaId;
	private String areaName;
	private int issueId;
	private String localImagePath;
	private String serviceImagePath;
//	private String content;
//	private int type;//是图片还是文字
	
	
	
	
	
	public IssueCheckedImage() {
		super();
	}
	public IssueCheckedImage(int checkId, String name, int areaId, String areaName,
			int issueId, String localImagePath, String serviceImagePath,
			 int type) {
		super();
		this.checkId = checkId;
		this.name = name;
		this.areaId = areaId;
		this.areaName = areaName;
		this.issueId = issueId;
		this.localImagePath = localImagePath;
		this.serviceImagePath = serviceImagePath;
//		this.type = type;
	}
	public IssueCheckedImage(int checkId, int areaId, int issueId, ImageItem imageItem) {
		if (imageItem == null) {
			return;
		}
		this.checkId = checkId;
		this.areaId = areaId;
		this.issueId = issueId;
		this.localImagePath = imageItem.getLocalImagePath();
		this.serviceImagePath = imageItem.getServiceSavePath();
	}
	public int getCheckId() {
		return checkId;
	}
	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getLocalImagePath() {
		return localImagePath;
	}
	public void setLocalImagePath(String localImagePath) {
		this.localImagePath = localImagePath;
	}
	public String getServiceImagePath() {
		return serviceImagePath;
	}
	public void setServiceImagePath(String serviceImagePath) {
		this.serviceImagePath = serviceImagePath;
	}
//	public int getType() {
//		return type;
//	}
//	public void setType(int type) {
//		this.type = type;
//	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public static IssueCheckedImage deleteItemByImageLocalPath(String localImagePath) {
		List<IssueCheckedImage> dataList = IssueCheckedImage.find(IssueCheckedImage.class, "LOCAL_IMAGE_PATH = ?", localImagePath);
		if (dataList != null) {
			for (IssueCheckedImage hotelCheck: dataList) {
				hotelCheck.delete();
				return hotelCheck;
			}
		}
		return null;
	}
	
	
//	public static boolean isTableExists(Context context) {
////		SugarDb sugarDb = new SugarDb(context);
////		Cursor cursor = sugarDb.getReadableDatabase().rawQuery(
////				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
////						+ getTableName(HotelCheck.class) + "'", null);
////		if (cursor != null) {
////			if (cursor.getCount() > 0) {
////				cursor.close();
////				return true;
////			}
////			cursor.close();
////		}
////		return false;
//		HotelCheck hotelCheck = new HotelCheck(1, "aa", 0, "aa", 2, "vv", "dd", "ff", 1);
//		hotelCheck.save();
//		List<HotelCheck> result = findWithQuery(HotelCheck.class, "select DISTINCT tbl_name from sqlite_master where tbl_name = '"
//						+ getTableName(HotelCheck.class) + "'", null);
//		if (result == null || result.size() == 0) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	
}
