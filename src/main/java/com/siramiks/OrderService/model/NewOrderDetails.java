package com.siramiks.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewOrderDetails {
  private List<UUID> productIds;
  private double orderPrice;
  private long orderQuantity;
  private String paymentMethod;
}
