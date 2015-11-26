package com.sg;

import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Conventions;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

/**
 * Created by qml_moon on 25/11/15.
 */
@Configuration
public class CORSFilterConfig implements ServletContextInitializer {

	protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter) {
		String filterName = Conventions.getVariableName(filter);
		FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter);
		registration.setAsyncSupported(true);
		registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
		return registration;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		registerServletFilter(servletContext, new SimpleCORSFilter());
	}
}
