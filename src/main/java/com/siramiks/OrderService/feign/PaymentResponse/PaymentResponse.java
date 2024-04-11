package com.siramiks.OrderService.feign.PaymentResponse;

import com.siramiks.OrderService.model.OrderResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
  private UUID paymentId;
  private String paymentStatus;
}
