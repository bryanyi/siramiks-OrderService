package com.siramiks.OrderService.service;

import com.siramiks.OrderService.entity.Order;
import com.siramiks.OrderService.entity.OrderRequest;
import com.siramiks.OrderService.model.OrderResponse;
import com.siramiks.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class OrderService implements OrderServiceInterface {

  @Autowired
  private OrderRepository orderRepository;

  public OrderResponse createOrder(OrderRequest orderRequest) {
    log.info("Creating order...");

    Order order = Order.builder()
            .product_id(orderRequest.getProductIds())
            .orderPrice(orderRequest.getOrderPrice())
            .orderQuantity((orderRequest.getOrderQuantity()))
            .paymentMethod(orderRequest.getPaymentMethod())
            .build();

    orderRepository.save(order);
    log.info("Order processed successfully!!");

    // check stock from product service
    // process payment here

    OrderResponse orderResponse = OrderResponse.builder()
            .orderId(order.getOrderId())
            .productIds(order.getProduct_id())
            .orderPrice(order.getOrderPrice())
            .orderQuantity(order.getOrderQuantity())
            .paymentMethod(order.getPaymentMethod())
            .build();

    return orderResponse;

  }

}
