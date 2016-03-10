package com.sg.spring.dao;

import com.sg.sql.model.Harbor;

import java.util.List;

/**
 * Created by qml_moon on 16/11/15.
 */
public interface HarborDAO {

	public int insert(List<Harbor> data, String date, int harborId, boolean override);

	public List<Harbor> dump(String date, int harborId);

	public int delete(String date, int harborId);

	public String[] getAllDate(int harborId);

	public int getPrevData(String date, int harborId, List<Harbor> container);

	public List<Harbor> getPrevTrend(int harborId);

	public void insertTrend(int harborId, List<Harbor> trend);
}
