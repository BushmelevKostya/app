package itmo.app.service;

import itmo.app.model.entity.ImportHistory;
import itmo.app.model.entity.ImportStatus;
import itmo.app.model.repository.ImportHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportService {
	
	@Autowired
	private ImportHistoryRepository importHistoryRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	public ImportHistory createImportHistory(int objectCount) {
		ImportHistory history = new ImportHistory();
		history.setCountObjects(objectCount);
		history.setStatus(ImportStatus.OK);
		history.setUsername(userService.getCurrentUser().getEmail());
		
		return importHistoryRepository.save(history);
	}
	
	@Transactional
	public void updateImportStatus(Long historyId, ImportStatus status) {
		ImportHistory history = importHistoryRepository.findById(historyId)
				.orElseThrow(() -> new RuntimeException("Import history not found"));
		
		history.setStatus(status);
		
		importHistoryRepository.save(history);
	}
	
	@Transactional(readOnly = true)
	public List<ImportHistory> getImportHistory() {
		String currentUserEmail = userService.getCurrentUser().getEmail();
		return importHistoryRepository.findAllByUsername(currentUserEmail).orElse(List.of());
	}
	
	@Transactional(readOnly = true)
	public List<ImportHistory> getAllImportHistory() {
		return importHistoryRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public ImportHistory getImportHistoryById(Long id) {
		return importHistoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Import history not found"));
	}
}
