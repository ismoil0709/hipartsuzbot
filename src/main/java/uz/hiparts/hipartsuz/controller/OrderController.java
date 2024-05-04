package uz.hiparts.hipartsuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.hiparts.hipartsuz.dto.OrderDto;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.User;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;
import uz.hiparts.hipartsuz.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public void createOrder(@Valid Order order) {
        orderService.create(order);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<OrderDto>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/get/user")
    public ResponseEntity<List<OrderDto>> getByUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(orderService.findByUser(user));
    }

    @GetMapping("/get/branch/{branch}")
    public ResponseEntity<List<OrderDto>> getByBranch(@PathVariable String branch) {
        return ResponseEntity.ok(orderService.findByBranch(branch));
    }

    @GetMapping("/get/active")
    public ResponseEntity<List<OrderDto>> getActive(boolean active) {
        return ResponseEntity.ok(orderService.findByActive(true));
    }

    @GetMapping("/get/address")
    public ResponseEntity<List<OrderDto>> getByAddress(@RequestBody String address) {
        return ResponseEntity.ok(orderService.findByAddress(address));
    }

    @GetMapping("/get/type")
    public ResponseEntity<List<OrderDto>> getByType(@RequestBody OrderType type) {
        return ResponseEntity.ok(orderService.findByOrderType(type));
    }

    @GetMapping("/get/type/active/{active}")
    public ResponseEntity<List<OrderDto>> getByActiveAndOrderType(@RequestBody OrderType type, @PathVariable boolean active) {
        return ResponseEntity.ok(orderService.findByActiveAndOrderType(active, type));
    }

    @GetMapping("/get/time")
    public ResponseEntity<List<OrderDto>> getByTime(@RequestBody LocalDateTime startTime, @RequestBody LocalDateTime endTime) {
        return ResponseEntity.ok(orderService.findByTimeBetween(startTime, endTime));
    }

    @GetMapping("/get/payment-type")
    public ResponseEntity<List<OrderDto>> getByPaymentType(@RequestBody PaymentType paymentType) {
        return ResponseEntity.ok(orderService.findByPaymentType(paymentType));
    }

    @GetMapping("/get/price")
    public ResponseEntity<List<OrderDto>> getByPrice(@RequestBody Double maxPrice, @RequestBody Double minPrice) {
        return ResponseEntity.ok(orderService.findByTotalPriceBetween(minPrice, maxPrice));
    }

    @GetMapping("/get/payment-type/active/{active}")
    public ResponseEntity<List<OrderDto>> getByPaymentTypeAndActive(@RequestBody PaymentType paymentType, @PathVariable boolean active) {
        return ResponseEntity.ok(orderService.findByActiveAndPaymentType(active, paymentType));
    }
}
