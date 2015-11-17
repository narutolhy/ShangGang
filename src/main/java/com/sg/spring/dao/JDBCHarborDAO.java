package com.sg.spring.dao;

import com.sg.sql.model.Customer;
import com.sg.sql.model.Harbor;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qml_moon on 16/11/15.
 */
public class JDBCHarborDAO implements HarborDAO {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int insert(List<Harbor> data, String date, boolean override) {
		String sql = "INSERT INTO harbor_measure " +
			"(LONGITUDE, LATITUDE, DEPTH, DATE_ID) VALUES (?, ?, ?, ?)";
		Connection conn = null;
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			int dateId = findFKOfDate(conn, date);
			if (dateId != 0) {
				if (!override) {
					return 0;
				} else {
					clear(conn, dateId);
				}
			} else {
				insertDate(conn, date);
				dateId = findFKOfDate(conn, date);
			}

			PreparedStatement ps = conn.prepareStatement(sql);

			for (Harbor harbor : data) {
				ps.setDouble(1, harbor.getLongitude());
				ps.setDouble(2, harbor.getLatitude());
				ps.setDouble(3, harbor.getDepth());
				ps.setInt(4, dateId);
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

	public int dump(String dir, String date) {
		String sql = "SELECT * FROM harbor_measure WHERE DATE_ID = ?";

		int isSuccess = 0;
		Connection conn = null;
		BufferedWriter bw = null;
		try {
			conn = dataSource.getConnection();
			int dateId = findFKOfDate(conn, date);
			bw = new BufferedWriter(new FileWriter(dir));

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, dateId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String line = new Harbor(rs.getDouble("longitude"),
										rs.getDouble("latitude"),
										rs.getDouble("depth")).toString();
				bw.write(line);
			}
			rs.close();
			ps.close();
			isSuccess = 1;
		} catch (Exception e) {
			isSuccess = -1;
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					bw.close();
					conn.close();
				} catch (Exception e) {}
			}
			return isSuccess;
		}
	}

	public String[] getAllDate() {
		String sql = "SELECT * FROM harbor_date ORDER BY measure_date DESC";
		Connection conn = null;
		List<String> result = new ArrayList<String>();
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				 result.add(rs.getString("MEASURE_DATE"));
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
			String[] date = new String[result.size()];
			for (int i = 0; i < result.size(); i++) {
				date[i] = result.get(i);
			}
			return date;
		}
	}

	private int findFKOfDate(Connection conn, String date) throws SQLException {
		String sql = "SELECT * FROM harbor_date WHERE MEASURE_DATE = ?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, date);
		ResultSet rs = ps.executeQuery();
		int id = 0;
		if (rs.next()) {
			id = rs.getInt("DATE_ID");
		}
		rs.close();
		ps.close();
		return id;

	}

	private void insertDate(Connection conn, String date) throws SQLException {
		String sql = "INSERT INTO harbor_date(MEASURE_DATE) values (?)";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, date);
		ps.executeUpdate();
		ps.close();

	}

	private void clear(Connection conn, int dateId) throws SQLException {
		String sql = "DELETE FROM harbor_measure WHERE DATE_ID = ?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, dateId);
		ps.execute();
		ps.close();

	}
}
