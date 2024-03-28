package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.hiparts.hipartsuz.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class OrderDto {
    private Long userId;
    private List<Long> productIds;
    private PaymentType paymentType;
    private LocalDateTime time;
}
