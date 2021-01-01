package de.vloryan.rest.log.service;

import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.vloryan.rest.log.model.entity.ServiceEntity;
import de.vloryan.rest.log.model.entity.WebServiceRequestEntity;
import de.vloryan.rest.log.model.entity.WebServiceResponseEntity;
import de.vloryan.rest.log.model.exception.RequestNotFoundException;
import de.vloryan.rest.log.model.webservice.ServiceCallLog;
import de.vloryan.rest.log.model.webservice.WebServiceMessage;
import de.vloryan.rest.log.repository.RequestRepository;
import de.vloryan.rest.log.repository.ResponseRepository;
import lombok.AllArgsConstructor;

/**
 * Service for logging webservice request/response.
 */
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class WebServiceLogService {

  private final WebserviceService webserviceService;
  private final RequestRepository requestRepo;
  private final ResponseRepository responseRepo;
  private final ModelMapper mapper;

  public long saveRequest(WebServiceMessage request) {
    return saveRequest(request, Optional.empty());
  }

  public long saveMessage(long requestId, WebServiceMessage message) {
    Optional<WebServiceRequestEntity> requestOpt = requestRepo.findById(requestId);
    if (requestOpt.isEmpty()) {
      throw new RequestNotFoundException(requestId);
    }
    boolean isResponse = message.getTarget() == null;
    if (isResponse) {
      return saveResponse(message, requestOpt.get());
    }
    return saveRequest(message, requestOpt);
  }

  public ServiceCallLog getServiceCallLog(long requestId) {
    Optional<WebServiceRequestEntity> requestOpt = requestRepo.findById(requestId);
    if (requestOpt.isEmpty()) {
      throw new RequestNotFoundException(requestId);
    }

    WebServiceRequestEntity request = requestOpt.get();
    ServiceCallLog serviceCall = mapServiceCall(request);

    boolean isRoot = request.getParentRequest() == null;
    if (isRoot) {
      List<WebServiceRequestEntity> nestedRequestList =
          requestRepo.findByParentRequest(request);
      for (WebServiceRequestEntity nestedRequest : nestedRequestList) {
        serviceCall.getAssociatedCalls()
                   .add(mapServiceCall(nestedRequest));
      }
    }
    return serviceCall;
  }

  private long saveRequest(WebServiceMessage message, Optional<WebServiceRequestEntity> request) {
    WebServiceRequestEntity entity = mapper.map(message, WebServiceRequestEntity.class);
    request.ifPresent(parent -> entity.setParentRequest(parent));
    mapWebservice(message, entity.getService());// FIXME: Dirty
    requestRepo.save(entity);
    return entity.getId();
  }

  private long saveResponse(WebServiceMessage message, WebServiceRequestEntity request) {
    WebServiceResponseEntity entity = mapper.map(message, WebServiceResponseEntity.class);
    responseRepo.save(entity);

    updateRequest(request, entity);

    return entity.getId();
  }

  private void updateRequest(WebServiceRequestEntity request, WebServiceResponseEntity response) {
    request.setResponse(response);
    requestRepo.save(request);
  }

  private void mapWebservice(WebServiceMessage message, ServiceEntity entity) {
    Long serviceId = webserviceService.getServiceId(entity.getName());
    if (serviceId == null) {
      // Possible to throw exception here if not wanted....
      serviceId = webserviceService.save(message.getTarget());
    }
    entity.setId(serviceId);
  }


  private ServiceCallLog mapServiceCall(WebServiceRequestEntity request) {
    ServiceCallLog serviceCall = new ServiceCallLog();
    serviceCall.setRequest(mapper.map(request, WebServiceMessage.class));
    if (request.getResponse() != null) {
      serviceCall.setResponse(mapper.map(request.getResponse(), WebServiceMessage.class));
    }
    return serviceCall;
  }

  public List<WebServiceMessage> getRequests() {
    List<WebServiceRequestEntity> entityList = requestRepo.findByParentRequestIsNull();
    return mapper.map(entityList, new TypeToken<List<WebServiceMessage>>() {}.getType());
  }
}
