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

	public Customer(String id, String password, String name, String phone, String privilege, String lastOnline) {
		this.userId = id;
		this.name = name;
		this.password = password;
		this.phone = phone;
		this.privilege = privilege;
		this.lastOnline = lastOnline;
	}

	public Customer(String id, String password, String name, String phone, String privilege) {
		this(id, password, name, phone, privilege, null);
	}

	public Customer(String id, String password) {
		this(id, password, null, "", null, null);
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