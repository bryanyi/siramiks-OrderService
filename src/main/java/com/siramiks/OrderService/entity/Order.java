package com.siramiks.OrderService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  // auto-generated via @PrePersist created below
  @Column(name = "order_id")
  private UUID orderId;

//  If I had more db resources, this would be the implementation. We'd have
// a separate database that holds these orders, and we'd create a junction table
// to hold the relationship of orders to products.
//  @ElementCollection
//  @CollectionTable(name="order_product_junction", joinColumns = @JoinColumn(name = "order_id"))
//  @Column(name = "products_in_order")
//  private List<UUID> product_id;

  @Column(name = "products_in_order")
  private List<UUID> product_id;

  @CreationTimestamp
  @Column(name = "order_created_at")
  private LocalDateTime createdAt;

  @Column(name = "order_total_price")
  private double orderPrice;

  @Column(name = "order_total_quantity")
  private long orderQuantity;

  @Column(name = "payment_method")
  private String paymentMethod;

  @Column(name = "payment_status")
  private String paymentStatus;

  /* will be automatically invoked by the JPA provider before the entity is persisted */
  @PrePersist
  protected void onCreate() {
    if (this.orderId == null) {
      // Generate a UUID if productId is not set
      this.orderId = UUID.randomUUID();
    }
  }

}
