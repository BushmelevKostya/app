package itmo.app.controller.services;

import itmo.app.model.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService {
	
	public void validateUserCredentials(User user) throws Exception {
		validateEmail(user.getEmail());
		validatePassword(user.getPassword());
	}
	
	private void validateEmail(String email) throws Exception {
		if (email == null || email.isEmpty()) {
			throw new Exception("Email cannot be null");
		}
		
		String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yandex\\.ru|mail\\.ru)$";
		if (!email.matches(emailPattern)) {
			throw new Exception("Invalid email format");
		}
	}
	
	private void validatePassword(String password) throws Exception {
		if (password == null || password.isEmpty()) {
			throw new Exception("Password cannot be null");
		}
		
		if (password.length() < 6 || password.contains(" ")) {
			throw new Exception("Password must be at least 6 characters long and cannot contain spaces");
		}
		
		String passwordPattern = "^[a-zA-Z0-9!@#\\$%\\^&\\*\\(\\)_\\+]+$";
		if (!password.matches(passwordPattern)) {
			throw new Exception("Invalid password format");
		}
	}
}
