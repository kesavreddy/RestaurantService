package com.venkat.restaurant.backend.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class OrderBuffer {
	BlockingQueue<Order> ordersQueue = new LinkedBlockingQueue<Order>();;
	public OrderBuffer(){
		
	}
	
	public void addOrder(Order order){
		try {
			ordersQueue.put(order);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Order removeOrder() throws InterruptedException{
		System.out.println("Queuse size :- "+ordersQueue.size());
		return ordersQueue.take();
	}
	
	// delete particular order when client requested to delete the order
	public boolean removeOrder(Order order){
		return ordersQueue.remove(order);
	}
}
