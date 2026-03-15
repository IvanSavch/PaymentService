package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.client.RandomClient;
import com.innowise.paymentservice.exception.ResourceNotFoundException;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.dto.PaymentDto;
import com.innowise.paymentservice.model.dto.TotalSumDto;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final RandomClient randomClient;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(RandomClient randomClient, PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.randomClient = randomClient;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public Payment createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toPayment(paymentDto);
        int random = randomClient.random();
        if (random % 2 == 0) {
            payment.setStatus(Payment.Status.SUCCESS);
            return paymentRepository.save(payment);
        }
        payment.setStatus(Payment.Status.FAILED);
        return paymentRepository.save(payment);
    }

    public List<Payment> findAllByUserId(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }

    public List<Payment> findAllByOrderId(Long orderId) {
        return paymentRepository.findAllByOrderId(orderId);
    }

    public List<Payment> findByStatus(Payment.Status status) {
        return paymentRepository.findAllByStatus(status);
    }

    public TotalSumDto getTotalSumForUser(Long userId, LocalDateTime from, LocalDateTime to) {
        return paymentRepository.getTotalSumForUser(userId, from, to)
                .orElseThrow(() -> new ResourceNotFoundException("Payments not found for user " + userId));
    }

    public TotalSumDto getTotalSumForAllUsers(LocalDateTime from, LocalDateTime to) {
        return paymentRepository.getTotalSum(from, to)
                .orElseThrow(() -> new ResourceNotFoundException("Payments not found for period"));
    }
}
