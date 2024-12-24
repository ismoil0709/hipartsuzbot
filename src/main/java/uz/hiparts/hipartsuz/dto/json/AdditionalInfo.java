package uz.hiparts.hipartsuz.dto.json;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdditionalInfo {

    private Long orderId;

    private Double orderSum;

    private String site = "pdp.uz";

    public AdditionalInfo(Long orderId, Double orderSum) {
        this.orderId = orderId;
        this.orderSum = orderSum;
    }
}
