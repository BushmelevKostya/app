package itmo.app.service;

import itmo.app.dto.response.PageResponse;
import itmo.app.exception.ResourceNotFoundException;
import itmo.app.model.entity.User;
import itmo.app.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public User getCurrentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
	}
	
	@Transactional(readOnly = true)
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
	}
	
	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}
	
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ADMIN')")
	public PageResponse<User> getAllUsers(int page, int size) {
		List<User> allUsers = userRepository.findAll();
		long totalElements = allUsers.size();
		
		int start = page * size;
		int end = Math.min(start + size, allUsers.size());
		List<User> content = allUsers.subList(start, end);
		
		return new PageResponse<>(content, page, size, totalElements);
	}
	
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	@Transactional(readOnly = true)
	public boolean isEmailAvailable(String email) {
		return !userRepository.existsByEmail(email);
	}
}
