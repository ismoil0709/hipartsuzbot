package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.CategoryDto;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.repository.CategoryRepository;
import uz.hiparts.hipartsuz.service.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    public Category save(CategoryDto createCategory) {
        return categoryRepository.save(Category
                .builder()
                .name(createCategory.getName())
                .build());
    }

    @Override
    public Category getById(Long id) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        return byId.get();
    }

    @Override
    public Category getByName(String name) {
        Optional<Category> byName = categoryRepository.findByName(name);
        if (byName.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        return byName.get();
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
