package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.Category;

import java.util.List;

@Service
public interface CategoryService {

    Category save(String name);

    void delete(Long id);

    Category getById(Long id);

    List<Category> getAll();

    Category getByName(String name);
}
