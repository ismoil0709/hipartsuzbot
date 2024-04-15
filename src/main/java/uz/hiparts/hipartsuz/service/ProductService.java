package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;

import java.util.List;

@Service
public interface ProductService {
    ProductDto create(ProductCreateUpdateDto dto);
    ProductDto update(ProductCreateUpdateDto dto);
    ProductDto getById(Long id);
    ProductDto getByName(String name);
    ProductDto getByCategory(Category category);
    void delete(Long id);
    List<ProductDto> getAll();
}
