package de.vloryan.rest.log.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Request")
public class WebServiceRequestEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ElementCollection
  @CollectionTable(name = "Request_Header", joinColumns = @JoinColumn(name = "Request_Id"))
  @Column(length = 1024)
  private List<String> header;

  @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "REQUEST_ID", nullable = true, updatable = true)
  private WebServiceRequestEntity parentRequest;

  @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "RESPONSE_ID", nullable = true, updatable = true)
  private WebServiceResponseEntity response;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "SERVICE_ID", nullable = false, updatable = false)
  private ServiceEntity service;

  @Embedded
  private PayloadEmbeddable payload;

  @Column(name = "timestamp", nullable = false, updatable = false, insertable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime timestamp;
}
