package de.vloryan.rest.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.vloryan.rest.log.model.entity.WebServiceResponseEntity;

public interface ResponseRepository extends JpaRepository<WebServiceResponseEntity, Long> {

}
