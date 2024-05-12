package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.OrderDto;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface OrderService {
    void create(OrderDto order);

    OrderDto getById(Long id);

    List<OrderDto> getAll();

    List<OrderDto> findByUser(User user);

    List<OrderDto> findByBranch(String branch);

    List<OrderDto> findByAddress(String address);

    List<OrderDto> findByActive(boolean active);

    List<OrderDto> findByOrderType(OrderType orderType);

    List<OrderDto> findByActiveAndOrderType(boolean active, OrderType orderType);

    List<OrderDto> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<OrderDto> findByPaymentType(PaymentType paymentType);

    List<OrderDto> findByTotalPriceBetween(Double minPrice, Double maxPrice);

    List<OrderDto> findByActiveAndPaymentType(boolean active, PaymentType paymentType);
}
