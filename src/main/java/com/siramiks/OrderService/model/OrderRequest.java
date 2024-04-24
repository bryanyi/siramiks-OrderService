package com.siramiks.OrderService.model;

import com.siramiks.OrderService.entity.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
  private NewOrderDetails newOrderDetails;
  private CardInfo cardInfo;
}
