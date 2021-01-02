package de.vloryan.rest.log.model.webservice;

import java.util.ArrayList;
import java.util.List;
import com.sun.istack.NotNull;
import de.vloryan.rest.log.model.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HttpMessage<PAYLOAD_TYPE extends Payload> {

  @NotNull
  @Schema(description = "Array of http header lines.", example = "[\"Content-Type: text/html\"]",
      required = true)
  private List<String> header;

  private PAYLOAD_TYPE payload;

  public HttpMessage() {
    header = new ArrayList<String>();
  }
}

