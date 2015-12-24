package com.lk.hotelcheck.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.manager.DataManager;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

import common.Constance.CheckDataType;
import common.Constance.HotelAction;
import common.Constance.ImageUploadState;
import common.Constance.UPAI;

public class UploadService extends Service {
	
	private static final String TAG = "UploadService";
	
	private ServiceBinder mServiceBinder = new ServiceBinder();
	private static final int MAX_UPLOAD_SIZE = 1;
	private ConcurrentHashMap<String, UploadBean> mRuningTaskQueue = new ConcurrentHashMap<String, UploadBean>();
	private CopyOnWriteArrayList<UploadBean> mWaitingTaskQueue = new CopyOnWriteArrayList<UploadBean>();
	private UploaderManager mUploaderManager = UploaderManager.getInstance(UPAI.BUCKET);
	
	@Override
	public IBinder onBind(Intent intent) {
		return mServiceBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate(); 
		mUploaderManager.setConnectTimeout(60);
		mUploaderManager.setResponseTimeout(60);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	public class ServiceBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		saveUnFinishData();
	}
	
	public synchronized void saveUnFinishData() {
		for (UploadBean bean : mRuningTaskQueue.values()) {
			bean.setImageState(ImageUploadState.STATE_FAIL);
			bean.save();
		}
		
		for (UploadBean bean : mWaitingTaskQueue) {
			bean.setImageState(ImageUploadState.STATE_FAIL);
			bean.save();
		}
	}
	
	public synchronized void addUploadTask(UploadBean uploadBean) {
		if (uploadBean == null) {
			return;
		}
		if (mRuningTaskQueue.containsKey(uploadBean.getLocalImagePath())) {
			return;
		}
		
		List<UploadBean> list = UploadBean.find(UploadBean.class, "LOCAL_IMAGE_PATH = ?", uploadBean.getLocalImagePath());
		// 如果上传任务已经存在则更新，否则新增一条记录
		if (list != null && list.size() > 0) {
			uploadBean = list.get(0);
		}
		uploadBean.setImageState(ImageUploadState.STATE_WAIT);
		sendBroadcast(uploadBean);
		if (mRuningTaskQueue.size() == MAX_UPLOAD_SIZE) {
			mWaitingTaskQueue.add(uploadBean);
			return;
		}
		startTask(uploadBean);
	}
	
	public synchronized void restart(UploadBean uploadBean) {
			addUploadTask(uploadBean);
	}
	
	public synchronized void addUploadTask(Hotel hotel) {
		if (hotel == null) {
			return;
		}
		for (CheckData checkData : hotel.getCheckDatas()) {
			//过滤掉动态区域的问题
			if (checkData.getType() != CheckDataType.TYPE_PASSWAY 
					&& checkData.getType() != CheckDataType.TYPE_ROOM) {
				addUploadTask(checkData, hotel.getCheckId());
			}
		}
		for (CheckData checkData : hotel.getRoomList()) {
			addUploadTask(checkData, hotel.getCheckId());
		}
		for (CheckData checkData : hotel.getPasswayList()) {
			addUploadTask(checkData, hotel.getCheckId());
		}
	}
	
	private synchronized void addUploadTask(CheckData checkData, long checkId) {
		if (checkData.getCheckedIssueCount() > 0) {
			for (IssueItem issueItem : checkData.getCheckedIssue()) {
				if (issueItem.getImagelist() != null) {
					for (ImageItem imageItem : issueItem.getImagelist()) {
						UploadBean uploadBean = new UploadBean(
								checkId, checkData.getId(),
								checkData.getName(), issueItem.getId(),
								issueItem.getName(),
								issueItem.getDimOneId(),
								issueItem.getDimOneName(), imageItem);
						if (imageItem.isWidth()) {
							uploadBean.setIsWidth(0);
						} else {
							uploadBean.setIsWidth(1);
						}
//						uploadBean.setType(imageItem.getType());
						uploadBean.setType(checkData.getType());
						addUploadTask(uploadBean);
					}
				}
			}
		}
	}
	
	
	private synchronized boolean startTask(UploadBean uploadBean) {
		if (null != uploadBean) {
			mRuningTaskQueue.put(uploadBean.getLocalImagePath(), uploadBean);
			uploadFile(uploadBean);
			return true;
		} else {
			return false;
		}
	}
	
	
	

		
		private synchronized void uploadFile(final UploadBean bean) {
			String localFilePath = bean.getLocalImagePath();
			if (TextUtils.isEmpty(localFilePath)) {
				Log.e(TAG, "uploadFile localFilePath is null or empty");
				bean.setImageState(ImageUploadState.STATE_FAIL);
				sendBroadcast(bean);
				mRuningTaskQueue.remove(bean.getLocalImagePath());
				return;
			}
			File localFile = new File(localFilePath);
			if (!localFile.exists()) {
				Log.e(TAG, "uploadFile localFile not exist and filepath = "+localFilePath);
				bean.setImageState(ImageUploadState.STATE_FAIL);
				sendBroadcast(bean);
				mRuningTaskQueue.remove(bean.getLocalImagePath());
				return;
			}
			try {
				/*
				 * 设置进度条回掉函数
				 * 
				 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
				 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
				 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
				 */
				ProgressListener progressListener = new ProgressListener() {
					@Override
					public void transferred(long transferedBytes, long totalBytes) {
						// do something...
						bean.setImageState(ImageUploadState.STATE_START);
						bean.setTransferedBytes(transferedBytes);
						bean.setTotalBytes(totalBytes);
						sendBroadcast(bean);
					}
				};
				
				CompleteListener completeListener = new CompleteListener() {
					@Override
					public void result(boolean isComplete, String result, String error) {
						if (isComplete) {
							bean.setImageState(ImageUploadState.STATE_FINISH);
							mRuningTaskQueue.remove(bean.getLocalImagePath());
						} else {
							bean.setImageState(ImageUploadState.STATE_FAIL);
							mRuningTaskQueue.remove(bean.getLocalImagePath());
						}
						sendBroadcast(bean);
						Log.d(TAG, "upload task result = "+isComplete);
					}
				};
				
				
				Map<String, Object> paramsMap = mUploaderManager.fetchFileInfoDictionaryWith(localFile, bean.getServiceImageSavePath());
				//还可以加上其他的额外处理参数...
				paramsMap.put("return_url", "http://httpbin.org/get");
				// signature & policy 建议从服务端获取
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, UPAI.FORM_API_SECRET);
				mUploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	
	
	private  void sendBroadcast(UploadBean bean) {
		if (bean != null) {
			if (bean.getImageState() == ImageUploadState.STATE_FINISH) {
				if (mWaitingTaskQueue.size() > 0) {
					UploadBean newTask = mWaitingTaskQueue.remove(0);
					addUploadTask(newTask);
				}
				DataManager.getInstance().updateImageStatus(UploadService.this, bean);
			}
			Intent intent = new Intent(HotelAction.ACTION_IMAGE_UPLOAD);
			intent.putExtra(HotelAction.IMAGE_UPLOAD_EXTRA, bean);
			sendBroadcast(intent);	
			bean.save();	
		}
	}
	
	
}
