package com.innowise.paymentservice.repository;

import com.innowise.paymentservice.model.dto.TotalSumDto;
import com.innowise.paymentservice.model.entity.Payment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findAllByUserId(Long userId);

    List<Payment> findAllByOrderId(Long orderId);

    List<Payment> findAllByStatus(Payment.Status status);

    @Aggregation(pipeline = {
            "{ $match: { userId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$userId', total: { $sum: '$paymentAmount' } } }",
            "{ $project: { userId: '$_id', total: 1, _id: 0 } }"
    })
    Optional<TotalSumDto> getTotalSumForUser(Long userId, LocalDateTime from, LocalDateTime to);
    @Aggregation(pipeline = {
            "{$match: { timestamp: { $gte: ?0, $lte: ?1 } } }",
            "{$group: { _id: null, total: { $sum: '$paymentAmount' } } }"})
    Optional<TotalSumDto> getTotalSum(LocalDateTime from, LocalDateTime to);
}
