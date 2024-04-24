package com.siramiks.OrderService.service;

import com.siramiks.OrderService.entity.CardInfo;
import com.siramiks.OrderService.entity.Order;
import com.siramiks.OrderService.feign.PaymentResponse.PaymentRequest;
import com.siramiks.OrderService.feign.PaymentResponse.PaymentResponse;
import com.siramiks.OrderService.feign.PaymentResponse.PaymentService;
import com.siramiks.OrderService.feign.ProductResponse.ProductService;
import com.siramiks.OrderService.model.NewOrderDetails;
import com.siramiks.OrderService.model.OrderRequest;
import com.siramiks.OrderService.model.OrderResponse;
import com.siramiks.OrderService.repository.CardRepository;
import com.siramiks.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private CardRepository cardRepository;
  @Mock
  private ProductService productService;
  @Mock
  private PaymentService paymentService;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  OrderService orderService = new OrderService();

  @DisplayName("Get Order By orderId - Success Test")
  @Test
  void testGetOrderByIdSuccess() {
    UUID mockOrderId = UUID.randomUUID();

    // mock
    Order mockOrder = getMockedOrder();
    when(orderRepository.findByOrderId(any(UUID.class)))
            .thenReturn(Optional.of(mockOrder));

    // actual
    OrderResponse orderResponse = orderService.getOrderDetails(mockOrderId.toString());

    // verify we're making just 1 call to findByOrderId
    verify(orderRepository, times(1)).findByOrderId(mockOrderId);

    // assert
    assertNotNull(orderResponse);
    assertEquals(mockOrder.getOrderId(), orderResponse.getOrderId());

  }

  @DisplayName("Creating New Order - Success Test")
  @Test
  public void testCreateOrder() {
    // Arrange
    UUID productId1 = UUID.fromString("3e552c6f-a081-48c7-a885-7ef5b7d43c02");
    UUID productId2 = UUID.fromString("d9bcf568-1102-41b9-b612-06949bc7bae0");

    NewOrderDetails newOrderDetails = NewOrderDetails.builder()
            .productIds(Arrays.asList(productId1, productId2))
            .orderPrice(100.0)
            .orderQuantity(1)
            .paymentMethod("VISA")
            .build();

    CardInfo cardInfo = CardInfo.builder()
            .cardNumber("4242424242424242")
            .expMonth("12")
            .expYear("29")
            .cvc("123")
            .build();

    OrderRequest orderRequest = OrderRequest.builder()
            .newOrderDetails(newOrderDetails)
            .cardInfo(cardInfo)
            .build();

    Order order = Order.builder()
            .product_id(Arrays.asList(productId1, productId2))
            .orderPrice(100.0)
            .orderQuantity(2)
            .paymentMethod("VISA")
            .paymentStatus("SUCCESS")
            .build();

    // Act
    OrderResponse orderResponse = orderService.createOrder(orderRequest);

    // Assert
    assertNotNull(orderResponse);
    assertEquals(Arrays.asList(productId1, productId2), orderResponse.getProductIds());
    assertEquals(100.0, orderResponse.getOrderPrice());
    assertEquals(2, orderResponse.getOrderQuantity());
    assertEquals("VISA", orderResponse.getPaymentMethod());
  }

  private OrderRequest getMockedOrderRequest() {
    NewOrderDetails orderDetails = NewOrderDetails.builder()
            .productIds(List.of(UUID.randomUUID(), UUID.randomUUID()))
            .orderPrice(19.99)
            .orderQuantity(2)
            .build();
    CardInfo cardInfo = CardInfo.builder()
            .cardNumber("4242424242424242")
            .expMonth("12")
            .expYear("89")
            .cvc("123")
            .build();
    return OrderRequest.builder()
            .newOrderDetails(orderDetails)
            .cardInfo(cardInfo)
            .build();
  }

  private Order getMockedOrder() {
    return Order.builder()
            .product_id(List.of(UUID.randomUUID(), UUID.randomUUID()))
            .orderPrice(19.99)
            .orderQuantity(2)
            .paymentMethod("pm_card_visa")
            .paymentStatus("SUCCESS")
            .build();
  }

}