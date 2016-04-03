package com.sg.spring.controller;

import com.sg.spring.dao.CustomerDAO;
import com.sg.sql.model.Customer;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qml_moon on 12/11/15.
 */
@RestController
public class CustomerController {

	CustomerDAO customerDAO;

	public CustomerController() {
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		customerDAO = (CustomerDAO) context.getBean("customerDAO");

	}
	@RequestMapping(path = "/getuser", method = RequestMethod.GET)
	public Customer[] getCustomer() {

		return customerDAO.getAllUser();
	}

	@RequestMapping(path = "/adduser", method = RequestMethod.POST)
	public int addCustomer(@RequestParam(value = "userId") String userId,
						   @RequestParam(value = "password") String password,
						   @RequestParam(value = "name") String name,
						   @RequestParam(value = "phone") String phone,
						   @RequestParam(value = "unit") String unit,
						   @RequestParam(value = "privilege") String rawPrivilege) {

		String privilege = parsePrivilege(rawPrivilege);
		return customerDAO.insert(new Customer(userId, password, name, phone, privilege, unit));
	}

	@RequestMapping(path = "/changeprivilege", produces = "application/json", method = RequestMethod.POST)
	public String changePrivilege(@RequestParam(value = "userId") String userId,
							   @RequestParam(value = "privilege") String rawPrivilege) {

		String privilege = parsePrivilege(rawPrivilege);

		int isSuccess = customerDAO.changePrivilege(new Customer(userId), privilege);
		JSONObject js = new JSONObject();

		js.put("isSuccess", isSuccess);
		if (isSuccess == 1) {
			js.put("privilege", privilege);
		}
		return js.toString();
	}

	@RequestMapping(path = "/deleteuser", method = RequestMethod.POST)
	public int deleteCustomer(@RequestParam(value = "userId") String userId) {

		return customerDAO.delete(userId);
	}

	@RequestMapping(path = "/changeinfo", method = RequestMethod.POST)
	public int changePassword(@RequestParam(value = "userId") String userId,
							  @RequestParam(value = "newPassword") String newPassword,
							  @RequestParam(value = "oldPassword") String oldPassword,
							  @RequestParam(value = "name") String name,
							  @RequestParam(value = "phone") String phone,
							  @RequestParam(value = "unit") String unit) {

		return customerDAO.changeInfo(new Customer(userId, oldPassword, name, phone, null, unit), newPassword);
	}

	@RequestMapping(path = "/login", produces = "application/json", method = RequestMethod.GET)
	public String login(@RequestParam(value = "userId") String userId,
					 @RequestParam(value = "password") String password) {

		Customer customer = new Customer(userId, password);
		int isSuccess = customerDAO.login(customer);
		JSONObject js = new JSONObject();

		js.put("isSuccess", isSuccess);
		if (isSuccess == 1) {
			js.put("name", customer.getName());
			js.put("privilege", customer.getPrivilege());
			js.put("phone", customer.getPhone());
			js.put("unit", customer.getUnit());
			js.put("userId", customer.getUserId());
			if (customer.getWarningStatus() != null) {
				js.put("warningStatus", customer.getWarningStatus());
			} else {
				js.put("warningStatus", "ggggg");
			}
		}
		return js.toString();

	}

	@RequestMapping(path = "/getdepthlevel", method = RequestMethod.GET)
	public double[] getDepthLevel(@RequestParam(value = "userId") String userId,
								  @RequestParam(value = "harborId") int harborId) {
		Customer customer = new Customer(userId);
		return customerDAO.getDepthLevel(customer, harborId);
	}

	@RequestMapping(path = "/setdepthlevel", method = RequestMethod.POST)
	public int setDepthLevel(@RequestParam(value = "userId") String userId,
							 @RequestParam(value = "harborId") int harborId,
							 @RequestParam(value = "depthLevel") String depthLevel) {
		return customerDAO.setDepthLevel(new Customer(userId), harborId, depthLevel);
	}

	@RequestMapping(path = "/setwarningstatus", method = RequestMethod.POST)
	public int setWarningStatus(@RequestParam(value = "userId") String userId,
							 @RequestParam(value = "warningStatus") String status) {
		return customerDAO.setWarningStatus(new Customer(userId), status);
	}

	@RequestMapping(path = "/getwarninglevel", produces = "application/json", method = RequestMethod.GET)
	public String getWarningLevel(@RequestParam(value = "userId") String userId,
								  @RequestParam(value = "harborId") int harborId) {
		Customer customer = new Customer(userId);
		int isSuccess = customerDAO.getWarning(customer, harborId);
		JSONObject js = new JSONObject();
		if (isSuccess == 1) {
			js.put("redWarning", customer.getRedWarning() > -50 ? customer.getRedWarning() : "");
			js.put("yellowWarning", customer.getYellowWarning() > -50 ? customer.getYellowWarning() : "");
			js.put("redWarning2", customer.getRedWarning2() > -50 ? customer.getRedWarning2() : "");
			js.put("yellowWarning2", customer.getYellowWarning2() > -50 ? customer.getYellowWarning2() : "");
		}
		return js.toString();
	}

	@RequestMapping(path = "/setwarninglevel", method = RequestMethod.POST)
	public int setWarningLevel(@RequestParam(value = "userId") String userId,
							   @RequestParam(value = "harborId") int harborId,
							   @RequestParam(value = "redWarning") String red,
							   @RequestParam(value = "yellowWarning") String yellow,
							   @RequestParam(value = "redWarning2") String red2,
							   @RequestParam(value = "yellowWarning2") String yellow2) {
		Customer c = new Customer(userId);
		if (!red.equals("")) {
			c.setRedWarning(Double.parseDouble(red));
		} else {
			c.setRedWarning(-100);
		}
		if (!yellow.equals("")) {
			c.setYellowWarning(Double.parseDouble(yellow));
		} else {
			c.setYellowWarning(-100);
		}
		if (!red2.equals("")) {
			c.setRedWarning2(Double.parseDouble(red2));
		} else {
			c.setRedWarning2(-100);
		}
		if (!yellow2.equals("")) {
			c.setYellowWarning2(Double.parseDouble(yellow2));
		} else {
			c.setYellowWarning2(-100);
		}

		return customerDAO.setWarning(c, harborId);
	}

	private String parsePrivilege(String rawPrivilege) {
		Set<String> ps = new HashSet<String>();
		String[] splits = rawPrivilege.split(",");
		for (String split : splits) {
			ps.add(split);
		}
		String privilege = "";
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 6; j++) {
				if (ps.contains("" + i + j)) {
					privilege += "Y";
				} else {
					privilege += "N";
				}
			}
			privilege += "|";
		}
		return privilege.substring(0, privilege.length() - 1);
	}

}
