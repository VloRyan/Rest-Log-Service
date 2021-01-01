package de.vloryan.rest;

import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import de.vloryan.rest.log.model.entity.WebServiceRequestEntity;
import de.vloryan.rest.log.model.webservice.WebServiceMessage;

@SpringBootApplication
public class RestLogServiceMain {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(RestLogServiceMain.class);
    app.setBannerMode(Mode.OFF);
    app.run(args);
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.typeMap(WebServiceMessage.class, WebServiceRequestEntity.class)
               .addMapping(WebServiceMessage::getTarget, WebServiceRequestEntity::setService);
    modelMapper.typeMap(WebServiceRequestEntity.class, WebServiceMessage.class)
               .addMapping(WebServiceRequestEntity::getService, WebServiceMessage::setTarget);
    return modelMapper;
  }

}
