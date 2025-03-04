package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.hiparts.hipartsuz.dto.OrderDto;
import uz.hiparts.hipartsuz.exception.InvalidArgumentException;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.ProductQuantity;
import uz.hiparts.hipartsuz.repository.OrderRepository;
import uz.hiparts.hipartsuz.repository.ProductRepository;
import uz.hiparts.hipartsuz.repository.UserRepository;
import uz.hiparts.hipartsuz.service.OrderService;
import uz.hiparts.hipartsuz.service.telegramService.BotService;
import uz.hiparts.hipartsuz.service.telegramService.SendMessageService;
import uz.hiparts.hipartsuz.util.UtilLists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BotService botService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SendMessageService sendMessageService;

    @Override
    public void create(OrderDto order) {
        if (UtilLists.orderMap.get(order.getUserId()) == null) {
            botService.send(
                    SendMessage.builder()
                            .chatId(order.getUserId())
                            .text("Please restart the bot with /start")
                    .build());
            return;
        }
        Order existsOrder = UtilLists.orderMap.get(order.getUserId());
        existsOrder.setPaymentType(order.getPaymentType());
        existsOrder.setTime(order.getTime());
        existsOrder.setComment(order.getComment());
        existsOrder.setProductQuantities(new ArrayList<>(
                order.getProductQuantities().stream().map(productQuantitiesDto ->
                        ProductQuantity.builder()
                                .product(productRepository.findById(productQuantitiesDto.getProductId()).orElseThrow(
                                        () -> new NotFoundException("Product")
                                ))
                                .quantity(productQuantitiesDto.getQuantity())
                                .build()).toList()
        ));
        existsOrder.setTotalPrice(order.getTotalPrice());
        existsOrder.setActive(true);
        existsOrder.setUser(userRepository.findByChatId(order.getUserId()).orElseThrow(
                () -> new NotFoundException("User")
        ));
        existsOrder.getProductQuantities().forEach(p -> p.getProduct().setPrice(p.getProduct().getPrice() - ((p.getProduct().getPrice() * p.getProduct().getDiscount()) / 100)));
        UtilLists.orderMap.put(order.getUserId(), existsOrder);
        botService.send(sendMessageService.sendOrderDetails(existsOrder));
        botService.send(sendMessageService.sendOrderConfirmation(existsOrder));
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

}
