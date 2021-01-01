package de.vloryan.rest.log.model.webservice;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WebServiceResponse extends HttpMessage<WebServicePayload> {
}

