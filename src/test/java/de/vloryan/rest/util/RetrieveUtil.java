package de.vloryan.rest.util;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RetrieveUtil {


  public static <T> T retrieveFromResponse(final HttpResponse response,
      final Class<T> clazz) throws IOException {
    final String jsonFromResponse = EntityUtils.toString(response.getEntity());
    final ObjectMapper mapper = new ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.readValue(jsonFromResponse, clazz);
  }

  public static <T> List<T> retrieveListFromResponse(final HttpResponse response,
      final Class<T> clazz) throws IOException {
    final String jsonFromResponse = EntityUtils.toString(response.getEntity());
    final ObjectMapper mapper = new ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.readValue(jsonFromResponse, new TypeReference<List<T>>() {});
  }

}
