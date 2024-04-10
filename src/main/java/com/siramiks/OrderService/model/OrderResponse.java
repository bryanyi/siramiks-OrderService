package com.siramiks.OrderService.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
  private UUID orderId;
  private List<UUID> productIds;
  private long orderPrice;
  private long orderQuantity;
  private String paymentMethod;
}
