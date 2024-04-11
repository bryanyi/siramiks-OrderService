package com.siramiks.OrderService.repository;

import com.siramiks.OrderService.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<CardInfo, UUID> {
  CardInfo findByCardNumber(String cardNumber);
}
