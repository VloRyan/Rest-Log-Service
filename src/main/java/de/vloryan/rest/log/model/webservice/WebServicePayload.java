package de.vloryan.rest.log.model.webservice;

import org.springframework.util.MimeType;
import de.vloryan.rest.log.model.Payload;
import de.vloryan.rest.log.model.enumeration.ContentEncoding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(description = "The body of one WebserviceMessage")
public class WebServicePayload extends Payload {

  public WebServicePayload(MimeType type, String content) {
    super(type.toString(), ContentEncoding.UTF8, content);
  }

}
