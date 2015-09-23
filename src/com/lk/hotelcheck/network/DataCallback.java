package com.lk.hotelcheck.network;


public interface DataCallback {
	void onSuccess();
	void onFail(int errorCode, String info);
}
