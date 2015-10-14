package com.lk.hotelcheck.network;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lk.hotelcheck.activity.login.LoginActivity;
import com.lk.hotelcheck.activity.main.MainActivity;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.manager.DataManager;
import com.lk.hotelcheck.util.CommonUtil;
import com.lk.hotelcheck.util.FileUtil;
import com.lk.hotelcheck.util.JsonParseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import common.NetConstance;

public class HttpRequest {
	private static final String TAG = "HttpRequest";
	private static HttpRequest mInstance = null;
	private static final Object mLocker = new Object();
	private static AsyncHttpClient mHttpClient = new AsyncHttpClient();
	
	private HttpRequest(){
		
	}
	
	public static HttpRequest getInstance() {
		synchronized (mLocker) {
			if (mInstance == null) {
				mInstance = new HttpRequest();
			}
			return mInstance;
		}
	}
	
	
	/**
	 * 
	 * @param context
	 * @param userId
	 * @param password
	 * @param callback
	 */
	public void login(Context context, String userId, String password, HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_LOGIN);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, userId);
			jsonObject.put(NetConstance.REQUEST_PARAM_PASSWORD, password);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		postRequest(context, url, jsonObject, callback);
	}
	
	/**
	 * 
	 * @param context
	 * @param userId
	 * @param password
	 * @param callback
	 */
	public void logout(Context context, HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_LOGOUT);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, DataManager.getInstance().getUserName());
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, DataManager.getInstance().getSession());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		postRequest(context, url, jsonObject, callback);
	}
	
	public void getHotelList(Context context, String session, final HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_GET_CHECK_HOTEL_LIST);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, DataManager.getInstance().getUserName());
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
	}
	
	public void getCheckDataList(Context context, String session, final HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_GET_CHECK_DATA);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, DataManager.getInstance().getUserName());
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
	}
	
	public void updateHotelCheckStatus(Context context, int checkId, String session, final HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_UPDATE_HOTEL_STATUS);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.PARAM_CHECK_ID, checkId);
			jsonObject.put(NetConstance.PARAM_STATE, 2);//2 = finish;
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, DataManager.getInstance().getUserName());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
	}
	
	
	
	
	public void uploadHotelData(Context context, Hotel hotel, String session, final HttpCallback callback) {
		if (hotel == null) {
			Log.d("lxk", "updateHotelData hotel is null");
			return;
		}
		String url = getRequestURL(NetConstance.METHOD_UPLOAD_HOTEL_DATA);
		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject json = JsonParseHandler.parseHotelToJson(hotel);
			jsonObject.put(NetConstance.PARAM_HOTEL, json);
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, DataManager.getInstance().getUserName());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		FileUtil.writeStringToSdCard(common.Constance.Path.DATA_PATH, jsonObject.toString());
		postRequest(context, url, jsonObject, callback);
		
	}
	
//	public void updateImageStatus(Context context, List<UploadBean> uploadBeans, String session, final HttpCallback callback) {
//		if (uploadBeans == null) {
//			Log.d("lxk", "updateImageStatus uploadBeans is null");
//			return;
//		}
//		String url = getRequestURL(NetConstance.METHOD_GET_CHECK_DATA);
//		JSONObject jsonObject = new JSONObject();
//		try {
//			JSONArray json = new JSONArray();
//			 Gson gson = new GsonBuilder()
//			    .excludeFieldsWithoutExposeAnnotation() // <---
//			    .create();
//			for (UploadBean uploadBean : uploadBeans) {
//				JSONObject temp = new JSONObject(gson.toJson(uploadBean));
//				json.put(temp);
//			}
//			jsonObject.put(NetConstance.PARAM_IMAGE_LIST, json);
//			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//		
//		postRequest(context, url, jsonObject, callback);
//	}
	
	public void updateImageStatus(Context context, UploadBean uploadBean, String session, final HttpCallback callback) {
		if (uploadBean == null) {
			Log.d("lxk", "updateImageStatus uploadBeans is null");
			return;
		}
		String url = getRequestURL(NetConstance.METHOD_UPLOAD_IMAGE_STATUS);
		JSONObject jsonObject = new JSONObject();
		try {
			 Gson gson = new GsonBuilder()
			    .excludeFieldsWithoutExposeAnnotation() // <---
			    .create();
			JSONObject json = new JSONObject(gson.toJson(uploadBean));
			jsonObject.put(NetConstance.PARAM_IMAGE_LIST, json);
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
	}
	
	public void validToken(Context context, String lastToken, final HttpCallback callback) {
		if (TextUtils.isEmpty(lastToken)) {
			Log.d("lxk", "validToken lastToken is null");
			return;
		}
		String url = getRequestURL(NetConstance.METHOD_VALID_TOKEN);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, lastToken);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
		
	}
	
	
	private void postRequest(final Context context, final String url, JSONObject jsonObject, final HttpCallback callback) {
		StringEntity entity = null;
		try {
			entity = new StringEntity(jsonObject.toString(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d("lxk", "url = "+url);
		Log.d("lxk", "param = "+jsonObject.toString());
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		mHttpClient.post(context, url, entity, "application/json", new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				callback.onError(statusCode, "error");
				Log.d("lxk", "<onFailure> 1 response = "+responseString);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				callback.onError(statusCode, "error");
				Log.d("lxk", "<onFailure> 2 response = "+errorResponse);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.d("lxk", "<onFailure> 3 response = "+errorResponse);
				callback.onError(statusCode, "error");
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Log.d("lxk", "<onSuccess>  response = "+response);
				int state = response.optInt(NetConstance.PARAM_STATE);
				String info = response.optString(NetConstance.PARAM_INFO);
//				if (state == NetConstance.ERROR_CODE_SESSSION_TIME_OUT) {
//					DataManager.getInstance().saveToken(context.getApplicationContext(), "");
//				}
				if (state == NetConstance.ERROR_CODE_SUCCESS) {
					callback.onSuccess(response);
				} else {
					callback.onError(state, info);
				}
			}
		});
	}
	
	
	public void downloadTest(String url, FileAsyncHttpResponseHandler handler) {
		mHttpClient.get(url, handler);
	}
	
	
	private String getRequestURL(String method) {
		return NetConstance.BASE_URL + method;
	}

	
}
