package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;

@AllArgsConstructor
@Getter
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private Category category;
    private String description;
    private boolean isActive;


    public ProductDto(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.isActive = product.isActive();
    }
}
