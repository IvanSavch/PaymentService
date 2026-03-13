package com.innowise.paymentservice.model.dto;

import com.innowise.paymentservice.model.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String orderId;
    private String userId;
    private Payment.Status status;
    private LocalDateTime timestamp;
    private BigDecimal paymentAmount;

}
