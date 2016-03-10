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
			"(NAME, USER_ID, PASSWORD, PHONE, PRIVILEGE, UNIT) VALUES (?, ?, ?, ?, ?, ?)";
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
				ps.setString(6, customer.getUnit());
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
		String sql1 = "DELETE FROM user_depth_setting WHERE user_id = ?";
		String sql2 = "DELETE FROM customer WHERE user_id = ?";
		Connection conn = null;
		//  1 : success
		//  0 : no such user
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			if (findByUserId(userId, conn) != null) {
				PreparedStatement ps = conn.prepareStatement(sql1);
				ps.setString(1, userId);
				ps.executeUpdate();

				ps = conn.prepareStatement(sql2);
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

	public int changeInfo(Customer customer, String newPassword) {
		String sql = "UPDATE CUSTOMER SET NAME = ?, PHONE = ?, UNIT = ?, PASSWORD = ? WHERE USER_ID = ?";
		Connection conn = null;
		//  1 : success
		//  0 : password is not correct
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			if (newPassword.equals("") ||
					findByUserId(customer.getUserId(), conn).getPassword().equals(customer.getPassword())) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, customer.getName());
				ps.setString(2, customer.getPhone());
				ps.setString(3, customer.getUnit());
				ps.setString(4, newPassword.equals("") ? customer.getPassword() : newPassword);
				ps.setString(5, customer.getUserId());
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

	public int setWarningStatus(Customer customer, String status) {
		String sql = "UPDATE CUSTOMER SET WARNING_STATUS = ? WHERE USER_ID = ?";
		Connection conn = null;
		//  1 : success
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, status);
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
				customer.setUnit(data.getUnit());
				customer.setPassword("");
				customer.setWarningStatus(data.getWarningStatus());
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
				//return all users except admin without password field.
				if (!rs.getString("USER_ID").equals("admin")) {
					result.add(new Customer(
						rs.getString("USER_ID"),
						"",
						rs.getString("NAME"),
						rs.getString("PHONE"),
						rs.getString("PRIVILEGE"),
						rs.getString("UNIT"),
						rs.getString("LAST_ONLINE")
					));
				}
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

	public double[] getDepthLevel(Customer customer, int harborId) {
		double[] result = new double[0];
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			String sql = "SELECT * FROM user_depth_setting WHERE user_id = ? AND harbor_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, customer.getUserId());
			ps.setInt(2, harborId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String[] splits = rs.getString("depth_level").split(",");
				result = new double[splits.length];
				for (int i = 0; i < splits.length; i++) {
					result[i] = Double.parseDouble(splits[i]);
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}

			return result;
		}
	}



	public int setDepthLevel(Customer customer, int harborId, String depthLevel) {
		String sql1 = "SELECT * from user_depth_setting WHERE user_id = ? AND harbor_id = ?";
		String sql2 = "UPDATE user_depth_setting SET depth_level = ? WHERE user_id = ? AND harbor_id = ?";
		String sql3 = "INSERT into user_depth_setting(user_id, harbor_id, depth_level) values(?, ?, ?)";
		Connection conn = null;
		//  1 : success
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql1);
			ps.setString(1, customer.getUserId());
			ps.setInt(2, harborId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ps = conn.prepareStatement(sql2);
				ps.setString(1, depthLevel);
				ps.setString(2, customer.getUserId());
				ps.setInt(3, harborId);
				ps.executeUpdate();
			} else {
				ps = conn.prepareStatement(sql3);
				ps.setString(1, customer.getUserId());
				ps.setInt(2, harborId);
				ps.setString(3, depthLevel);
				ps.executeUpdate();
			}
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

	public int getWarning(Customer customer, int harborId) {
		String sql = "SELECT * from user_warning_setting WHERE user_id = ? AND harbor_id = ?";
		Connection conn = null;
		//  1 : success
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, customer.getUserId());
			ps.setInt(2, harborId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				customer.setRedWarning(rs.getObject("RED_WARNING") == null ? -100 : rs.getDouble("RED_WARNING"));
				customer.setYellowWarning(rs.getObject("YELLOW_WARNING") == null ? -100 : rs.getDouble("YELLOW_WARNING"));
				customer.setBlueWarning(rs.getObject("BLUE_WARNING") == null ? -100 : rs.getDouble("BLUE_WARNING"));
			} else {
				customer.setRedWarning(-100);
				customer.setYellowWarning(-100);
				customer.setBlueWarning(-100);
			}
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

	public int setWarning(Customer customer, int harborId) {
		String sql1 = "SELECT * from user_warning_setting WHERE user_id = ? AND harbor_id = ?";
		String sql2 = "UPDATE user_warning_setting SET red_warning = ?, yellow_warning = ?, blue_warning = ? WHERE user_id = ? AND harbor_id = ?";
		String sql3 = "INSERT into user_warning_setting(user_id, harbor_id, red_warning, yellow_warning, blue_warning) values(?, ?, ?, ?, ?)";
		Connection conn = null;
		//  1 : success
		// -1 : database is down
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql1);
			ps.setString(1, customer.getUserId());
			ps.setInt(2, harborId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ps = conn.prepareStatement(sql2);
				ps.setObject(1, customer.getRedWarning() > -50 ? customer.getRedWarning() : null);
				ps.setObject(2, customer.getYellowWarning() > -50 ? customer.getYellowWarning() : null);
				ps.setObject(3, customer.getBlueWarning() > -50 ? customer.getBlueWarning() : null);
				ps.setString(4, customer.getUserId());
				ps.setInt(5, harborId);
				ps.executeUpdate();
			} else {
				ps = conn.prepareStatement(sql3);
				ps.setString(1, customer.getUserId());
				ps.setInt(2, harborId);
				ps.setObject(3, customer.getRedWarning() > -50 ? customer.getRedWarning() : null);
				ps.setObject(4, customer.getYellowWarning() > -50 ? customer.getYellowWarning() : null);
				ps.setObject(5, customer.getBlueWarning() > -50 ? customer.getBlueWarning() : null);
				ps.executeUpdate();
			}
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
					rs.getString("PRIVILEGE"),
					rs.getString("UNIT")
				);
				customer.setWarningStatus(rs.getString("WARNING_STATUS"));
			}
			rs.close();
			ps.close();
			return customer;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}

}