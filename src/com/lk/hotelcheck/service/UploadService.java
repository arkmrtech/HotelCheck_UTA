package com.lk.hotelcheck.service;

import java.io.File;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.transform.Templates;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.lk.hotelcheck.bean.CheckData;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.HotelUploadQueneBean;
import com.lk.hotelcheck.bean.HotelUploadTask;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.IssueItem;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.manager.DataManager;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

import common.Constance;
import common.Constance.HotelAction;
import common.Constance.ImageUploadState;
import common.Constance.UPAI;

public class UploadService extends Service {

	private ServiceBinder mServiceBinder = new ServiceBinder();
	private Executor mTaskExecutor = Executors.newFixedThreadPool(MAX_DOWNLOAD_SIZE);
	private ArrayList<UploadBean> mWaitTaskQueue = new ArrayList<UploadBean>();
	private ArrayList<UploadBean> mRuningTaskQueue = new ArrayList<UploadBean>();
	private ArrayList<UploadBean> mExceptionQueue = new ArrayList<UploadBean>();
	private static final int MAX_DOWNLOAD_SIZE = 3;
	private HotelUploadQueneBean mUploadQuene;
	
	@Override
	public IBinder onBind(Intent intent) {
		return mServiceBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate(); 
		mUploadQuene = new HotelUploadQueneBean();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		saveUnFinishData();
	}
	
	public void saveUnFinishData() {
		for (UploadBean bean : mWaitTaskQueue) {
			bean.setImageState(ImageUploadState.STATE_FAIL);
			bean.save();
		}
		for (UploadBean bean : mRuningTaskQueue) {
			bean.setImageState(ImageUploadState.STATE_FAIL);
			bean.save();
		}
		for (UploadBean bean : mExceptionQueue) {
			bean.setImageState(ImageUploadState.STATE_FAIL);
			bean.save();
		}
	}

	
	public synchronized void addUploadTask(UploadBean uploadBean) {
		uploadBean.setImageState(ImageUploadState.STATE_WAIT);
		mWaitTaskQueue.add(uploadBean);
		sendBroadcast(uploadBean);
		if (mRuningTaskQueue.size() < MAX_DOWNLOAD_SIZE) {
			startNext();
		}
	}
	
	public synchronized void restart(UploadBean uploadBean) {
		boolean result = false;
		if (mExceptionQueue != null && mExceptionQueue.size() >0) {
			for (int i = 0; i < mExceptionQueue.size(); i++) {
				UploadBean tmp = mExceptionQueue.get(i);
				if (tmp.getId() == uploadBean.getId()) {
					mExceptionQueue.remove(i);
//					result = startTask(uploadBean);
					addUploadTask(uploadBean);
					return;
				}
			}
			if (!result) {
				addUploadTask(uploadBean);
			} 
		} else {
			addUploadTask(uploadBean);
		}
		
	}
	
	public synchronized void addUploadTask(Hotel hotel) {
		
		if (hotel == null) {
			return;
		}
//		List<UploadBean> uploadList = DataManager.getInstance().getUploadTaskList(hotel.getCheckId());
//		if (uploadList != null) {
//			for (UploadBean uploadBean : uploadList) {
//				addUploadTask(uploadBean);
//			}
//		} else {
			for (CheckData  checkData : hotel.getCheckDatas()) {
				addUploadTask(checkData, hotel.getCheckId());
			}
			for (CheckData  checkData : hotel.getRoomList()) {
				addUploadTask(checkData, hotel.getCheckId());
			}
			for (CheckData  checkData : hotel.getPasswayList()) {
				addUploadTask(checkData, hotel.getCheckId());
			}
//		}
		
	}
	
	private void addUploadTask(CheckData checkData, long checkId) {
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
						uploadBean.setType(imageItem.getType());
						addUploadTask(uploadBean);
					}
				}
			}
		}
	}
	
	private boolean hasHotelTask(int hotelId) {
		boolean flag = false;
		if (mUploadQuene.getHotelUploadTaskList() == null) {
			return flag;
		}
		for (HotelUploadTask task : mUploadQuene.getHotelUploadTaskList()) {
			if (task.getHotelId() == hotelId) {
				flag = true;
				return flag;
			}
		}
		return flag;
	}
	
	private HotelUploadTask getHotelUploadTask(int hotelId) {
		for (HotelUploadTask task : mUploadQuene.getHotelUploadTaskList()) {
			if (task.getHotelId() == hotelId) {
				return task;
			}
		}
		return null;
	}
	
	private synchronized boolean startNext() {
		int size = mWaitTaskQueue.size();
		if (size <= 0) {
//			DataManager.getInstance().updateImageStatus(UploadService.this);
			return true;
		}

		UploadBean tmp = mWaitTaskQueue.remove(0);
//		if (null != tmp) {
//			tmp.setImageState(ImageUploadState.STATE_WAIT);
//			mRuningTaskQueue.add(tmp);
//			UploadTask task = new UploadTask(tmp);
//			mTaskExecutor.execute(task);
//			return true;
//		}

//		return false;
		return startTask(tmp);
		
	}
	
	
	private boolean startTask(UploadBean uploadBean) {
		if (null != uploadBean) {
			uploadBean.setImageState(ImageUploadState.STATE_WAIT);
			mRuningTaskQueue.add(uploadBean);
			UploadTask task = new UploadTask(uploadBean);
			mTaskExecutor.execute(task);
			return true;
		} else {
			return false;
		}
	}
	
	public class ServiceBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }
	
	
	class UploadTask implements Runnable {

		private static final String TAG = "UploadTask";
		private UploadBean mBean;
		
		public UploadTask(UploadBean bean) {
			super();
			this.mBean = bean;
		}

		@Override
		public void run() {
			uploadFile();
		}

		private void uploadFile() {
			String localFilePath = mBean.getLocalImagePath();
			if (TextUtils.isEmpty(localFilePath)) {
				Log.e(TAG, "uploadFile localFilePath is null or empty");
				mBean.setImageState(ImageUploadState.STATE_FAIL);
				sendBroadcast(mBean);
				mRuningTaskQueue.remove(mBean);
				mExceptionQueue.add(mBean);
				startNext();
				return;
			}
			File localFile = new File(localFilePath);
			if (!localFile.exists()) {
				Log.e(TAG, "uploadFile localFile not exist and filepath = "+localFilePath);
				mBean.setImageState(ImageUploadState.STATE_FAIL);
				sendBroadcast(mBean);
				mRuningTaskQueue.remove(mBean);
				mExceptionQueue.add(mBean);
				startNext();
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
						mBean.setImageState(ImageUploadState.STATE_START);
						mBean.setTransferedBytes(transferedBytes);
						mBean.setTotalBytes(totalBytes);
						sendBroadcast(mBean);
						Log.d("lxk", "trans:" + transferedBytes + "; total:" + totalBytes +"imageurl = "+mBean.getServiceImageSavePath());
					}
				};
				
				CompleteListener completeListener = new CompleteListener() {
					@Override
					public void result(boolean isComplete, String result, String error) {
						// do something...
						if (isComplete) {
							mBean.setImageState(ImageUploadState.STATE_FINISH);
							mRuningTaskQueue.remove(mBean);
							DataManager.getInstance().updateImageStatus(UploadService.this, mBean);
						} else {
							mBean.setImageState(ImageUploadState.STATE_FAIL);
							mRuningTaskQueue.remove(mBean);
							mExceptionQueue.add(mBean);
						}
						sendBroadcast(mBean);
						startNext();
						Log.d("lxk", "isComplete:"+isComplete+" imageurl = "+mBean.getServiceImageSavePath());
//						Log.d("lxk", "isComplete:"+isComplete+";result:"+result+";error:"+error);
					}
				};
				
				UploaderManager uploaderManager = UploaderManager.getInstance(UPAI.BUCKET);
				uploaderManager.setConnectTimeout(60);
				uploaderManager.setResponseTimeout(60);
				Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, mBean.getServiceImageSavePath());
				//还可以加上其他的额外处理参数...
				paramsMap.put("return_url", "http://httpbin.org/get");
				// signature & policy 建议从服务端获取
				String policyForInitial = UpYunUtils.getPolicy(paramsMap);
				String signatureForInitial = UpYunUtils.getSignature(paramsMap, UPAI.FORM_API_SECRET);
				uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
//	public List<UploadBean> getUploadingList(int hotelId) {
//		if (mUploadTaskArray.indexOfKey(hotelId) >-1) {
//			SparseArray<UploadBean> tmp = mUploadTaskArray.get(hotelId);
//			List<UploadBean> uploadBeanList = new ArrayList<UploadBean>();
//			for (int i = 0; i < tmp.size(); i++) {
//				UploadBean uploadBean = tmp.valueAt(i);
//				if (uploadBean.getImageState() != ImageUploadState.STATE_FINISH) {
//					uploadBeanList.add(uploadBean);
//				}
//			}
//			return uploadBeanList;
//		} else {
//			return Collections.emptyList();
//		}
//	}
//	
//	public List<UploadBean> getUploadCompleteList(int hotelId) {
//		if (mUploadTaskArray.indexOfKey(hotelId) >-1) {
//			SparseArray<UploadBean> tmp = mUploadTaskArray.get(hotelId);
//			List<UploadBean> uploadBeanList = new ArrayList<UploadBean>();
//			for (int i = 0; i < tmp.size(); i++) {
//				UploadBean uploadBean = tmp.valueAt(i);
//				if (uploadBean.getImageState() == ImageUploadState.STATE_FINISH) {
//					uploadBeanList.add(uploadBean);
//				}
//			}
//			return uploadBeanList;
//		} else {
//			return Collections.emptyList();
//		}
//	}
	
	
	public List<UploadBean> getUploadingList(int hotelId) {
		return DataManager.getInstance().getUploadingList(hotelId);
	}
	
	public List<UploadBean> getUploadCompleteList(int hotelId) {
		return DataManager.getInstance().getUploadCompleteList(hotelId);
	}
	
	public void sendBroadcast(UploadBean bean) {
		Intent intent = new Intent(HotelAction.ACTION_IMAGE_UPLOAD);
		intent.putExtra(HotelAction.IMAGE_UPLOAD_EXTRA, bean);
		sendBroadcast(intent);
		bean.save();
	}
	
	
}
