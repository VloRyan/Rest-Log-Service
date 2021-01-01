package de.vloryan.rest.log.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import de.vloryan.rest.log.model.entity.WebServiceRequestEntity;

public interface RequestRepository extends JpaRepository<WebServiceRequestEntity, Long> {

  @Override
  @EntityGraph(attributePaths = {"parentRequest", "response", "service"})
  Optional<WebServiceRequestEntity> findById(Long id);

  @EntityGraph(attributePaths = {"response", "service"})
  List<WebServiceRequestEntity> findByParentRequest(WebServiceRequestEntity parentRequest);

  @EntityGraph(attributePaths = {"response", "service"})
  List<WebServiceRequestEntity> findByParentRequestIsNull();

}
