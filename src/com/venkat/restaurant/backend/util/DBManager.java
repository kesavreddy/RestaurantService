package com.venkat.restaurant.backend.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/*
 * Used SQLLite to store/retrivew records
 * http://www.tutorialspoint.com/sqlite/sqlite_java.htm
 */
public class DBManager {
	private static volatile Connection m_connection;
	private static volatile DBManager m_instance;

	private DBManager() {

	}

	public static DBManager getInstance() {
		if (m_instance == null) {
			synchronized (DBManager.class) {
				if (m_instance == null) {
					m_instance = new DBManager();
					try {
						Class.forName("org.sqlite.JDBC");
						m_connection = DriverManager
								.getConnection("jdbc:sqlite:test.db");
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return m_instance;
	}

	public void insertOrder(Order order) {
		System.out.println("insertOrder invoked");
		if (order == null) {
			System.err.println("Order is null. Can not be inserted");
			return;
		}
		Statement stmt = null;
		try {
			int deleted = order.isDeleted() ? 1 : 0;
			stmt = m_connection.createStatement();
			String sql = String.format(
					"INSERT INTO ORDERS (ID,STATUS,ITEMS,USERNAME,ISDELETED,TIME) "
							+ "VALUES (%d, \'%s\', \'%s\', \'%s\',%d,%d);",
					order.getId(), order.getOrderStatus().toString(),
					order.getItem(), order.getUserName(), deleted, order.getReqTime());
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateOrder(Order order) {
		if (order == null) {
			System.err.println("Order is null. Can not be updated");
			return;
		}
		System.out.println("updateOrder invoked for "+order.getId());
		Statement stmt = null;
		try {
			stmt = m_connection.createStatement();
			String sql = String.format(
					"UPDATE ORDERS set STATUS = 'ACK' where id = %d;",
					order.getId());
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteOrder(Order order) {
		if (order == null) {
			System.err.println("Order is null. Can not be deleted");
			return;
		}
		System.out.println("deleteOrder invoked for "+order.getId());
		Statement stmt = null;
		try {
			stmt = m_connection.createStatement();
			String sql = String.format("UPDATE ORDERS set ISDELETED = 1 where id = %d;",
					order.getId());
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Order> getAllOrdersFromDB() {
		System.out.println("getAllOrdersFromDB invoked");
		Statement stmt = null;
		ResultSet rs = null;
		List<Order> orders = new ArrayList<Order>();
		Order order = null;
		try {
			stmt = m_connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM ORDERS;");
			while (rs.next()) {
				long id = rs.getLong("ID");
				String name = rs.getString("USERNAME");
				long time = rs.getLong("TIME");
				String items = rs.getString("ITEMS");
				String status = rs.getString("STATUS");
				order = new Order(id, name, items, time);
				int deleted = rs.getInt("ISDELETED");
				order.setOrderStatus(OrderStatus.valueOf(status));
				boolean isDeleted = deleted == 0 ? false : true;
				order.setDeleted(isDeleted);
				orders.add(order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Number of records from DB :-"+orders.size());
		return orders;
	}

	public static void main(String args[]) {
		Order order = new Order(663747443876495360l, "venkat", "chicken", 84656);
		DBManager.getInstance().updateOrder(order);
		/*DBManager.getInstance().insertOrder(order);
		System.out.println(DBManager.getInstance().getAllOrdersFromDB());*/
		
		System.exit(1);
		 
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			stmt = c.createStatement();
			String sql = "DROP TABLE ORDERS";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE ORDERS " + "(ID LONG PRIMARY KEY     NOT NULL,"
					+ " STATUS           CHAR(50)    NOT NULL, "
					+ " ITEMS            CHAR(250)     NOT NULL, "
					+ " USERNAME        CHAR(50) NOT NULL, "
					+ " ISDELETED        INT, "
					+ " TIME         LONG  NOT NULL)";
			stmt.executeUpdate(sql);
			System.exit(0);
			sql = "INSERT INTO ORDERS (ID,STATUS,ITEMS,USERNAME,TIME) "
					+ "VALUES (663451658496552960, 'UNACK', '#cheeseburger', 'venkat_kesav',1447014176000);";
			System.out.println(sql);
			stmt.executeUpdate(sql);

			sql = "INSERT INTO ORDERS (ID,STATUS,ITEMS,USERNAME,TIME) "
					+ "VALUES (673451658496552890, 'UNACK', '#chickenburger', 'venkat_kesav',1447014179000);";
			stmt.executeUpdate(sql);

			ResultSet rs = stmt.executeQuery("SELECT * FROM ORDERS;");
			// Order order = null;
			while (rs.next()) {
				long id = rs.getLong("ID");
				String name = rs.getString("USERNAME");
				long time = rs.getLong("TIME");
				String items = rs.getString("ITEMS");
				String status = rs.getString("STATUS");
				order = new Order(id, name, items, time);
				order.setOrderStatus(OrderStatus.valueOf(status));

				System.out.println("ID = " + id);
				System.out.println("USERNAME = " + name);
				System.out.println("TIME = " + time);
				System.out.println("ITEMS = " + items);
				System.out.println("STATUS = " + status);
				System.out.println();
			}
			rs.close();
			/*
			 * stmt = c.createStatement(); String sql = "CREATE TABLE ORDERS " +
			 * "(ID LONG PRIMARY KEY     NOT NULL," +
			 * " STATUS           BOOLEAN    NOT NULL, " +
			 * " ITEMS            INT     NOT NULL, " +
			 * " USERNAME        CHAR(50), " + " TIME         LONG)";
			 * stmt.executeUpdate(sql);
			 */
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Table created successfully");
	}
}
