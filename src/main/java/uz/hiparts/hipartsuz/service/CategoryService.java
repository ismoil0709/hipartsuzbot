package uz.hiparts.hipartsuz.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.CategoryDto;
import uz.hiparts.hipartsuz.model.Category;

import java.util.List;

@Service
public interface CategoryService {
    Category save(@NotNull CategoryDto createCategory);
    Category getById(Long id);
    Category getByName(String name);
    List<Category> getAll();

}
