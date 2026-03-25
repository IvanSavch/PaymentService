package com.innowise.paymentservice.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TotalSumDto {
    private Long userId;
    private BigDecimal total;

}
