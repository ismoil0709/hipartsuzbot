package uz.hiparts.hipartsuz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryDto {
    @NotBlank
    private String name;
    private String description;
}