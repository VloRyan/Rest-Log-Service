package de.vloryan.rest.log.model.webservice;

import javax.validation.constraints.NotEmpty;
import org.springframework.http.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One webservice interface.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebService {

  @Schema(description = "Logical name of service.", example = "VendorService V3", required = true)
  @NotEmpty
  private String name;

  @Schema(description = "Url of the service interface.",
      example = "https://vendor.com/api/getInfo")
  private String url;

  @Schema(description = "HTTP method of the service interface.", example = "GET")
  private HttpMethod method;
}
