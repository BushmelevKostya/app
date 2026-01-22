package itmo.app.controller;

import itmo.app.model.entity.ImportHistory;
import itmo.app.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/history")
public class ImportHistoryController {
	
	@Autowired
	private ImportService importService;
	
	@GetMapping
	public ResponseEntity<List<ImportHistory>> getHistory() {
		List<ImportHistory> history = importService.getImportHistory();
		return ResponseEntity.ok(history);
	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<ImportHistory>> getAllHistory() {
		List<ImportHistory> history = importService.getAllImportHistory();
		return ResponseEntity.ok(history);
	}
}
