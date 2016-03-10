package com.sg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by qml_moon on 11/11/15.
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
@ComponentScan
public class App  {

	public static void main( String[] args ) {

		SpringApplication.run(App.class, args);

	}


}
