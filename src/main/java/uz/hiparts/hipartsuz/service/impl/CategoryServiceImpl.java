package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.exception.AlreadyExistsException;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.repository.CategoryRepository;
import uz.hiparts.hipartsuz.repository.ProductRepository;
import uz.hiparts.hipartsuz.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public Category save(String name) {
        if (categoryRepository.findByName(name).isPresent())
            throw new AlreadyExistsException("Category");
        return categoryRepository.save(Category.builder().name(name).build());
    }

    @Override
    public boolean delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category"));

        if (productRepository.existsByCategory(category.getId())) {
            return false;
        }

        categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category")
        );
    }

    @Override
    public Category getByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category"));
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

}
