package de.vloryan.rest.log.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RequestNotFoundException extends RuntimeException {
  @Getter
  private long requestId;
  private static final long serialVersionUID = 4440432930632746556L;

  public RequestNotFoundException(long requestId) {
    super("Request with id " + requestId + " not found.");
  }

}
