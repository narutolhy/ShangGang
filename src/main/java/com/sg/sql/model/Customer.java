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

	public Customer(String id, String password, String name, String phone, String privilege) {
		this.userId = id;
		this.name = name;
		this.password = password;
		this.phone = phone;
		this.privilege = privilege;
	}

	public Customer(String id, String password) {
		this(id, password, null, "", null);
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
}