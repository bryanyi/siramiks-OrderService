package com.siramiks.OrderService.feign.Stripe;

import lombok.Data;

@Data
public class StripeTokenDto {
  private String cardNumber;
  private String expMonth;
  private String expYear;
  private String cvc;
  private String username;
  private String status;
}
