package com.sg.spring.dao;

/**
 * Created by qml_moon on 11/11/15.
 */
import com.sg.sql.model.Customer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class JDBCCustomerDAO implements CustomerDAO {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int insert(Customer customer) {

		String sql = "INSERT INTO CUSTOMER " +
			"(NAME, USER_ID, PASSWORD, PHONE, PRIVILEGE) VALUES (?, ?, ?, ?, ?)";
		Connection conn = null;
		//  1 : success
		//  0 : user already exists
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			if (findByUserId(customer.getUserId(), conn) == null) {
				PreparedStatement ps = conn.prepareStatement(sql);

				ps.setString(1, customer.getName());
				ps.setString(2, customer.getUserId());
				ps.setString(3, customer.getPassword());
				ps.setString(4, customer.getPhone());
				ps.setString(5, customer.getPrivilege());
				ps.executeUpdate();
				ps.close();
				isSuccess = 1;
			} else {
				isSuccess = 0;
			}

		} catch (SQLException e) {
			isSuccess = -1;
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			return isSuccess;
		}
	}

	public int delete(String userId) {
		String sql = "DELETE FROM CUSTOMER WHERE USER_ID = ?";
		Connection conn = null;
		//  1 : success
		//  0 : no such user
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			if (findByUserId(userId, conn) != null) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				ps.executeUpdate();
				ps.close();
				isSuccess = 1;
			} else {
				isSuccess = 0;
			}

		} catch (SQLException e) {
			isSuccess = -1;
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			return isSuccess;
		}
	}

	public int changePassword(Customer customer, String newPassword) {
		String sql = "UPDATE CUSTOMER SET PASSWORD = ? WHERE USER_ID = ?";
		Connection conn = null;
		//  1 : success
		//  0 : password is not correct
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			if (findByUserId(customer.getUserId(), conn).getPassword().equals(customer.getPassword())) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, newPassword);
				ps.setString(2, customer.getUserId());
				ps.executeUpdate();
				ps.close();
				isSuccess = 1;
			} else {
				isSuccess = 0;
			}

		} catch (SQLException e) {
			isSuccess = -1;
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			return isSuccess;
		}
	}

	public int changePrivilege(Customer customer, String privilege) {
		String sql = "UPDATE CUSTOMER SET PRIVILEGE = ? WHERE USER_ID = ?";
		Connection conn = null;
		//  1 : success
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, privilege);
			ps.setString(2, customer.getUserId());
			ps.executeUpdate();
			ps.close();
			isSuccess = 1;
		} catch (SQLException e) {
			isSuccess = -1;
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			return isSuccess;
		}
	}

	public int login(Customer customer) {
		String sql = "UPDATE CUSTOMER SET LAST_ONLINE = ? WHERE USER_ID = ?";
		Connection conn = null;
		//  1 : success
		//  0 : password is not correct
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			Customer data = findByUserId(customer.getUserId(), conn);
			if (data.getPassword().equals(customer.getPassword())) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setDate(1, new Date(new java.util.Date().getTime()));
				ps.setString(2, customer.getUserId());
				ps.executeUpdate();
				ps.close();
				isSuccess = 1;
				customer.setName(data.getName());
				customer.setPhone(data.getPhone());
				customer.setPrivilege(data.getPrivilege());
				customer.setPassword("");
			} else {
				isSuccess = 0;
			}

		} catch (SQLException e) {
			isSuccess = -1;
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			return isSuccess;
		}
	}

	public Customer[] getAllUser() {
		String sql = "SELECT * FROM CUSTOMER";
		List<Customer> result = new ArrayList<Customer>();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new Customer(
					rs.getString("USER_ID"),
					"",
					rs.getString("NAME"),
					rs.getString("PHONE"),
					rs.getString("PRIVILEGE"),
					rs.getString("LAST_ONLINE")
				));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			Customer[] customers = new Customer[result.size()];
			for (int i = 0; i < result.size(); i++) {
				customers[i] = result.get(i);
			}
			return customers;
		}
	}

	private Customer findByUserId(String userId, Connection conn) {

		String sql = "SELECT * FROM CUSTOMER WHERE USER_ID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			Customer customer = null;
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				customer = new Customer(
					rs.getString("USER_ID"),
					rs.getString("PASSWORD"),
					rs.getString("NAME"),
					rs.getString("PHONE"),
					rs.getString("PRIVILEGE")
				);
			}
			rs.close();
			ps.close();
			return customer;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}