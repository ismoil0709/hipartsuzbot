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
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Double price;
    @NotBlank
    private String imgPath;
    @NotNull
    private Category category;
    private Double discount;
}
