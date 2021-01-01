package de.vloryan.rest.log.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Response")
public class WebServiceResponseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ElementCollection
  @CollectionTable(name = "Response_Header", joinColumns = @JoinColumn(name = "Response_Id"))
  @Column(length = 1024, nullable = true)
  private List<String> header;

  @Embedded
  private PayloadEmbeddable payload;

  @Column(name = "timestamp", nullable = false, updatable = false, insertable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime timestamp;
}

