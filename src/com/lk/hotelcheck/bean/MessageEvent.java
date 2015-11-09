package com.lk.hotelcheck.bean;

public class MessageEvent {

	private int messageType;

	public static final int MESSAGE_UPDATE_HOTEL_DATA = 0;
	
	public MessageEvent(int messageType) {
		super();
		this.messageType = messageType;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	
	
	
}
