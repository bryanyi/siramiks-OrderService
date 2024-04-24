package com.siramiks.OrderService.feign.ProductResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "PRODUCT-SERVICE/api/v1/product")
public interface ProductService {

  @PutMapping("/decreaseQuantity/{productId}")
  ResponseEntity<Long> decreaseQuantity(@PathVariable("productId") UUID productId, @RequestParam long qtyToDecrease);

  @GetMapping("/checkStock/{productId}")
  ResponseEntity<Boolean> hasEnoughStock(@PathVariable("productId") UUID productId, @RequestParam long qtyToDecrease);
}
