package com.lk.hotelcheck.bean;

import java.util.ArrayList;
import java.util.List;

public class HotelList {
	private List<Hotel> hotels;

	public List<Hotel> getHotels() {
		return hotels;
	}

	public void setHotels(List<Hotel> hotels) {
		this.hotels = hotels;
	}
	
	public int size(){
		if (hotels == null) {
			return 0;
		} else {
			return hotels.size();
		}
	}
	
	public Hotel getHotelByPosition(int position) {
		if (hotels == null || position < 0 || position >= hotels.size()) {
			return null;
		}
		return hotels.get(position);
	}

	public void setHotel(Hotel hotel, int position) {
		if (hotels == null) {
			hotels = new ArrayList<Hotel>();
		}
//		hotels.add(hotel);
		hotels.set(position, hotel);
	}
}
