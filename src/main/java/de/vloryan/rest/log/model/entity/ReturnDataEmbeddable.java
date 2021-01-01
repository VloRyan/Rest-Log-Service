package de.vloryan.rest.log.model.entity;

import javax.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ReturnDataEmbeddable {
  private String code;
  private String message;
}
