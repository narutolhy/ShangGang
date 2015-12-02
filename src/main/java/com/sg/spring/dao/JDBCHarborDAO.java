package com.sg.spring.dao;

import com.sg.sql.model.Harbor;

import javax.sql.DataSource;
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

	public int insert(List<Harbor> data, String date, int harborId, boolean override) {
		String sql = "INSERT INTO harbor_measure " +
			"(LONGITUDE, LATITUDE, DEPTH, DATE_ID) VALUES (?, ?, ?, ?)";
		Connection conn = null;
		int isSuccess = 0;
		try {
			conn = dataSource.getConnection();
			int dateId = findFKOfDate(conn, date, harborId);
			if (dateId != 0) {
				if (!override) {
					return 0;
				} else {
					clear(conn, dateId);
				}
			} else {
				insertDate(conn, date, harborId);
				dateId = findFKOfDate(conn, date, harborId);
			}

			PreparedStatement ps = conn.prepareStatement(sql);

			for (Harbor harbor : data) {
				ps.setDouble(1, harbor.getLongitude());
				ps.setDouble(2, harbor.getLatitude());
				ps.setDouble(3, harbor.getDepth());
				ps.setInt(4, dateId);
				ps.addBatch();
			}
			ps.executeBatch();
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

	public List<Harbor> dump(String date, int harborId) {
		String sql = "SELECT * FROM harbor_measure WHERE DATE_ID = ?";

		List<Harbor> result= new ArrayList<Harbor>();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			int dateId = findFKOfDate(conn, date, harborId);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, dateId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new Harbor(rs.getDouble("longitude"),
										rs.getDouble("latitude"),
										rs.getDouble("depth")));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {}
			}
			return result;
		}
	}

	public String[] getAllDate(int harborId) {
		String sql = "SELECT * FROM harbor_date where HARBOR_ID = ? ORDER BY measure_date DESC";
		Connection conn = null;
		List<String> result = new ArrayList<String>();
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, harborId);

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

	public int getPrevData(String date, int harborId, List<Harbor> container) {
		String sql = "SELECT date_id, measure_date from harbor_date WHERE measure_date = " + "" +
						"(SELECT MAX(measure_date) FROM harbor_date WHERE harbor_id = ? AND measure_date< ?)";

		Connection conn = null;
		int numberOfMonth = -1;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, harborId);
			ps.setString(2, date);

			ResultSet rs = ps.executeQuery();
			int dateId = -1;
			String prevDate = "";
			if (rs.next()) {
				dateId = rs.getInt("date_id");
				prevDate = rs.getString("measure_date");
			}
			rs.close();
			ps.close();
			if (dateId != -1) {
				sql = "SELECT * FROM harbor_measure WHERE DATE_ID = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, dateId);
				rs = ps.executeQuery();
				while (rs.next()) {
					container.add(new Harbor(rs.getDouble("longitude"),
						rs.getDouble("latitude"),
						rs.getDouble("depth")));
				}
				rs.close();
				ps.close();
				numberOfMonth = (Integer.parseInt(date.substring(0, 2)) - Integer.parseInt(prevDate.substring(0, 2))) * 12
									+ Integer.parseInt(date.substring(3, 5)) - Integer.parseInt(prevDate.substring(3, 5));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
			return numberOfMonth;
		}
	}

	public List<Harbor> getPrevTrend(int harborId) {
		String sql = "SELECT * FROM harbor_trend WHERE harbor_id = ?";

		List<Harbor> result= new ArrayList<Harbor>();
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, harborId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(new Harbor(rs.getDouble("longitude"),
					rs.getDouble("latitude"),
					rs.getDouble("trend")));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {}
			}
			return result;
		}
	}

	public void insertTrend(int harborId, List<Harbor> trend) {
		String sql1 = "DELETE FROM harbor_trend";
		String sql2 = "INSERT INTO harbor_trend " +
			"(LONGITUDE, LATITUDE, TREND, HARBOR_ID) VALUES (?, ?, ?, ?)";

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql1);
			ps.executeUpdate();
			ps.close();

			ps = conn.prepareStatement(sql2);
			for (Harbor data : trend) {
				ps.setDouble(1, data.getLongitude());
				ps.setDouble(2, data.getLatitude());
				ps.setDouble(3, data.getDepth());
				ps.setInt(4, harborId);
				ps.addBatch();
			}
			ps.executeBatch();

			ps.close();

		} catch (Exception e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {}
			}
		}
	}

	private int findFKOfDate(Connection conn, String date, int harborId) throws SQLException {
		String sql = "SELECT * FROM harbor_date WHERE MEASURE_DATE = ? AND HARBOR_ID = ?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, date);
		ps.setInt(2, harborId);
		ResultSet rs = ps.executeQuery();
		int id = 0;
		if (rs.next()) {
			id = rs.getInt("DATE_ID");
		}
		rs.close();
		ps.close();
		return id;

	}

	private void insertDate(Connection conn, String date, int harborId) throws SQLException {
		String sql = "INSERT INTO harbor_date(MEASURE_DATE, HARBOR_ID) values (?, ?)";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, date);
		ps.setInt(2, harborId);
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
