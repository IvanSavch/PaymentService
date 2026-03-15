package com.innowise.paymentservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    @NotBlank(message = "Order id can't be null")
    private String orderId;
    @NotBlank(message = "User id can't be null")
    private String userId;
    private LocalDateTime timestamp;
    @Field(targetType = FieldType.DECIMAL128)
    @PositiveOrZero(message = "Payment amount can't be negative")
    private BigDecimal paymentAmount;

}
