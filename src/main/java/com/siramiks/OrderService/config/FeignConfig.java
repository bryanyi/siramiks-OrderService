package com.siramiks.OrderService.config;

import com.siramiks.OrderService.feign.decoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
  // Purposes:
  // 1. Make feign utilize the CustomErrorDecoder. Since it uses ErrorDecoder by default, we have to tell
  // feign like this to user our custom version.
  @Bean
  ErrorDecoder errorDecoder() {
    return new CustomErrorDecoder();
  }
}
