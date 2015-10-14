package common;

import android.os.Environment;

public class Constance {

	public static final class Path{
		public final static String SDCARD = Environment.getExternalStorageDirectory().getPath();
		public final static String HOTEL_SRC = SDCARD+"/FZHotelCheck";
		public static final String IMAGE_PATH = HOTEL_SRC+"/Image/";
//		public static final String TEMP_IMAGE_PATH = HOTEL_SRC+"/temp/";
		public static final String TEMP_IMAGE_FLOER_PATH = HOTEL_SRC + "/temp/";
		public static final String TEMP_IMAGE = TEMP_IMAGE_FLOER_PATH +"ImageTemp.jpg";
		public static final String IMAGE_CACHE = HOTEL_SRC + "/imageCache/";
		public static final String DATA_PATH = HOTEL_SRC+"/data/hotel.json";
		public static final String UPLOAD_TASK_DATA_PATH = HOTEL_SRC+"/data/upload_task.json";
		public static final String SERVER_IMAGE_PATH = "/checkedImage/";
		public static final String CRASH_PATH = HOTEL_SRC+"/crash/";
	}
	
	public static final class IntentKey {
		public static final String INTENT_KEY_POSITION = "position";
		public static final String INTENT_KEY_NAME = "name";
		public static final String INTENT_KEY_ID = "position";
		public static final String INTENT_KEY_CHECK_DATA_POSITION = "checkDataPosition";
		public static final String INTENT_KEY_ISSUE_POSITION = "issuePosition";
		public static final String INTENT_KEY_FILE_PATH = "filePath";
		public static final String INTENT_KEY_TYPE = "type";
	}
	
	public static final class UPAI {
//		// 空间名
		public static final String BUCKET = "sevenday-qctest";
//		// 表单密钥
		public static final String FORM_API_SECRET = "7zkTYpfFXWK43AeiXw2pfryVi2A=";
		
		// 空间名
//		public static final String BUCKET = "bayes-space";
		// 表单密钥
//		public static final String FORM_API_SECRET = "aI2nEa+I9lFx5CSGl8QOyo08FoM=";
		
	}
	
	public static final class ImageUploadState {
		public static final int STATE_WAIT = 0X10000;
		public static final int STATE_START = 0X10001;
		public static final int STATE_FINISH = 0X10002;
		public static final int STATE_FAIL = 0X10003;
	}
	
	
	public static final class HotelAction {
		public static final String ACTION_IMAGE_UPLOAD = "image_upload_action";
		public static final String IMAGE_UPLOAD_EXTRA = "image_upload_extra";
	}
	
	public static final int CHECK_DATA_ID_ROOM = 1004;
	public static final int CHECK_DATA_ID_PASSWAY = 1005;
	public static final int ISSUE_ITEM_WIFI = 1010;
	public static final int REQUEST_CODE_WIFI = 999;
	
	public static final class CheckDataType {
		public static final int TYPE_NORMAL = 1;
		public static final int TYPE_ROOM = 2;
		public static final int TYPE_PASSWAY = 3;
	}
	
	/**
	 * 是否自定义问题，0=固定问题，1=自定义问题
	 * @author lk
	 *
	 */
	public static final class DefQueType {
		public static final int TYPE_NORMAL = 0;
		public static final int TYPE_DYMIC = 1;
	}
	
	/**
	 * 是否整改问题：0.新问题，1.旧问题（针对于每一次复检来说）
	 * @author lk
	 *
	 */
	public static final class PreQueType {
		public static final int TYPE_NEW = 0;
		public static final int TYPE_REVIEW = 1;
	}
	
	public static final class CheckType {
		public static final int CHECK_TYPE_NEW = 0;
		public static final int CHECK_TYPE_REVIEW = 1;
	}
	
}
