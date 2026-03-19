package com.innowise.paymentservice.controller;

import com.innowise.paymentservice.mapper.PaymentMapper;
import com.innowise.paymentservice.model.dto.PaymentDto;
import com.innowise.paymentservice.model.dto.TotalSumDto;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentMapper paymentMapper, PaymentService paymentService) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/")
    public ResponseEntity<List<PaymentDto>> createPayment() {
        List<Payment> payment = paymentService.createPayment();
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toPaymentDtoList(payment));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("@authenticationServiceImpl.adminRole(authentication) or @authenticationServiceImpl.isSelf(#userId, authentication)")
    public ResponseEntity<List<PaymentDto>> findByUserId(@PathVariable Long userId) {
        List<Payment> byUserId = paymentService.findAllByUserId(userId);
        return ResponseEntity.ok(paymentMapper.toPaymentDtoList(byUserId));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("@authenticationServiceImpl.adminRole(authentication)")
    public ResponseEntity<List<PaymentDto>> findByOrderId(@PathVariable Long orderId) {
        List<Payment> byOrderId = paymentService.findAllByOrderId(orderId);
        return ResponseEntity.ok(paymentMapper.toPaymentDtoList(byOrderId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("@authenticationServiceImpl.adminRole(authentication)")
    public ResponseEntity<List<PaymentDto>> findByStatus(@PathVariable("status") Payment.Status status) {
        List<Payment> byStatus = paymentService.findByStatus(status);
        return ResponseEntity.ok(paymentMapper.toPaymentDtoList(byStatus));
    }

    @GetMapping("/{userId}/")
    @PreAuthorize("@authenticationServiceImpl.adminRole(authentication) or @authenticationServiceImpl.isSelf(#userId, authentication)")
    public ResponseEntity<TotalSumDto> getTotalSumForUser(@PathVariable Long userId,
                                                         @RequestParam LocalDateTime from,
                                                         @RequestParam LocalDateTime to) {
        TotalSumDto totalSumForUser = paymentService.getTotalSumForUser(userId, from, to);
        return ResponseEntity.ok().body(totalSumForUser);
    }
    @GetMapping("/")
    @PreAuthorize("@authenticationServiceImpl.adminRole(authentication)")
    public ResponseEntity<TotalSumDto> getTotalSumForUser(@RequestParam LocalDateTime from,
                                                          @RequestParam LocalDateTime to) {
        TotalSumDto totalSumForAllUsers = paymentService.getTotalSumForAllUsers(from, to);
        return ResponseEntity.ok().body(totalSumForAllUsers);
    }
}
