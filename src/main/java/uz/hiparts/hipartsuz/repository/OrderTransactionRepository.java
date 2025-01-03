package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.OrderTransaction;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
    Optional<OrderTransaction> findByTransactionId(String id);

    List<OrderTransaction> findAllByStateAndTransactionCreationTimeBetween(Integer code, Timestamp from, Timestamp to);

    Optional<OrderTransaction> findByOrderId(Long orderId);
}
