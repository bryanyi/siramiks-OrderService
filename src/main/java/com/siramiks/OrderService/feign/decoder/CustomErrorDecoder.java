package com.siramiks.OrderService.feign.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siramiks.OrderService.exception.CustomException;
import com.siramiks.OrderService.feign.ProductResponse.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {
  @Override
  public Exception decode(String s, Response response) {
    ObjectMapper objectMapper = new ObjectMapper();

    log.info("Error decoder request url: {}", response.request().url());
    log.info("Error decoder request headers: {}", response.request().headers());

    try {
      ErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
      return new CustomException(errorResponse.getErrorMessage(), errorResponse.getErrorCode(), response.status());
    } catch (IOException e) {
      throw new CustomException("Internal server error!", "INTERNAL_SERVER_ERROR", 500);
    }
  }
}
