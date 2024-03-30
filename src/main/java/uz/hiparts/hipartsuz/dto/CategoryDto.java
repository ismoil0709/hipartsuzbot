package uz.hiparts.hipartsuz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.hiparts.hipartsuz.model.Category;

@Getter
@AllArgsConstructor
public class CategoryDto {
    @NotBlank
    private String name;

    public CategoryDto (Category category){
        this.name =  category.getName();
    }
}