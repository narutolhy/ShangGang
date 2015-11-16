package com.sg;

import com.sg.spring.dao.CustomerDAO;
import com.sg.sql.model.Customer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by qml_moon on 11/11/15.
 */
@SpringBootApplication
@EnableAutoConfiguration
public class App {

	public static void main( String[] args ) {
		SpringApplication.run(App.class, args);

	}
}
