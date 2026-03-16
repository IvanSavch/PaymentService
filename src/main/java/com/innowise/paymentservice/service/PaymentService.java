package com.innowise.paymentservice.service;

import com.innowise.paymentservice.model.dto.TotalSumDto;
import com.innowise.paymentservice.model.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    List<Payment> createPayment();
    List<Payment> findAllByUserId(Long userId);
    List<Payment> findAllByOrderId(Long orderId);
    List<Payment> findByStatus(Payment.Status status);
    TotalSumDto getTotalSumForUser(Long userId, LocalDateTime from, LocalDateTime to);
    TotalSumDto getTotalSumForAllUsers(LocalDateTime from, LocalDateTime to);

}
