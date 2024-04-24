package com.siramiks.OrderService.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.siramiks.OrderService.OrderServiceConfig;
import com.siramiks.OrderService.entity.CardInfo;
import com.siramiks.OrderService.entity.Order;
import com.siramiks.OrderService.model.NewOrderDetails;
import com.siramiks.OrderService.model.OrderRequest;
import com.siramiks.OrderService.model.OrderResponse;
import com.siramiks.OrderService.repository.OrderRepository;
import com.siramiks.OrderService.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
public class OrderControllerTest {

  /*
    Annotations explained:
    @SpringBootTest({"server.port=0"}) - don't depend on any port
    @EnableConfigurationProperties - Utilize the setup from application.yaml
    @AutoConfigureMockMvc - When we test the controller, we want to hit the actual endpoints, not the methods.
  */

  @Autowired
  private OrderService orderService;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private MockMvc mockMvc;

  private String mockProductUUID = "d9bcf568-1102-41b9-b612-06949bc7bae0";

  @RegisterExtension
  static WireMockExtension wireMockServer = WireMockExtension.newInstance()
          .options((WireMockConfiguration.wireMockConfig().port(8080)))
          .build();

  private ObjectMapper objectMapper = new ObjectMapper()
          .findAndRegisterModules()
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // set up the inner microservice method calls
  @BeforeEach
  void setup() throws IOException {
    getProductDetails();
    checkProductStock();
    completeTransaction();
    decreaseProductQty();
  }


  public void getProductDetails() throws IOException {

    wireMockServer.stubFor(WireMock.get("/product/" + mockProductUUID)
            .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(StreamUtils.copyToString(
                            OrderControllerTest.class
                                    .getClassLoader()
                                    .getResourceAsStream(
                                            "mock/GetProduct.json"),
                            Charset.defaultCharset()))));

  }

  public void checkProductStock() {
    // circuitBreakerRegistry.circuitBreaker("external").reset();
    wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/api/v1/product/checkStock/.*")).willReturn(
            WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody("{\"body\":true}")));
  }

  public void decreaseProductQty() {
    // circuitBreakerRegistry.circuitBreaker("external").reset();
    wireMockServer.stubFor(WireMock.put(WireMock.urlMatching("/api/v1/product/decreaseQuantity/.*"))
            .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

  }

  public void completeTransaction() throws IOException {
    // circuitBreakerRegistry.circuitBreaker("external").reset();
    wireMockServer.stubFor(WireMock.post(WireMock.urlMatching("/api/v1/payment/completeTransaction")).willReturn(
            WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(StreamUtils.copyToString(
                            OrderControllerTest.class
                                    .getClassLoader()
                                    .getResourceAsStream("mock/PaymentResponse.json"),
                            Charset.defaultCharset()
                    ))
    ));

  }


  @Test
  public void tester() {
    int expected = 5;
    int actual = 5;
    Assertions.assertEquals(expected, actual);
  }

  @DisplayName("Test successful order placement")
  @Test
  public void testPlaceOrderSuccess() throws Exception {
    // Check that product has enough stock with productService
    // Call paymentService to init a transaction
    // Decrease quantity with productService

    OrderRequest orderRequest = getMockOrderRequest();
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/order/createOrder")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderRequest))
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    String orderResponseStr = mvcResult.getResponse().getContentAsString();
    OrderResponse orderResponse = objectMapper.readValue(orderResponseStr, OrderResponse.class);

    Assertions.assertNotNull(orderResponse.getOrderId());

    Optional<Order> fetchedOrder = orderRepository.findByOrderId(orderResponse.getOrderId());

    Assertions.assertTrue(fetchedOrder.isPresent());

    Order order = fetchedOrder.get();
    Assertions.assertEquals(orderResponse.getOrderId(), order.getOrderId());
    Assertions.assertEquals(orderResponse.getProductIds(), order.getProduct_id());
    Assertions.assertEquals(orderResponse.getOrderPrice(), order.getOrderPrice());
    Assertions.assertEquals(orderResponse.getOrderQuantity(), order.getOrderQuantity());
    Assertions.assertEquals(orderResponse.getPaymentMethod(), order.getPaymentMethod());
    Assertions.assertEquals("SUCCESS", order.getPaymentStatus());
  }

  public OrderRequest getMockOrderRequest() {
    NewOrderDetails newOrderDetails = NewOrderDetails.builder()
            .productIds(Arrays.asList(UUID.fromString(mockProductUUID)))
            .orderPrice(17.00)
            .orderQuantity(1)
            .paymentMethod("VISA")
            .build();

    CardInfo cardInfo = CardInfo.builder()
            .cardNumber("4242424242424242")
            .expMonth("12")
            .expYear("2030")
            .cvc("123")
            .build();
    return OrderRequest.builder()
            .newOrderDetails(newOrderDetails)
            .cardInfo(cardInfo)
            .build();
  }


}