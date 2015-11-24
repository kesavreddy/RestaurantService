package com.venkat.restaurant.twitter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class TweetMenuCollector{
	private Set<String> menuItems;
	private long menuUpdatedDate;
	private Twitter twitter;
	private User user;
	
	public TweetMenuCollector(){
		menuItems = new HashSet<String>();
		twitter = new TwitterFactory().getInstance();
		try {
			user = twitter.verifyCredentials();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	public Set<String> loadRestaurantMenu(){
		if (menuItems.isEmpty()){
			updateRestaurantMenu();
		}
		return menuItems;
	}
	
	// Go to twitter timeline and get the menu order
	public Set<String> updateRestaurantMenu(){
		// Clear the set because when next day menu available then we need to clear yesterday
		menuItems.clear();
		try{
			List<Status> statuses = twitter.getHomeTimeline();
			System.out.println("Showing @" + user.getScreenName()
					+ "'s home timeline. account id :-"+twitter.getScreenName());
			for (Status status : statuses) {
				if (!user.getScreenName().equals(status.getUser().getScreenName()) || status.getInReplyToScreenName() != null){
					continue;
				}
				for (HashtagEntity hashEnity : status.getHashtagEntities()) {
					/*System.out.println("#" + status.getUser().getScreenName()
						+ " - " + hashEnity.getText());*/
					menuItems.add(hashEnity.getText());
				}
				setMenuUpdatedDate(status.getCreatedAt().getTime());
				System.out.println("fetchRestaurantMenu:- @" + status.getUser().getScreenName()
						+ " - " + status.getText() + " - " + status.getId());
				System.out.println("Menu updated to "+menuItems);
				break;
			}
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
		}
		return menuItems;
	}

	public long getMenuUpdatedDate() {
		return menuUpdatedDate;
	}

	private void setMenuUpdatedDate(long menuUpdatedDate) {
		this.menuUpdatedDate = menuUpdatedDate;
	}
}
