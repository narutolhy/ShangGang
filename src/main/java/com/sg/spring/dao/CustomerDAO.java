package com.sg.spring.dao;

import com.sg.sql.model.Customer;

import java.sql.Connection;

/**
 * Created by qml_moon on 11/11/15.
 */
public interface CustomerDAO {
	public int insert(Customer customer);

	public int delete(String userId);

	public int changeInfo(Customer customer, String newPassword);

	public int changePrivilege(Customer customer, String privilege);

	public int login(Customer customer);

	public Customer[] getAllUser();

}