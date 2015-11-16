package com.sg.sql.model;

/**
 * Created by qml_moon on 11/11/15.
 */
public class Harbor {
	double longitude;
	double latitude;
	double depth;

	public Harbor(long longitude, long latitude, double depth) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.depth = depth;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getDepth() {
		return depth;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setDepth(double depth) {
		this.depth = depth;
	}
}
