//package com.lk.hotelcheck.upload;
//
//import java.io.File;
//import java.net.URLEncoder;
//import java.util.Map;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.lk.hotelcheck.bean.ImageItem;
//import com.upyun.block.api.listener.CompleteListener;
//import com.upyun.block.api.listener.ProgressListener;
//import com.upyun.block.api.main.UploaderManager;
//import com.upyun.block.api.utils.UpYunUtils;
//
//import common.Constance;
//import common.Constance.UPAI;
//
//public class UploadTask implements Runnable {
//
//	private static final String TAG = "UploadTask";
//	private ImageItem mImageItem;
//	
//	
//	
//	
//	public UploadTask(ImageItem imageItem) {
//		super();
//		this.mImageItem = imageItem;
//	}
//
//	@Override
//	public void run() {
//		uploadFile();
//	}
//
//	private void uploadFile() {
//		String localFilePath = mImageItem.getLocalImagePath();
//		if (TextUtils.isEmpty(localFilePath)) {
//			Log.e(TAG, "uploadFile localFilePath is null or empty");
//			return;
//		}
//		File localFile = new File(localFilePath);
//		if (!localFile.exists()) {
//			Log.e(TAG, "uploadFile localFile not exist and filepath = "+localFilePath);
//			return;
//		}
//		try {
//			/*
//			 * 设置进度条回掉函数
//			 * 
//			 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
//			 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
//			 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
//			 */
//			ProgressListener progressListener = new ProgressListener() {
//				@Override
//				public void transferred(long transferedBytes, long totalBytes) {
//					// do something...
//					Log.d("lxk", "trans:" + transferedBytes + "; total:" + totalBytes);
//				}
//			};
//			
//			CompleteListener completeListener = new CompleteListener() {
//				@Override
//				public void result(boolean isComplete, String result, String error) {
//					// do something...
//					Log.d("lxk", "isComplete:"+isComplete+";result:"+result+";error:"+error);
//				}
//			};
//			
//			UploaderManager uploaderManager = UploaderManager.getInstance(UPAI.BUCKET);
//			uploaderManager.setConnectTimeout(60);
//			uploaderManager.setResponseTimeout(60);
//			Log.d("lxk", "service path = "+mImageItem.getServiceSavePath());
//			Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, mImageItem.getServiceSavePath());
//			//还可以加上其他的额外处理参数...
//			paramsMap.put("return_url", "http://httpbin.org/get");
//			// signature & policy 建议从服务端获取
//			String policyForInitial = UpYunUtils.getPolicy(paramsMap);
//			String signatureForInitial = UpYunUtils.getSignature(paramsMap, UPAI.FORM_API_SECRET);
//			uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//}
