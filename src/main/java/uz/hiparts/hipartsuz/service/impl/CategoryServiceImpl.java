package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.exception.AlreadyExistsException;
import uz.hiparts.hipartsuz.exception.NotFoundException;
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
    public Category save(String name) {
        if (categoryRepository.findByName(name).isPresent())
            throw new AlreadyExistsException("Category");
        return categoryRepository.save(Category.builder().name(name).isActive(true).build());
    }

    @Override
    public Category getById(Long id) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isEmpty()) {
            throw new NotFoundException("Category");
        }
        return byId.get();
    }

    @Override
    public Category getByName(String name) {
        Optional<Category> byName = categoryRepository.findByName(name);
        if (byName.isEmpty()) {
            throw new NotFoundException("Category");
        }
        return byName.get();
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category"));
        category.setActive(false);
        categoryRepository.save(category);
    }
}
