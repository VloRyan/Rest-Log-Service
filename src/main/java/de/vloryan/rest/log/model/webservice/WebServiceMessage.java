package de.vloryan.rest.log.model.webservice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request/Response of one webservice.
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "One http message (request/response)")
public class WebServiceMessage extends HttpMessage<WebServicePayload> {

  @Schema(description = "Target of the request. Shall be empty for response.", nullable = true)
  private WebService target;
}

