package com.sg;

import com.sg.spring.dao.CustomerDAO;
import com.sg.sql.model.Customer;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qml_moon on 12/11/15.
 */
@RestController
public class CustomerController {


	@RequestMapping(path = "/adduser", method = RequestMethod.POST)
	public int addCustomer(@RequestParam(value = "userId") String userId,
						   @RequestParam(value = "password") String password,
						   @RequestParam(value = "name") String name,
						   @RequestParam(value = "phone") String phone,
						   @RequestParam(value = "privilege") String rawPrivilege) {

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		CustomerDAO customerDAO = (CustomerDAO) context.getBean("customerDAO");

		Set<String> ps = new HashSet<String>();
		String[] splits = rawPrivilege.split(",");
		for (String split : splits) {
			ps.add(split);
		}
		String privilege = "";
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 6; j++) {
				if (ps.contains("" + i + j)) {
					privilege += "Y";
				} else {
					privilege += "N";
				}
			}
			privilege += "|";
		}
		privilege = privilege.substring(0, privilege.length() - 1);
		return customerDAO.insert(new Customer(userId, password, name, phone, privilege));
	}

	@RequestMapping(path = "/deleteuser", method = RequestMethod.POST)
	public int deleteCustomer(@RequestParam(value = "userId") String userId) {

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		CustomerDAO customerDAO = (CustomerDAO) context.getBean("customerDAO");
		return customerDAO.delete(userId);
	}

	@RequestMapping(path = "/changepassword", method = RequestMethod.POST)
	public int changePassword(@RequestParam(value = "userId") String userId,
							  @RequestParam(value = "oldPassword") String oldPassword,
							  @RequestParam(value = "newPassword") String newPassword) {

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		CustomerDAO customerDAO = (CustomerDAO) context.getBean("customerDAO");

		return customerDAO.changePassword(new Customer(userId, oldPassword), newPassword);

	}

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String login(@RequestParam(value = "userId") String userId,
					 @RequestParam(value = "password") String password) {

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		CustomerDAO customerDAO = (CustomerDAO) context.getBean("customerDAO");

		Customer customer = new Customer(userId, password);
		int isSuccess = customerDAO.login(customer);
		JSONObject js = new JSONObject();

		js.put("isSuccess", isSuccess);
		if (isSuccess == 1) {
			js.put("name", customer.getName());
			js.put("privilege", customer.getPrivilege());
			js.put("phone", customer.getPhone());
		}
		return js.toString();

	}



}
