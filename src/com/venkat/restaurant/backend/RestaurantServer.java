package com.venkat.restaurant.backend;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.venkat.restaurant.backend.servlets.RestaurantServlet;
import com.venkat.restaurant.backend.util.Constants;
import com.venkat.restaurant.backend.util.FileUtils;
import com.venkat.restaurant.backend.util.DBManager;
import com.venkat.restaurant.backend.util.Order;
import com.venkat.restaurant.backend.util.OrderBuffer;
import com.venkat.restaurant.twitter.TweetMenuCollector;
import com.venkat.restaurant.twitter.TweetOrderManager;
import com.venkat.restaurant.twitter.TweetStatusUpdater;

/*
 * Server is implemented using embedded jetty server
 * Refer http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty for details
 */
public class RestaurantServer {
	public static final String PORT = "port";
	public static final String CONFIG_UPDATE_PERIOD = "updatePeriod";

	private static final String CONTEXT_PATH = "contextPath";
	private Properties configurnProperties;
	private Server server;
	private ServletContextHandler context;

	public RestaurantServer(String configPropertiesLocation) {
		configurnProperties = FileUtils
				.loadFileIntoProperties(configPropertiesLocation);
		setupAndConfigure();
	}

	private void setupAndConfigure() {
		int port = 4080;
		String serverContextPath = configurnProperties.getProperty(
				CONTEXT_PATH, "/");

		try {
			port = Integer.valueOf(configurnProperties
					.getProperty(PORT, "4080").trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err
					.println("port key not configured, seeting default - 4080");
			port = 4080;
		}
		System.out.println("Going to run on port :- " + port);
		server = new Server();
		SelectChannelConnector connector0 = new SelectChannelConnector();
		connector0.setPort(port);
		connector0.setRequestHeaderSize(16384);
		server.addConnector(connector0);

		// server = new Server(port);
		context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(serverContextPath);

		server.setHandler(context);

		addServletHolders();
	}

	/**
	 * Add a servlet, its a wrapper method over context. Other is get handle
	 * over context, by invoking <CODE>getContext().add(...)<CODE>
	 * 
	 * @param svHolder
	 * @param pathSpec
	 */
	public void addServlet(ServletHolder svHolder, String pathSpec) {
		context.addServlet(svHolder, pathSpec);
	}

	private void addServletHolders() {
		context.addServlet(new ServletHolder(new RestaurantServlet()), "/*");
	}

	public void start() {
		try {
			server.start();
			initializeTwitterHandler();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeTwitterHandler() {
		OrderBuffer buff = new OrderBuffer();
		TweetOrderManager tweetCollector = new TweetOrderManager(buff);
		context.setAttribute(Constants.ORDER_COLLECTOR, tweetCollector);
		Thread produder = new Thread(tweetCollector);
		// Get the order status from DB
		List<Order> orders = DBManager.getInstance().getAllOrdersFromDB();
		tweetCollector.restoreOrdersfromDB(orders);
		// Get latest menu
		TweetMenuCollector menuCollector = new TweetMenuCollector();
		menuCollector.loadRestaurantMenu();

		TweetStatusUpdater tweetsUpdate = new TweetStatusUpdater(buff);
		Thread consumer = new Thread(tweetsUpdate);

		produder.start();
		consumer.start();
	}

	public static void main(String[] args) throws Exception {
		String configFile = "conf/config.properties";
		if (args.length > 0) {
			configFile = args[0];
		}
		System.out.println("Going to load config of :-"
				+ new File(configFile).getAbsolutePath());

		RestaurantServer server = new RestaurantServer(configFile);
		server.start();
	}

}
