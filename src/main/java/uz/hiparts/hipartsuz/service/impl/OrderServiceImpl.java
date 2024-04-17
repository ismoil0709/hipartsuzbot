package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.OrderDto;
import uz.hiparts.hipartsuz.exception.AlreadyExistsException;
import uz.hiparts.hipartsuz.exception.InvalidArgumentException;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.exception.NullOrEmptyException;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.service.OrderService;
import uz.hiparts.hipartsuz.util.Validations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    @Override
    public void create(Order order) {
        if (order.getId() != null) {
            if (orderRepository.findById(order.getId()).isPresent())
                throw new AlreadyExistsException("Order id");
        }
        orderRepository.save(order);
    }
    @Override
    public OrderDto getById(Long id) {
        return new OrderDto(orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order")));
    }
    @Override
    public List<OrderDto> getAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }
    @Override
    public List<OrderDto> findByUser(User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findByBranch(String branch) {
        if (branch.isBlank())
            throw new NullOrEmptyException("Branch");

        List<Order> orders = orderRepository.findByBranch(branch);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findByAddress(String address) {
        if (address.isBlank())
            throw new NullOrEmptyException("Address");

        List<Order> orders = orderRepository.findByAddress(address);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }
    @Override
    public List<OrderDto> findByActive(boolean active) {
        List<Order> orders = orderRepository.findByActive(active);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }
    @Override
    public List<OrderDto> findByOrderType(OrderType orderType) {
        List<Order> orders = orderRepository.findByOrderType(orderType);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }
    @Override
    public List<OrderDto> findByActiveAndOrderType(boolean active, OrderType orderType) {
        List<Order> orders = orderRepository.findByActiveAndOrderType(active, orderType);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null)
            throw new NullOrEmptyException("Start date");
        if (endTime == null)
            throw new NullOrEmptyException("End date");
        if (startTime.isAfter(endTime))
            throw new InvalidArgumentException("Start time");

        List<Order> orders = orderRepository.findByTimeBetween(startTime, endTime);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findByPaymentType(PaymentType paymentType) {
        List<Order> orders = orderRepository.findByPaymentType(paymentType);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> findByTotalPriceBetween(Double minPrice, Double maxPrice) {
        if (minPrice == null || minPrice.isInfinite() || minPrice.isNaN())
            throw new InvalidArgumentException("Min price");
        if (maxPrice == null || maxPrice.isInfinite() || maxPrice.isNaN())
            throw new InvalidArgumentException("Max price");

        List<Order> orders = orderRepository.findByTotalPriceBetween(minPrice, maxPrice);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }


    @Override
    public List<OrderDto> findByActiveAndPaymentType(boolean active, PaymentType paymentType) {
        List<Order> orders = orderRepository.findByActiveAndPaymentType(active, paymentType);
        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }
}
