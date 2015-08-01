package com.lk.hotelcheck.upload;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.lk.hotelcheck.R;
import com.lk.hotelcheck.bean.Hotel;
import com.lk.hotelcheck.bean.ImageItem;
import com.lk.hotelcheck.bean.UploadBean;
import com.lk.hotelcheck.service.UploadService;
import com.lk.hotelcheck.util.Machine;

public class UploadProxy {
	
	private UploadService mBinder;
	
//	private Queue<Runnable> mRunnableList = new LinkedList<Runnable>();
	
	private static UploadProxy sInstance;
	
	private Context mContext = null;

	synchronized public static UploadProxy initInstance(Context context) {
		if (null == sInstance) {
			sInstance = new UploadProxy(context);
			sInstance.doBindService();
			
		}
		return sInstance;
	}

	private UploadProxy(Context context) {
		mContext = context;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBinder = ((UploadService.ServiceBinder)service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBinder = null;
			doBindService();
		}
	};

	private void doBindService() {
		try {
			mContext.bindService(new Intent(mContext,
					UploadService.class), mConnection,
					Context.BIND_AUTO_CREATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doUnbindService() {
		try {
			mContext.unbindService(mConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void addUploadTask(UploadBean bean) {
		sInstance.mBinder.addUploadTask(bean);
	}
	
	public static void addUploadTask(Hotel hotel) {
		sInstance.mBinder.addUploadTask(hotel);
	}
	
	public static List<UploadBean> getUploadingList(int hotelId) {
		return sInstance.mBinder.getUploadingList(hotelId);
	}
	
	public static List<UploadBean> getUploadComplete(int hotelId) {
		return sInstance.mBinder.getUploadCompleteList(hotelId);
	}
	
	public static void restart(UploadBean bean) {
		sInstance.mBinder.restart(bean);
	}

	public void saveData() {
		sInstance.mBinder.saveUnFinishData();
	}

//	public static void saveData() {
//		sInstance.mBinder.saveData();
//	}
	
	
}
