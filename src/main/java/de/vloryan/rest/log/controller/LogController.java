package de.vloryan.rest.log.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import de.vloryan.rest.log.model.LogSuccess;
import de.vloryan.rest.log.model.exception.RequestNotFoundException;
import de.vloryan.rest.log.model.webservice.ServiceCallLog;
import de.vloryan.rest.log.model.webservice.WebServiceMessage;
import de.vloryan.rest.log.service.WebServiceLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/log")
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "webserice log")
public class LogController {

  private WebServiceLogService logService;

  @Operation(summary = "Log one request.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Request is logged and requestId will be returned.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = LogSuccess.class))}),
  })
  @PostMapping("request")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ResponseEntity<LogSuccess> logMiddlewareRequest(@RequestBody WebServiceMessage request) {
    long requestId = logService.saveRequest(request);
    LogSuccess logResponse = new LogSuccess(requestId);
    return ResponseEntity.status(HttpStatus.OK)
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(logResponse);
  }

  @Operation(
      summary = "Update the request and associate the message(another request or response).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Message is logged and messageId will be returned.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = LogSuccess.class))}),
      @ApiResponse(responseCode = "404",
          description = "Request can not be found.")
  })
  @PatchMapping("request/{requestId}")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ResponseEntity<LogSuccess> logServiceMessage(@PathVariable(
      name = "requestId") long requestId, @RequestBody WebServiceMessage message) {
    long responseId = logService.saveMessage(requestId, message);
    LogSuccess logResponse = new LogSuccess(responseId);
    return ResponseEntity.status(HttpStatus.OK)
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(logResponse);
  }

  @Operation(
      summary = "Get one root request and all associated requests or responses.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Message is logged and messageId will be returned.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = LogSuccess.class))}),
      @ApiResponse(responseCode = "404",
          description = "Request can not be found.")
  })
  @GetMapping("request/{requestId}")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ResponseEntity<ServiceCallLog> readRequest(@PathVariable(
      name = "requestId") long requestId) {
    ServiceCallLog serviceCall = logService.getServiceCallLog(requestId);
    return ResponseEntity.status(HttpStatus.OK)
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(serviceCall);
  }

  @Operation(
      summary = "List all root requests.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Message is logged and messageId will be returned.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = LogSuccess.class))}),
      @ApiResponse(responseCode = "404",
          description = "Request can not be found.")
  })
  @GetMapping("request")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ResponseEntity<List<WebServiceMessage>> readAllRequest() {
    List<WebServiceMessage> requestList = logService.getRequests();
    return ResponseEntity.status(HttpStatus.OK)
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(requestList);
  }

  @ExceptionHandler(RequestNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handleLogRequestNotFound(RuntimeException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }
}
