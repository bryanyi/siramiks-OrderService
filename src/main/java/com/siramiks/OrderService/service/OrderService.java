package com.siramiks.OrderService.service;

import com.siramiks.OrderService.entity.Order;
import com.siramiks.OrderService.entity.OrderRequest;
import com.siramiks.OrderService.feign.ProductService;
import com.siramiks.OrderService.model.OrderResponse;
import com.siramiks.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class OrderService implements OrderServiceInterface {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private ProductService productService;

  public OrderResponse createOrder(OrderRequest orderRequest) {
    // REMEMBER - when testing this, do NOT use the id key from DB - use product_id!
    log.info("Creating order...");

    Order order = Order.builder()
            .product_id(orderRequest.getProductIds())
            .orderPrice(orderRequest.getOrderPrice())
            .orderQuantity((orderRequest.getOrderQuantity()))
            .paymentMethod(orderRequest.getPaymentMethod())
            .build();

    // When we save to DB, the return will provide a full order object with the UUID's filled out
    order = orderRepository.save(order);
    log.info("Order processed successfully!!");

    // check stock from product service
    int productCount = order.getProduct_id().size();
    for (int i = 0; i < productCount; i++) {
      UUID productId = order.getProduct_id().get(i);
      log.info("product id to decrease: {}", productId);

      // Just doing 1 for now - in the future, we'd have to recreate a response object
      // where we can have diff quantities for each product
      productService.decreaseQuantity(productId, order.getOrderQuantity());
      log.info("product quantity successfully reduced for order id of {}, for product id of {}", order.getOrderId(), productId);
    }

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
