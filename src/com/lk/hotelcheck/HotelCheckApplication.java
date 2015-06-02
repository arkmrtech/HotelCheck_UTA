package com.lk.hotelcheck;

import java.io.File;

import android.app.Application;

import com.lk.hotelcheck.util.FileUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import common.Constance;

public class HotelCheckApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		File tempCacheFile = new File(Constance.Path.TEMP_IMAGE_PATH);
		if (!tempCacheFile.exists()) {
			tempCacheFile.mkdirs();
		}
		initImageLoader();
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
	
	
}
