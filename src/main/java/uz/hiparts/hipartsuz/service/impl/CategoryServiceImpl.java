package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.exception.AlreadyExistsException;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.repository.CategoryRepository;
import uz.hiparts.hipartsuz.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category save(String name) {
        if (categoryRepository.findByName(name).isPresent())
            throw new AlreadyExistsException("Category");
        return categoryRepository.save(Category.builder().name(name).build());
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category"));
        /// todo categoryni ochirgan payt qaysidur product ga tegishli bosa categoryni ochirish mumkinmas bo'sin
        categoryRepository.deleteById(id);
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
