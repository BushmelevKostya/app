package itmo.app.controller.services;

import itmo.app.model.repository.CoordinatesRepository;
import itmo.app.model.repository.LocationRepository;
import itmo.app.model.repository.PersonRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrphanCleaner {
	private final Logger logger;
	private final PersonRepository personRepository;
	
	private final CoordinatesRepository coordinatesRepository;
	
	private final LocationRepository locationRepository;
	
	@Autowired
	public OrphanCleaner(PersonRepository personRepository, CoordinatesRepository coordinatesRepository, LocationRepository locationRepository) {
		this.personRepository = personRepository;
		this.coordinatesRepository = coordinatesRepository;
		this.locationRepository = locationRepository;
		this.logger = GlobalLogger.getLogger();
	}
	
	@Scheduled(fixedRate = 300000) // 5 minutes
	@Transactional
	public void cleanOrphanPersons() {
		int pCount = personRepository.deleteOrphanPersons();
		int cCount = coordinatesRepository.deleteOrphanCoordinates();
		int lCount = locationRepository.deleteOrphanLocations();
		logger.info("The scheduled cleaning orphan objects was delete {} unused records", pCount + cCount + lCount);
	}
}

