package com.lk.hotelcheck;

import java.io.File;
import java.util.Map;

import android.os.Build;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orm.SugarApp;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.CrashHandleCallback;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;

import common.Constance;

public class HotelCheckApplication extends SugarApp{

	private String mWifiSpeed;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader();
//		CrashHandler crashHandler = CrashHandler.getInstance();  
//        crashHandler.init(this);  
        initDataFile();
        //bugly
        String appId = "900006006";   //上Bugly(bugly.qq.com)注册产品获取的AppId
        boolean isDebug = false ;  //true代表App处于调试阶段，false代表App发布阶段
        CrashReport.initCrashReport(this, appId ,isDebug);  //初始化SDK    
        UserStrategy strategy = new UserStrategy(this); //App的策略Bean
//        strategy.setAppChannel(getPackageName());     //设置渠道
//        strategy.setAppVersion(""+Build.VERSION);      //App的版本
//        strategy.setAppReportDelay(1000);  //设置SDK处理延时，毫秒
//        strategy.setDeviceID(GlobalUtil.getInstance().getDeviceID(instance));
        strategy.setCrashHandleCallback(new CrashHandleCallback() {
        	@Override
        	public synchronized Map<String, String> onCrashHandleStart(
        			int arg0, String arg1, String arg2, String arg3) {
        		android.os.Process.killProcess(android.os.Process.myPid()); 
        		return super.onCrashHandleStart(arg0, arg1, arg2, arg3);
        	}
        });
	}
	
	
	private void initDataFile() {
		File tempCacheFile = new File(Constance.Path.TEMP_IMAGE_FLOER_PATH);
		if (!tempCacheFile.exists()) {
			tempCacheFile.mkdirs();
		}
		File imageFile = new File(Constance.Path.IMAGE_PATH);
		if (!imageFile.exists()) {
			imageFile.mkdirs();
		}
	} 
	
	
	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon) // resource or drawable
				.showImageForEmptyUri(R.drawable.icon) // resource or drawable
				.showImageOnFail(R.drawable.icon) // resource or drawable
				// .resetViewBeforeLoading(true) // default
				.cacheInMemory(true).cacheOnDisk(true)

				// .considerExifParams(false) // default
				// .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) //
				// default
				.build();
		File cacheFile = new File(Constance.Path.IMAGE_CACHE);
		if (!cacheFile.exists()) {
			cacheFile.mkdirs();
		}
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.threadPriority(Thread.NORM_PRIORITY)
				.threadPoolSize(3)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.diskCache(new UnlimitedDiskCache(cacheFile))
				.memoryCacheSize(8*1024*1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(options).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}



	public String getWifiSpeed() {
		return mWifiSpeed;
	}



	public void setWifiSpeed(String mWifiSpeed) {
		this.mWifiSpeed = mWifiSpeed;
	}
	
	
}
