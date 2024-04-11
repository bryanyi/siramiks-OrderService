package com.siramiks.OrderService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "card_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "card_id")
  private UUID cardId;
  @Column(name = "card_number")
  private String cardNumber;
  @Column(name = "exp_month")
  private String expMonth;
  @Column(name = "exp_year")
  private String expYear;
  @Column(name = "cvc")
  private String cvc;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (this.cardId == null) {
      // Generate a UUID if productId is not set
      this.cardId = UUID.randomUUID();
    }
  }
}
