package com.innowise.paymentservice.mapper;

import com.innowise.paymentservice.model.dto.OrderDto;
import com.innowise.paymentservice.model.dto.PaymentDto;
import com.innowise.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPayment(PaymentDto paymentDto);
    PaymentDto toPaymentDto(Payment payment);
    List<PaymentDto> toPaymentDtoList(List<Payment> payments);
    @Mapping(target = "orderId",source = "id")
    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "paymentAmount",source = "totalPrice")
    @Mapping(target = "id",ignore = true)
    Payment orderDtoToPayment(OrderDto orderDto);
    List<Payment> toPaymentFromOrderDtoList(List<OrderDto> orderDto);
}
