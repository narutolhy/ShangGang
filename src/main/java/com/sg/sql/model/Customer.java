package com.sg.sql.model;

/**
 * Created by qml_moon on 11/11/15.
 */

public class Customer {
	private String userId;
	private String password;
	private String name;
	private String phone;
	private String privilege;
	private String lastOnline;
	private String unit;

	private double redWarning;
	private double yellowWarning;
	private double redWarning2;
	private double yellowWarning2;
	private String warningStatus;

	public Customer(String id, String password, String name, String phone, String privilege, String unit, String lastOnline) {
		this.userId = id;
		this.name = name;
		this.password = password;
		this.phone = phone;
		this.privilege = privilege;
		this.unit = unit;
		this.lastOnline = lastOnline;
	}

	public Customer(String id, String password, String name, String phone, String privilege, String unit) {
		this(id, password, name, phone, privilege, unit, null);
	}

	public Customer(String id, String password) {
		this(id, password, null, "", null, null, null);
	}

	public Customer(String userId) {
		this(userId, null);
	}

	//getter and setter methods
	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public String getPrivilege() {
		return privilege;
	}

	public String getLastOnline() {
		return lastOnline;
	}

	public String getUnit() {
		return unit;
	}

	public double getRedWarning() {
		return redWarning;
	}

	public double getYellowWarning() {
		return yellowWarning;
	}

	public double getRedWarning2() {
		return redWarning2;
	}

	public double getYellowWarning2() {
		return yellowWarning2;
	}

	public String getWarningStatus() {
		return warningStatus;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setRedWarning(double red) {
		this.redWarning = red;
	}

	public void setYellowWarning(double yellow) {
		this.yellowWarning = yellow;
	}

	public void setRedWarning2(double red) {
		this.redWarning2 = red;
	}

	public void setYellowWarning2(double yellow) {
		this.yellowWarning2 = yellow;
	}


	public void setWarningStatus(String status) {
		this.warningStatus = status;
	}
}