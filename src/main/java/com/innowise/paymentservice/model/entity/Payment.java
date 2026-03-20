package com.innowise.paymentservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    @Indexed
    private Long orderId;
    @Indexed
    private Long userId;
    private Status status;
    private LocalDateTime timestamp;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal paymentAmount;
    public enum Status {
        SUCCESS, FAILED
    }
}
