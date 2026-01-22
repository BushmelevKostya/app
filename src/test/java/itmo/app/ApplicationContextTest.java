package itmo.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationContextTest {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Test
	void contextLoads() {
		assertNotNull(applicationContext, "Application context should load successfully");
	}
	
	@Test
	void allBeansLoaded() {
		// Verify critical beans are loaded
		assertNotNull(applicationContext.getBean("movieService"));
		assertNotNull(applicationContext.getBean("userService"));
		assertNotNull(applicationContext.getBean("authenticationService"));
		assertNotNull(applicationContext.getBean("notificationService"));
		assertNotNull(applicationContext.getBean("jwtTokenProvider"));
		assertNotNull(applicationContext.getBean("securityConfig"));
	}
}
