package com.siramiks.OrderService.service;

import com.siramiks.OrderService.entity.Order;
import com.siramiks.OrderService.entity.CardInfo;
import com.siramiks.OrderService.exception.CustomException;
import com.siramiks.OrderService.feign.PaymentResponse.PaymentResponse;
import com.siramiks.OrderService.feign.Stripe.StripePaymentRequest;
import com.siramiks.OrderService.model.NewOrderDetails;
import com.siramiks.OrderService.model.OrderRequest;
import com.siramiks.OrderService.feign.PaymentResponse.PaymentRequest;
import com.siramiks.OrderService.feign.PaymentResponse.PaymentService;
import com.siramiks.OrderService.feign.ProductResponse.ProductService;
import com.siramiks.OrderService.model.OrderResponse;
import com.siramiks.OrderService.repository.CardRepository;
import com.siramiks.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private CardRepository cardRepository;
  @Autowired
  private ProductService productService;
  @Autowired
  private PaymentService paymentService;

  public OrderResponse getOrderDetails(String orderId) {
    Order order = orderRepository.findByOrderId(UUID.fromString(orderId))
            .orElseThrow(() -> new CustomException("Order not found", "NO_FOUND", 404));

    OrderResponse orderResponse = OrderResponse.builder()
            .orderId(order.getOrderId())
            .productIds(order.getProduct_id())
            .orderPrice(order.getOrderPrice())
            .orderQuantity(order.getOrderQuantity())
            .paymentMethod(order.getPaymentMethod())
            .build();

    return orderResponse;
  }

  public OrderResponse createOrder(OrderRequest orderRequest) {
    // REMEMBER - when testing this, do NOT use the id key from DB - use product_id!
    log.info("Creating order...");
    NewOrderDetails orderDetails = orderRequest.getNewOrderDetails();
    CardInfo cardInfo = orderRequest.getCardInfo();

    // Save card in DB
    CardInfo paymentCard = CardInfo.builder()
            .cardNumber(cardInfo.getCardNumber())
            .expMonth(cardInfo.getExpMonth())
            .expYear(cardInfo.getExpYear())
            .cvc(cardInfo.getCvc())
            .build();
    CardInfo updatedCardInfo = cardRepository.save(paymentCard);

    log.info("UPDATED CARD INFO: {}", updatedCardInfo);

    // Save order in DB
    Order order = Order.builder()
            .product_id(orderDetails.getProductIds())
            .orderPrice(orderDetails.getOrderPrice())
            .orderQuantity((orderDetails.getOrderQuantity()))
            .paymentMethod(orderDetails.getPaymentMethod())
            .paymentStatus("PROCESSING")
            .build();
    // When we save to DB, the return will provide a full order object with the UUID's filled out
    order = orderRepository.save(order);
    UUID orderId = order.getOrderId();
    log.info("Order stored in DB...");

    // check stock from product service
    log.info("Checking if product is in stock in product service");
    int productCount = order.getProduct_id().size();

    try {
      for (int i = 0; i < productCount; i++) {
        UUID productId = order.getProduct_id().get(i);
        ResponseEntity<Boolean> inStockResponse = productService.hasEnoughStock(productId, order.getOrderQuantity());

        boolean inStock = false;
        if (inStockResponse != null && inStockResponse.getBody() != null) {
          inStock = inStockResponse.getBody();
        } else {
          return OrderResponse.builder()
                  .enoughStock(false)
                  .build();
        }
        if (!inStock) {
          log.info("Product not in stock!");
          return OrderResponse.builder()
                  .enoughStock(false)
                  .build();
        }
      }

    } catch (Exception e) {

    }


    log.info("Product in stock!");
    log.info("Processing payment...");
    // process payment if in stock
    StripePaymentRequest stripePaymentRequest = StripePaymentRequest.builder()
            .amount(order.getOrderPrice())
            .paymentMethodId(order.getPaymentMethod())
            .build();
    PaymentRequest paymentRequest = PaymentRequest.builder()
            .orderId(orderId)
            .stripePaymentRequest(stripePaymentRequest)
            .newOrderDetails(orderDetails)
            .cardInfo(updatedCardInfo)
            .build();

    // call payment microservice
    log.info("Attempting to initiate transaction in product service...");
    ResponseEntity<PaymentResponse> paymentResponse = paymentService.completeTransaction(paymentRequest);
    String paymentStatus = "";
    if (paymentResponse != null) {
      paymentStatus = paymentResponse.getBody().getPaymentStatus();
    } else {
      throw new CustomException("Payment Service error!", "SERVICE_ERROR", 500);
    }

    log.info("Payment status received...");

    if (!paymentStatus.equals("SUCCESS")) {
      log.info("Payment failed!");
      throw new CustomException("Payment failed!", "PAYMENT_FAILED", 200);
    }
    log.info("Payment SUCCESSFULL!");

    // update order's payment status
    log.info("Updating order in order DB to reflect successful payment...");
    Order fetchedOrder = orderRepository.findByOrderId(order.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

    fetchedOrder.setPaymentStatus("SUCCESS");
    order = orderRepository.save(fetchedOrder);
    log.info("Updated order's payment status successfully!");
    log.info("FINAL ORDER OBJECT: {}", order);

    // Decrease quantity in product database
    log.info("Updating product's quantity in product service...");
    for (UUID productId : order.getProduct_id()) {
//      UUID productId = order.getProduct_id().get(i);
      log.info("product id to decrease: {}", productId);

      // Just doing 1 for now - in the future, we'd have to recreate a response object
      // where we can have diff quantities for each product
      productService.decreaseQuantity(productId, order.getOrderQuantity());
      log.info("product quantity successfully reduced for order id of {}, for product id of {}", order.getOrderId(), productId);
    }

    log.info("Updating product complete!");

    // Finally, return the order response to the client
    OrderResponse orderResponse = OrderResponse.builder()
            .orderId(order.getOrderId())
            .productIds(order.getProduct_id())
            .orderPrice(order.getOrderPrice())
            .orderQuantity(order.getOrderQuantity())
            .paymentMethod(order.getPaymentMethod())
            .enoughStock(true)
            .build();
    log.info("Order successfully completed!");

    return orderResponse;
  }

}
