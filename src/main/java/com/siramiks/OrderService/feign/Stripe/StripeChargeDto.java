package com.siramiks.OrderService.feign.Stripe;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class StripeChargeDto {
  private String username;
  private Double amount;
}
