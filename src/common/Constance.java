package common;

import com.lk.hotelcheck.R;

import android.os.Environment;

public class Constance {

	public static final class Path{
		public final static String SDCARD = Environment.getExternalStorageDirectory().getPath();
		public final static String HOTEL_SRC = SDCARD+"/FZHotelCheck";
		public static final String IMAGE_PATH = HOTEL_SRC+"/Image/";
		public static final String TEMP_IMAGE_PATH = HOTEL_SRC+"/temp/";
		public static final String TEMP_IMAGE = TEMP_IMAGE_PATH +"ImageTemp.jpg";
		public static final String TEMP_IMAGE_FLOER_PATH = HOTEL_SRC + "/temp/";
		public static final String IMAGE_CACHE = HOTEL_SRC + "/imageCache/";
		public static final String DATA_PATH = HOTEL_SRC+"/data/hotel.json";
	}
	
	public static final class IntentKey {
		public static final String INTENT_KEY_POSITION = "position";
		public static final String INTENT_KEY_NAME = "name";
		public static final String INTENT_KEY_ID = "position";
		public static final String INTENT_KEY_CHECK_DATA_POSITION = "checkDataPosition";
		public static final String INTENT_KEY_ISSUE_POSITION = "issuePosition";
	}
	
	
	public static final int CHECK_DATA_ID_ROOM = 8006;
	public static final int CHECK_DATA_ID_FLOOR = 8005;
	
}
