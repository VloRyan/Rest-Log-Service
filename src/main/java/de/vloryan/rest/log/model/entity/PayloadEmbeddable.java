package de.vloryan.rest.log.model.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import de.vloryan.rest.log.model.enumeration.ContentEncoding;
import lombok.Data;

@Embeddable
@Data
public class PayloadEmbeddable {
  private String type;
  @Enumerated(EnumType.STRING)
  private ContentEncoding encoding;
  private String content;
}
