package com.innowise.paymentservice.mapper;

import com.innowise.paymentservice.model.dto.PaymentDto;
import com.innowise.paymentservice.model.dto.TotalSumDto;
import com.innowise.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPayment(PaymentDto paymentDto);
    PaymentDto toPaymentDto(Payment payment);
    List<PaymentDto> toPaymentDtoList(List<Payment> payments);
    TotalSumDto toTotalSumDto(Long userId, BigDecimal total);
}
