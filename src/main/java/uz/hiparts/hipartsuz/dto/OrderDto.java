package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uz.hiparts.hipartsuz.model.Order;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.model.ProductQuantity;
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
    private List<ProductQuantitiesDto> productQuantities;
    public OrderDto(Order order) {
        this.userId = order.getUser().getId();
        this.paymentType = order.getPaymentType();
        this.time = order.getTime();
        this.totalPrice = order.getTotalPrice();
        this.comment = order.getComment();
        this.productQuantities = order.getProductQuantities()
                .stream()
                .map(productQuantity -> new ProductQuantitiesDto(productQuantity.getProduct().getId(), productQuantity.getQuantity()))
                .toList();
    }
}
