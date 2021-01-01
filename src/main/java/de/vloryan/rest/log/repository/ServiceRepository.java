package de.vloryan.rest.log.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import de.vloryan.rest.log.model.entity.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
  Optional<ServiceEntity> findByName(String name);
}
