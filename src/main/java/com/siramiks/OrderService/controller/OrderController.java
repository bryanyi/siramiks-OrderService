package com.siramiks.OrderService.controller;

import com.siramiks.OrderService.model.OrderRequest;
import com.siramiks.OrderService.model.OrderResponse;
import com.siramiks.OrderService.model.PaymentFailedResponse;
import com.siramiks.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@Log4j2
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/createOrder")
  public ResponseEntity<Object> createOrder(@RequestBody OrderRequest orderRequest) {
    OrderResponse orderResponse = orderService.createOrder(orderRequest);
    if (orderResponse == null) {
      PaymentFailedResponse paymentFailedResponse = PaymentFailedResponse.builder()
              .message("Payment failed!")
              .errorCode("PAYMENT_FAILED")
              .build();
      return new ResponseEntity<>(paymentFailedResponse, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(orderResponse, HttpStatus.OK);
  }
}
