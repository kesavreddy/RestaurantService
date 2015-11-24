package com.venkat.restaurant.tests;

import org.json.JSONArray;

public class RestaurantServerTest {
	
	public void validateGetAllOrder(){
		URL url = new URL("http://localhost:4080/1.1/restaurant/orders?isPending=true");
		HTTPClient client = new HTTPClient();
		HTTPGet getMethod = new HTTPGet();
		JSONArray orders = client.getMethos(url);
		assertTrue(orders.length() > 0 , "No order found");
		
	}
}
