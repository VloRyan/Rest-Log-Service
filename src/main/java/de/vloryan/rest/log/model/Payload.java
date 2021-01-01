package de.vloryan.rest.log.model;

import javax.validation.constraints.NotEmpty;
import com.sun.istack.NotNull;
import de.vloryan.rest.log.model.enumeration.ContentEncoding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload of one service call.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payload {

  @Schema(description = "Type of payload, can be MIME Type.", example = "application/json")
  @NotEmpty
  private String type;

  @Schema(description = "Encoding of the content data.", example = "UTF8")
  @NotNull
  private ContentEncoding encoding;

  @Schema(description = "The content data.", example = "{\"id\": 4711}")
  private String content;

}
