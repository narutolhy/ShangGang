package com.sg.spring.dao;

import com.sg.sql.model.Customer;

import java.sql.Connection;

/**
 * Created by qml_moon on 11/11/15.
 */
public interface CustomerDAO {
	public int insert(Customer customer);

	public int delete(String userId);

	public int changePassword(Customer customer, String newPassword);

	public int login(Customer customer);

}