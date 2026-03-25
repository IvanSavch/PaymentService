package com.innowise.paymentservice.model.dto;

import com.innowise.paymentservice.model.entity.Payment;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long orderId;
    private Long userId;
    private Payment.Status status;
    private LocalDateTime timestamp;
    @PositiveOrZero(message = "Payment amount can't be negative")
    private BigDecimal paymentAmount;

}
