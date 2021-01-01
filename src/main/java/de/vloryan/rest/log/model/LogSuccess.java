package de.vloryan.rest.log.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogSuccess {

  @Schema(description = "Id of the successful logged message")
  private long id;
}
