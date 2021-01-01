package de.vloryan.rest.log.controller;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.IsNaN.notANumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MimeTypeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.vloryan.rest.log.model.exception.RequestNotFoundException;
import de.vloryan.rest.log.model.webservice.WebService;
import de.vloryan.rest.log.model.webservice.WebServiceMessage;
import de.vloryan.rest.log.model.webservice.WebServicePayload;
import de.vloryan.rest.log.service.WebServiceLogService;


@ExtendWith(SpringExtension.class)
@WebMvcTest(LogController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LogControllerIT {

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private WebServiceLogService service;

  @Autowired
  private MockMvc mockMvc;

  private JacksonTester<WebServiceMessage> json;

  @BeforeEach
  public void setup() {
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  public void givenValidRequest_whenLogRequest_then200AndIdIsReceived() throws Exception {
    WebServiceMessage serviceRequest = new WebServiceMessage();
    serviceRequest.setTarget(new WebService("TestMWService", "test/this", HttpMethod.GET));
    serviceRequest.setPayload(new WebServicePayload(MimeTypeUtils.APPLICATION_JSON,
        "{\"test\": true}"));

    JsonContent<WebServiceMessage> serviceRequestJSON = json.write(serviceRequest);
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/log/request")
                                                                         .content(serviceRequestJSON
                                                                                                    .getJson())
                                                                         .contentType(
                                                                             MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder)
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("id", not(notANumber())));
  }

  @Test
  public void givenUnknownRequestId_whenLogResponse_then404IsReceived()
      throws Exception {
    long invalidRequestId = 40211;


    Mockito.doThrow(RequestNotFoundException.class)
           .when(service)
           .saveMessage(invalidRequestId, Mockito.any());


    WebServiceMessage serviceResponse = new WebServiceMessage();
    serviceResponse.setPayload(new WebServicePayload(MimeTypeUtils.APPLICATION_JSON,
        "{\"test\": true}"));

    JsonContent<WebServiceMessage> serviceResponseJSON = json.write(serviceResponse);
    MockHttpServletRequestBuilder requestBuilder;
    requestBuilder = MockMvcRequestBuilders.patch("/api/log/request/" + invalidRequestId)
                                           .content(serviceResponseJSON.getJson())
                                           .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder)
           .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void givenValidRequestAndResponse_whenLogResponse_then200AndIdIsReceived()
      throws Exception {
    long validRequestId = 4711;

    WebServiceMessage serviceResponse = new WebServiceMessage();
    serviceResponse.setPayload(new WebServicePayload(MimeTypeUtils.APPLICATION_JSON,
        "{\"test\": true}"));

    JsonContent<WebServiceMessage> serviceResponseJSON = json.write(serviceResponse);

    MockHttpServletRequestBuilder requestBuilder;
    requestBuilder = MockMvcRequestBuilders.patch("/api/log/request/" + validRequestId)
                                           .content(serviceResponseJSON.getJson())
                                           .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder)
           .andExpect(MockMvcResultMatchers.status().isOk())
           .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(MockMvcResultMatchers.jsonPath("id", not(notANumber())));
  }
}
