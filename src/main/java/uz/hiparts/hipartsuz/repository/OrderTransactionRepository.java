package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.OrderTransaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
    Optional<OrderTransaction> findByTransactionId(String id);

    List<OrderTransaction> findAllByTransactionCreationTimeBetweenOrderByTransactionCreationTimeAsc(
            Long from, Long to);
    Optional<OrderTransaction> findByOrderId(Long orderId);
}
