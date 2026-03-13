package com.innowise.paymentservice.repository;

import com.innowise.paymentservice.model.entity.Payment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByUserId(Long userId);

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findAllByStatus(Payment.Status status);

    @Aggregation(pipeline = {
            "{ $match: { userId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: null, total: { $sum: '$paymentAmount' } } }"})
    BigDecimal getTotalSumForUser(Long userId, LocalDateTime from, LocalDateTime to);
    @Aggregation(pipeline = {
            "{$match: { timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{$group: { _id: null, total: { $sum: '$paymentAmount' } } }"})
    BigDecimal getTotalSum(LocalDateTime from, LocalDateTime to);
}
