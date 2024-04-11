package com.siramiks.OrderService.feign.PaymentResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("PAYMENT-SERVICE/api/v1/payment")
public interface PaymentService {

  @PostMapping("/completeTransaction")
  ResponseEntity<PaymentResponse> completeTransaction(@RequestBody PaymentRequest paymentRequest);
}
