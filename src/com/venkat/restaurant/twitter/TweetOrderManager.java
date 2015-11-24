package com.venkat.restaurant.twitter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.venkat.restaurant.backend.util.Constants;
import com.venkat.restaurant.backend.util.DBManager;
import com.venkat.restaurant.backend.util.Order;
import com.venkat.restaurant.backend.util.OrderBuffer;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class TweetOrderManager implements Runnable {
	private OrderBuffer buffer;
	private Map<Long, Order> orders;
	private Map<Long, Order> pendingOrders;
	private Map<Long, Order> deletedOrders;
	private Twitter twitter;
	private User user;

	public TweetOrderManager(OrderBuffer buffer) {
		this.buffer = buffer;
		this.buffer = buffer;
		orders = new ConcurrentHashMap<Long, Order>();
		pendingOrders = new ConcurrentHashMap<Long, Order>();
		deletedOrders = new ConcurrentHashMap<Long, Order>();
		twitter = new TwitterFactory().getInstance();
		try {
			user = twitter.verifyCredentials();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			getCustomerOrders();
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void getCustomerOrders() {
		try {
			List<Status> statuses = twitter.getMentionsTimeline();
			System.out.println("Fetching from @" + user.getScreenName()
					+ "'s mentions.");
			Set<String> hashTags;
			for (Status status : statuses) {
				// If order is already present then do not process
				if (!getAllOrders().containsKey(status.getId()) && !getDeletedOrders().containsKey(status.getId())) {
					hashTags = new HashSet<String>();
					// Get all hashtags from order
					for (HashtagEntity hashEnity : status.getHashtagEntities()) {
						/*
						 * System.out.println("#" +
						 * status.getUser().getScreenName() + " - " +
						 * hashEnity.getText());
						 */
						hashTags.add(hashEnity.getText());
					}
					// When specific hash tag is present then only consider it
					// as valid order
					if (hashTags.contains(Constants.ORDER_HASH_TAG)) {
						StringBuffer orderItems = new StringBuffer();
						String delim = "";
						// remove order hashtag and get final order list
						hashTags.remove(Constants.ORDER_HASH_TAG);
						for (String hashTag : hashTags) {
							orderItems.append(delim).append(hashTag);
							delim = ",";
						}

						Order order = new Order(status.getId(), status
								.getUser().getScreenName(), orderItems.toString(),
								status.getCreatedAt().getTime());
						System.out.println("Got new order " + orderItems.toString() + " from :"
								+ order.getUserName());

						buffer.addOrder(order);
						orders.put(status.getId(), order);
						/*System.out.println("@" + order.getUserName() + " - "
								+ order.getItem() + " - " + order.getId()
								+ " - " + order.getReqTime());*/
						DBManager.getInstance().insertOrder(order);
					}
				}
			}
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
		}
	}

	public static void main(String[] args) {
		OrderBuffer buff = new OrderBuffer();
		TweetOrderManager handler = new TweetOrderManager(buff);
		//handler.getCustomerOrders();
		handler.isValidOrder("663747443876495400");
		handler.deleteOrder("663747443876495400");
		
	}

	public Map<Long, Order> getAllOrders() {
		return orders;
	}
	
	public Map<Long, Order> getDeletedOrders() {
		return deletedOrders;
	}

	public void restoreOrdersfromDB(List<Order> orders) {
		for (Order order : orders) {
			if (order.isDeleted() == false){
				this.orders.put(order.getId(), order);
			} else {
				this.deletedOrders.put(order.getId(), order);
			}
		}
	}

	public Map<Long, Order> getPendingOrders() {
		Collection<Order> allOrders = orders.values();
		for (Order order:allOrders){
			// Check Current time to Order time difference is within the ETA time
			if ((System.currentTimeMillis() - order.getReqTime() < Constants.ETA_TIME)){
				pendingOrders.put(order.getId(), order);
			}
		}
		return pendingOrders;
	}

	public void deleteOrder(String orderId) {
		if (orderId == null || orderId.isEmpty() || !isValidOrder(orderId)) {
			System.err.println("Order id is not valid :-"+orderId);
			return;
		}
		Long id = Long.parseLong(orderId);
		System.out.println("REST request delete Order for :- " + id);
		// Delete orders from queue
		buffer.removeOrder(orders.get(id));
		// Delete orders from DB
		DBManager.getInstance().deleteOrder(orders.get(id));
		// Delete orders from internal memory
		pendingOrders.remove(id);
		orders.remove(id);

	}
	
	public boolean isValidOrder(String orderId){
		try{
			Long.parseLong(orderId);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
            return false;
        }
        
        return true;
	}
	

}
