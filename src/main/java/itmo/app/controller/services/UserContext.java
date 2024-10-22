package itmo.app.controller.services;

import itmo.app.model.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class UserContext {
	private User loggedInUser;
	
	public User getLoggedInUser() {
		return loggedInUser;
	}
	
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
}
