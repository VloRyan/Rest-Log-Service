package de.vloryan.rest.log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeTypeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vloryan.rest.log.model.LogSuccess;
import de.vloryan.rest.log.model.webservice.ServiceCallLog;
import de.vloryan.rest.log.model.webservice.WebService;
import de.vloryan.rest.log.model.webservice.WebServiceMessage;
import de.vloryan.rest.log.model.webservice.WebServicePayload;
import de.vloryan.rest.util.RetrieveUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class RestLogServiceIT {

  @LocalServerPort
  private int port;

  @Value("${server.servlet.context-path}")
  String contextPath;

  @Autowired
  private ObjectMapper objectMapper;

  private String baseUrl;

  private static WebServiceMessage SERVICE_REQUEST;
  private static long REQUEST_ID;
  private static WebServiceMessage SERVICE_RESPONSE;

  @BeforeEach
  public void setup() {
    baseUrl = "http://localhost:" + port + contextPath + "/api/log/";
  }

  @Test
  @Order(1)
  public void givenEmptyDatabase_whenAllRequestAreRequested_then200AndEmptyListIsReceived()
      throws ClientProtocolException, IOException {

    HttpUriRequest request = new HttpGet(baseUrl + "request");
    HttpResponse httpResponse = HttpClientBuilder
                                                 .create()
                                                 .build()
                                                 .execute(request);

    List<WebServiceMessage> resource = RetrieveUtil.retrieveListFromResponse(httpResponse,
        WebServiceMessage.class);
    Assertions.assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
    Assertions.assertTrue(resource.isEmpty());
  }

  @Test
  @Order(2)
  public void givenEmptyDatabase_whenValidRequestIsSend_then200AndIdIsReceived()
      throws ClientProtocolException, IOException {

    // Given
    SERVICE_REQUEST = new WebServiceMessage();
    SERVICE_REQUEST.setHeader(Collections.singletonList("Content-Type: text/html"));
    SERVICE_REQUEST.setTarget(new WebService("TestMWService", "test/this", HttpMethod.GET));
    SERVICE_REQUEST.setPayload(new WebServicePayload(MimeTypeUtils.APPLICATION_JSON,
        "{\"test\": true}"));
    HttpPost request = new HttpPost(baseUrl + "request");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(SERVICE_REQUEST),
        ContentType.APPLICATION_JSON));


    // When
    HttpResponse httpResponse = HttpClientBuilder
                                                 .create()
                                                 .build()
                                                 .execute(request);

    // Then
    LogSuccess resource = RetrieveUtil.retrieveFromResponse(httpResponse, LogSuccess.class);
    Assertions.assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
    Assertions.assertTrue(resource.getId() > 0);

    REQUEST_ID = resource.getId();
  }

  @Test
  @Order(3)
  public void givenOneRequestLogged_whenValidResponseIsSend_then200AndIdIsReceived()
      throws ClientProtocolException, IOException {

    // Response (no Target)
    SERVICE_RESPONSE = new WebServiceMessage();
    SERVICE_RESPONSE.setHeader(Collections.singletonList("Content-Type: text/html"));
    SERVICE_RESPONSE.setPayload(new WebServicePayload(MimeTypeUtils.APPLICATION_JSON,
        "{\"test\": true}"));
    HttpPatch response = new HttpPatch(baseUrl + "request/" + REQUEST_ID);
    response.setEntity(new StringEntity(objectMapper.writeValueAsString(SERVICE_RESPONSE),
        ContentType.APPLICATION_JSON));

    HttpResponse httpResponse = HttpClientBuilder
                                                 .create()
                                                 .build()
                                                 .execute(response);

    // Then
    LogSuccess resource = RetrieveUtil.retrieveFromResponse(httpResponse, LogSuccess.class);
    Assertions.assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
    Assertions.assertTrue(resource.getId() > 0);

  }

  @Test
  @Order(4)
  public void givenRequestAndResponseAreLogged_whenServiceCallLogIsRequested_thenServiceCallLogIsReceived()
      throws ClientProtocolException, IOException {
    // Read Log

    HttpUriRequest requestLog = new HttpGet(baseUrl + "request/" + REQUEST_ID);
    HttpResponse httpResponse = HttpClientBuilder
                                                 .create()
                                                 .build()
                                                 .execute(requestLog);

    ServiceCallLog log = RetrieveUtil.retrieveFromResponse(httpResponse,
        ServiceCallLog.class);
    Assertions.assertEquals(HttpStatus.OK.value(), httpResponse.getStatusLine().getStatusCode());
    Assertions.assertEquals(SERVICE_REQUEST, log.getRequest());
    Assertions.assertEquals(SERVICE_RESPONSE, log.getResponse());

  }
}
