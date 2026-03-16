package com.innowise.paymentservice.model.dto;

import com.innowise.paymentservice.model.entity.Payment;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long orderId;
    private Long userId;
    private Payment.Status status;
    @Field(targetType = FieldType.DECIMAL128)
    @PositiveOrZero(message = "Payment amount can't be negative")
    private BigDecimal paymentAmount;

}
