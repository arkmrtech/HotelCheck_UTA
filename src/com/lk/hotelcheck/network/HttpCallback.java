package com.lk.hotelcheck.network;

import org.json.JSONObject;

public interface HttpCallback {
	void onSuccess(JSONObject response);
	void onError(int errorCode, String info);
}
