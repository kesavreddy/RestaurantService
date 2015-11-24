package com.venkat.restaurant.backend.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.venkat.restaurant.backend.util.Constants;
import com.venkat.restaurant.backend.util.Order;
import com.venkat.restaurant.twitter.TweetMenuCollector;
import com.venkat.restaurant.twitter.TweetOrderManager;


public class RestaurantServlet extends HttpServlet{
	
	public static final String CONFIG_FILE_PATH = "./conf/";
	

	/**
	 * Generated serial id 
	 */
	private static final long serialVersionUID = -1119871127587995253L;
	
	public RestaurantServlet(){
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException{
		// Authunticate the user
		if ( ! formResponse(response, req) ){
			prepareErrorResponse(req,response);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException{
		String requestURI = req.getRequestURI();
		System.out.println("delete requestURI :-"+requestURI);
		if (0 == requestURI.compareTo("/1.1/restaurant/orders")){ 
			TweetOrderManager orderManager = getOrderManager(req);
			BufferedReader br = req.getReader();
			if (br != null){
				String orderId = br.readLine();
				orderManager.deleteOrder(orderId);
			}
			return;
		}
		prepareErrorResponse(req,response);
	}
	
	private boolean formResponse(HttpServletResponse response, HttpServletRequest req) throws IOException{
		String requestURI = req.getRequestURI();
		System.out.println("requestURI :-"+requestURI);
		//System.out.println(getHeadersInfo(req));
		
		if (0 == requestURI.compareTo("/1.1/restaurant/orders")){
			TweetOrderManager orderManager = getOrderManager(req);
			String isPending = "false";
			String isDeleted = "false";
			if (req.getParameterValues("ispending") != null){
				isPending = req.getParameterValues("ispending")[0];
			}
			if (req.getParameterValues("isdeleted") != null){
				isDeleted = req.getParameterValues("isdeleted")[0];
			}
			if ("true".equals(isPending)){
				prepareOrderResponse(response, orderManager.getPendingOrders());
			} else if("true".equals(isDeleted)){
				prepareOrderResponse(response, orderManager.getDeletedOrders());
			} else {
				prepareOrderResponse(response, orderManager.getAllOrders());
			}
			return true;
		} else if (0 == requestURI.compareTo("/1.1/restaurant/updatemenu")){
			TweetMenuCollector menuCollector = new TweetMenuCollector();
			menuCollector.updateRestaurantMenu();
			try {
				response.setContentType("application/json");
				JSONObject resp = new JSONObject();
				resp.put("status","menu updated");
				response.getWriter().println(resp.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		} /*else if (0 == requestURI.compareTo("/1.1/restaurant/deletedorders")){
			TweetOrderManager orderManager = getOrderManager(req);
			prepareOrderResponse(response, orderManager.getDeletedOrders());
			return true;
		}*/
		return false;
	}
	
	public TweetOrderManager getOrderManager(HttpServletRequest req){
		return (TweetOrderManager) req.getSession().getServletContext().getAttribute(Constants.ORDER_COLLECTOR);
	}
	
	//get request headers
	/*private Map<String, String> getHeadersInfo(HttpServletRequest req) {

		Map<String, String> map = new HashMap<String, String>();

		Enumeration headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = req.getHeader(key);
			map.put(key, value);
		}
		return map;
	}*/
	
	private void prepareErrorResponse(HttpServletRequest req,
			HttpServletResponse response) throws IOException{
			response.setContentType("application/json");
			try {
				JSONObject error = new JSONObject();
				error.put("code", 404);
				error.put("reason", "request url not implemented");
				JSONArray errors = new JSONArray();
				errors.put(error);
				JSONObject resp = new JSONObject();
				resp.put("errors",errors);
				response.getWriter().println(resp.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
	}
	
	private void prepareOrderResponse(HttpServletResponse response,
			Map<Long,Order> orders) throws IOException {
		response.setContentType("application/json");
		Set<Entry<Long, Order>> ordersSet = orders.entrySet();
		JSONArray objects = new JSONArray();
		JSONObject object = null;
		for(Entry<Long, Order> order:ordersSet){
			try {
				object = new JSONObject();
				String temp = new Long(order.getKey()).toString();
				object.put("id",temp);
				object.put("user",order.getValue().getUserName());
				object.put("item",order.getValue().getItem());
				object.put("time",order.getValue().getReqTimeAsStr());
				objects.put(object);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		response.getWriter().println(objects.toString());
	}	
}
