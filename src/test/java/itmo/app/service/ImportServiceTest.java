package itmo.app.service;

import itmo.app.model.entity.ImportHistory;
import itmo.app.model.entity.ImportStatus;
import itmo.app.model.entity.User;
import itmo.app.model.repository.ImportHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {
	
	@Mock
	private ImportHistoryRepository importHistoryRepository;
	
	@Mock
	private UserService userService;
	
	@InjectMocks
	private ImportService importService;
	
	private User testUser;
	private ImportHistory testHistory;
	
	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("test@example.com");
		
		testHistory = new ImportHistory();
		testHistory.setId(1L);
		testHistory.setCountObjects(10);
		testHistory.setStatus(ImportStatus.OK);
		testHistory.setUsername("test@example.com");
	}
	
	@Test
	void createImportHistory_ShouldCreateHistory() {
		// Arrange
		when(userService.getCurrentUser()).thenReturn(testUser);
		when(importHistoryRepository.save(any(ImportHistory.class))).thenReturn(testHistory);
		
		// Act
		ImportHistory result = importService.createImportHistory(10);
		
		// Assert
		assertNotNull(result);
		assertEquals(10, result.getCountObjects());
		assertEquals(ImportStatus.OK, result.getStatus());
		assertEquals("test@example.com", result.getUsername());
		verify(importHistoryRepository, times(1)).save(any(ImportHistory.class));
	}
	
	@Test
	void updateImportStatus_ShouldUpdateStatus_WhenHistoryExists() {
		// Arrange
		when(importHistoryRepository.findById(1L)).thenReturn(Optional.of(testHistory));
		when(importHistoryRepository.save(any(ImportHistory.class))).thenReturn(testHistory);
		
		// Act
		importService.updateImportStatus(1L, ImportStatus.ERROR);
		
		// Assert
		assertEquals(ImportStatus.ERROR, testHistory.getStatus());
		verify(importHistoryRepository, times(1)).save(testHistory);
	}
	
	@Test
	void updateImportStatus_ShouldThrowException_WhenHistoryNotFound() {
		// Arrange
		when(importHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(RuntimeException.class, 
				() -> importService.updateImportStatus(999L, ImportStatus.ERROR));
	}
	
	@Test
	void getImportHistory_ShouldReturnCurrentUserHistory() {
		// Arrange
		List<ImportHistory> histories = Arrays.asList(testHistory);
		when(userService.getCurrentUser()).thenReturn(testUser);
		when(importHistoryRepository.findAllByUsername("test@example.com"))
				.thenReturn(Optional.of(histories));
		
		// Act
		List<ImportHistory> result = importService.getImportHistory();
		
		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("test@example.com", result.get(0).getUsername());
		verify(importHistoryRepository, times(1)).findAllByUsername("test@example.com");
	}
	
	@Test
	void getImportHistory_ShouldReturnEmptyList_WhenNoHistory() {
		// Arrange
		when(userService.getCurrentUser()).thenReturn(testUser);
		when(importHistoryRepository.findAllByUsername("test@example.com"))
				.thenReturn(Optional.empty());
		
		// Act
		List<ImportHistory> result = importService.getImportHistory();
		
		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void getAllImportHistory_ShouldReturnAllHistory() {
		// Arrange
		ImportHistory history2 = new ImportHistory();
		history2.setId(2L);
		history2.setUsername("other@example.com");
		
		List<ImportHistory> allHistories = Arrays.asList(testHistory, history2);
		when(importHistoryRepository.findAll()).thenReturn(allHistories);
		
		// Act
		List<ImportHistory> result = importService.getAllImportHistory();
		
		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(importHistoryRepository, times(1)).findAll();
	}
	
	@Test
	void getImportHistoryById_ShouldReturnHistory_WhenExists() {
		// Arrange
		when(importHistoryRepository.findById(1L)).thenReturn(Optional.of(testHistory));
		
		// Act
		ImportHistory result = importService.getImportHistoryById(1L);
		
		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		verify(importHistoryRepository, times(1)).findById(1L);
	}
	
	@Test
	void getImportHistoryById_ShouldThrowException_WhenNotFound() {
		// Arrange
		when(importHistoryRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(RuntimeException.class, 
				() -> importService.getImportHistoryById(999L));
	}
}
