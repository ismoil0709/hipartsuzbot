package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.OrderDto;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface OrderService {
    void create(Order order);

    Order update(Order order);

    void delete(Long id);

    OrderDto getById(Long id);

    List<OrderDto> getAll();
    List<OrderDto> findByUser(User user);

    List<OrderDto> findByBranch(String branch);

    List<OrderDto> findByAddress(String address);

    List<OrderDto> findActive(boolean active);

    List<OrderDto> findByOrderType(OrderType orderType);

    List<OrderDto> findActiveByOrderType(boolean active, OrderType orderType);

    List<OrderDto> findByTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<OrderDto> findByPaymentType(PaymentType paymentType);

    List<OrderDto> findByTotalPriceBetween(Double minPrice, Double maxPrice);

    List<OrderDto> findActiveByPaymentType(boolean active, PaymentType paymentType);
}
