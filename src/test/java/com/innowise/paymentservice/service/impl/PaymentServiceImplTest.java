package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.client.OrderClient;
import com.innowise.paymentservice.client.RandomClient;
import com.innowise.paymentservice.exception.ResourceNotFoundException;
import com.innowise.paymentservice.kafka.PaymentKafkaProducer;
import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.dto.TotalSumDto;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private RandomClient randomClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentKafkaProducer paymentKafkaProducer;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId("1");
        payment.setUserId(10L);
    }

    @Test
    void createPayment_success() {
        when(randomClient.random()).thenReturn(2);
        when(orderClient.findOrderByUserId()).thenReturn(List.of());
        when(paymentMapper.toPaymentFromOrderDtoList(any()))
                .thenReturn(List.of(payment));
        when(paymentRepository.saveAll(any()))
                .thenReturn(List.of(payment));

        List<Payment> result = paymentService.createPayment();

        assertEquals(1, result.size());
        assertEquals(Payment.Status.SUCCESS, result.get(0).getStatus());
        assertNotNull(result.get(0).getTimestamp());

        verify(paymentKafkaProducer, times(1)).send(payment);
    }

    @Test
    void createPayment_failed() {
        when(randomClient.random()).thenReturn(3);
        when(orderClient.findOrderByUserId()).thenReturn(List.of());
        when(paymentMapper.toPaymentFromOrderDtoList(any()))
                .thenReturn(List.of(payment));
        when(paymentRepository.saveAll(any()))
                .thenReturn(List.of(payment));

        List<Payment> result = paymentService.createPayment();

        assertEquals(Payment.Status.FAILED, result.get(0).getStatus());
    }

    @Test
    void findAllByUserId() {
        when(paymentRepository.findAllByUserId(10L)).thenReturn(List.of(payment));
        List<Payment> result = paymentService.findAllByUserId(10L);

        assertEquals(1, result.size());
        verify(paymentRepository).findAllByUserId(10L);
    }

    @Test
    void findAllByOrderId() {
        when(paymentRepository.findAllByOrderId(5L)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findAllByOrderId(5L);

        assertEquals(1, result.size());
        verify(paymentRepository).findAllByOrderId(5L);
    }

    @Test
    void findByStatus() {
        when(paymentRepository.findAllByStatus(Payment.Status.SUCCESS)).thenReturn(List.of(payment));

        List<Payment> result = paymentService.findByStatus(Payment.Status.SUCCESS);

        assertEquals(1, result.size());
    }

    @Test
    void getTotalSumForUser_success() {
        TotalSumDto dto = new TotalSumDto();
        dto.setTotal(new BigDecimal(100));

        when(paymentRepository.getTotalSumForUser(anyLong(), any(), any()))
                .thenReturn(Optional.of(dto));

        TotalSumDto result =
                paymentService.getTotalSumForUser(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(new BigDecimal(100), result.getTotal());
    }

    @Test
    void getTotalSumForUser_notFound() {
        when(paymentRepository.getTotalSumForUser(anyLong(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                paymentService.getTotalSumForUser(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now()));
    }

    @Test
    void getTotalSumForAllUsers_success() {
        TotalSumDto dto = new TotalSumDto();
        dto.setTotal(new BigDecimal(200));

        when(paymentRepository.getTotalSum(any(), any())).thenReturn(Optional.of(dto));

        TotalSumDto result =
                paymentService.getTotalSumForAllUsers(LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertEquals(new BigDecimal(200), result.getTotal());
    }

    @Test
    void getTotalSumForAllUsers_notFound() {
        when(paymentRepository.getTotalSum(any(), any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getTotalSumForAllUsers(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now()));
    }
}