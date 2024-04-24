package com.siramiks.OrderService.feign.PaymentResponse;

import com.siramiks.OrderService.exception.CustomException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@CircuitBreaker(name = "ext-service", fallbackMethod = "fallback")
@FeignClient("PAYMENT-SERVICE/api/v1/payment")
public interface PaymentService {

  @PostMapping("/completeTransaction")
  ResponseEntity<PaymentResponse> completeTransaction(@RequestBody PaymentRequest paymentRequest);

  default ResponseEntity<Void> fallback(Exception e) {
    throw new CustomException("payment service not available", "UNAVAILABLE", 500);
  }
}
