package com.sg.spring.dao;

import com.sg.sql.model.Harbor;

import java.util.List;

/**
 * Created by qml_moon on 16/11/15.
 */
public interface HarborDAO {

	public int insert(List<Harbor> data, String date, boolean override);

	public List<Harbor> dump(String date);

	public String[] getAllDate();

	public int getPrevData(String date, List<Harbor> container);

	public List<Harbor> getPrevTrend();

	public void insertTrend(List<Harbor> trend);
}
