package itmo.app.model.repository;

import itmo.app.model.entity.FailedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedRequestRepository extends JpaRepository<FailedRequest, Long> {

}
