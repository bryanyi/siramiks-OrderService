package com.siramiks.OrderService.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentFailedResponse {
  private String message;
  private String errorCode;
}
