package com.lk.hotelcheck.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年11月15日]
 */
public class UtilTool {
	public static final String NET_WIFI = "wifi";
	public static final String GOOGLE_PALY_PACKAGE = "com.android.vending";
	public static final int SUCCESS = 100;      // 本地检测通过
	public static final int ERROR_PASSWORD_LENGTH = 101; // 密码长度要求6~20位
	public static final int ERROR_NICKNAME_LENGTH = 106; // 密码长度要求6~20位
	
	public static final int NETTYPE_MOBILE = 0; // 中国移动
	public static final int NETTYPE_UNICOM = 1; // 中国联通
	public static final int NETTYPE_TELECOM = 2; // 中国电信
	private static final String SMS_URI_SEND = "content://sms/sent";
	public static PackageInfo getVersionInfo(Context context, String pkg) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(pkg, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 检查是安装某包
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @return
	 */
	public static boolean isAppExist(final Context context, final String packageName) {
		boolean result = false;
		if (context == null || packageName == null) {
			return result;
		}
		try {
			// context.createPackageContext(packageName,
			// Context.CONTEXT_IGNORE_SECURITY);
			context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SHARED_LIBRARY_FILES);
			result = true;
		} catch (NameNotFoundException e) {
			// TODO: handle exception
			result = false;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	public static boolean isGooglePalyExist(final Context context) {
		return isAppExist(context, GOOGLE_PALY_PACKAGE);
	}

	public static int boolean2Int(boolean bool) {
		return bool ? 1 : 0;
	}
	public static boolean int2Boolean(int value) {
		return value == 1 ? true : false;
	}

	public static String getNetworkType(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();

			if (info != null && info.isConnected()) {
				// 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED && info.getTypeName() != null) {
					if (info.getTypeName().equals("WIFI")) {
						return NET_WIFI;
					} else {
						return info.getTypeName().toLowerCase();
					}
				}
			}
		}
		return null;

	}

	public static String unzip(String zipStr) throws IOException {
		int buffer_size = 1024;
		ByteArrayInputStream is = new ByteArrayInputStream(zipStr.getBytes("ISO-8859-1"));
		GZIPInputStream gis = new GZIPInputStream(is, buffer_size);
		StringBuilder str = new StringBuilder();
		byte[] data = new byte[buffer_size];
		int bytesRead;
		while ((bytesRead = gis.read(data, 0, buffer_size)) != -1) {
			str.append(new String(data, 0, bytesRead));
		}
		gis.close();
		is.close();
		return str.toString();
	}

	public static String gzip(byte[] bs) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream gzout = null;
		try {
			gzout = new GZIPOutputStream(bout);
			gzout.write(bs);
			gzout.flush();
		} catch (Exception e) {
			throw e;

		} finally {
			if (gzout != null) {
				try {
					gzout.close();
				} catch (Exception ex) {
				}
			}
		}
		String result = null;
		if (bout != null) {
			result = bout.toString("ISO-8859-1");
		}
		return result;
	}
	public static byte[] gzipByte(byte[] bs) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
		GZIPOutputStream gzout = null;
		try {
			gzout = new GZIPOutputStream(bout);
			gzout.write(bs);
			gzout.flush();
		} catch (Exception e) {
			throw e;

		} finally {
			if (gzout != null) {
				try {
					gzout.close();
				} catch (Exception ex) {
				}
			}
		}
		byte result[] = null;
		if (bout != null) {
			result = bout.toByteArray();
		}
		return result;
	}

	/**
	 * 是否cmwap连接
	 * 
	 * @author huyong
	 * @param context
	 * @return
	 */
	public static boolean isCWWAPConnect(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if (Proxy.getDefaultHost() != null || Proxy.getHost(context) != null) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * 获取网络类型
	 * 
	 * @author huyong
	 * @param context
	 * @return 1 for 移动，2 for 联通，3 for 电信，-1 for 不能识别
	 */
	public static int getNetWorkType(Context context) {
		int netType = -1;
		// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000") || simOperator.startsWith("46002")) {
				// 因为移动网络编号46000下的IMSI已经用完，
				// 所以虚拟了一个46002编号，134/159号段使用了此编号
				// 中国移动
				netType = NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				// 中国联通
				netType = NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				// 中国电信
				netType = NETTYPE_TELECOM;
			}
		}
		return netType;
	}
	/**
	 * <br>功能简述: 获取sim卡状态
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static int getSimStatus(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getSimState();
	}
	/**
	 * <br>功能简述:判断sim卡是否能用
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static boolean isSimReady(Context context) {
		boolean res = false;
		int status = getSimStatus(context);
		if (status == TelephonyManager.SIM_STATE_READY) {
			res = true;
		}
		return res;
	}

	public static String getProxyHost(Context context) {
		return Proxy.getHost(context);
	}

	public static int getProxyPort(Context context) {
		return Proxy.getPort(context);
	}

	public static boolean isLegalEmailAddress(String email) {
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isLegalPhoneNumber(String number) {
		number = number.replace(" ", "");
		if (number.startsWith("+86")) {
			number = number.substring(number.indexOf("+86") + 3).trim();
		}
		Pattern pattern = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(number);

		return matcher.matches();
	}

	public static int isLegalPassword(String password) {

		int ret = SUCCESS;
		if (password.length() < 6 || password.length() > 20) {
			ret = ERROR_PASSWORD_LENGTH;
		}
		return ret;

	}
	
	public static int isLegalNickName(String nickName) {

		int ret = SUCCESS;
		if (nickName.length() > 8) {
			ret = ERROR_PASSWORD_LENGTH;
		}
		return ret;

	}

	public static void putJsonValue(JSONObject obj, String key, int value) {
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void putJsonValue(JSONObject obj, String key, double value) {
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void putJsonValue(JSONObject obj, String key, long value) {
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void putJsonValue(JSONObject obj, String key, boolean value) {
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void putJsonValue(JSONObject obj, String key, Object value) {
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String unzipData(InputStream inStream) {
		try {
			byte[] old_bytes = toByteArray(inStream);
			String old = new String(old_bytes, "UTF-8");
			//			Loger.e(null, old);
			byte[] new_bytes = ungzip(old_bytes);
			return new String(new_bytes, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static byte[] ungzip(byte[] bs) throws Exception {
		GZIPInputStream gzin = null;
		ByteArrayInputStream bin = null;
		try {
			bin = new ByteArrayInputStream(bs);
			gzin = new GZIPInputStream(bin);
			return toByteArray(gzin);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bin != null) {
				bin.close();
			}
		}
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}
	public static int copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * <br>功能简述:解析actValue;
	 * <br>actValue的格式如下：
	 * <br>a) intent:classname=*****?arg1=I3#arg2=Stest#arg3=Ztrue#arg4=J8888#arg5=F1.0#arg5=D1.00
	 * <br>b) intent:action=*****?arg1=I3#arg2=Stest
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return 
	 * @throws Exception 
	 */
	public static Intent praseIntent(Context context, String url) throws Exception {
		// TODO Auto-generated method stub
		String[] actValues = url.split("\\?", 4);
		String intentStr = actValues[0];
		intentStr = intentStr.replace("intent:", "");

		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String[] actionOrClass = intentStr.split("=", 2);
		if (actionOrClass.length > 1) {
			if (intentStr.startsWith("classname=")) {
				intent.setClassName(context, actionOrClass[1]);
			} else if (intentStr.startsWith("action=")) {
				intent.setAction(actionOrClass[1]);
			}
		} else {
//			throw new Exception("messageManager->parseActValue:actionOrClass 不能为空");
			return null;
		}

		if (actValues.length > 1) {
			// 如果有参数
			String[] attributes = actValues[1].split("#");
			for (int i = 0; i < attributes.length; i++) {
				String[] atts = attributes[i].split("=", 2);
				if (atts.length != 2) {
					break;
				}
				String attName = atts[0];
				String attType = "";
				String attValue = "";
				attType = atts[1].substring(0, 1);
				if (atts[1].length() < 2) {
					// 等号后面只有1个字符，可能参数为空字符串
					// 当attType=S的时候attValue保持空字符串
					if (!attType.equals("S")) {
						// 因为参数格式错误，舍弃这次的参数
						continue;
					}
				} else {
					attValue = atts[1].substring(1, atts[1].length());
				}

				if (attName.equals("Uri")) {
					intent.setData(Uri.parse(attValue));
				}
				buildInent(intent, attName, attType, attValue);
			}
		}

		return intent;
	}

	/**
	 * <br>功能简述:构建intent的参数
	 * <br>功能详细描述:
	 * <br>注意:attType的含义:
	 *  I表示int，
		S表示String,
		Z表示bool,
		J表示long
		F表示float
		D表示double
		B表示Byte
	 * @param intent
	 * @param attName 属性名
	 * @param attType 属性类型
	 * @param attValue 属性值
	 */
	public static void buildInent(Intent intent, String attName, String attType, String attValue) {
		// TODO Auto-generated method stub
		if (attType.equals("I")) {
			intent.putExtra(attName, Integer.parseInt(attValue));
		} else if (attType.equals("S")) {
			if (attName.equals("Uri")) {
				intent.setData(Uri.parse(formatUri(attValue)));
			} else {
				intent.putExtra(attName, attValue);
			}
		} else if (attType.equals("Z")) {
			intent.putExtra(attName, Boolean.parseBoolean(attValue));
		} else if (attType.equals("J")) {
			intent.putExtra(attName, Long.parseLong(attValue));
		} else if (attType.equals("F")) {
			intent.putExtra(attName, Float.parseFloat(attValue));
		} else if (attType.equals("D")) {
			intent.putExtra(attName, Double.parseDouble(attValue));
		} else if (attType.equals("B")) {
			intent.putExtra(attName, Byte.parseByte(attValue));
		}

	}

	/**
	 * 功能简述:创建缩放处理后的新图，若缩放后大小与原图大小相同，则直接返回原图。
	 * 功能详细描述:
	 * 注意:若缩放目标尺寸与原图尺寸相等，则直接返回原图，不再创建新的bitmap
	 * @param bmp：待处理bmp
	 * @param scaleWidth：缩放目标宽
	 * @param scaleHeight：缩放目标高
	 * @return
	 */
	public static final Bitmap createScaledBitmap(Bitmap bmp, int scaleWidth, int scaleHeight) {
		Bitmap pRet = null;
		if (null == bmp) {
			return pRet;
		}
		// 这里有待改进，这里直接返回原图，有可能原图会在后面recycle，导致创建出来的都会被recycle
		if (scaleWidth == bmp.getWidth() && scaleHeight == bmp.getHeight()) {
			return bmp;
		}

		try {
			pRet = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
		} catch (OutOfMemoryError e) {
			pRet = null;
		} catch (Exception e) {
			pRet = null;
//			Loger.printException(null, e);
		}

		return pRet;
	}

	public static void gotoBrowser(Context context, String uriString) {
		// 跳转intent
		Uri uri = Uri.parse(uriString);
		Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);

		// 1:已安装的浏览器列表
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveList = pm.queryIntentActivities(myIntent, 0);
		// 无正在运行的浏览器，直接取浏览器列表的第1个打开
		ResolveInfo resolveInfo = resolveList.get(0);
		String pkgString = resolveInfo.activityInfo.packageName;
		String activityName = resolveInfo.activityInfo.name;
		myIntent.setClassName(pkgString, activityName);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(myIntent);
	}

	/**
	 * 跳转到Android Market
	 * 
	 * @param uriString
	 *            market的uri
	 * @return 成功打开返回true
	 */
	public static boolean gotoMarket(Context context, String uriString) {
		boolean ret = false;
		Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
		marketIntent.setPackage(GOOGLE_PALY_PACKAGE);
		if (context instanceof Activity) {
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		} else {
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		try {
			context.startActivity(marketIntent);
			ret = true;
		} catch (Exception e) {
//			Loger.printException(null, e);
		}
		return ret;
	}

	public static String filterContent(String content, String beginTag, String endTag) {
		String result = null;
		do {
			if (content != null && beginTag != null && endTag != null) {
				int start = -1;
				int end = -1;
				if (TextUtils.isEmpty(beginTag)) {
					start = 0;
				} else {
					start = content.indexOf(beginTag.trim()) + beginTag.length();
				}

				if (TextUtils.isEmpty(endTag)) {
					end = 0;
				} else {
					end = content.indexOf(endTag);
				}
				if (start == -1 || end == -1 || end <= start) {
					break;
				}
				result = content.substring(start, end);
			}
		} while (false);
		return result;
	}

	public static boolean isSDMounted() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		return sdCardExist;
	}

	public static long getSDFreeSize() {
		// 取得SD卡文件路径  
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量  
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小  
		return freeBlocks * blockSize / 1024; // 单位K  
	}

	public static boolean hasEnoughSDFreeSize() {
		return getSDFreeSize() > 2;
	}

	public static void hideSoftInput(Context context) {
		if (context != null) {
			InputMethodManager inputManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputManager.isActive()) {
				inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	/**
	 * 用于把被转义下发的URL还原的工具
	 * @param url
	 * @return
	 */
	public static String formatUri(String uri) {
		if (uri == null) {
			return null;
		}
		String newUri = uri.replaceAll("%3A", ":");
		newUri = newUri.replaceAll("%2F", "/");
		newUri = newUri.replaceAll("%2B", "＋");
		newUri = newUri.replaceAll("%20", " ");
		newUri = newUri.replaceAll("%3F", "？");
		newUri = newUri.replaceAll("%25 ", "％");
		newUri = newUri.replaceAll("%23 ", "＃");
		newUri = newUri.replaceAll("%26", "＆");
		newUri = newUri.replaceAll("%3D", "＝");
		return newUri;
	}
	
	// 设置过滤字符函数(过滤掉我们不需要的字符)
	public static String stringFilter(String str) {
		String result = str;
		try {
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			result = m.replaceAll("");
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(300);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			//Log.i("UtilTool---->", "服务名：-->"+serviceList.get(i).service.getClassName());
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/*
	 * 根据电话号码取得联系人姓名
	 */
	public static String getContactNameByPhoneNumber(Context context,
			String PhoneNum) {
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		// 将自己添加到 msPeers 中
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				projection, // Which columns to return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
						+ PhoneNum + "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.

		if (cursor == null) {
			Log.w("TAG", "getPeople null");
			return null;
		}
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			// 取得联系人名字
			int nameFieldColumnIndex = cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			String name = cursor.getString(nameFieldColumnIndex);
			return name;
		}
		return null;
	}	
}
