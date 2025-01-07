package itmo.app.controller.services;

import itmo.app.filter.RequestCacherFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public FilterRegistrationBean<RequestCacherFilter> loggingFilter() {
		FilterRegistrationBean<RequestCacherFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestCacherFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
		return registrationBean;
	}
}
