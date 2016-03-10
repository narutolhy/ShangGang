package com.sg.spring.controller.util;

import com.sg.spring.dao.HarborDAO;
import com.sg.sql.model.Harbor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qml_moon on 27/11/15.
 * TODO: if the data is not inserted in order.
 */
@Component
public class Prediction {

	@Async
	public void updatePredictTable(List<Harbor> currDepth, String date, int harborId, boolean dreged, HarborDAO harborDAO) {

		List<Harbor> prevDepth = new ArrayList<Harbor>();
		int numOfMonth = harborDAO.getPrevData(date, harborId, prevDepth);
		if (prevDepth.size() != 0 && !dreged) {
			List<Harbor> currTrend = calculateTrend(currDepth, prevDepth, numOfMonth);
			List<Harbor> prevTrend = harborDAO.getPrevTrend(harborId);
			if (prevTrend.size() != 0) {
				merge(currTrend, prevTrend, 1.0 / 3);
			}
			harborDAO.insertTrend(harborId, currTrend);
		}
	}

	public void predict(List<Harbor> prevDepth, List<Harbor> trend, int numOfMonths) {
		for (Harbor depth : prevDepth) {
			Harbor nearest = trend.get(0);
			for (int i = 1; i < trend.size(); i++) {
				if (euclidianDistance(depth, trend.get(i)) < euclidianDistance(depth, nearest)) {
					nearest = trend.get(i);
				}
			}
			depth.setDepth(depth.getDepth() + nearest.getDepth() * numOfMonths);
		}
	}

	private List<Harbor> calculateTrend(List<Harbor> currDepth, List<Harbor> prevDepth, int numOfMonth) {
		List<Harbor> trend = new ArrayList<Harbor>();
		for (Harbor depth : currDepth) {
			Harbor nearest = prevDepth.get(0);
			for (int i = 1; i < prevDepth.size(); i++) {
				if (euclidianDistance(depth, prevDepth.get(i)) < euclidianDistance(depth, nearest)) {
					nearest = prevDepth.get(i);
				}
			}
			trend.add(new Harbor(depth.getLongitude(), depth.getLatitude(),
				(depth.getDepth() - nearest.getDepth()) / numOfMonth));
		}
		return trend;
	}

	private void merge(List<Harbor> currTrend, List<Harbor> prevTrend, double ratio) {
		for (Harbor trend : currTrend) {
			Harbor nearest = prevTrend.get(0);
			for (int i = 1; i < prevTrend.size(); i++) {
				if (euclidianDistance(trend, prevTrend.get(i)) < euclidianDistance(trend, nearest)) {
					nearest = prevTrend.get(i);
				}
			}
			trend.setDepth(trend.getDepth() * ratio + nearest.getDepth() * (1 - ratio));
		}
	}

	/**
	 * euclidian distance takes too much time, use hamilton distance instead.
	 */
	private double euclidianDistance(Harbor h1, Harbor h2) {
//		return Math.pow((h1.getLatitude() - h2.getLatitude()), 2) +
//							Math.pow((h1.getLongitude() - h2.getLongitude()), 2);
		return Math.abs(h1.getLatitude()-h2.getLatitude()) + Math.abs(h1.getLongitude() - h2.getLongitude());
	}


}
