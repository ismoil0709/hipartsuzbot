package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantitiesDto{
    private Long productId;
    private Integer quantity;
}