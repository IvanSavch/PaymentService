package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.dto.PaymentDto;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public Payment createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toPayment(paymentDto);
        return paymentRepository.save(payment);
    }

    public Payment findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).orElseThrow();
    }

    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow();
    }

    public List<Payment> findByStatus(Payment.Status status) {
        return paymentRepository.findAllByStatus(status);
    }
    public BigDecimal getTotalSumForUser(Long userId, LocalDateTime from,LocalDateTime to){
        BigDecimal totalPayments = paymentRepository.getTotalSumForUser(userId, from, to);
//        Payment payment = paymentRepository.findByUserId(userId).orElseThrow();
//        payment.setPaymentAmount(totalPayments);
        return totalPayments;
    }
    public BigDecimal getTotalSumForAllUsers(LocalDateTime from,LocalDateTime to){
        BigDecimal totalSum = paymentRepository.getTotalSum(from, to);
        return totalSum;
    }
}
