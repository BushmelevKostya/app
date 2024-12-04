package itmo.app.controller;

import itmo.app.model.entity.ImportHistory;
import itmo.app.model.entity.ImportStatus;
import itmo.app.model.entity.User;
import itmo.app.model.repository.ImportHistoryRepository;
import itmo.app.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/history")
@CrossOrigin(origins = "http://localhost:4200")
public class ImportHistoryController {
	@Autowired
	private ImportHistoryRepository importHistoryRepository;
	
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/all")
	public ResponseEntity<List<ImportHistory>> getHistoryForAdmin() {
		List<ImportHistory> list = importHistoryRepository.findAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping("/user/{email}")
	public ResponseEntity<List<ImportHistory>> getHistoryForUser(@PathVariable String email) {
		User user = userRepository.findByEmail(email).get();
		List<ImportHistory> list;
		if (user.isApprovedAdmin()) {
			list = importHistoryRepository.findAll();
		} else {
			Optional<List<ImportHistory>> optList = importHistoryRepository.findAllByUsername(email);
			list = optList.orElseGet(ArrayList::new);
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping("/create/{email}")
	public ResponseEntity<ImportHistory> createHistory(@PathVariable String email) {
		ImportHistory importHistory = new ImportHistory();
		importHistory.setUsername(email);
		importHistory.setStatus(ImportStatus.ERROR);
		return new ResponseEntity<>(importHistoryRepository.save(importHistory), HttpStatus.CREATED);
	}
}
