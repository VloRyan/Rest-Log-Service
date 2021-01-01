package de.vloryan.rest.log.service;

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import de.vloryan.rest.log.model.entity.WebServiceRequestEntity;
import de.vloryan.rest.log.model.entity.WebServiceResponseEntity;
import de.vloryan.rest.log.model.exception.RequestNotFoundException;
import de.vloryan.rest.log.model.webservice.ServiceCallLog;
import de.vloryan.rest.log.model.webservice.WebService;
import de.vloryan.rest.log.model.webservice.WebServiceMessage;
import de.vloryan.rest.log.repository.RequestRepository;
import de.vloryan.rest.log.repository.ResponseRepository;

@ExtendWith(SpringExtension.class)
public class WebServiceLogServiceTest {


  @MockBean
  private WebserviceService webserviceService;
  @MockBean
  private RequestRepository requestRepo;
  @MockBean
  private ResponseRepository responseRepo;

  @Autowired
  private ModelMapper mapper;

  private WebServiceLogService service;

  @TestConfiguration
  static class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
      ModelMapper modelMapper = new ModelMapper();
      modelMapper.typeMap(WebServiceMessage.class, WebServiceRequestEntity.class)
                 .addMapping(WebServiceMessage::getTarget, WebServiceRequestEntity::setService);
      modelMapper.typeMap(WebServiceRequestEntity.class, WebServiceMessage.class)
                 .addMapping(WebServiceRequestEntity::getService, WebServiceMessage::setTarget);
      return modelMapper;
    }
  }

  @BeforeEach
  public void setup() {
    service = new WebServiceLogService(webserviceService, requestRepo, responseRepo, mapper);

    // set id like real repo
    Mockito.when(requestRepo.save(Mockito.any()))
           .then(invocation -> {
             ((WebServiceRequestEntity) invocation.getArgument(0)).setId(Long.valueOf(1));
             return invocation.getArgument(0);
           });

    // set id like real repo
    Mockito.when(responseRepo.save(Mockito.any()))
           .then(invocation -> {
             ((WebServiceResponseEntity) invocation.getArgument(0)).setId(Long.valueOf(1));
             return invocation.getArgument(0);
           });
  }


  @Test
  public void saveRequest_validRequest_returnSavedId() {
    WebServiceMessage request = new WebServiceMessage();
    request.setTarget(new WebService("TestService", "/test", HttpMethod.GET));

    long result = service.saveRequest(request);

    Assertions.assertTrue(result > 0);
    Mockito.verify(requestRepo).save(Mockito.any());
  }

  @Test
  public void saveRequest_requestIsNull_throwException() {
    Assertions.assertThrows(Exception.class, () -> {
      service.saveRequest(null);
    });
  }

  @Test
  public void saveMessage_validRequest_returnSavedRequestId() {
    WebServiceMessage request = new WebServiceMessage();
    request.setTarget(new WebService("TestService", "/test", HttpMethod.GET));

    Long dbId = Long.valueOf(4711);
    WebServiceRequestEntity dbEntity = new WebServiceRequestEntity();
    dbEntity.setId(dbId);

    Mockito.doReturn(Optional.of(dbEntity))
           .when(requestRepo)
           .findById(Mockito.anyLong());

    long result = service.saveMessage(dbId, request);

    Assertions.assertTrue(result > 0);
    Mockito.verify(requestRepo).save(Mockito.any());
  }

  @Test
  public void saveMessage_validResponse_returnSavedResponseId() {
    WebServiceMessage response = new WebServiceMessage();

    Long dbId = Long.valueOf(4711);
    WebServiceRequestEntity dbEntity = new WebServiceRequestEntity();
    dbEntity.setId(dbId);

    Mockito.doReturn(Optional.of(dbEntity))
           .when(requestRepo)
           .findById(Mockito.anyLong());

    long result = service.saveMessage(dbId, response);

    Assertions.assertTrue(result > 0);
    Mockito.verify(responseRepo).save(Mockito.any());
  }

  @Test
  public void saveMessage_unknownRequest_throwsRequestNotFoundException() {
    WebServiceMessage response = new WebServiceMessage();

    Long unknownId = Long.valueOf(40211);

    Assertions.assertThrows(RequestNotFoundException.class, () -> {
      service.saveMessage(unknownId, response);
    });
  }

  @Test
  public void getServiceCallLog_validId_returnsCallLog() {
    Long dbId = Long.valueOf(4711);
    WebServiceRequestEntity dbEntity = new WebServiceRequestEntity();
    dbEntity.setId(dbId);

    Mockito.doReturn(Optional.of(dbEntity))
           .when(requestRepo)
           .findById(Mockito.anyLong());

    ServiceCallLog log = service.getServiceCallLog(dbId);

    Assertions.assertNotNull(log);
    Assertions.assertNotNull(log.getRequest());
    Assertions.assertNull(log.getResponse());
    Assertions.assertNotNull(log.getAssociatedCalls());
    Assertions.assertTrue(log.getAssociatedCalls().isEmpty());
  }

  @Test
  public void getServiceCallLog_unknownRequest_throwsRequestNotFoundException() {
    Long unknownId = Long.valueOf(40211);

    Assertions.assertThrows(RequestNotFoundException.class, () -> {
      service.getServiceCallLog(unknownId);
    });
  }

  @Test
  public void getServiceCallLog_validId_returnsCallLogWithAssociates() {
    Long dbId = Long.valueOf(4711);
    WebServiceRequestEntity dbEntity = new WebServiceRequestEntity();
    dbEntity.setId(dbId);
    WebServiceRequestEntity assotiatedDbEntity = new WebServiceRequestEntity();
    dbEntity.setId(Long.valueOf(4712));


    Mockito.doReturn(Optional.of(dbEntity))
           .when(requestRepo)
           .findById(Mockito.anyLong());

    Mockito.doReturn(Collections.singletonList(assotiatedDbEntity))
           .when(requestRepo)
           .findByParentRequest(Mockito.any());

    ServiceCallLog log = service.getServiceCallLog(dbId);

    Assertions.assertNotNull(log);
    Assertions.assertNotNull(log.getRequest());
    Assertions.assertNotNull(log.getAssociatedCalls());
    Assertions.assertTrue(log.getAssociatedCalls().size() == 1);
    Assertions.assertNull(log.getResponse());
  }


}

