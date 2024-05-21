package uz.hiparts.hipartsuz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class ProductQuantity {
    @Id
    private Long productId;
    private Integer quantity;
}
