package uz.hiparts.hipartsuz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;

@AllArgsConstructor
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private String imgPath;
    private String imgId;
    private Category category;
    private String description;
    private boolean isActive;
    private Double discount;

    public ProductDto(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.category = product.getCategory();
        this.description = product.getDescription();
        this.isActive = product.isActive();
        this.imgPath = product.getImgPath();
        this.imgId = product.getImgId();
        this.discount = product.getDiscount();
    }
}
