package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class OrderDto {
    private Long userId;
    private List<Long> productIds;
    private PaymentType paymentType;
    private LocalDateTime time;
    private Double totalPrice;
    private String comment;
    public OrderDto(Order order) {
        this.userId = order.getUser().getId();
        this.productIds = order.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        this.paymentType = order.getPaymentType();
        this.time = order.getTime();
        this.totalPrice = order.getTotalPrice();
        this.comment = order.getComment();
    }
}
