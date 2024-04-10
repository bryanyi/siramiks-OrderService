package com.siramiks.OrderService.service;

import com.siramiks.OrderService.entity.OrderRequest;
import com.siramiks.OrderService.model.OrderResponse;

public interface OrderServiceInterface {
  OrderResponse createOrder(OrderRequest orderRequest);
}
