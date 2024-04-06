package uz.hiparts.hipartsuz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import uz.hiparts.hipartsuz.model.Category;

@AllArgsConstructor
@Getter
public class ProductCreateUpdateDto {
    @NotBlank
    private String name;
    @NotNull
    private Double price;
    @NotNull
    private Category category;
    @NotBlank
    private String description;
    private boolean isActive;
}
