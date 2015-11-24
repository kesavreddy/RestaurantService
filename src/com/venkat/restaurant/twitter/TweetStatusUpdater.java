package com.venkat.restaurant.twitter;

import com.venkat.restaurant.backend.util.Constants;
import com.venkat.restaurant.backend.util.Order;
import com.venkat.restaurant.backend.util.OrderBuffer;
import com.venkat.restaurant.backend.util.OrderStatus;
import com.venkat.restaurant.backend.util.DBManager;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TweetStatusUpdater implements Runnable {
	private OrderBuffer buffer;
	private Twitter twitter;

	public TweetStatusUpdater(OrderBuffer buffer) {
		this.buffer = buffer;
		twitter = new TwitterFactory().getInstance();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Order order = buffer.removeOrder();
				System.out.println("Updating staus for :-"+order.getId());
				StatusUpdate statusUpdate = new StatusUpdate("@"
						+ order.getUserName()
						+ " ,Thanks for your order. Your order will be ready in next "+Constants.ETA_TIME_STR);
				if (order.getOrderStatus() == OrderStatus.UNACK){
					statusUpdate = statusUpdate
							.inReplyToStatusId(order.getId());
					statusUpdate.setInReplyToStatusId(order.getId());
					twitter.updateStatus(statusUpdate);
					System.out.println("Successfully updated the order to ["
							+ order.getUserName() + "].");
					// After update status to twitter then update the DB record as well
					DBManager.getInstance().updateOrder(order);
				}
			} catch (InterruptedException | TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
