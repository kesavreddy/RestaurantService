package com.venkat.restaurant.backend.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Order {
	private long id;
	private String userName;
	private long reqTime;
	private long etaTime;
	// TODO change it to list. currently item are separated by ,
	private String item;
	private OrderStatus status;
	private boolean deleted;
	
	public Order(long id, String user,String item,long time){
		this.id = id;
		this.userName = user;
		this.item = item;
		this.reqTime = time;
		this.etaTime = time + Constants.ETA_TIME;
		this.setDeleted(false);
		this.setOrderStatus(OrderStatus.valueOf("UNACK"));
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public long getReqTime(){
		return reqTime;
	}
	
	public String getReqTimeAsStr() {
		Date date = new Date(reqTime);
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy");
		formatter.setTimeZone(TimeZone.getTimeZone("PST"));
		return formatter.format(date);
	}
	
	public void setReqTime(long reqTime) {
		this.reqTime = reqTime;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	
	public OrderStatus getOrderStatus() {
		return status;
	}
	public void setOrderStatus(OrderStatus status) {
		this.status = status;
	}

	public long getEtaTime() {
		return etaTime;
	}

	public void setEtaTime(long etaTime) {
		this.etaTime = etaTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
