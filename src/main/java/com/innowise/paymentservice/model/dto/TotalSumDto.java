package com.innowise.paymentservice.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TotalSumDto {
    private Long userId;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal total;

}
