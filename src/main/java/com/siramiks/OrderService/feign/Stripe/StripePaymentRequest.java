package com.siramiks.OrderService.feign.Stripe;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StripePaymentRequest {
  private double amount;
  private String paymentMethodId;
}
