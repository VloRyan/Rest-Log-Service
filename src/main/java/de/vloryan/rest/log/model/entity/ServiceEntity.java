package de.vloryan.rest.log.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.http.HttpMethod;
import de.vloryan.rest.log.model.enumeration.WebserviceType;
import lombok.Data;

@Data
@Entity
@Table(name = "Service")
public class ServiceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = true)
  private String url;

  @Enumerated(EnumType.STRING)
  private WebserviceType type;

  @Enumerated(EnumType.STRING)
  private HttpMethod method;
}
