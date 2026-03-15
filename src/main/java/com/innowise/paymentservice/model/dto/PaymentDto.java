package com.innowise.paymentservice.model.dto;

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
    private String orderId;
    private String userId;
    private LocalDateTime timestamp;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal paymentAmount;

}
