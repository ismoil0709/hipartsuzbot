package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderType(OrderType orderType);
    List<Order> findByUser(User user);
    List<Order> findByBranch(String branch);
    List<Order> findByAddress(String address);
    List<Order> findByPaymentType(PaymentType paymentType);
    List<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice);
    List<Order> findByTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByActive(boolean active);
    List<Order> findByActiveAndOrderType(boolean active, OrderType orderType);
    List<Order> findByActiveAndPaymentType(boolean active, PaymentType paymentType);
}
