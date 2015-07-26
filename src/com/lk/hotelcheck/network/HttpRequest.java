package com.lk.hotelcheck.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.util.JsonParseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.upyun.block.api.main.UploaderManager;

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
//		String url = "http://182.254.157.92:8080//app/service/login.html";
		Log.d("lxk", "url = "+url);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, userId);
			jsonObject.put(NetConstance.REQUEST_PARAM_PASSWORD, password);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		postRequest(context, url, jsonObject, callback);
	}
	
	public void getHotelList(Context context, String session, final HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_GET_CHECK_HOTEL_LIST);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, "1");
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
			jsonObject.put(NetConstance.REQUEST_PARAM_USER_NAME, "1");
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
	}
	
	public void updateHotelCheckStatus(Context context, int checkId, boolean status, String session, final HttpCallback callback) {
		String url = getRequestURL(NetConstance.METHOD_UPDATE_HOTEL_STATUS);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(NetConstance.PARAM_BRANCH_CHECK_ID, checkId);
			jsonObject.put(NetConstance.PARAM_STATUS, status);
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		
		postRequest(context, url, jsonObject, callback);
		
	}
	
	
	
	
	public void updateHotelData(Context context, Hotel hotel, String session, final HttpCallback callback) {
		if (hotel == null) {
			Log.d("lxk", "updateHotelData hotel is null");
			return;
		}
		String url = getRequestURL(NetConstance.METHOD_GET_CHECK_DATA);
		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject json = new JSONObject(new Gson().toJson(hotel));
			jsonObject.put(NetConstance.PARAM_HOTEL, json);
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
		
	}
	
	public void updateImageStatus(Context context, List<UploadBean> uploadBeans, String session, final HttpCallback callback) {
		if (uploadBeans == null) {
			Log.d("lxk", "updateImageStatus uploadBeans is null");
			return;
		}
		String url = getRequestURL(NetConstance.METHOD_GET_CHECK_DATA);
		JSONObject jsonObject = new JSONObject();
		try {
			JSONArray json = new JSONArray();
			for (UploadBean uploadBean : uploadBeans) {
				JSONObject temp = new JSONObject(new Gson().toJson(uploadBean));
				json.put(temp);
			}
			jsonObject.put(NetConstance.PARAM_IMAGE_LIST, json);
			jsonObject.put(NetConstance.REQUEST_PARAM_KEY, session);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		postRequest(context, url, jsonObject, callback);
	}
	
	private void postRequest(Context context, String url, JSONObject jsonObject, final HttpCallback callback) {
		StringEntity entity = null;
		try {
			entity = new StringEntity(jsonObject.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		mHttpClient.post(context, url, entity, "application/json", new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				callback.onError();
				Log.d("lxk", "<onFailure> 1 response = "+responseString);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
				callback.onError();
				Log.d("lxk", "<onFailure> 2 response = "+errorResponse);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.d("lxk", "<onFailure> 3 response = "+errorResponse);
				callback.onError();
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				callback.onSuccess(response);
			}
			
		});
	}
	
	
//	public void uploadSingleImage(String filePath, FileUploadHandle handle) {
//		if (TextUtils.isEmpty(filePath)) {
//			Log.e(TAG, "fielpath is null or empty");
//			return;
//		}
//		File file = new File(filePath);
//		if (file.exists()) {
//			String url = "";
//			RequestParams params = new RequestParams();
//			try {
//				params.put("imageFile", file, "image/jpeg");
//				mHttpClient.post(url, params, handle);
//			} catch (FileNotFoundException e) {
//				Log.e(TAG, "uploadSingleImage FileNotFoundException file : "+filePath+"does not exists");
//				e.printStackTrace();
//			}
//		} else {
//			Log.e(TAG, "uploadSingleImage error file : "+filePath+"does not exists");
//		}
//	}
//	
//	public void uploadMutilImage(String[] filePathArray, FileUploadHandle handle) {
//		if (filePathArray == null || filePathArray.length == 0) {
//			Log.e(TAG, "uploadMutilImage filePathArray null or empty");
//			return;
//		}
//		String url = "";
//		RequestParams params = new RequestParams();
//		String filePath;
//		File imageFile;
//		for (int i = 0; i < filePathArray.length; i++) {
//			filePath = filePathArray[i];
//			if (TextUtils.isEmpty(filePath)) {
//				Log.d(TAG, "uploadMutilImage");
//				break;
//			}
//			imageFile = new File(filePath);
//			if (imageFile.exists()) {
//				try {
//					params.put(""+i, imageFile, "image/jpeg");
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//					Log.e(TAG, "uploadMutilImage error file : "+filePath+"does not exists");
//				}
//			} else {
//				Log.e(TAG, "uploadMutilImage error file : "+filePath+"does not exists");
//			}
//					
//		}
//		mHttpClient.post(url, params, handle);
//		
//	}
	
	
	
	
	public void downloadTest(String url, FileAsyncHttpResponseHandler handler) {
		mHttpClient.get(url, handler);
//		mHttpClient.get("http://example.com/file.png", new FileAsyncHttpResponseHandler(/* Context */ this) {
//		    @Override
//		    public void onSuccess(int statusCode, Header[] headers, File response) {
//		        // Do something with the file `response`
//		    }
//		});
	}
	
	
	private String getRequestURL(String method) {
		return NetConstance.BASE_URL + method;
	}
	
}
