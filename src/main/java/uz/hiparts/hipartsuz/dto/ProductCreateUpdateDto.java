package uz.hiparts.hipartsuz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.hiparts.hipartsuz.model.Category;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductCreateUpdateDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Double price;
    @NotBlank
    private String imgPath;
    @NotBlank
    private String imgId;
    @NotNull
    private Category category;
    private Double discount;

    public ProductCreateUpdateDto(ProductDto productDto) {
        this.id = productDto.getId();
        this.name = productDto.getName();
        this.description = productDto.getDescription();
        this.price = productDto.getPrice();
        this.imgPath = productDto.getImgPath();
        this.category = productDto.getCategory();
        this.discount = productDto.getDiscount();
    }
}
