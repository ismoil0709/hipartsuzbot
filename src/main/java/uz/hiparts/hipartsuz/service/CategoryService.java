package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.Category;

import java.util.List;

@Service
public interface CategoryService {
    Category save(String name);
    Category getById(Long id);
    Category getByName(String name);
    List<Category> getAll();
    void deleteById(Long id);
}
