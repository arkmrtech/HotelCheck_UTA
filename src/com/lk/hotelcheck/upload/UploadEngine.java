package com.lk.hotelcheck.upload;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UploadEngine {

	private Executor taskDistributor = Executors.newFixedThreadPool(5);
	
	/** Submits task to execution pool */
	void submit() {
		taskDistributor.execute(new Runnable() {
			@Override
			public void run() {
				
			}
		});
	}
	
	
}
