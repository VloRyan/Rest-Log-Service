package de.vloryan.rest.log.service;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.vloryan.rest.log.model.entity.ServiceEntity;
import de.vloryan.rest.log.model.webservice.WebService;
import de.vloryan.rest.log.repository.ServiceRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class WebserviceService {
  private ServiceRepository serviceRepo;
  private final ModelMapper mapper;

  public WebService getService(String name) {
    Optional<ServiceEntity> optService = serviceRepo.findByName(name);
    if (optService.isEmpty()) {
      return null;
    }
    return mapper.map(optService.get(), WebService.class);
  }

  public Long getServiceId(String name) {
    Optional<ServiceEntity> optService = serviceRepo.findByName(name);
    if (optService.isEmpty()) {
      return null;
    }
    return optService.get().getId();
  }


  public long save(WebService service) {
    ServiceEntity serviceEntity = mapper.map(service, ServiceEntity.class);
    serviceRepo.save(serviceEntity);
    return serviceEntity.getId();
  }
}
