package de.vloryan.rest.log.model.webservice;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "Represents one service call with all associated sub calls.")
public class ServiceCallLog {

  private WebServiceMessage request;
  private WebServiceMessage response;

  @Schema(description = "Associated ServiceCallLog.")
  private List<ServiceCallLog> associatedCalls;

  public ServiceCallLog() {
    associatedCalls = new ArrayList<>();
  }
}
