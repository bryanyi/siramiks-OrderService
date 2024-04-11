package com.siramiks.OrderService.feign.PaymentResponse;

import com.siramiks.OrderService.entity.CardInfo;
import com.siramiks.OrderService.feign.Stripe.StripePaymentRequest;
import com.siramiks.OrderService.model.NewOrderDetails;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentRequest {
  private UUID orderId;
  private StripePaymentRequest stripePaymentRequest;
  private NewOrderDetails newOrderDetails;
  private CardInfo cardInfo;
}
